package com.bsi.pusbin.modules.export;

import com.bsi.pusbin.modules.tabel.schema.TabelResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock ExportRepository exportRepository;
    @InjectMocks ExportService exportService;

    private static final TabelResponse ROW = new TabelResponse(
            1001L, "PNS", "Pusat", "Laki-laki", "S1",
            "Instansi A", "Pusat", "Jabatan B", "Ahli Muda", "III/c",
            "Diklat C", "Wilker D", "Pokja I",
            LocalDate.of(2021, 4, 1), 36,
            LocalDate.of(2020, 10, 1), 42);

    @Test
    void exportToXlsx_withData_returnsParsableXlsx() throws Exception {
        when(exportRepository.findAll(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(ROW));

        byte[] result = exportService.exportToXlsx(null, null, null, null, null, null, null, null);

        assertThat(result).isNotEmpty();
        Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(result));
        Sheet sheet = wb.getSheetAt(0);
        assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(2); // header + 1 data row
        wb.close();
    }

    @Test
    void exportToXlsx_emptyData_returnsXlsxWithHeaderOnly() throws Exception {
        when(exportRepository.findAll(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        byte[] result = exportService.exportToXlsx(null, null, null, null, null, null, null, null);

        Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(result));
        Sheet sheet = wb.getSheetAt(0);
        assertThat(sheet.getPhysicalNumberOfRows()).isEqualTo(1); // header only
        wb.close();
    }

    @Test
    void exportToXlsx_headerRow_hasCorrect17Columns() throws Exception {
        when(exportRepository.findAll(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());

        byte[] result = exportService.exportToXlsx(null, null, null, null, null, null, null, null);

        Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(result));
        Row header = wb.getSheetAt(0).getRow(0);
        assertThat(header.getPhysicalNumberOfCells()).isEqualTo(17);
        assertThat(header.getCell(0).getStringCellValue()).isEqualTo("ID ASN");
        assertThat(header.getCell(16).getStringCellValue()).isEqualTo("Masa Kerja Golongan");
        wb.close();
    }

    @Test
    void exportToXlsx_nullStringFields_writesEmptyString() throws Exception {
        TabelResponse rowWithNulls = new TabelResponse(
                1002L, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null);
        when(exportRepository.findAll(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(rowWithNulls));

        byte[] result = exportService.exportToXlsx(null, null, null, null, null, null, null, null);

        Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(result));
        Row dataRow = wb.getSheetAt(0).getRow(1);
        assertThat(dataRow.getCell(1).getStringCellValue()).isEmpty(); // jenisAsn null → ""
        wb.close();
    }

    @Test
    void exportToXlsx_passesFiltersToRepository() {
        when(exportRepository.findAll(1, 2, 3, 4, 5, 6, 7, 8)).thenReturn(List.of());

        exportService.exportToXlsx(1, 2, 3, 4, 5, 6, 7, 8);

        verify(exportRepository).findAll(1, 2, 3, 4, 5, 6, 7, 8);
    }
}
