package com.bsi.pusbin.modules.export;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.modules.grafik.GrafikService;
import com.bsi.pusbin.modules.grafik.schema.ChartResponse;
import com.bsi.pusbin.modules.grafik.schema.SeriesValue;
import com.bsi.pusbin.modules.table.TableService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExportService {

    private final TableService tableService;
    private final GrafikService grafikService;
    private final NamedParameterJdbcTemplate jdbc;

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

    public byte[] generateDashboardPdf(FilterRequest req) {
        log.info("Generating PDF export for active filters: {}", req);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        // Document: margins 20pt left/right, 30pt top/bottom
        Document document = new Document(PageSize.A4, 20, 20, 30, 30);
        
        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // 1. Report Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(30, 41, 59));
            Paragraph title = new Paragraph("Laporan Dashboard ASN", titleFont);
            title.setAlignment(Element.ALIGN_LEFT);
            title.setSpacingAfter(4);
            document.add(title);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(100, 116, 139));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", new Locale("id", "ID"));
            String timeStr = LocalDateTime.now().format(formatter);
            Paragraph subtitle = new Paragraph("Waktu Unduh: " + timeStr, subFont);
            subtitle.setSpacingAfter(15);
            document.add(subtitle);

            // 2. Filter Summary Card
            addFilterSummary(document, req);

            // Add small vertical spacer
            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingAfter(10);
            document.add(spacer);

            // 3. Render charts and group them in 2-column grid
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(30, 41, 59));
            Paragraph chartTitle = new Paragraph("Visualisasi Grafik", sectionFont);
            chartTitle.setSpacingAfter(10);
            document.add(chartTitle);

            renderChartsGrid(document, req);

            document.newPage();

            // 4. Render Tables
            Paragraph tableTitle = new Paragraph("Data Tabel", sectionFont);
            tableTitle.setSpacingAfter(15);
            document.add(tableTitle);

            addTables(document, req);

        } catch (Exception e) {
            log.error("Failed to generate PDF document", e);
            throw new RuntimeException("Gagal menghasilkan file PDF: " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return baos.toByteArray();
    }

    private void addFilterSummary(Document doc, FilterRequest req) throws DocumentException {
        // Find resolved names of selected filters
        List<String> summaryLines = new ArrayList<>();

        String jenisAsn = resolveFilterNames("jenis_asn", "id_jenis_asn", "nama_jenis", req.jenisAsnId());
        summaryLines.add("Jenis ASN: " + jenisAsn);

        String jenjang = resolveJenjang(req);
        summaryLines.add("Jenjang: " + jenjang);

        String pokja = resolveFilterNames("wilayah_pokja", "id_wilayah_pokja", "nama_pokja", req.wilayahPokjaId());
        summaryLines.add("Wilayah Pokja: " + pokja);

        String instansi = resolveFilterNames("instansi", "id_instansi", "nama_instansi", req.instansiId());
        summaryLines.add("Instansi: " + instansi);

        String kategori = resolveKategori(req);
        summaryLines.add("Kategori Instansi: " + kategori);

        String nomenklatur = resolveFilterNames("nomenklatur", "id_nomenklatur", "nama_nomenklatur", req.nomenklaturId());
        summaryLines.add("Nomenklatur: " + nomenklatur);

        String jenisInstansi = resolveJenisInstansi(req);
        summaryLines.add("Jenis Instansi: " + jenisInstansi);

        String gender = resolveFilterNames("jenis_kelamin", "id_jenis_kelamin", "nama_kelamin", req.jenisKelaminId());
        summaryLines.add("Jenis Kelamin: " + gender);

        String golongan = resolveFilterNames("golongan", "id_golongan", "golongan_ruang", req.golonganId());
        summaryLines.add("Golongan: " + golongan);

        String pendidikan = resolveFilterNames("pendidikan", "id_pendidikan", "nama_pendidikan", req.pendidikanId());
        summaryLines.add("Pendidikan: " + pendidikan);

        if (req.masaKerjaGolongan() != null && !req.masaKerjaGolongan().isEmpty()) {
            summaryLines.add("Masa Kerja Golongan: " + String.join(", ", req.masaKerjaGolongan()));
        }
        if (req.masaKerjaJabatan() != null && !req.masaKerjaJabatan().isEmpty()) {
            summaryLines.add("Masa Kerja Jabatan: " + String.join(", ", req.masaKerjaJabatan()));
        }
        if (req.kategoriJf() != null && !req.kategoriJf().isEmpty()) {
            summaryLines.add("Kategori JF: " + String.join(", ", req.kategoriJf()));
        }

        // Draw summary inside a table to make it look like a visual card
        PdfPTable card = new PdfPTable(1);
        card.setWidthPercentage(100);
        
        PdfPCell headerCell = new PdfPCell(new Phrase("Filter Aktif", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(51, 65, 85))));
        headerCell.setBackgroundColor(new Color(241, 245, 249));
        headerCell.setPadding(8);
        headerCell.setBorderColor(new Color(226, 232, 240));
        card.addCell(headerCell);

        StringBuilder bodyText = new StringBuilder();
        for (String line : summaryLines) {
            bodyText.append("• ").append(line).append("\n");
        }
        
        PdfPCell bodyCell = new PdfPCell(new Phrase(bodyText.toString().trim(), FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(71, 85, 105))));
        bodyCell.setPadding(10);
        bodyCell.setBorderColor(new Color(226, 232, 240));
        bodyCell.setLeading(12, 1);
        card.addCell(bodyCell);

        doc.add(card);
    }

    private void renderChartsGrid(Document doc, FilterRequest req) throws Exception {
        // We will generate the 12 charts and arrange them in 2-column tables.
        // List of charts to produce:
        // 1. Gender Percentage (Pie)
        // 2. ASN JF MASN (Pie)
        // 3. Sebaran Kategori JFMASN (Bar)
        // 4. Sebaran ASN Jenjang (Bar)
        // 5. Bidang MASN (Bar)
        // 6. Masa Kerja Golongan (Bar)
        // 7. Masa Kerja Jabatan (Bar)
        // 8. Golongan Ruang (Bar)
        // 9. Sebaran KLPD (Bar)
        // 10. Sebaran Jabatan (Bar)
        // 11. Sebaran ASN JFMASN (Bar)
        // 12. Tren Kenaikan (Line)

        JFreeChart genderChart = drawPieChart("Persentase Gender", grafikService.getPersentaseGender(req));
        JFreeChart jfMasnAsnChart = drawPieChart("Persentase ASN JF MASN", grafikService.getPersentaseAsnJfMasn(req));
        JFreeChart kategoriChart = drawStackedBarChart("Sebaran Kategori JF MASN", "Kategori", grafikService.getSebaranKategori(req));
        JFreeChart jenjangChart = drawStackedBarChart("Sebaran Jenjang Jabatan", "Jenjang", grafikService.getSebaranAsnJenjang(req));
        JFreeChart bidangChart = drawBarChart("Sebaran ASN di Bidang MASN", "Jabatan", grafikService.getPersentaseJfMasn(req));
        JFreeChart mkGolonganChart = drawBarChart("Masa Kerja Golongan", "Kategori", grafikService.getMasaKerjaGolongan(req));
        JFreeChart mkJabatanChart = drawBarChart("Masa Kerja Jabatan", "Kategori", grafikService.getMasaKerjaJabatan(req));
        JFreeChart golonganChart = drawBarChart("Sebaran Golongan Ruang", "Golongan", grafikService.getGolonganRuang(req));
        JFreeChart klpdChart = drawStackedBarChart("Sebaran ASN di K/L/PD", "Kategori", grafikService.getSebaranAsnKlpd(req));
        JFreeChart sebaranJabatanChart = drawStackedBarChart("Sebaran ASN Berdasar Jabatan", "Jabatan", grafikService.getSebaranAsnJabatan(req));
        JFreeChart jfmasnInstansiChart = drawStackedBarChart("Sebaran ASN JFMASN", "Kategori", grafikService.getSebaranAsnJfmasnInstansi(req));
        JFreeChart trenChart = drawLineChart("Tren Kenaikan Jumlah JF", "Tanggal", grafikService.getTrenKenaikanJf(req));

        List<JFreeChart> charts = List.of(
                genderChart, jfMasnAsnChart,
                kategoriChart, jenjangChart,
                bidangChart, golonganChart,
                mkGolonganChart, mkJabatanChart,
                klpdChart, sebaranJabatanChart,
                jfmasnInstansiChart, trenChart
        );

        PdfPTable grid = new PdfPTable(2);
        grid.setWidthPercentage(100);
        grid.setSpacingBefore(10);
        grid.setSpacingAfter(10);

        for (int i = 0; i < charts.size(); i++) {
            JFreeChart chart = charts.get(i);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(out, chart, 380, 240);
            Image img = Image.getInstance(out.toByteArray());
            
            PdfPCell cell = new PdfPCell(img);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPadding(6);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            grid.addCell(cell);

            // Add page breaks after every 4 charts to maintain document layout and spacing
            if (i > 0 && (i + 1) % 4 == 0 && (i + 1) < charts.size()) {
                doc.add(grid);
                grid = new PdfPTable(2);
                grid.setWidthPercentage(100);
                grid.setSpacingBefore(10);
                grid.setSpacingAfter(10);
                doc.newPage();
            }
        }

        // Add remaining grid cells if any
        if (grid.getRows().size() > 0) {
            doc.add(grid);
        }
    }

    private JFreeChart drawPieChart(String title, ChartResponse res) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < res.x().size(); i++) {
            String key = res.x().get(i);
            Number val = (Number) res.y().get(i);
            dataset.setValue(key, val);
        }
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, false);
        customizeChartTheme(chart);
        return chart;
    }

    private JFreeChart drawBarChart(String title, String categoryLabel, ChartResponse res) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < res.x().size(); i++) {
            String cat = res.x().get(i);
            Number val = (Number) res.y().get(i);
            dataset.addValue(val, "Jumlah", cat);
        }
        JFreeChart chart = ChartFactory.createBarChart(title, categoryLabel, "Jumlah", dataset, PlotOrientation.VERTICAL, false, true, false);
        customizeChartTheme(chart);
        return chart;
    }

    @SuppressWarnings("unchecked")
    private JFreeChart drawStackedBarChart(String title, String categoryLabel, ChartResponse res) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<List<SeriesValue>> yList = (List<List<SeriesValue>>) res.y();
        for (int i = 0; i < res.x().size(); i++) {
            String cat = res.x().get(i);
            List<SeriesValue> series = yList.get(i);
            for (SeriesValue sv : series) {
                dataset.addValue(sv.value(), sv.label(), cat);
            }
        }
        JFreeChart chart = ChartFactory.createStackedBarChart(title, categoryLabel, "Jumlah", dataset, PlotOrientation.VERTICAL, true, true, false);
        customizeChartTheme(chart);
        return chart;
    }

    @SuppressWarnings("unchecked")
    private JFreeChart drawLineChart(String title, String categoryLabel, ChartResponse res) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<List<SeriesValue>> yList = (List<List<SeriesValue>>) res.y();
        for (int i = 0; i < res.x().size(); i++) {
            String cat = res.x().get(i);
            List<SeriesValue> series = yList.get(i);
            for (SeriesValue sv : series) {
                dataset.addValue(sv.value(), sv.label(), cat);
            }
        }
        JFreeChart chart = ChartFactory.createLineChart(title, categoryLabel, "Jumlah", dataset, PlotOrientation.VERTICAL, true, true, false);
        customizeChartTheme(chart);
        return chart;
    }

    private void customizeChartTheme(JFreeChart chart) {
        chart.setBackgroundPaint(Color.WHITE);
        if (chart.getPlot() != null) {
            chart.getPlot().setBackgroundPaint(new Color(248, 250, 252));
        }
    }

    private void addTables(Document doc, FilterRequest req) throws DocumentException {
        // Wilayah Kerja
        addSingleTable(doc, "Tabel Wilayah Kerja", tableService.getWilayahKerjaTable(req));
        
        // Instansi
        addSingleTable(doc, "Tabel Instansi", tableService.getInstansiTable(req));

        doc.newPage();

        // Nama Jabatan
        addSingleTable(doc, "Tabel Nama Jabatan", tableService.getJabatanTable(req));

        // Pendidikan
        addSingleTable(doc, "Tabel Pendidikan", tableService.getPendidikanTable(req));
    }

    private void addSingleTable(Document doc, String title, List<List<Object>> tableData) throws DocumentException {
        if (tableData == null || tableData.size() <= 1) return;

        Font tableTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(51, 65, 85));
        Paragraph pTitle = new Paragraph(title, tableTitleFont);
        pTitle.setSpacingBefore(10);
        pTitle.setSpacingAfter(6);
        doc.add(pTitle);

        List<Object> headers = tableData.get(0);
        PdfPTable table = new PdfPTable(headers.size());
        table.setWidthPercentage(100);
        table.setSpacingAfter(15);

        // Header style
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Color headerBg = new Color(30, 41, 59);

        for (Object h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(h), headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setPadding(6);
            cell.setBorderColor(new Color(71, 85, 105));
            table.addCell(cell);
        }

        // Body style
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(51, 65, 85));
        Color alternateBg = new Color(248, 250, 252);

        for (int i = 1; i < tableData.size(); i++) {
            List<Object> row = tableData.get(i);
            boolean isAlt = (i % 2 == 0);
            for (Object cellVal : row) {
                PdfPCell cell = new PdfPCell(new Phrase(String.valueOf(cellVal), bodyFont));
                if (isAlt) {
                    cell.setBackgroundColor(alternateBg);
                }
                cell.setPadding(5);
                cell.setBorderColor(new Color(226, 232, 240));
                table.addCell(cell);
            }
        }

        doc.add(table);
    }

    // --- Helper to resolve IDs to names ---

    private String resolveFilterNames(String table, String idCol, String nameCol, List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return "Semua";
        }
        String sql = "SELECT " + nameCol + " FROM " + table + " WHERE " + idCol + " IN (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        List<String> names = jdbc.queryForList(sql, params, String.class);
        return names.isEmpty() ? "Semua" : String.join(", ", names);
    }

    private String resolveJenjang(FilterRequest req) {
        List<String> names = new ArrayList<>();
        if (req.jenjang() != null) names.addAll(req.jenjang());
        if (req.jenjangId() != null) {
            for (Integer id : req.jenjangId()) {
                String name = JENJANG_ID_TO_NAME.get(id);
                if (name != null) names.add(name);
            }
        }
        return names.isEmpty() ? "Semua" : String.join(", ", names);
    }

    private String resolveKategori(FilterRequest req) {
        List<String> names = new ArrayList<>();
        if (req.kategoriInstansi() != null) names.addAll(req.kategoriInstansi());
        if (req.kategoriInstansiId() != null) {
            for (Integer id : req.kategoriInstansiId()) {
                String name = KATEGORI_ID_TO_NAME.get(id);
                if (name != null) names.add(name);
            }
        }
        return names.isEmpty() ? "Semua" : String.join(", ", names);
    }

    private String resolveJenisInstansi(FilterRequest req) {
        List<String> names = new ArrayList<>();
        if (req.jenisInstansi() != null) names.addAll(req.jenisInstansi());
        if (req.jenisInstansiId() != null) {
            for (Integer id : req.jenisInstansiId()) {
                if (id == Math.abs("Instansi Pusat".hashCode()) + 100 || id == Math.abs("Pusat".hashCode()) + 100) {
                    names.add("Instansi Pusat");
                    names.add("Pusat");
                } else if (id == Math.abs("Instansi Daerah".hashCode()) + 100) {
                    names.add("Instansi Daerah");
                    names.add("Daerah");
                } else if (id == Math.abs("Daerah Provinsi".hashCode()) + 100) {
                    names.add("Daerah Provinsi");
                } else if (id == Math.abs("Daerah Kabupaten".hashCode()) + 100) {
                    names.add("Daerah Kabupaten");
                } else if (id == Math.abs("Daerah Kota".hashCode()) + 100) {
                    names.add("Daerah Kota");
                }
            }
        }
        return names.isEmpty() ? "Semua" : String.join(", ", names);
    }
}
