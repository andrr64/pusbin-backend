package com.bsi.pusbin.modules.iam.schema;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String nip, @NotBlank String password) {}
