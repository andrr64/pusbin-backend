package com.bsi.pusbin.modules.input.master.schema;

import lombok.Data;

@Data
public class UsersDto {
    private Integer id;
    private String nip;
    private String passwordHash;
}
