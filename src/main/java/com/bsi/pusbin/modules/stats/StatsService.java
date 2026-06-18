package com.bsi.pusbin.modules.stats;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.modules.stats.schema.StatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsRepository repository;

    private static final Map<Integer, String> JENJANG_ID_TO_NAME = Map.ofEntries(
            Map.entry(1, "Pertama"),
            Map.entry(2, "Muda"),
            Map.entry(3, "Madya"),
            Map.entry(4, "Utama"),
            Map.entry(5, "Terampil"),
            Map.entry(6, "Mahir"),
            Map.entry(7, "Penyelia"),
            Map.entry(8, "Ahli Pertama"),
            Map.entry(9, "Ahli Muda"),
            Map.entry(10, "Ahli Madya"),
            Map.entry(11, "Ahli Utama"),
            Map.entry(12, "Administrator"),
            Map.entry(13, "Pengawas"),
            Map.entry(14, "Pelaksana"),
            Map.entry(15, "JPT Pratama"),
            Map.entry(16, "JPT Madya"),
            Map.entry(17, "JPT Utama")
    );
    private static final Map<Integer, String> KATEGORI_ID_TO_NAME = Map.of(
            1, "Kementerian", 2, "Kabupaten", 3, "LPNK", 4, "Provinsi", 5, "Kota", 6, "KLNS", 7, "KLN", 8, "Kementerian Koordinator",
            9, "Pemda", 10, "Lembaga"
    );

    private Criteria resolveCriteria(FilterRequest req) {
        List<String> jenjangList = req.jenjang() != null ? new ArrayList<>(req.jenjang()) : new ArrayList<>();
        if (req.jenjangId() != null) {
            for (Integer id : req.jenjangId()) {
                String name = JENJANG_ID_TO_NAME.get(id);
                if (name != null) {
                    jenjangList.add(name);
                }
            }
        }

        List<String> kategoriList = req.kategoriInstansi() != null ? new ArrayList<>(req.kategoriInstansi()) : new ArrayList<>();
        if (req.kategoriInstansiId() != null) {
            for (Integer id : req.kategoriInstansiId()) {
                String name = KATEGORI_ID_TO_NAME.get(id);
                if (name != null) {
                    kategoriList.add(name);
                }
            }
        }

        List<String> jenisInstansiList = req.jenisInstansi() != null ? new ArrayList<>(req.jenisInstansi()) : new ArrayList<>();
        if (req.jenisInstansiId() != null) {
            for (Integer id : req.jenisInstansiId()) {
                if (id == Math.abs("Instansi Pusat".hashCode()) + 100 || id == Math.abs("Pusat".hashCode()) + 100) {
                    jenisInstansiList.add("Instansi Pusat");
                    jenisInstansiList.add("Pusat");
                } else if (id == Math.abs("Instansi Daerah".hashCode()) + 100) {
                    jenisInstansiList.add("Instansi Daerah");
                    jenisInstansiList.add("Daerah Provinsi");
                    jenisInstansiList.add("Daerah Kabupaten");
                    jenisInstansiList.add("Daerah Kota");
                } else if (id == Math.abs("Daerah Provinsi".hashCode()) + 100) {
                    jenisInstansiList.add("Daerah Provinsi");
                } else if (id == Math.abs("Daerah Kabupaten".hashCode()) + 100) {
                    jenisInstansiList.add("Daerah Kabupaten");
                } else if (id == Math.abs("Daerah Kota".hashCode()) + 100) {
                    jenisInstansiList.add("Daerah Kota");
                }
            }
        }

        return new Criteria(
                req.instansiId(),
                req.jenisAsnId(),
                req.nomenklaturId(),
                jenjangList,
                kategoriList,
                req.wilayahPokjaId(),
                req.namaJabatanId(),
                jenisInstansiList,
                req.jenisKelaminId(),
                req.golonganId(),
                req.pendidikanId(),
                req.masaKerjaGolongan(),
                req.masaKerjaJabatan(),
                req.kategoriJf()
        );
    }

    private record Criteria(
            List<Integer> instansiId,
            List<Integer> jenisAsnId,
            List<Integer> nomenklaturId,
            List<String> jenjang,
            List<String> kategori,
            List<Integer> wilayahPokjaId,
            List<Integer> namaJabatanId,
            List<String> jenisInstansi,
            List<Integer> jenisKelaminId,
            List<Integer> golonganId,
            List<Integer> pendidikanId,
            List<String> masaKerjaGolongan,
            List<String> masaKerjaJabatan,
            List<String> kategoriJf
    ) {}

    public StatsResponse getSummary(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        
        long totalPegawai = repository.countTotalPegawai(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        
        long totalInstansi = repository.countTotalInstansi(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        
        return new StatsResponse(totalPegawai, totalInstansi);
    }

    public String getLastUpdatedAt() {
        return repository.getLastUpdatedAt();
    }
}
