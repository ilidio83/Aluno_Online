package br.com.alunoonline.api.dtos;

import br.com.alunoonline.api.model.Usuario;

public record DadosDetalhamentoUsuario(Long id, String login) {
    public DadosDetalhamentoUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getLogin());
    }
}
