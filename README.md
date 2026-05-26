# Aluno_Online

## 1. Visão geral do projeto

Este projeto é uma **API REST desenvolvida com Spring Boot** para o gerenciamento completo de um ecossistema acadêmico. A aplicação evoluiu de um escopo básico para um sistema robusto que gerencia as seguintes entidades:

- **Aluno**
- **Professor**
- **Disciplina**
- **Matrícula**

A aplicação expõe operações de **cadastro, consulta, atualização, remoção (CRUD) e regras de negócio acadêmicas**, adotando uma organização em camadas típica de aplicações corporativas Java com **Spring Web + Spring Data JPA**, além do padrão **DTO (Data Transfer Object)** para comunicação externa segura.

---

## 2. Objetivo da aplicação

A API tem como finalidade disponibilizar endpoints HTTP para administrar o ambiente escolar, permitindo:

- Gerenciar cadastros de alunos e professores;
- Criar disciplinas e vinculá-las a professores responsáveis;
- Realizar matrículas de alunos em disciplinas;
- Trancar matrículas com validação de status;
- Lançar notas com cálculo automático de média e aprovação/reprovação;
- Emitir o histórico acadêmico completo do aluno.

Trata-se de uma API educacional completa, adequada para compreender o fluxo de dados relacional e a aplicação de regras de negócio no backend.

---

## 3. Tecnologias e componentes identificados

Com base na estrutura do projeto, foram identificadas as seguintes tecnologias:

- **Java 21**
- **Spring Boot 3.x** (`@SpringBootApplication`)
- **Spring Web** (`@RestController`, `@RequestMapping`, mapeamentos HTTP)
- **Spring Data JPA** (`JpaRepository`, mapeamento ORM)
- **Jakarta Persistence / JPA** (`@Entity`, `@Table`, `@Id`, `@ManyToOne`)
- **PostgreSQL** (Banco de dados relacional definido via `application.properties`)
- **Lombok** (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor` para redução de boilerplate)

---

## 4. Estrutura lógica do projeto

A organização observada segue o pacote-base `br.com.alunoonline.api`, dividida nas seguintes responsabilidades:

## 5. Descrição da arquitetura utilizada

### 5.1. Arquitetura em camadas
* **a) Camada de apresentação (controller)**: Recebe requisições HTTP, mapeia endpoints REST, desserializa JSON (via DTOs ou Models) e delega o processamento à camada de serviço.
* **b) Camada de serviço (service)**: Centraliza a lógica de aplicação. Valida status de matrículas, calcula médias de notas e atua como intermediária entre controller e repository.
* **c) Camada de persistência (repository)**: Responsável pelo acesso ao banco de dados PostgreSQL. Herdam de `JpaRepository`, fornecendo operações prontas de CRUD e consultas personalizadas.
* **d) Camada de domínio (model e enums)**: Representa as entidades persistidas no banco, com mapeamento JPA relacional (como `@ManyToOne`).
* **e) Camada de Transporte (dtos)**: Objetos simples que transportam apenas os dados necessários entre o cliente (front-end/Insomnia) e o servidor, evitando exposição direta das entidades do banco.

### 5.2. Fluxo da requisição (Exemplo com DTO)
1. Cliente faz requisição HTTP `PATCH` enviando um JSON (`AtualizarNotasRequestDTO`).
2. **Controller** recebe a requisição e chama o **Service**.
3. **Service** processa as notas, calcula a média, altera o status (Enum) e delega ao **Repository**.
4. **Repository** atualiza a entidade `MatriculaAluno` no banco.
5. Resposta HTTP `204 NO CONTENT` retorna ao cliente.

---

## 6. Detalhamento do código

### 6.1. Classe principal
* **`ApiApplication`**: Possui a anotação `@SpringBootApplication` e o método `main` para iniciar o contexto do Spring Boot.

### 6.2. Modelos de domínio (model)
* **Aluno**: Entidade base com `id`, `nome`, `cpf` e `email`.
* **Professor**: Entidade base com `id`, `nome`, `cpf`, `email` e `formacaoAcademica`.
* **Disciplina**:
  * Campos: `id`, `nome`, `sigla`.
  * Relacionamento: `@ManyToOne` com Professor (uma disciplina pertence a um professor).
* **MatriculaAluno**:
  * A entidade central (Tabela associativa).
  * Campos: `id`, `nota1`, `nota2`.
  * Relacionamentos: `@ManyToOne` com Aluno e `@ManyToOne` com Disciplina.
  * Possui um campo `status` baseado no Enum `MatriculaAlunoStatusEnum`.

### 6.3. Enums e DTOs
* **MatriculaAlunoStatusEnum**: Define os estados possíveis: `MATRICULADO`, `APROVADO`, `REPROVADO`, `TRANCADO`.
* **AtualizarNotasRequestDTO**: Recebe apenas `nota1` e `nota2` do cliente.
* **DisciplinasAlunoResponseDTO**: Estrutura de saída para listar as matérias (nome, professor, notas, média e status).
* **HistoricoAlunoResponseDTO**: Estrutura "envelope" que junta os dados do aluno com uma lista de `DisciplinasAlunoResponseDTO`.

### 6.4. Camada de serviço (service)
* **AlunoService** e **ProfessorService**: Gerenciam os CRUDs básicos (salvar, listar, buscar por ID, excluir).
* **DisciplinaService**: Salva disciplinas vinculando-as aos professores.
* **MatriculaAlunoService** (Contém as principais regras de negócio):
  * `criarMatricula`: Associa aluno e disciplina, definindo o status inicial como `MATRICULADO`.
  * `trancarMatricula`: Busca a matrícula; se o status for `MATRICULADO`, altera para `TRANCADO`. Caso contrário, lança erro `400 BAD REQUEST`.
  * `atualizarNotas`: Recebe o DTO de notas. Se ambas as notas estiverem preenchidas, calcula a média. Se a média for `>= 7.0`, altera o status para `APROVADO`; senão, `REPROVADO`.
  * `emitirHistoricoAluno`: Busca todas as matrículas de um aluno específico pelo ID e monta o `HistoricoAlunoResponseDTO` formatado para o front-end.

### 6.5. Repositórios (repository)
Além dos CRUDs automáticos de `JpaRepository` para Aluno, Professor e Disciplina, o `MatriculaAlunoRepository` contém o método de busca personalizada:
* `findByAlunoId(Long alunoId)`: Retorna uma lista de matrículas de um aluno específico (usado para gerar o histórico).

---

## 7. Resumo dos endpoints

### Alunos e Professores
| Método | Endpoint | Descrição | Status esperado |
| :--- | :--- | :--- | :--- |
| POST | `/alunos` | Criar aluno | 201 |
| GET | `/alunos` | Listar alunos | 200 |
| DELETE | `/alunos/{id}` | Excluir aluno | 204 |
| POST | `/professores` | Criar professor | 201 |
| GET | `/professores` | Listar professores | 200 |

### Disciplinas e Matrículas
| Método | Endpoint | Descrição | Status esperado |
| :--- | :--- | :--- | :--- |
| POST | `/disciplinas` | Cadastra nova disciplina | 201 |
| POST | `/matriculas` | Matricula aluno na disciplina | 201 |
| PATCH | `/matriculas/trancar/{id}` | Tranca a matrícula | 204 |
| PATCH | `/matriculas/atualizar-notas/{id}` | Lança notas (atualiza média/status) | 204 |
| GET | `/matriculas/historico/{alunoId}` | Emite histórico completo do aluno | 200 |

---

## 8. Exemplos de payload JSON

**Cadastrar Disciplina (POST `/disciplinas`)**
```json
{
  "nome": "Backend com Java",
  "sigla": "BACK-JAVA",
  "professor": {
    "id": 1
  }
}

{
  "aluno": { "id": 1 },
  "disciplina": { "id": 1 }
}

{
  "nota1": 8.5,
  "nota2": 7.0
}

{
  "nomeAluno": "Maria da Silva",
  "cpfAluno": "12345678900",
  "emailAluno": "maria@email.com",
  "disciplinas": [
    {
      "nomeDisciplina": "Backend com Java",
      "nomeProfessor": "João Pereira",
      "nota1": 8.5,
      "nota2": 7.0,
      "media": 7.75,
      "status": "APROVADO"
    }
  ]
}


```
## 9. Características arquiteturais observadasPontos positivosSeparação clara de responsabilidades nas 4 camadas;Uso excelente do padrão DTO para emitir relatórios (Histórico) e receber parciais (Notas);Lógica de negócio isolada corretamente na camada Service (evitando "Fat Controllers");Relacionamentos JPA (@ManyToOne) bem estruturados.Pontos de melhoriaIncluir validações de entrada (@Valid, @NotBlank, @NotNull) nos DTOs e Models;Implementar um GlobalExceptionHandler (@ControllerAdvice) para padronizar os retornos de erro (ex: aluno não encontrado);Paginação (Pageable) nos endpoints de listagem GET.10. Exemplo de arquitetura conceitualPlaintext.
```
┌─────────────────────────┐
│       Cliente HTTP      │
│  (Insomnia / Frontend)  │
└────────────┬────────────┘
             │ JSON (DTOs)
             ▼
┌─────────────────────────┐
│       Controllers       │
│ Aluno, Prof, Disc, Matr │
└────────────┬────────────┘
             │ Delega fluxo
             ▼
┌─────────────────────────┐
│        Services         │
│  Regras (Cálculo Notas) │
└────────────┬────────────┘
             │ Usa Models
             ▼
┌─────────────────────────┐
│      Repositories       │
│      JpaRepository      │
└────────────┬────────────┘
             │ SQL (JPA)
             ▼
┌─────────────────────────┐
│     Banco de Dados      │
│  PostgreSQL (4 Tabelas) │
└─────────────────────────┘
```
## 10. Arquivos analisados para esta documentação

* ApiApplication.java

* Pacote controller: Aluno, Professor, Disciplina, MatriculaAluno

* Pacote service: Aluno, Professor, Disciplina, MatriculaAluno

* Pacote repository: Aluno, Professor, Disciplina, MatriculaAluno

* Pacote model: Aluno, Professor, Disciplina, MatriculaAluno

* Pacote dtos: DTOs de Request e Response

* Pacote enums: MatriculaAlunoStatusEnum

## 11. Prints das requisições no Insomnia e tabelas no DBeaver

<img width="1285" height="390" alt="Captura de tela 2026-05-26 120448" src="https://github.com/user-attachments/assets/26def8b1-6306-4450-b79d-44d1b80a64b3" />

---

<img width="1296" height="595" alt="Captura de tela 2026-05-26 120513" src="https://github.com/user-attachments/assets/dad5a715-525c-4d0f-8793-9dfc33ac04cf" />


