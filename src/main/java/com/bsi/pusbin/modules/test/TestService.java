package com.bsi.pusbin.modules.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;

    public String getHelloMessage() {
        return testRepository.getWelcomeMessage();
    }

    public String getTestServerMessage() {
        return testRepository.getTestServerMessage();
    }
}
