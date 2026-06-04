package com.bsi.pusbin.modules.export;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/dashboard")
    public ResponseEntity<byte[]> exportDashboard(@RequestBody FilterRequest request) {
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
