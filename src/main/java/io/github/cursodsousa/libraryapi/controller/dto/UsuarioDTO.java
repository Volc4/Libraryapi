package io.github.cursodsousa.libraryapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UsuarioDTO (
        @NotBlank
        @Size()
        String login,
        @NotBlank
        @Size()
        String senha,
        @NotNull
        List<String> roles
){}
