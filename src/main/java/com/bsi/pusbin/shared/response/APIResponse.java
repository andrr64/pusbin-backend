package com.bsi.pusbin.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class APIResponse<T> {
    private boolean success;
    private String message;
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

    public static <T> APIResponse<T> ok(T data, String message) {
        return APIResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> APIResponse<T> ok(T data) {
        return ok(data, "Request processed successfully");
    }

    public static <T> APIResponse<T> error(String message) {
        return APIResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    public static <T> APIResponse<T> error(String message, List<String> errors) {
        return APIResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
}
