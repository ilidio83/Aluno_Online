# Aluno_Online

## 1. Visão geral do projeto

Este projeto é uma **API REST desenvolvida com Spring Boot** para gerenciamento básico de duas entidades acadêmicas:

- **Aluno**
- **Professor**

A aplicação expõe operações de **cadastro, consulta, atualização e remoção** (CRUD) para ambas as entidades, adotando uma organização em camadas típica de aplicações Java com **Spring Web + Spring Data JPA**.

Pelos artefatos analisados, a aplicação foi estruturada para persistir dados em banco relacional por meio de entidades JPA e repositórios baseados em `JpaRepository`.

> **Observação importante**
>
> Este README foi elaborado a partir da análise dos arquivos compilados (`.class`) disponibilizados. Assim, a documentação abaixo descreve com fidelidade a estrutura observada no código compilado, mas pode não incluir configurações externas não enviadas, como `pom.xml`, `application.properties`, perfis, banco de dados ou classes auxiliares eventualmente existentes no projeto original.

---

## 2. Objetivo da aplicação

A API tem como finalidade disponibilizar endpoints HTTP para administrar registros de alunos e professores, permitindo:

- criar novos registros;
- listar todos os registros;
- buscar um registro específico por ID;
- atualizar um registro existente;
- remover um registro por ID.

Trata-se de uma base de API educacional simples, adequada para fins didáticos, exercícios de Spring Boot ou como ponto de partida para sistemas acadêmicos mais completos.

---

## 3. Tecnologias e componentes identificados

Com base na estrutura compilada, foram identificadas as seguintes tecnologias:

- **Java**
- **Spring Boot** (`@SpringBootApplication`)
- **Spring Web** (`@RestController`, `@RequestMapping`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`)
- **Spring Data JPA** (`JpaRepository`)
- **Jakarta Persistence / JPA** (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`)
- **Lombok** (indícios de geração automática de construtores, getters, setters, `equals`, `hashCode` e `toString`)

---

## 4. Estrutura lógica do projeto

A organização observada segue o pacote-base:

```text
br.com.alunoonline.api
```

Com a seguinte divisão:

```text
br.com.alunoonline.api
├── ApiApplication
├── controller
│   ├── AlunoController
│   └── ProfessorController
├── model
│   ├── Aluno
│   └── Professor
├── repository
│   ├── AlunoRepository
│   └── ProfessorRepository
└── service
    ├── AlunoService
    └── ProfessorService
```

Essa composição deixa claro o uso de **arquitetura em camadas**, separando responsabilidades entre exposição HTTP, regras de aplicação, acesso a dados e representação de domínio.

---

## 5. Descrição da arquitetura utilizada

### 5.1. Arquitetura em camadas

A aplicação utiliza uma arquitetura clássica em camadas, composta por:

#### a) Camada de apresentação (`controller`)
Responsável por receber requisições HTTP, mapear endpoints REST, desserializar JSON e delegar o processamento à camada de serviço.

#### b) Camada de serviço (`service`)
Responsável por centralizar a lógica de aplicação. Nesta implementação, a camada de serviço atua principalmente como intermediária entre controller e repository.

#### c) Camada de persistência (`repository`)
Responsável pelo acesso ao banco de dados. Os repositórios herdam de `JpaRepository`, o que fornece operações prontas de CRUD.

#### d) Camada de domínio (`model`)
Representa as entidades persistidas no banco de dados, com mapeamento JPA para tabelas relacionais.

### 5.2. Fluxo da requisição

O fluxo padrão da aplicação é o seguinte:

1. O cliente faz uma requisição HTTP para um endpoint REST.
2. O **Controller** recebe a requisição.
3. O **Controller** chama o **Service** correspondente.
4. O **Service** delega ao **Repository** a operação de persistência ou consulta.
5. O **Repository** interage com o banco via JPA.
6. O resultado retorna pelo mesmo caminho até a resposta HTTP.

Representação simplificada:

```text
Cliente HTTP
   ↓
Controller
   ↓
Service
   ↓
Repository
   ↓
Banco de Dados
```

### 5.3. Vantagens dessa arquitetura

- separação clara de responsabilidades;
- facilidade de manutenção;
- maior legibilidade do código;
- melhor testabilidade;
- facilidade para evolução futura.

---

## 6. Detalhamento do código

## 6.1. Classe principal

### `ApiApplication`
Classe de bootstrap da aplicação.

**Responsabilidade:** iniciar o contexto Spring Boot.

### Comportamento identificado
Possui método `main(String[] args)` que executa:

```java
SpringApplication.run(ApiApplication.class, args)
```

### Anotação identificada
- `@SpringBootApplication`

Isso indica que a classe concentra:
- configuração automática do Spring Boot;
- varredura de componentes;
- inicialização da aplicação.

---

## 6.2. Modelos de domínio

### `Aluno`
Entidade JPA mapeada para a tabela:

```text
aluno
```

#### Campos identificados
- `Long id`
- `String nomeCompleto`
- `String cpf`
- `String email`

#### Anotações identificadas
- `@Entity`
- `@Table(name = "aluno")`
- `@Id`
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` no campo `id`

#### Métodos identificados
- construtor vazio;
- construtor com todos os campos;
- getters e setters;
- `equals`;
- `hashCode`;
- `toString`.

#### Observação
A presença de métodos típicos gerados automaticamente indica uso de **Lombok** no código-fonte original.

---

### `Professor`
Entidade JPA mapeada para a tabela:

```text
professor
```

#### Campos identificados
- `Long id`
- `String nomeCompleto`
- `String cpf`
- `String email`
- `String formacaoAcademica`

#### Anotações identificadas
- `@Entity`
- `@Table(name = "professor")`
- `@Id`
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` no campo `id`

#### Métodos identificados
- construtor vazio;
- construtor com todos os campos;
- getters e setters;
- `equals`;
- `hashCode`;
- `toString`.

#### Observação
Também há indícios de uso de **Lombok** para geração de métodos utilitários.

---

## 6.3. Repositórios

### `AlunoRepository`
Interface que estende:

```java
JpaRepository<Aluno, Long>
```

**Responsabilidade:** fornecer operações de persistência da entidade `Aluno`.

Como herda de `JpaRepository`, passa a disponibilizar automaticamente métodos como:

- `save(...)`
- `findAll()`
- `findById(...)`
- `deleteById(...)`

---

### `ProfessorRepository`
Interface que estende:

```java
JpaRepository<Professor, Long>
```

**Responsabilidade:** fornecer operações de persistência da entidade `Professor`.

Assim como o repositório de alunos, herda métodos prontos do Spring Data JPA.

---

## 6.4. Camada de serviço

### `AlunoService`
Classe anotada com:

- `@Service`

Possui injeção de dependência de `AlunoRepository` com `@Autowired`.

#### Métodos identificados

##### `criarAluno(Aluno aluno)`
Salva um novo aluno no banco.

Internamente chama:

```java
alunoRepository.save(aluno)
```

##### `listarTodosAlunos()`
Retorna a lista de todos os alunos.

Internamente chama:

```java
alunoRepository.findAll()
```

##### `buscarAlunoPorId(long id)`
Busca um aluno pelo identificador.

Internamente chama:

```java
alunoRepository.findById(id)
```

Retorno:

```java
Optional<Aluno>
```

##### `deletarAlunoPorId(long id)`
Remove um aluno pelo ID.

Internamente chama:

```java
alunoRepository.deleteById(id)
```

##### `atualizarAlunoPorId(long id, Aluno alunoEditado)`
Atualiza um aluno existente.

Fluxo identificado:
1. define o `id` no objeto recebido;
2. salva novamente a entidade.

Lógica equivalente a:

```java
alunoEditado.setId(id);
alunoRepository.save(alunoEditado);
```

#### Observações sobre a implementação
- não há validação explícita dos dados;
- não há verificação prévia de existência antes do update ou delete;
- não há tratamento de exceções visível no código compilado enviado.

---

### `ProfessorService`
Classe anotada com:

- `@Service`

Possui injeção de dependência de `ProfessorRepository` com `@Autowired`.

#### Métodos identificados

##### `criarProfessor(Professor professor)`
Salva um novo professor.

##### `listarTodosProfessores()`
Lista todos os professores.

##### `buscarProfessorPorId(long id)`
Busca professor por ID.

Retorna:

```java
Optional<Professor>
```

##### `deletarProfessorPorId(long id)`
Remove professor por ID.

##### `atualizaProfessorPorId(long id, Professor professor)`
Atualiza o professor informado.

A lógica observada é:

```java
professor.setId(id);
professorRepository.save(professor);
```

#### Observações sobre a implementação
Assim como no serviço de alunos:
- não há validações explícitas;
- não há checagem de existência do registro antes da atualização;
- não há camada de DTO visível;
- o objeto da entidade trafega diretamente entre controller e service.

---

## 6.5. Camada de controle (endpoints REST)

### `AlunoController`
Classe anotada com:

- `@RestController`
- `@RequestMapping("/alunos")`

Possui injeção de `AlunoService` com `@Autowired`.

#### Endpoints identificados

##### `POST /alunos`
Cria um novo aluno.

- Método: `criarAluno(Aluno aluno)`
- Parâmetro: `@RequestBody`
- Status: `201 CREATED`

##### `GET /alunos`
Lista todos os alunos.

- Método: `listarTodosAlunos()`
- Status: `200 OK`

##### `GET /alunos/{id}`
Busca aluno por ID.

- Método: `buscarAlunoPorId(long id)`
- Parâmetro: `@PathVariable`
- Status: `200 OK`

##### `DELETE /alunos/{id}`
Remove aluno por ID.

- Método: `deletarAlunoPorId(long id)`
- Parâmetro: `@PathVariable`
- Status: `204 NO_CONTENT`

##### `PUT /alunos/{id}`
Atualiza um aluno.

- Método: `atualizarAlunoPorId(long id, Aluno alunoAtualizado)`
- Parâmetros:
  - `@PathVariable`
  - `@RequestBody`
- Status: `204 NO_CONTENT`

---

### `ProfessorController`
Classe anotada com:

- `@RestController`
- `@RequestMapping("/professores")`

Possui injeção de `ProfessorService` com `@Autowired`.

#### Endpoints identificados

##### `POST /professores`
Cria um novo professor.

- Método: `criarProfessor(Professor professor)`
- Parâmetro: `@RequestBody`
- Status: `201 CREATED`

##### `GET /professores`
Lista todos os professores.

- Método: `listarTodosProfessores()`
- Status: `200 OK`

##### `GET /professores/{id}`
Busca professor por ID.

- Método: `buscarProfessorPorId(long id)`
- Parâmetro: `@PathVariable`
- Status: `200 OK`

##### `DELETE /professores/{id}`
Remove professor por ID.

- Método: `deletarProfessorPorId(long id)`
- Parâmetro: `@PathVariable`
- Status: `204 NO_CONTENT`

##### `PUT /professores/{id}`
Atualiza um professor.

- Método compilado identificado no controller: `atualizarProdessorPorId(...)`
- Método chamado no service: `atualizaProfessorPorId(...)`
- Parâmetros:
  - `@PathVariable`
  - `@RequestBody`
- Status: `204 NO_CONTENT`

#### Observação importante
Há um indício de pequena inconsistência nominal no código compilado:
- no controller, o nome do método aparece como **`atualizarProdessorPorId`**;
- no service, o método aparece como **`atualizaProfessorPorId`**.

Isso não impede o funcionamento, pois o mapeamento REST ocorre por anotação, mas vale a pena padronizar os nomes no código-fonte para melhorar a legibilidade.

---

## 7. Resumo dos endpoints

### Alunos

| Método | Endpoint | Descrição | Status esperado |
|---|---|---|---|
| POST | `/alunos` | Criar aluno | 201 |
| GET | `/alunos` | Listar alunos | 200 |
| GET | `/alunos/{id}` | Buscar aluno por ID | 200 |
| PUT | `/alunos/{id}` | Atualizar aluno | 204 |
| DELETE | `/alunos/{id}` | Excluir aluno | 204 |

### Professores

| Método | Endpoint | Descrição | Status esperado |
|---|---|---|---|
| POST | `/professores` | Criar professor | 201 |
| GET | `/professores` | Listar professores | 200 |
| GET | `/professores/{id}` | Buscar professor por ID | 200 |
| PUT | `/professores/{id}` | Atualizar professor | 204 |
| DELETE | `/professores/{id}` | Excluir professor | 204 |

---

## 8. Exemplos de payload JSON

### Exemplo de aluno

```json
{
  "nomeCompleto": "Maria da Silva",
  "cpf": "12345678900",
  "email": "maria@email.com"
}
```

### Exemplo de professor

```json
{
  "nomeCompleto": "João Pereira",
  "cpf": "98765432100",
  "email": "joao@email.com",
  "formacaoAcademica": "Doutorado em Matemática"
}
```

---

## 9. Características arquiteturais observadas

### Pontos positivos

- estrutura simples e organizada;
- separação entre controller, service, repository e model;
- uso de convenções do Spring Data JPA;
- endpoints REST bem definidos;
- baixo acoplamento entre camadas.

### Pontos de melhoria

A partir da análise do código compilado, algumas melhorias podem ser consideradas:

1. **Validação de entrada**
   - incluir `@Valid` e anotações como `@NotBlank`, `@Email`, `@Size`.

2. **Tratamento de exceções**
   - implementar `@ControllerAdvice` para erros padronizados.

3. **Uso de DTOs**
   - evitar expor diretamente as entidades JPA na API.

4. **Retorno mais expressivo**
   - em vez de `void`, retornar `ResponseEntity<?>` em alguns casos.

5. **Checagem de existência antes de atualizar/excluir**
   - evitar operações cegas em IDs inexistentes.

6. **Padronização de nomenclatura**
   - ajustar nomes de métodos, especialmente no fluxo de atualização de professor.

7. **Documentação automática da API**
   - integrar Swagger/OpenAPI.

8. **Testes automatizados**
   - criar testes unitários e de integração.

---

## 10. Exemplo de arquitetura conceitual

```text
┌───────────────────────┐
│     Cliente HTTP      │
│ (frontend/Postman)    │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│      Controllers      │
│ AlunoController       │
│ ProfessorController   │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│       Services        │
│ AlunoService          │
│ ProfessorService      │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│     Repositories      │
│ AlunoRepository       │
│ ProfessorRepository   │
└───────────┬───────────┘
            │
            ▼
┌───────────────────────┐
│      Banco de Dados   │
│   tabelas: aluno,     │
│          professor    │
└───────────────────────┘
```

---

## 11. Possível evolução futura do projeto

Esta base pode evoluir facilmente para um sistema acadêmico mais robusto, com inclusão de:

- cursos;
- disciplinas;
- matrículas;
- turmas;
- histórico escolar;
- autenticação e autorização com Spring Security;
- paginação e filtros;
- documentação OpenAPI;
- versionamento de API.

---

## 12. Conclusão

O projeto apresenta uma **API REST CRUD simples e bem organizada**, construída com Spring Boot e arquitetura em camadas. A implementação segue um padrão bastante utilizado no ecossistema Java para aplicações corporativas e acadêmicas, servindo tanto como exemplo didático quanto como base inicial para expansão.

A estrutura observada é adequada para pequenos sistemas e demonstra corretamente o uso integrado de:

- Spring Boot para inicialização da aplicação;
- Spring Web para exposição dos endpoints REST;
- Spring Data JPA para persistência;
- JPA/Jakarta Persistence para modelagem relacional;
- Lombok para redução de código boilerplate.

---

## 13. Arquivos analisados para esta documentação

- `ApiApplication.class`
- `AlunoController.class`
- `ProfessorController.class`
- `Aluno.class`
- `Professor.class`
- `AlunoRepository.class`
- `ProfessorRepository.class`
- `AlunoService.class`
- `ProfessorService.class`

