package com.bsi.pusbin.modules.input.master.schema;

import lombok.Data;

@Data
public class AdminDto {
    private Integer id;
    private String nip;
    private String passwordHash;
}
