package com.bsi.pusbin.modules.test;

import com.bsi.pusbin.shared.response.APIResponse;
import com.bsi.pusbin.shared.security.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Auth
@RestController
@RequestMapping("/api/v1/tests")
@RequiredArgsConstructor
public class TestModuleController {

    private final TestService testService;

    @GetMapping
    public ResponseEntity<APIResponse<String>> getHelloMessage() {
        return ResponseEntity.ok(APIResponse.ok(testService.getHelloMessage()));
    }

    @GetMapping("/server-status")
    public ResponseEntity<APIResponse<String>> getTestServerStatus() {
        return ResponseEntity.ok(APIResponse.ok(testService.getTestServerMessage()));
    }
}
