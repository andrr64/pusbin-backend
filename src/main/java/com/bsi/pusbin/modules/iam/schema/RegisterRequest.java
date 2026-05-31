package com.bsi.pusbin.modules.iam.schema;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String nip, @NotBlank String password) {}
