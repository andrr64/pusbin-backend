package com.bsi.pusbin.modules.importdata;

import com.bsi.pusbin.modules.importdata.schema.ImportErrorRow;
import com.bsi.pusbin.modules.importdata.schema.ImportResult;
import com.bsi.pusbin.shared.exception.service.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportService {

    private final ImportRepository importRepository;
    private static final int CHUNK_SIZE = 1000;

    @Transactional
    public ImportResult importData(MultipartFile file) {
        // 1. Validate File
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File tidak boleh kosong");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new BusinessException("Nama file tidak valid");
        }

        String lowerFilename = filename.toLowerCase();
        boolean isCsv = lowerFilename.endsWith(".csv");
        boolean isXls = lowerFilename.endsWith(".xls");
        boolean isXlsx = lowerFilename.endsWith(".xlsx");

        if (!isCsv && !isXls && !isXlsx) {
            throw new BusinessException("Format file harus csv/xls/xlsx");
        }

        if (file.getSize() > 50 * 1024 * 1024) {
            throw new BusinessException("Ukuran file melebihi batas 50 MB");
        }

        long startTime = System.currentTimeMillis();
        log.info("Starting import for file: {}, size: {} bytes", filename, file.getSize());

        int totalRows = 0;
        int successRows = 0;
        int failedRows = 0;
        List<ImportErrorRow> errors = new ArrayList<>();

        // 2. Preload Master Data Caches
        Map<String, Integer> jenisAsnCache = importRepository.getJenisAsnMap();
        Map<String, Integer> kedudukanAsnCache = importRepository.getKedudukanAsnMap();
        Map<String, Integer> jenisKelaminCache = importRepository.getJenisKelaminMap();
        Map<String, Integer> wilayahPokjaCache = importRepository.getWilayahPokjaMap();
        Map<String, Integer> wilayahBknCache = importRepository.getWilayahBknMap();
        Map<String, Integer> instansiCache = new HashMap<>();
        Map<String, Integer> pendidikanCache = importRepository.getPendidikanMap();
        Map<String, Integer> nomenklaturCache = importRepository.getNomenklaturMap();
        Map<String, Integer> jenisJfCache = importRepository.getJenisJfMap();
        Map<String, Integer> jabatanCache = importRepository.getJabatanMap();
        Map<String, Integer> golonganCache = importRepository.getGolonganMap();
        Map<String, Integer> jenisDiklatCache = importRepository.getJenisDiklatMap();

        List<ImportRepository.AsnRecord> batchList = new ArrayList<>();

        try {
            if (isCsv) {
                // Auto-detect delimiter
                char delimiter = ',';
                try (InputStream tempIs = file.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(tempIs, StandardCharsets.UTF_8))) {
                    String firstLine = reader.readLine();
                    if (firstLine != null) {
                        int comma = 0, semi = 0;
                        for (char c : firstLine.toCharArray()) {
                            if (c == ',') comma++;
                            else if (c == ';') semi++;
                        }
                        delimiter = semi > comma ? ';' : ',';
                    }
                }

                try (InputStream is = file.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                     CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                             .withDelimiter(delimiter)
                             .withFirstRecordAsHeader()
                             .withIgnoreHeaderCase()
                             .withTrim())) {

                    Map<String, Integer> headerMap = new HashMap<>();
                    Map<String, Integer> csvHeaders = csvParser.getHeaderMap();
                    if (csvHeaders != null) {
                        for (Map.Entry<String, Integer> entry : csvHeaders.entrySet()) {
                            headerMap.put(entry.getKey().toLowerCase(), entry.getValue());
                        }
                    }

                    // Get header indices
                    HeaderIndices indices = resolveHeaderIndices(headerMap);

                    int rowNum = 1; // 1-based data row counter (row 1 is first data row after header)
                    for (CSVRecord record : csvParser) {
                        rowNum++;
                        totalRows++;

                        String nip = getValue(record, indices.nipIdx);
                        String nama = getValue(record, indices.namaIdx);
                        String jenisAsn = getValue(record, indices.jenisAsnIdx);
                        String kedudukanAsn = getValue(record, indices.kedudukanAsnIdx);
                        String jenisKelamin = getValue(record, indices.jenisKelaminIdx);
                        String instansiKerja = getValue(record, indices.instansiKerjaIdx);
                        String kategoriInstansi = getValue(record, indices.kategoriInstansiIdx);
                        String tingkatPendidikan = getValue(record, indices.tingkatPendidikanIdx);
                        String pendidikan = getValue(record, indices.pendidikanIdx);
                        String jabatan = getValue(record, indices.jabatanIdx);
                        String jenjang = getValue(record, indices.jenjangIdx);
                        String jenisJf = getValue(record, indices.jenisJfIdx);
                        String namaJabatan = getValue(record, indices.namaJabatanIdx);
                        String nomenklatur = getValue(record, indices.nomenklaturIdx);
                        String golongan = getValue(record, indices.golonganIdx);
                        String jenisDiklat = getValue(record, indices.jenisDiklatIdx);
                        String tmtJabatanStr = getValue(record, indices.tmtJabatanIdx);
                        String golonganRuang = getValue(record, indices.golonganRuangIdx);
                        String tmtGolruStr = getValue(record, indices.tmtGolruIdx);
                        String wilkerBkn = getValue(record, indices.wilkerBknIdx);
                        String noUrutWilkerStr = getValue(record, indices.noUrutWilkerIdx);
                        String wilayahPokja = getValue(record, indices.wilayahPokjaIdx);
                        String mkGolonganStr = getValue(record, indices.mkGolonganIdx);
                        String mkJabatanStr = getValue(record, indices.mkJabatanIdx);
                        String jenisInstansi = getValue(record, indices.jenisInstansiIdx);

                        boolean rowHasError = false;

                        // Validation
                        if (nip.isEmpty()) {
                            errors.add(new ImportErrorRow(rowNum, "nip", "NIP tidak boleh kosong"));
                            rowHasError = true;
                        }

                        LocalDate tmtJab = null;
                        if (!tmtJabatanStr.isEmpty()) {
                            tmtJab = parseDate(tmtJabatanStr);
                            if (tmtJab == null) {
                                errors.add(new ImportErrorRow(rowNum, "tmt_jabatan", "Format tanggal TMT Jabatan tidak valid (harus yyyy-MM-dd atau dd/MM/yyyy)"));
                                rowHasError = true;
                            }
                        }

                        LocalDate tmtGol = null;
                        if (!tmtGolruStr.isEmpty()) {
                            tmtGol = parseDate(tmtGolruStr);
                            if (tmtGol == null) {
                                errors.add(new ImportErrorRow(rowNum, "tmt_golru", "Format tanggal TMT Golongan tidak valid (harus yyyy-MM-dd atau dd/MM/yyyy)"));
                                rowHasError = true;
                            }
                        }

                        Integer noUrutWilker = null;
                        if (!noUrutWilkerStr.isEmpty()) {
                            try {
                                noUrutWilker = Integer.parseInt(noUrutWilkerStr);
                            } catch (NumberFormatException e) {
                                errors.add(new ImportErrorRow(rowNum, "no_urut_wilker", "No Urut Wilker harus berupa angka"));
                                rowHasError = true;
                            }
                        }

                        if (rowHasError) {
                            failedRows++;
                            continue;
                        }

                        // Process Row
                        Integer idJenisAsn = resolveJenisAsn(jenisAsn, jenisAsnCache);
                        Integer idKedudukan = resolveKedudukanAsn(kedudukanAsn, kedudukanAsnCache);
                        Integer idJenisKelamin = resolveJenisKelamin(jenisKelamin, jenisKelaminCache);
                        Integer idPokja = resolveWilayahPokja(wilayahPokja, wilayahPokjaCache);
                        Integer idWilker = resolveWilayahBkn(wilkerBkn, idPokja, noUrutWilker, wilayahBknCache);
                        Integer idInstansi = resolveInstansi(instansiKerja, kategoriInstansi, jenisInstansi, idWilker, instansiCache);
                        Integer idPendidikan = resolvePendidikan(pendidikan, tingkatPendidikan, pendidikanCache);
                        Integer idNomenklatur = resolveNomenklatur(nomenklatur, nomenklaturCache);
                        Integer idJenisJf = resolveJenisJf(jenisJf, jenisJfCache);

                        String resolvedNamaJabatan = namaJabatan.isEmpty() ? jabatan : namaJabatan;
                        Integer idJabatan = resolveJabatan(resolvedNamaJabatan, jenjang, idNomenklatur, idJenisJf, jabatanCache);

                        String resolvedGol = golonganRuang.isEmpty() ? golongan : golonganRuang;
                        Integer idGolongan = resolveGolongan(resolvedGol, golonganCache);
                        Integer idJenisDiklat = resolveJenisDiklat(jenisDiklat, jenisDiklatCache);

                        Integer mkGol = parseMasaKerja(mkGolonganStr);
                        Integer mkJab = parseMasaKerja(mkJabatanStr);

                        batchList.add(new ImportRepository.AsnRecord(
                                nip, nama, idJenisAsn, idKedudukan, idJenisKelamin,
                                idPendidikan, idInstansi, idJabatan, idGolongan, idJenisDiklat,
                                tmtJab, mkJab, tmtGol, mkGol
                        ));

                        if (batchList.size() >= CHUNK_SIZE) {
                            importRepository.batchUpsertAsn(batchList);
                            successRows += batchList.size();
                            batchList.clear();
                        }
                    }
                }
            } else {
                // Excel Parser
                try (InputStream is = file.getInputStream();
                     Workbook workbook = WorkbookFactory.create(is)) {
                    Sheet sheet = workbook.getSheetAt(0);

                    Row headerRow = sheet.getRow(0);
                    if (headerRow == null) {
                        throw new BusinessException("Header sheet tidak ditemukan");
                    }

                    Map<String, Integer> headerMap = new HashMap<>();
                    for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                        Cell cell = headerRow.getCell(i);
                        if (cell != null) {
                            String val = getCellValueAsString(cell).trim().toLowerCase();
                            if (!val.isEmpty()) {
                                headerMap.put(val, i);
                            }
                        }
                    }

                    HeaderIndices indices = resolveHeaderIndices(headerMap);

                    for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                        Row row = sheet.getRow(rowNum);
                        if (row == null || isRowBlank(row)) {
                            continue;
                        }

                        totalRows++;

                        String nip = getCellValue(row, indices.nipIdx);
                        String nama = getCellValue(row, indices.namaIdx);
                        String jenisAsn = getCellValue(row, indices.jenisAsnIdx);
                        String kedudukanAsn = getCellValue(row, indices.kedudukanAsnIdx);
                        String jenisKelamin = getCellValue(row, indices.jenisKelaminIdx);
                        String instansiKerja = getCellValue(row, indices.instansiKerjaIdx);
                        String kategoriInstansi = getCellValue(row, indices.kategoriInstansiIdx);
                        String tingkatPendidikan = getCellValue(row, indices.tingkatPendidikanIdx);
                        String pendidikan = getCellValue(row, indices.pendidikanIdx);
                        String jabatan = getCellValue(row, indices.jabatanIdx);
                        String jenjang = getCellValue(row, indices.jenjangIdx);
                        String jenisJf = getCellValue(row, indices.jenisJfIdx);
                        String namaJabatan = getCellValue(row, indices.namaJabatanIdx);
                        String nomenklatur = getCellValue(row, indices.nomenklaturIdx);
                        String golongan = getCellValue(row, indices.golonganIdx);
                        String jenisDiklat = getCellValue(row, indices.jenisDiklatIdx);
                        String tmtJabatanStr = getCellValue(row, indices.tmtJabatanIdx);
                        String golonganRuang = getCellValue(row, indices.golonganRuangIdx);
                        String tmtGolruStr = getCellValue(row, indices.tmtGolruIdx);
                        String wilkerBkn = getCellValue(row, indices.wilkerBknIdx);
                        String noUrutWilkerStr = getCellValue(row, indices.noUrutWilkerIdx);
                        String wilayahPokja = getCellValue(row, indices.wilayahPokjaIdx);
                        String mkGolonganStr = getCellValue(row, indices.mkGolonganIdx);
                        String mkJabatanStr = getCellValue(row, indices.mkJabatanIdx);
                        String jenisInstansi = getCellValue(row, indices.jenisInstansiIdx);

                        boolean rowHasError = false;

                        // Validation
                        if (nip.isEmpty()) {
                            errors.add(new ImportErrorRow(rowNum + 1, "nip", "NIP tidak boleh kosong"));
                            rowHasError = true;
                        }

                        LocalDate tmtJab = null;
                        if (!tmtJabatanStr.isEmpty()) {
                            tmtJab = parseDate(tmtJabatanStr);
                            if (tmtJab == null) {
                                errors.add(new ImportErrorRow(rowNum + 1, "tmt_jabatan", "Format tanggal TMT Jabatan tidak valid (harus yyyy-MM-dd atau dd/MM/yyyy)"));
                                rowHasError = true;
                            }
                        }

                        LocalDate tmtGol = null;
                        if (!tmtGolruStr.isEmpty()) {
                            tmtGol = parseDate(tmtGolruStr);
                            if (tmtGol == null) {
                                errors.add(new ImportErrorRow(rowNum + 1, "tmt_golru", "Format tanggal TMT Golongan tidak valid (harus yyyy-MM-dd atau dd/MM/yyyy)"));
                                rowHasError = true;
                            }
                        }

                        Integer noUrutWilker = null;
                        if (!noUrutWilkerStr.isEmpty()) {
                            try {
                                noUrutWilker = Integer.parseInt(noUrutWilkerStr);
                            } catch (NumberFormatException e) {
                                errors.add(new ImportErrorRow(rowNum + 1, "no_urut_wilker", "No Urut Wilker harus berupa angka"));
                                rowHasError = true;
                            }
                        }

                        if (rowHasError) {
                            failedRows++;
                            continue;
                        }

                        // Process Row
                        Integer idJenisAsn = resolveJenisAsn(jenisAsn, jenisAsnCache);
                        Integer idKedudukan = resolveKedudukanAsn(kedudukanAsn, kedudukanAsnCache);
                        Integer idJenisKelamin = resolveJenisKelamin(jenisKelamin, jenisKelaminCache);
                        Integer idPokja = resolveWilayahPokja(wilayahPokja, wilayahPokjaCache);
                        Integer idWilker = resolveWilayahBkn(wilkerBkn, idPokja, noUrutWilker, wilayahBknCache);
                        Integer idInstansi = resolveInstansi(instansiKerja, kategoriInstansi, jenisInstansi, idWilker, instansiCache);
                        Integer idPendidikan = resolvePendidikan(pendidikan, tingkatPendidikan, pendidikanCache);
                        Integer idNomenklatur = resolveNomenklatur(nomenklatur, nomenklaturCache);
                        Integer idJenisJf = resolveJenisJf(jenisJf, jenisJfCache);

                        String resolvedNamaJabatan = namaJabatan.isEmpty() ? jabatan : namaJabatan;
                        Integer idJabatan = resolveJabatan(resolvedNamaJabatan, jenjang, idNomenklatur, idJenisJf, jabatanCache);

                        String resolvedGol = golonganRuang.isEmpty() ? golongan : golonganRuang;
                        Integer idGolongan = resolveGolongan(resolvedGol, golonganCache);
                        Integer idJenisDiklat = resolveJenisDiklat(jenisDiklat, jenisDiklatCache);

                        Integer mkGol = parseMasaKerja(mkGolonganStr);
                        Integer mkJab = parseMasaKerja(mkJabatanStr);

                        batchList.add(new ImportRepository.AsnRecord(
                                nip, nama, idJenisAsn, idKedudukan, idJenisKelamin,
                                idPendidikan, idInstansi, idJabatan, idGolongan, idJenisDiklat,
                                tmtJab, mkJab, tmtGol, mkGol
                        ));

                        if (batchList.size() >= CHUNK_SIZE) {
                            importRepository.batchUpsertAsn(batchList);
                            successRows += batchList.size();
                            batchList.clear();
                        }
                    }
                }
            }

            // Flush remaining records
            if (!batchList.isEmpty()) {
                importRepository.batchUpsertAsn(batchList);
                successRows += batchList.size();
                batchList.clear();
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to parse/import file content", e);
            throw new BusinessException("Gagal membaca file: " + e.getMessage());
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Import completed: file: {}, duration: {} ms, totalRows: {}, successRows: {}, failedRows: {}",
                filename, duration, totalRows, successRows, failedRows);

        return ImportResult.builder()
                .totalRows(totalRows)
                .successRows(successRows)
                .failedRows(failedRows)
                .errors(errors)
                .build();
    }

    // --- Parser Helpers ---

    private static class HeaderIndices {
        int nipIdx = -1;
        int namaIdx = -1;
        int jenisAsnIdx = -1;
        int kedudukanAsnIdx = -1;
        int jenisKelaminIdx = -1;
        int instansiKerjaIdx = -1;
        int kategoriInstansiIdx = -1;
        int tingkatPendidikanIdx = -1;
        int pendidikanIdx = -1;
        int jabatanIdx = -1;
        int jenjangIdx = -1;
        int jenisJfIdx = -1;
        int namaJabatanIdx = -1;
        int nomenklaturIdx = -1;
        int golonganIdx = -1;
        int jenisDiklatIdx = -1;
        int tmtJabatanIdx = -1;
        int golonganRuangIdx = -1;
        int tmtGolruIdx = -1;
        int wilkerBknIdx = -1;
        int noUrutWilkerIdx = -1;
        int wilayahPokjaIdx = -1;
        int mkGolonganIdx = -1;
        int mkJabatanIdx = -1;
        int jenisInstansiIdx = -1;
    }

    private HeaderIndices resolveHeaderIndices(Map<String, Integer> headerMap) {
        HeaderIndices indices = new HeaderIndices();
        indices.nipIdx = findHeaderIndex(headerMap, "nip", "id_asn", "id asn", "id");
        indices.namaIdx = findHeaderIndex(headerMap, "nama", "nama_asn");
        indices.jenisAsnIdx = findHeaderIndex(headerMap, "jenis_asn", "jenis asn", "jenis");
        indices.kedudukanAsnIdx = findHeaderIndex(headerMap, "kedudukan_asn", "kedudukan asn", "kedudukan");
        indices.jenisKelaminIdx = findHeaderIndex(headerMap, "jenis_kelamin", "jenis kelamin", "kelamin", "gender");
        indices.instansiKerjaIdx = findHeaderIndex(headerMap, "instansi_kerja", "instansi kerja", "instansi", "nama_instansi", "nama instansi");
        indices.kategoriInstansiIdx = findHeaderIndex(headerMap, "kategori_instansi", "kategori instansi", "kategori");
        indices.tingkatPendidikanIdx = findHeaderIndex(headerMap, "tingkat_pendidikan", "tingkat pendidikan", "tingkat");
        indices.pendidikanIdx = findHeaderIndex(headerMap, "pendidikan", "nama_pendidikan", "nama pendidikan");
        indices.jabatanIdx = findHeaderIndex(headerMap, "jabatan", "nama_jabatan", "nama jabatan");
        indices.jenjangIdx = findHeaderIndex(headerMap, "jenjang");
        indices.jenisJfIdx = findHeaderIndex(headerMap, "jenis_jf", "jenis jf");
        indices.namaJabatanIdx = findHeaderIndex(headerMap, "nama_jabatan", "nama jabatan");
        indices.nomenklaturIdx = findHeaderIndex(headerMap, "nomenklatur", "nama_nomenklatur", "nama nomenklatur");
        indices.golonganIdx = findHeaderIndex(headerMap, "golongan", "gol", "golongan_ruang", "golongan ruang");
        indices.jenisDiklatIdx = findHeaderIndex(headerMap, "jenis_diklat", "jenis diklat");
        indices.tmtJabatanIdx = findHeaderIndex(headerMap, "tmt_jabatan", "tmt jabatan");
        indices.golonganRuangIdx = findHeaderIndex(headerMap, "golongan_ruang", "golongan ruang");
        indices.tmtGolruIdx = findHeaderIndex(headerMap, "tmt_golru", "tmt golru", "tmt_golongan", "tmt golongan");
        indices.wilkerBknIdx = findHeaderIndex(headerMap, "wilker_bkn", "wilker bkn", "wilker");
        indices.noUrutWilkerIdx = findHeaderIndex(headerMap, "no_urut_wilker", "no urut wilker", "no_urut");
        indices.wilayahPokjaIdx = findHeaderIndex(headerMap, "wilayah_pokja", "wilayah pokja", "pokja");
        indices.mkGolonganIdx = findHeaderIndex(headerMap, "masa_kerja_golongan", "masa kerja golongan", "mk_golongan", "mk golongan");
        indices.mkJabatanIdx = findHeaderIndex(headerMap, "masa_kerja_jabatan", "masa kerja jabatan", "mk_jabatan", "mk jabatan");
        indices.jenisInstansiIdx = findHeaderIndex(headerMap, "jenis_instansi", "jenis instansi");
        return indices;
    }

    private int findHeaderIndex(Map<String, Integer> headerMap, String... aliases) {
        for (String alias : aliases) {
            Integer idx = headerMap.get(alias.toLowerCase());
            if (idx != null) {
                return idx;
            }
        }
        return -1;
    }

    private String getValue(CSVRecord record, int index) {
        if (index < 0 || index >= record.size()) {
            return "";
        }
        String val = record.get(index);
        return val == null ? "" : val.trim();
    }

    private String getCellValue(Row row, int index) {
        if (index < 0 || index >= row.getLastCellNum()) {
            return "";
        }
        Cell cell = row.getCell(index);
        return getCellValueAsString(cell).trim();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double num = cell.getNumericCellValue();
                if (num == (long) num) {
                    return String.valueOf((long) num);
                } else {
                    return String.valueOf(num);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        double val = cell.getNumericCellValue();
                        if (val == (long) val) {
                            return String.valueOf((long) val);
                        }
                        return String.valueOf(val);
                    } catch (Exception ex) {
                        return "";
                    }
                }
            case BLANK:
            default:
                return "";
        }
    }

    private boolean isRowBlank(Row row) {
        if (row == null) return true;
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private LocalDate parseDate(String val) {
        if (val == null || val.trim().isEmpty()) {
            return null;
        }
        String str = val.trim();
        // Try yyyy-MM-dd
        try {
            return LocalDate.parse(str);
        } catch (Exception ignored) {}
        // Try dd/MM/yyyy
        try {
            String[] parts = str.split("/");
            if (parts.length == 3) {
                int day = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                return LocalDate.of(year, month, day);
            }
        } catch (Exception ignored) {}
        // Try dd-MM-yyyy
        try {
            String[] parts = str.split("-");
            if (parts.length == 3) {
                if (parts[0].length() == 4) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int day = Integer.parseInt(parts[2]);
                    return LocalDate.of(year, month, day);
                } else {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);
                    return LocalDate.of(year, month, day);
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Integer parseMasaKerja(Object value) {
        if (value == null) {
            return null;
        }
        String str = value.toString().trim();
        if (str.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            try {
                LocalDate date = LocalDate.parse(str);
                return java.time.Period.between(date, LocalDate.now()).getYears();
            } catch (Exception ex) {
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

    // --- Cache Resolvers ---

    private Integer resolveJenisAsn(String val, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertJenisAsn(val);
        cache.put(key, id);
        return id;
    }

    private Integer resolveKedudukanAsn(String val, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertKedudukanAsn(val);
        cache.put(key, id);
        return id;
    }

    private Integer resolveJenisKelamin(String val, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertJenisKelamin(val);
        cache.put(key, id);
        return id;
    }

    private Integer resolveWilayahPokja(String val, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertWilayahPokja(val);
        cache.put(key, id);
        return id;
    }

    private Integer resolveWilayahBkn(String val, Integer idPokja, Integer noUrut, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertWilayahBkn(val, idPokja, noUrut);
        cache.put(key, id);
        return id;
    }

    private Integer resolveInstansi(String val, String kategori, String jenisInstansi, Integer idWilker, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertInstansi(val, kategori, jenisInstansi, idWilker);
        cache.put(key, id);
        return id;
    }

    private Integer resolvePendidikan(String val, String tingkat, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertPendidikan(val, tingkat);
        cache.put(key, id);
        return id;
    }

    private Integer resolveNomenklatur(String val, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertNomenklatur(val);
        cache.put(key, id);
        return id;
    }

    private Integer resolveJenisJf(String val, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertJenisJf(val);
        cache.put(key, id);
        return id;
    }

    private Integer resolveJabatan(String val, String jenjang, Integer idNomenklatur, Integer idJenisJf, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase() + "||" + (jenjang == null ? "" : jenjang.trim().toLowerCase()) + "||" + idNomenklatur + "||" + idJenisJf;
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertJabatan(val, jenjang, idNomenklatur, idJenisJf);
        cache.put(key, id);
        return id;
    }

    private Integer resolveGolongan(String val, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertGolongan(val);
        cache.put(key, id);
        return id;
    }

    private Integer resolveJenisDiklat(String val, Map<String, Integer> cache) {
        if (val == null || val.trim().isEmpty()) return null;
        String key = val.trim().toLowerCase();
        if (cache.containsKey(key)) return cache.get(key);
        Integer id = importRepository.getOrInsertJenisDiklat(val);
        cache.put(key, id);
        return id;
    }
}
