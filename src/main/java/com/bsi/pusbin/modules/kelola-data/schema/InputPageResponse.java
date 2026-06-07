package com.bsi.pusbin.modules.input.schema;

import java.util.List;

public record InputPageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {}
