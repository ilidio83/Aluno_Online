package br.com.alunoonline.api.service;

import br.com.alunoonline.api.MatriculaAlunoStatusEnum;
import br.com.alunoonline.api.model.MatriculaAluno;
import br.com.alunoonline.api.repository.MatriculaAlunoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import br.com.alunoonline.api.dtos.atualizarNotaRequestDTO;

@Service
public class MatriculaAlunoService {
    @Autowired
    MatriculaAlunoRepository matriculaAlunoRepository;

    private static final Double MEDIA_PARA_APROVAR = 7.0;

    // ═══════════════ ATUALIZAR NOTAS ═══════════════
    public void atualizarNotas(Long id,
                               atualizarNotaRequestDTO dto) {

        // 1) Buscar matrícula (mesmo padrão do trancar)
        MatriculaAluno matricula =
                matriculaAlunoRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Matricula não encontrada"));

        // 2) Só atualiza o que veio preenchido (PATCH parcial!)
        if (dto.getNota1() != null)
            matricula.setNota1(dto.getNota1());
        if (dto.getNota2() != null)
            matricula.setNota2(dto.getNota2());

        // 3) Se as 2 notas existem, calcula média e define status
        if (matricula.getNota1() != null
                && matricula.getNota2() != null) {
            Double media = (matricula.getNota1()
                    + matricula.getNota2()) / 2;
            matricula.setStatus(
                    media >= MEDIA_PARA_APROVAR
                            ? MatriculaAlunoStatusEnum.APROVADO
                            : MatriculaAlunoStatusEnum.REPROVADO);
        }

        // 4) Salvar a matrícula atualizada
        matriculaAlunoRepository.save(matricula);
    }
    public void criarMatricula(MatriculaAluno matriculaAluno){
        matriculaAluno.setStatus(MatriculaAlunoStatusEnum.MATRICULADO);
        matriculaAlunoRepository.save(matriculaAluno);
    }

    public void trancarMatricula(Long id){

        // buscar matricula antes de atualizar, se nao existe - 404
        MatriculaAluno matriculaAluno = matriculaAlunoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Mtricula nao encontrada"
        ));

        if (matriculaAluno.getStatus()
                .equals(MatriculaAlunoStatusEnum.MATRICULADO)){
                matriculaAluno.setStatus(MatriculaAlunoStatusEnum.TRANCADO);
            matriculaAlunoRepository.save(matriculaAluno);
        }else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"So e possivel tranca com o status mariculado");
        }
    }
}
