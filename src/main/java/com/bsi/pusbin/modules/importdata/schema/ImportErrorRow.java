package com.bsi.pusbin.modules.importdata.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportErrorRow {
    private int row;
    private String column;
    private String message;
}
