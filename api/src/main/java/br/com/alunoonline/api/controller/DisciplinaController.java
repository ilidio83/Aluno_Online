package br.com.alunoonline.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import br.com.alunoonline.api.model.Disciplina;
import br.com.alunoonline.api.service.DisciplinaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearer-key")
@RequestMapping("/disciplinas")
public class DisciplinaController {

    @Autowired
    DisciplinaService disciplinaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void criarDisciplina(@RequestBody Disciplina disciplina){
        disciplinaService.criarDisciplina(disciplina);

    }
}
