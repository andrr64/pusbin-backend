package com.bsi.pusbin.modules.stats;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.modules.stats.schema.StatsResponse;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/stats")
@RequiredArgsConstructor
public class PublicStatsController {

    private final StatsService service;

    @GetMapping("/summary")
    public ResponseEntity<APIResponse<StatsResponse>> getSummary(FilterRequest request) {
        StatsResponse response = service.getSummary(request);
        return ResponseEntity.ok(APIResponse.ok(response));
    }

    @GetMapping("/updated-at")
    public ResponseEntity<APIResponse<String>> getLastUpdatedAt() {
        String response = service.getLastUpdatedAt();
        return ResponseEntity.ok(APIResponse.ok(response));
    }
}
