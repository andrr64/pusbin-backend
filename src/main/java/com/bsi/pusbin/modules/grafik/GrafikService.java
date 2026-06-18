package com.bsi.pusbin.modules.grafik;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.modules.grafik.schema.ChartResponse;
import com.bsi.pusbin.modules.grafik.schema.RawChartRow;
import com.bsi.pusbin.modules.grafik.schema.SeriesValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GrafikService {

    private final GrafikRepository repository;

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

    // Helper: Map flat list (Simple charts)
    private ChartResponse mapSimpleChart(List<RawChartRow> rows) {
        List<String> x = new ArrayList<>();
        Map<String, Long> valueMap = new HashMap<>();
        for (RawChartRow r : rows) {
            String cat = r.category() != null ? r.category() : "Unknown";
            valueMap.put(cat, r.value());
            if (!x.contains(cat)) {
                x.add(cat);
            }
        }
        x.sort(String::compareToIgnoreCase);

        List<Long> y = new ArrayList<>();
        for (String cat : x) {
            y.add(valueMap.getOrDefault(cat, 0L));
        }
        return new ChartResponse(x, y);
    }

    // Helper: Map grouped list (Grouped/Stacked charts)
    private ChartResponse mapGroupedChart(List<RawChartRow> rows) {
        List<String> xList = new ArrayList<>();
        List<String> seriesLabels = new ArrayList<>();
        Map<String, Map<String, Long>> lookup = new HashMap<>();

        for (RawChartRow r : rows) {
            String cat = r.category() != null ? r.category() : "Unknown";
            String series = r.seriesLabel() != null ? r.seriesLabel() : "Unknown";

            if (!xList.contains(cat)) {
                xList.add(cat);
            }
            if (!seriesLabels.contains(series)) {
                seriesLabels.add(series);
            }

            lookup.computeIfAbsent(cat, k -> new HashMap<>()).put(series, r.value());
        }

        xList.sort(String::compareToIgnoreCase);
        seriesLabels.sort(String::compareToIgnoreCase);

        List<List<SeriesValue>> yList = new ArrayList<>();
        for (String cat : xList) {
            List<SeriesValue> seriesForCat = new ArrayList<>();
            Map<String, Long> seriesValues = lookup.getOrDefault(cat, Collections.emptyMap());

            for (String series : seriesLabels) {
                long val = seriesValues.getOrDefault(series, 0L);
                seriesForCat.add(new SeriesValue(series, val));
            }
            yList.add(seriesForCat);
        }

        return new ChartResponse(xList, yList);
    }

    // Helper: Map trend chart (Timeline/Trend charts)
    private ChartResponse mapTrendChart(List<RawChartRow> rows) {
        List<LocalDate> dates = new ArrayList<>();
        List<String> seriesLabels = new ArrayList<>();
        Map<LocalDate, Map<String, Long>> lookup = new HashMap<>();

        for (RawChartRow r : rows) {
            if (r.category() == null) continue;
            LocalDate date = LocalDate.parse(r.category());
            String series = r.seriesLabel() != null ? r.seriesLabel() : "Unknown";

            if (!dates.contains(date)) {
                dates.add(date);
            }
            if (!seriesLabels.contains(series)) {
                seriesLabels.add(series);
            }

            lookup.computeIfAbsent(date, k -> new HashMap<>()).put(series, r.value());
        }

        Collections.sort(dates);
        seriesLabels.sort(String::compareToIgnoreCase);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy", new Locale("id", "ID"));

        List<String> xList = new ArrayList<>();
        List<List<SeriesValue>> yList = new ArrayList<>();

        for (LocalDate date : dates) {
            xList.add(date.format(formatter));

            List<SeriesValue> seriesForDate = new ArrayList<>();
            Map<String, Long> seriesValues = lookup.getOrDefault(date, Collections.emptyMap());

            for (String series : seriesLabels) {
                long val = seriesValues.getOrDefault(series, 0L);
                seriesForDate.add(new SeriesValue(series, val));
            }
            yList.add(seriesForDate);
        }

        return new ChartResponse(xList, yList);
    }

    // Chart 1: Sebaran ASN Berdasar Jenjang Jabatan
    public ChartResponse getSebaranAsnJenjang(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranAsnJenjang(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapGroupedChart(data);
    }

    // Chart 2: Persentase Gender
    public ChartResponse getPersentaseGender(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getPersentaseGender(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapSimpleChart(data);
    }

    // Chart 3: Persentase ASN JF MASN
    public ChartResponse getPersentaseAsnJfMasn(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getPersentaseAsnJfMasn(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapSimpleChart(data);
    }

    // Chart 4: Sebaran ASN JFMASN
    public ChartResponse getSebaranAsnJfmasnInstansi(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranAsnJfmasnInstansi(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapGroupedChart(data);
    }

    // Chart 5: Sebaran ASN K/L/PD
    public ChartResponse getSebaranAsnKlpd(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranAsnKlpd(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapGroupedChart(data);
    }

    // Chart 6: Sebaran ASN Berdasar Jabatan
    public ChartResponse getSebaranAsnJabatan(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranAsnJabatan(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapGroupedChart(data);
    }

    // Chart 7: Tren Kenaikan Jumlah JF Bidang MASN
public ChartResponse getTrenKenaikanJf(FilterRequest req) {

    Criteria c = resolveCriteria(req);

    List<RawChartRow> data = repository.getTrenKenaikanJf(
            c.namaJabatanId
    );

    return mapTrendChart(data);
}

    // Chart 8: Golongan Ruang
    public ChartResponse getGolonganRuang(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getGolonganRuang(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapSimpleChart(data);
    }

    // Chart 9: Presentase JF Bidang MASN
    public ChartResponse getPersentaseJfMasn(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getPersentaseJfMasn(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapSimpleChart(data);
    }

    // Chart 10: Sebaran Kategori
    public ChartResponse getSebaranKategori(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranKategori(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );
        return mapGroupedChart(data);
    }

    // Chart 11: Masa Kerja Jabatan
    public ChartResponse getMasaKerjaJabatan(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> rows = repository.getMasaKerjaJabatan(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );

        long lessThan9 = 0;
        long greaterOrEqual9 = 0;
        for (RawChartRow r : rows) {
            if ("<9 Tahun".equals(r.category())) {
                lessThan9 = r.value();
            } else if (">=9 Tahun".equals(r.category())) {
                greaterOrEqual9 = r.value();
            }
        }
        return new ChartResponse(List.of("<9 Tahun", ">=9 Tahun"), List.of(lessThan9, greaterOrEqual9));
    }

    // Chart 12: Masa Kerja Golongan
    public ChartResponse getMasaKerjaGolongan(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> rows = repository.getMasaKerjaGolongan(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId, c.jenisInstansi,
                c.jenisKelaminId, c.golonganId, c.pendidikanId, c.masaKerjaGolongan, c.masaKerjaJabatan, c.kategoriJf
        );

        long lessThan5 = 0;
        long greaterOrEqual5 = 0;
        for (RawChartRow r : rows) {
            if ("<5 Tahun".equals(r.category())) {
                lessThan5 = r.value();
            } else if (">=5 Tahun".equals(r.category())) {
                greaterOrEqual5 = r.value();
            }
        }
        return new ChartResponse(List.of("<5 Tahun", ">=5 Tahun"), List.of(lessThan5, greaterOrEqual5));
    }
}

