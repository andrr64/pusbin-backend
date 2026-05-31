package com.bsi.pusbin.modules.table;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.modules.table.schema.TableDataRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    // Helper static mappings identical to filter logic
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

        return new Criteria(req.instansiId(), req.jenisAsnId(), req.nomenklaturId(), jenjangStr, kategoriStr, req.wilayahPokjaId(), req.namaJabatanId());
    }

    public List<List<Object>> getWilayahKerjaTable(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<TableDataRow> data = tableRepository.getWilayahKerjaData(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );

        List<List<Object>> result = new ArrayList<>();
        result.add(List.of("Wilayah Kerja", "Jumlah"));
        for (TableDataRow r : data) {
            result.add(List.of(r.getLabel(), r.getCount()));
        }
        return result;
    }

    public List<List<Object>> getJabatanTable(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<TableDataRow> data = tableRepository.getJabatanData(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );

        List<List<Object>> result = new ArrayList<>();
        result.add(List.of("Jabatan", "Jumlah"));
        for (TableDataRow r : data) {
            result.add(List.of(r.getLabel(), r.getCount()));
        }
        return result;
    }

    public List<List<Object>> getPendidikanTable(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<TableDataRow> data = tableRepository.getPendidikanData(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );

        long total = 0;
        for (TableDataRow r : data) {
            total += r.getCount();
        }

        List<List<Object>> result = new ArrayList<>();
        result.add(List.of("tingkat pendidikan", "Jumlah", "persentase"));
        for (TableDataRow r : data) {
            double percentage = total > 0 ? ((double) r.getCount() / total) * 100 : 0.0;
            String percentageStr = Math.round(percentage) + "%";
            result.add(List.of(r.getLabel(), r.getCount(), percentageStr));
        }
        return result;
    }

    public List<List<Object>> getInstansiTable(FilterRequest req) {
        Criteria c = resolveCriteria(req);
        List<TableDataRow> data = tableRepository.getInstansiData(
                c.instansiId, c.jenisAsnId, c.nomenklaturId, c.jenjang, c.kategori, c.wilayahPokjaId, c.namaJabatanId
        );

        List<List<Object>> result = new ArrayList<>();
        result.add(List.of("Instansi Kerja", "Jumlah"));
        for (TableDataRow r : data) {
            result.add(List.of(r.getLabel(), r.getCount()));
        }
        return result;
    }

    private record Criteria(
            Integer instansiId, Integer jenisAsnId, Integer nomenklaturId,
            String jenjang, String kategori, Integer wilayahPokjaId, Integer namaJabatanId
    ) {}
}
