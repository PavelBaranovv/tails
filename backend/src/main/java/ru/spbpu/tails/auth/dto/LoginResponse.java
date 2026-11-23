package ru.spbpu.tails.auth.dto;

public record LoginResponse(String token, String tokenType) {
    public LoginResponse(String token) {
        this(token, "Bearer");
    }
}
