package com.bsi.pusbin.modules.importdata;

import com.bsi.pusbin.modules.importdata.schema.ImportResponse;
import com.bsi.pusbin.modules.importdata.schema.ImportResult;
import com.bsi.pusbin.shared.security.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Auth
@RestController
@RequestMapping("/api/v1/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ImportResponse> importData(@RequestParam("file") MultipartFile file) {
        ImportResult result = importService.importData(file);
        return ResponseEntity.ok(ImportResponse.builder()
                .success(true)
                .message("Import selesai")
                .data(result)
                .build());
    }
}
