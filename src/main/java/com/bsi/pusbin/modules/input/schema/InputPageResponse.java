package com.bsi.pusbin.modules.input.schema;

import lombok.Data;
import java.util.List;

@Data
public class InputPageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
