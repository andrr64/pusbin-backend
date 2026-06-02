package com.bsi.pusbin.modules.input.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest {
    private List<InputRequest> upsert;
    private List<Long> delete;
}
