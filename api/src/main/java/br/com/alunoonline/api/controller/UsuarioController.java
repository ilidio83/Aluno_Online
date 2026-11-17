package br.com.alunoonline.api.controller;

import br.com.alunoonline.api.dtos.DadosCadastroUsuario;
import br.com.alunoonline.api.dtos.DadosDetalhamentoUsuario;
import br.com.alunoonline.api.model.Usuario;
import br.com.alunoonline.api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        // Verificar se o login já está cadastrado
        if (usuarioRepository.findByLogin(dados.login()) != null) {
            return ResponseEntity.badRequest().body("Login já cadastrado.");
        }

        // Criptografar a senha
        var senhaCriptografada = passwordEncoder.encode(dados.senha());

        // Criar o usuário com o login e senha criptografada
        var usuario = new Usuario(dados.login(), senhaCriptografada);

        // Salvar o usuário no banco de dados
        usuarioRepository.save(usuario);

        // Criar o URI para o novo usuário
        var uri = uriBuilder.path("/usuarios/{id}")
                .buildAndExpand(usuario.getId()).toUri();

        // Retornar resposta com status 201 e os dados do usuário criado
        return ResponseEntity.created(uri).body(new DadosDetalhamentoUsuario(usuario));
    }
}
