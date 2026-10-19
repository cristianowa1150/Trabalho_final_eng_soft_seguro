package com.github.trabalhoengsoftseguro.artificio.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para cadastro de novas contas de usuários (Clientes ou Artistas).
 */
public record CadastroDto(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 2, max = 150, message = "O nome deve ter entre 2 e 150 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Size(max = 180, message = "O e-mail deve ter no máximo 180 caracteres")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
        String senha,

        @NotNull(message = "O papel é obrigatório")
        Role role,

        @Size(max = 2000, message = "A biografia deve ter no máximo 2000 caracteres")
        String bio,

        @Size(max = 100, message = "A chave PIX deve ter no máximo 100 caracteres")
        String chavePix
) {
}
