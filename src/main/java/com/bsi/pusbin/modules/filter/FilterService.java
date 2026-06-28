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
            8, "Kementerian Koordinator",
            9, "Pemda",
            10, "Lembaga");
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
        List<String> jenjangList = req.jenjang() != null ? new ArrayList<>(req.jenjang()) : new ArrayList<>();
        if (req.jenjangId() != null) {
            for (Integer id : req.jenjangId()) {
                String name = JENJANG_ID_TO_NAME.get(id);
                if (name != null) {
                    jenjangList.add(name);
                }
            }
        }

        // Resolve Kategori Instansi
        List<String> kategoriList = req.kategoriInstansi() != null ? new ArrayList<>(req.kategoriInstansi()) : new ArrayList<>();
        if (req.kategoriInstansiId() != null) {
            for (Integer id : req.kategoriInstansiId()) {
                String name = KATEGORI_ID_TO_NAME.get(id);
                if (name != null) {
                    kategoriList.add(name);
                }
            }
        }

        // Resolve Jenis Instansi
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

        // 1. Fetch Instansi options (exclude instansiId filter)
        Set<List<Object>> instansiSet = new HashSet<>();
        List<FilterRow> instansiRows = filterRepository.fetchFilterRows(
                null, req.jenisAsnId(), req.nomenklaturId(), jenjangList, kategoriList, req.wilayahPokjaId(), req.namaJabatanId(), jenisInstansiList
        );
        for (FilterRow r : instansiRows) {
            if (r.getIdInstansi() != null && r.getNamaInstansi() != null) {
                instansiSet.add(List.of(r.getNamaInstansi(), r.getIdInstansi()));
            }
        }

        // 2. Fetch Jenis ASN options (exclude jenisAsnId filter)
        Set<List<Object>> jenisAsnSet = new HashSet<>();
        List<FilterRow> jenisAsnRows = filterRepository.fetchFilterRows(
                req.instansiId(), null, req.nomenklaturId(), jenjangList, kategoriList, req.wilayahPokjaId(), req.namaJabatanId(), jenisInstansiList
        );
        for (FilterRow r : jenisAsnRows) {
            if (r.getIdJenisAsn() != null && r.getNamaJenis() != null) {
                jenisAsnSet.add(List.of(r.getNamaJenis(), r.getIdJenisAsn()));
            }
        }

        // 3. Fetch Nomenklatur options (exclude nomenklaturId filter)
        Set<List<Object>> nomenklaturSet = new HashSet<>();
        List<FilterRow> nomenklaturRows = filterRepository.fetchFilterRows(
                req.instansiId(), req.jenisAsnId(), null, jenjangList, kategoriList, req.wilayahPokjaId(), req.namaJabatanId(), jenisInstansiList
        );
        for (FilterRow r : nomenklaturRows) {
            if (r.getIdNomenklatur() != null && r.getNamaNomenklatur() != null) {
                nomenklaturSet.add(List.of(r.getNamaNomenklatur(), r.getIdNomenklatur()));
            }
        }

        // 4. Fetch Jenjang options (exclude jenjang list/id filter)
        Set<List<Object>> jenjangSet = new HashSet<>();
        List<FilterRow> jenjangRows = filterRepository.fetchFilterRows(
                req.instansiId(), req.jenisAsnId(), req.nomenklaturId(), null, kategoriList, req.wilayahPokjaId(), req.namaJabatanId(), jenisInstansiList
        );
        for (FilterRow r : jenjangRows) {
            if (r.getJenjang() != null) {
                String val = r.getJenjang();
                int id = JENJANG_NAME_TO_ID.getOrDefault(val.toLowerCase(), Math.abs(val.hashCode()) + 100);
                jenjangSet.add(List.of(val, id));
            }
        }

        // 5. Fetch Kategori Instansi options (exclude kategori list/id filter)
        Set<List<Object>> kategoriSet = new HashSet<>();
        List<FilterRow> kategoriRows = filterRepository.fetchFilterRows(
                req.instansiId(), req.jenisAsnId(), req.nomenklaturId(), jenjangList, null, req.wilayahPokjaId(), req.namaJabatanId(), jenisInstansiList
        );
        for (FilterRow r : kategoriRows) {
            if (r.getKategori() != null) {
                String val = r.getKategori();
                int id = KATEGORI_NAME_TO_ID.getOrDefault(val.toLowerCase(), Math.abs(val.hashCode()) + 100);
                kategoriSet.add(List.of(val, id));
            }
        }

        // 6. Fetch Wilayah Pokja options (exclude wilayahPokjaId filter)
        Set<List<Object>> pokjaSet = new HashSet<>();
        List<FilterRow> pokjaRows = filterRepository.fetchFilterRows(
                req.instansiId(), req.jenisAsnId(), req.nomenklaturId(), jenjangList, kategoriList, null, req.namaJabatanId(), jenisInstansiList
        );
        for (FilterRow r : pokjaRows) {
            if (r.getIdWilayahPokja() != null && r.getNamaPokja() != null) {
                pokjaSet.add(List.of(r.getNamaPokja(), r.getIdWilayahPokja()));
            }
        }

        // 7. Fetch Nama Jabatan options (exclude namaJabatanId filter)
        // Collect distinct namaJabatan values, grouping multiple id_jabatan under the same name
        Map<String, List<String>> distinctNamaJabatan = new LinkedHashMap<>();
        List<FilterRow> jabatanRows = filterRepository.fetchFilterRows(
                req.instansiId(), req.jenisAsnId(), req.nomenklaturId(), jenjangList, kategoriList, req.wilayahPokjaId(), null, jenisInstansiList
        );
        for (FilterRow r : jabatanRows) {
            if (r.getIdJabatan() != null && r.getNamaJabatan() != null) {
                String rawJab = r.getNamaJabatan().trim();
                String[] jenjangs = {
                    " Ahli Pertama", " Ahli Madya", " Ahli Utama", " Ahli Muda",
                    " JPT Pratama", " JPT Madya", " JPT Utama",
                    " Administrator", " Pengawas", " Pelaksana",
                    " Terampil", " Mahir", " Penyelia",
                    " Pertama", " Muda", " Madya", " Utama"
                };
                for (String j : jenjangs) {
                    if (rawJab.endsWith(j)) {
                        rawJab = rawJab.substring(0, rawJab.length() - j.length()).trim();
                        break;
                    }
                }
                String namaJab = rawJab;
                
        if (!namaJab.isEmpty()) {
                    List<String> idList = distinctNamaJabatan.computeIfAbsent(namaJab, k -> new java.util.ArrayList<>());
                    String idStr = String.valueOf(r.getIdJabatan());
                    if (!idList.contains(idStr)) {
                        idList.add(idStr);
                    }
                }
            }
        }
        Set<List<Object>> jabatanSet = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : distinctNamaJabatan.entrySet()) {
            jabatanSet.add(List.of(entry.getKey(), String.join(",", entry.getValue())));
        }

        // 8. Fetch Jenis Instansi options (exclude jenisInstansi list/id filter)
        Set<List<Object>> jenisInstansiSet = new HashSet<>();
        List<FilterRow> jenisInstansiRows = filterRepository.fetchFilterRows(
                req.instansiId(), req.jenisAsnId(), req.nomenklaturId(), jenjangList, kategoriList, req.wilayahPokjaId(), req.namaJabatanId(), null
        );
        for (FilterRow r : jenisInstansiRows) {
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
