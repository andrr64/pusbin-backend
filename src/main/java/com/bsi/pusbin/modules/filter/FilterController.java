package com.bsi.pusbin.modules.filter;

import com.bsi.pusbin.modules.filter.schema.FilterRequest;
import com.bsi.pusbin.shared.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/filter")
@RequiredArgsConstructor
public class FilterController {

    private final FilterService filterService;

    @GetMapping
    public ResponseEntity<APIResponse<Map<String, List<List<Object>>>>> getFilters(@ModelAttribute FilterRequest request) {
        Map<String, List<List<Object>>> filters = filterService.getDynamicFilters(request);
        return ResponseEntity.ok(APIResponse.ok(filters, "Filter values retrieved successfully"));
    }
}
