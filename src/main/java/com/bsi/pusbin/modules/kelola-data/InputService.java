package com.bsi.pusbin.modules.input;

import com.bsi.pusbin.modules.input.schema.InputPageResponse;
import com.bsi.pusbin.modules.input.schema.InputRequest;
import com.bsi.pusbin.modules.input.schema.InputResponse;
import com.bsi.pusbin.modules.input.schema.SyncRequest;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.bsi.pusbin.modules.input.schema.DropdownOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class InputService {

    private final InputRepository inputRepository;

    @Transactional
    public void save(InputRequest req) {
        if (req.getIdAsn() == null) {
            throw new BusinessException("ID ASN/NIP tidak boleh kosong");
        }

        // 1. Resolve or create independent master data records
        Integer idJenisAsn = inputRepository.findOrCreateJenisAsn(req.getJenisAsn());
        Integer idKedudukan = inputRepository.findOrCreateKedudukanAsn(req.getKedudukanAsn());
        Integer idJenisKelamin = inputRepository.findOrCreateJenisKelamin(req.getJenisKelamin());
        
        // 2. Resolve or create location/instansi related masters
        Integer idPokja = inputRepository.findOrCreateWilayahPokja(req.getWilayahPokja());
        Integer idWilker = inputRepository.findOrCreateWilayahBkn(req.getWilkerBkn(), idPokja, req.getNoUrutWilker());
        Integer idInstansi = inputRepository.findOrCreateInstansi(req.getInstansiKerja(), req.getKategoriInstansi(), idWilker);
        
        // 3. Resolve or create education master data
        Integer idPendidikan = inputRepository.findOrCreatePendidikan(req.getPendidikan(), req.getTingkatPendidikan());
        
        // 4. Resolve or create position/job details
        Integer idNomenklatur = inputRepository.findOrCreateNomenklatur(req.getNomenklatur());
        Integer idJenisJf = inputRepository.findOrCreateJenisJf(req.getJenisJf());
        
        // Fallback for namaJabatan if not specified
        String namaJabatan = req.getNamaJabatan();
        if (namaJabatan == null || namaJabatan.trim().isEmpty()) {
            namaJabatan = req.getJabatan();
        }
        Integer idJabatan = inputRepository.findOrCreateJabatan(namaJabatan, req.getJenjang(), idNomenklatur, idJenisJf);
        
        // Fallback for golongan if not specified
        String gol = req.getGolonganRuang();
        if (gol == null || gol.trim().isEmpty()) {
            gol = req.getGolongan();
        }
        Integer idGolongan = inputRepository.findOrCreateGolongan(gol);
        Integer idJenisDiklat = inputRepository.findOrCreateJenisDiklat(req.getJenisDiklat());
        
        // 5. Parse and calculate masa kerja values
        Integer mkGol = parseMasaKerja(req.getMkGolongan());
        Integer mkJab = parseMasaKerja(req.getMkJabatan());
        
        // 6. Persist/Upsert target ASN record
        inputRepository.upsertAsn(
                req.getIdAsn(),
                req.getNip(),
                idJenisAsn,
                idKedudukan,
                idJenisKelamin,
                idPendidikan,
                idInstansi,
                idJabatan,
                idGolongan,
                idJenisDiklat,
                req.getTmtJabatan(),
                mkJab,
                req.getTmtGolru(),
                mkGol
        );
    }

    @Transactional(readOnly = true)
    public InputPageResponse<InputResponse> getPaginatedList(String search, int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0) size = 20;
        int offset = page * size;
        
        List<InputResponse> content = inputRepository.fetchPaginated(search, size, offset);
        long totalElements = inputRepository.countTotal(search);
        int totalPages = (int) Math.ceil((double) totalElements / size);
        
        return new InputPageResponse<>(content, page, size, totalElements, totalPages);
    }

    @Transactional(readOnly = true)
    public InputResponse getDetail(Long idAsn) {
        return inputRepository.findById(idAsn)
                .orElseThrow(() -> new BusinessException("Data ASN dengan ID " + idAsn + " tidak ditemukan"));
    }

    @Transactional(readOnly = true)
    public Map<String, List<DropdownOption>> getFormOptions() {
        Map<String, List<DropdownOption>> options = new HashMap<>();
        options.put("jenisAsn", inputRepository.getJenisAsnOptions());
        options.put("kedudukanAsn", inputRepository.getKedudukanAsnOptions());
        options.put("jenisKelamin", inputRepository.getJenisKelaminOptions());
        options.put("instansiKerja", inputRepository.getInstansiKerjaOptions());
        options.put("kategoriInstansi", inputRepository.getKategoriInstansiOptions());
        options.put("jenisInstansi", inputRepository.getJenisInstansiOptions());
        options.put("tingkatPendidikan", inputRepository.getTingkatPendidikanOptions());
        options.put("pendidikan", inputRepository.getPendidikanOptions());
        options.put("jabatan", inputRepository.getJabatanOptions());
        options.put("jenjang", inputRepository.getJenjangOptions());
        options.put("jenisJf", inputRepository.getJenisJfOptions());
        options.put("namaJabatan", inputRepository.getJabatanOptions());
        options.put("nomenklatur", inputRepository.getNomenklaturOptions());
        options.put("golonganRuang", inputRepository.getGolonganOptions());
        options.put("jenisDiklat", inputRepository.getJenisDiklatOptions());
        options.put("wilkerBkn", inputRepository.getWilkerBknOptions());
        options.put("wilayahPokja", inputRepository.getWilayahPokjaOptions());
        return options;
    }

    @Transactional
    public void delete(Long idAsn) {
        boolean deleted = inputRepository.deleteById(idAsn);
        if (!deleted) {
            throw new BusinessException("Data ASN dengan ID " + idAsn + " tidak ditemukan untuk dihapus");
        }
    }

    @Transactional
    public void sync(SyncRequest syncReq) {
        if (syncReq.getDelete() != null) {
            for (Long id : syncReq.getDelete()) {
                inputRepository.deleteById(id);
            }
        }
        if (syncReq.getUpsert() != null) {
            for (InputRequest req : syncReq.getUpsert()) {
                save(req);
            }
        }
    }

    // Parses masa kerja from Date (LocalDate), String, or Integer representation
    private Integer parseMasaKerja(Object value) {
        if (value == null) {
            return null;
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }
        
        // 1. Try parsing as direct integer
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            // 2. Try parsing as date (yyyy-MM-dd)
            try {
                LocalDate date = LocalDate.parse(str);
                return java.time.Period.between(date, LocalDate.now()).getYears();
            } catch (Exception ex) {
                // 3. Fallback to extracting the first group of digits (e.g. "5 Tahun" -> 5)
                try {
                    Matcher m = Pattern.compile("\\d+").matcher(str);
                    if (m.find()) {
                        return Integer.parseInt(m.group());
                    }
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
}
