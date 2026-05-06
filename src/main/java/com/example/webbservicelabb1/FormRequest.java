package com.example.webbservicelabb1;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class FormRequest {

    private String personality;

    @NotBlank
    private String content;


    public String getContent() {
        return content;
    }

    public void setContent(String message) {
        this.content = message;
    }

    public String getPersonality() {
        return personality;
    }

    public void setPersonality(String personality) {
        this.personality = personality;
    }

    @Override
    public String toString() {
        return "FormRequest{}";
    }
}
