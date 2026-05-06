package com.example.webbservicelabb1;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class FormRequest {

    @NotEmpty
    @NotNull
    private String content;


    public String getContent() {
        return content;
    }

    public void setContent(String message) {
        this.content = message;
    }

    @Override
    public String toString() {
        return "FormRequest{" +
                "content='" + content + '\'' +
                '}';
    }
}
