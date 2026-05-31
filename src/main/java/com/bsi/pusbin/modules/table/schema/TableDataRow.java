package com.bsi.pusbin.modules.table.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDataRow {
    private String label;
    private long count;
}
