package com.example.webbservicelabb1.model;

import java.util.List;


public record ChatResponse(List<Choice> choices
) {
    public record Choice(Message message){}
    public record Message(String content){}
}
