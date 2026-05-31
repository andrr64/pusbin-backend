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

    private static final Map<Integer, String> JENJANG_ID_TO_NAME = Map.of(
            1, "Pertama", 2, "Muda", 3, "Madya", 4, "Utama", 5, "Terampil", 6, "Mahir", 7, "Penyelia"
    );
    private static final Map<Integer, String> KATEGORI_ID_TO_NAME = Map.of(
            1, "Kementerian", 2, "Kabupaten", 3, "LPNK", 4, "Provinsi", 5, "Kota", 6, "KLNS", 7, "KLN", 8, "Kementerian Koordinator"
    );

    private Criteria resolveCriteria(FilterRequest req) {
        String jenjangStr = req.jenjang();
        if (jenjangStr == null && req.jenjangId() != null) {
            jenjangStr = JENJANG_ID_TO_NAME.get(req.jenjangId());
        }

        String kategoriStr = req.kategoriInstansi();
        if (kategoriStr == null && req.kategoriInstansiId() != null) {
            kategoriStr = KATEGORI_ID_TO_NAME.get(req.kategoriInstansiId());
        }

        return new Criteria(
                req.instansiId(),
                req.jenisAsnId(),
                req.nomenklaturId(),
                jenjangStr,
                kategoriStr,
                req.wilayahPokjaId(),
                req.namaJabatanId()
        );
    }

    private record Criteria(
            Integer instansiId,
            Integer jenisAsnId,
            Integer nomenklaturId,
            String jenjang,
            String kategori,
            Integer wilayahPokjaId,
            Integer namaJabatanId
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
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );
        return mapGroupedChart(data);
    }

    // Chart 2: Persentase Gender
    public ChartResponse getPersentaseGender(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getPersentaseGender(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );
        return mapSimpleChart(data);
    }

    // Chart 3: Persentase ASN JF MASN
    public ChartResponse getPersentaseAsnJfMasn(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getPersentaseAsnJfMasn(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );
        return mapSimpleChart(data);
    }

    // Chart 4: Sebaran ASN JFMASN
    public ChartResponse getSebaranAsnJfmasnInstansi(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranAsnJfmasnInstansi(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );
        return mapGroupedChart(data);
    }

    // Chart 5: Sebaran ASN K/L/PD
    public ChartResponse getSebaranAsnKlpd(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranAsnKlpd(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );
        return mapGroupedChart(data);
    }

    // Chart 6: Sebaran ASN Berdasar Jabatan
    public ChartResponse getSebaranAsnJabatan(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranAsnJabatan(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
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
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );
        return mapSimpleChart(data);
    }

    // Chart 9: Presentase JF Bidang MASN
    public ChartResponse getPersentaseJfMasn(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getPersentaseJfMasn(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );
        return mapSimpleChart(data);
    }

    // Chart 10: Sebaran Kategori
    public ChartResponse getSebaranKategori(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> data = repository.getSebaranKategori(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );
        return mapGroupedChart(data);
    }

    // Chart 11: Masa Kerja Jabatan
    public ChartResponse getMasaKerjaJabatan(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<RawChartRow> rows = repository.getMasaKerjaJabatan(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
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
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
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

