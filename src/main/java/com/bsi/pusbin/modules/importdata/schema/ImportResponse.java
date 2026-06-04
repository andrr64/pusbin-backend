package com.bsi.pusbin.modules.importdata.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResponse {
    private boolean success;
    private String message;
    private ImportResult data;
}
