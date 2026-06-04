package com.bsi.pusbin.modules.importdata.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    private int totalRows;
    private int successRows;
    private int failedRows;
    private List<ImportErrorRow> errors;
}
