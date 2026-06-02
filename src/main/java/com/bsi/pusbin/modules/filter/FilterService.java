package com.bsi.pusbin.modules.filter;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.modules.filter.schema.FilterRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilterService {

    private final FilterRepository filterRepository;

    // Static mappings for Jenjang
    private static final Map<Integer, String> JENJANG_ID_TO_NAME = Map.of(
            1, "Pertama",
            2, "Muda",
            3, "Madya",
            4, "Utama",
            5, "Terampil",
            6, "Mahir",
            7, "Penyelia");
    private static final Map<String, Integer> JENJANG_NAME_TO_ID;
    static {
        Map<String, Integer> map = new HashMap<>();
        JENJANG_ID_TO_NAME.forEach((id, name) -> {
            map.put(name.toLowerCase(), id);
            if (name.equalsIgnoreCase("Terampil")) {
                map.put("terampil", id); // support variant
            }
        });
        JENJANG_NAME_TO_ID = Collections.unmodifiableMap(map);
    }

    // Static mappings for Kategori Instansi
    private static final Map<Integer, String> KATEGORI_ID_TO_NAME = Map.of(
            1, "Kementerian",
            2, "Kabupaten",
            3, "LPNK",
            4, "Provinsi",
            5, "Kota",
            6, "KLNS",
            7, "KLN",
            8, "Kementerian Koordinator");
    private static final Map<String, Integer> KATEGORI_NAME_TO_ID;
    static {
        Map<String, Integer> map = new HashMap<>();
        KATEGORI_ID_TO_NAME.forEach((id, name) -> {
            map.put(name.toLowerCase(), id);
            if (name.equalsIgnoreCase("Kementerian")) {
                map.put("kementrian", id); // support variant
            }
            if (name.equalsIgnoreCase("Kementerian Koordinator")) {
                map.put("kementrian koordinator", id); // support variant
            }
        });
        KATEGORI_NAME_TO_ID = Collections.unmodifiableMap(map);
    }

    public Map<String, List<List<Object>>> getDynamicFilters(FilterRequest req) {
        // Resolve Jenjang
        String jenjangStr = req.jenjang();
        if (jenjangStr == null && req.jenjangId() != null) {
            jenjangStr = JENJANG_ID_TO_NAME.get(req.jenjangId());
        }

        // Resolve Kategori Instansi
        String kategoriStr = req.kategoriInstansi();
        if (kategoriStr == null && req.kategoriInstansiId() != null) {
            kategoriStr = KATEGORI_ID_TO_NAME.get(req.kategoriInstansiId());
        }

        // Fetch matching combinations
        List<FilterRow> rows = filterRepository.fetchFilterRows(
                req.instansiId(),
                req.jenisAsnId(),
                req.nomenklaturId(),
                jenjangStr,
                kategoriStr,
                req.wilayahPokjaId(),
                req.namaJabatanId(),
                req.jenisInstansi());

        // Sets to guarantee uniqueness
        Set<List<Object>> instansiSet = new HashSet<>();
        Set<List<Object>> jenisAsnSet = new HashSet<>();
        Set<List<Object>> nomenklaturSet = new HashSet<>();
        Set<List<Object>> jenjangSet = new HashSet<>();
        Set<List<Object>> kategoriSet = new HashSet<>();
        Set<List<Object>> pokjaSet = new HashSet<>();
        Set<List<Object>> jabatanSet = new HashSet<>();
        Set<List<Object>> jenisInstansiSet = new HashSet<>();

        for (FilterRow r : rows) {
            if (r.getIdInstansi() != null && r.getNamaInstansi() != null) {
                instansiSet.add(List.of(r.getNamaInstansi(), r.getIdInstansi()));
            }
            if (r.getIdJenisAsn() != null && r.getNamaJenis() != null) {
                jenisAsnSet.add(List.of(r.getNamaJenis(), r.getIdJenisAsn()));
            }
            if (r.getIdNomenklatur() != null && r.getNamaNomenklatur() != null) {
                nomenklaturSet.add(List.of(r.getNamaNomenklatur(), r.getIdNomenklatur()));
            }
            if (r.getJenjang() != null) {
                String val = r.getJenjang();
                int id = JENJANG_NAME_TO_ID.getOrDefault(val.toLowerCase(), Math.abs(val.hashCode()) + 100);
                jenjangSet.add(List.of(val, id));
            }
            if (r.getKategori() != null) {
                String val = r.getKategori();
                int id = KATEGORI_NAME_TO_ID.getOrDefault(val.toLowerCase(), Math.abs(val.hashCode()) + 100);
                kategoriSet.add(List.of(val, id));
            }
            if (r.getIdWilayahPokja() != null && r.getNamaPokja() != null) {
                pokjaSet.add(List.of(r.getNamaPokja(), r.getIdWilayahPokja()));
            }
            if (r.getIdJabatan() != null && r.getNamaJabatan() != null) {
                jabatanSet.add(List.of(r.getNamaJabatan(), r.getIdJabatan()));
            }
            if (r.getJenisInstansi() != null) {
                String val = r.getJenisInstansi();
                int id = Math.abs(val.hashCode()) + 100;
                jenisInstansiSet.add(List.of(val, id));
            }
        }

        // Prepare response structure
        Map<String, List<List<Object>>> data = new LinkedHashMap<>();
        data.put("Instansi", sortAndConvert(instansiSet));
        data.put("JenisASN", sortAndConvert(jenisAsnSet));
        data.put("Nomenklatur", sortAndConvert(nomenklaturSet));
        data.put("Jenjang", sortAndConvert(jenjangSet));
        data.put("Kategori Instansi", sortAndConvert(kategoriSet));
        data.put("Wilayah Pokja", sortAndConvert(pokjaSet));
        data.put("Nama Jabatan", sortAndConvert(jabatanSet));
        data.put("Jenis Instansi", sortAndConvert(jenisInstansiSet));

        return data;
    }

    private List<List<Object>> sortAndConvert(Set<List<Object>> set) {
        List<List<Object>> list = new ArrayList<>(set);
        list.sort((a, b) -> {
            String labelA = (String) a.get(0);
            String labelB = (String) b.get(0);
            return labelA.compareToIgnoreCase(labelB);
        });
        return list;
    }
}
