package com.bsi.pusbin.modules.export;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/export")
@RequiredArgsConstructor
public class PublicExportController {

    private final ExportService exportService;

    @GetMapping("/dashboard")
    public ResponseEntity<byte[]> exportDashboard(@ModelAttribute FilterRequest request) {
        byte[] pdfBytes = exportService.generateDashboardPdf(request);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "dashboard_export.pdf");
        headers.setCacheControl("no-cache, no-store, must-revalidate");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
