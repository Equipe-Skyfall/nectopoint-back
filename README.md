# Rodando o Projeto

## 📌 Instale o Maven

### Verifique se o Maven já está instalado
Abra um terminal e execute:

```sh
mvn -version
```

Se aparecer um erro como **"mvn não é reconhecido como um comando interno ou externo"**, siga os passos abaixo para instalar.

---
### Passo 1: Baixar o Maven
1. Acesse o site oficial: [Maven Download](https://maven.apache.org/download.cgi).
2. Baixe a versão **Binary zip archive**.
3. Extraia o conteúdo para uma pasta, por exemplo: `C:\apache-maven-3.x.x`.
---
### Passo 2: Configurar o PATH 
---
### Windows
1. Abra o **Explorador de Arquivos** e copie o caminho onde extraiu o Maven, por exemplo:
   ```
   C:\apache-maven-3.x.x\bin
   ```
2. No Windows, vá para:
   - **Este Computador** → **Propriedades** → **Configurações Avançadas do Sistema** → **Variáveis de Ambiente**.
   - Encontre a variável **Path**, clique em **Editar** e adicione o caminho acima.
---
### Linux/macOS
Se estiver no Linux ou macOS, edite o arquivo `~/.bashrc`, `~/.zshrc` ou `~/.bash_profile` e adicione:

```sh
export MAVEN_HOME=/caminho/para/maven
export PATH=$MAVEN_HOME/bin:$PATH
```
Depois, execute:

```sh
source ~/.bashrc
```
---
### Passo 4: Testar a instalação
Abra um novo terminal e execute:
```sh
mvn -version
```
### Passo 5: Compilar o projeto
Agora execute:
```sh
mvn clean compile
```
E para rodar:
```sh
mvn spring-boot:run
```
---
## ✅ Tudo pronto!
### Só acessar a rota **http://localhost:8080/** para começar
<br><br>
[ Rotas usuário CRUD (usar para cadastro, edição, login, etc.) ](#rotas-usuário-crud)
```sh
POST   http://localhost:8080/usuario/
DELETE http://localhost:8080/usuario/{id}
GET    http://localhost:8080/usuario/
GET    http://localhost:8080/usuario/{id}
PUT    http://localhost:8080/usuario/{id}
PUT    http://localhost:8080/usuario/{id}/senha

```

[ Rotas sessão usuário (usar para salvar informações relevantes do usuário nos cookies etc.) ](#rotas-sessão-usuário)
```sh
GET http://localhost:8080/sessao/usuario/
GET http://localhost:8080/sessao/usuario/{id}
GET http://localhost:8080/sessao/usuario/me
```

Rotas de login (em desenvolvimento, no momento usado somente para simular login)
```sh
POST   http://localhost:8080/usuario/auth
POST   http://localhost:8080/usuario/verify
```

[ Rotas dos pontos ](#rotas-dos-pontos)
```sh
POST http://localhost:8080/turno/bater-ponto/{id_colaborador}
POST http://localhost:8080/turno/bater-ponto/correcao
GET  http://localhost:8080/turno/{id}
GET  http://localhost:8080/turno/historico
```

[ Rotas dos alertas ](#rotas-dos-alertas)
```sh
GET http://localhost:8080/alertas/{id}
GET http://localhost:8080/alertas/listar
```

[ Rotas dos tickets ](#rotas-dos-tickets)
```sh
POST http://localhost:8080/tickets/postar
GET  http://localhost:8080/tickets/{id}
GET  http://localhost:8080/tickets/listar
```

[ Rotas de teste ](#rotas-de-teste)
```sh
GET http://localhost:8080/test/finalizar-dia
GET http://localhost:8080/test/sync-databases
```
<br><br>
# Utilizando as Rotas

### Rotas usuário CRUD

**POST:** Cadastre o usuário em http://localhost:8080/usuario/ e formate o json da seguinte maneira:
```sh
{
  "name": "Alice Johnson",
  "email": "alice.johnson@example.com",
  "password": "Alice#2025!",
  "cpf": "34567890123",
  "title": "COLABORADOR",
  "department": "Finance",
  "workJourneyType": "Part-time",
  "employeeNumber": "EMP1004",
  "dailyHours": 4
}
```
- **email** é único e não pode ser nulo!
- **password** deve conter pelo menos 1 letra maiúscula, 1 letra minúscula, 1 número, 1 caractere especial e ter no mínimo 8 caracteres.
- **cpf** é único e não pode ser nulo!
- **title** é um enum dos valores "GERENTE" ou "COLABORADOR"
- **employeeNumber** é único
---
**DELETE:** Delete um usuário em http://localhost:8080/usuario/{id} | Exemplo:
```sh
http://localhost:8080/usuario/1
```
---
**GET:** Puxe uma lista com todos os usuários ***do banco relacional*** em http://localhost:8080/usuario/
<details>
  <summary>Clique para ver a lista retornada</summary>

```sh
[
    {
        "id": 1,
        "name": "John Doe",
        "email": "johndoe@example.com",
        "password": "sdaj@#2ASKF223",
        "cpf": "12345678901",
        "title": "COLABORADOR",
        "department": "Engineering",
        "workJourneyType": "Full-time",
        "employeeNumber": "EMP12345",
        "bankOfHours": 2.0,
        "dailyHours": 8
    },
    {
        "id": 2,
        "name": "Alice Smith",
        "email": "alice.smith@example.com",
        "password": "sdaj@#2ASKF223",
        "cpf": "98765432100",
        "title": "COLABORADOR",
        "department": "Sales",
        "workJourneyType": "Part-time",
        "employeeNumber": "EMP1001",
        "bankOfHours": 6.5,
        "dailyHours": 6
    },
    {
        "id": 3,
        "name": "Charlie Brown",
        "email": "charlie.brown@example.com",
        "password": "sdaj@#2ASKF223",
        "cpf": "23456789012",
        "title": "COLABORADOR",
        "department": "HR",
        "workJourneyType": "Full-time",
        "employeeNumber": "EMP1003",
        "bankOfHours": 10.0,
        "dailyHours": 8
    }
]
```

</details>

❗️ **NOTA: Essa rota não é recomendada para dar display nas informações dos usuários no FRONTEND. Ela está sendo documentada aqui para o propósito de testes.**

---
**GET:** Puxe as informações de um usuário específico ***do banco relacional*** em http://localhost:8080/usuario/{id}
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "id": 1,
    "name": "John Doe",
    "email": "johndoe@example.com",
    "password": "sdaj@#2ASKF223",
    "cpf": "12345678901",
    "title": "COLABORADOR",
    "department": "Engineering",
    "workJourneyType": "Full-time",
    "employeeNumber": "EMP12345",
    "bankOfHours": 2.0,
    "dailyHours": 8
}
```

</details>

❗️ **NOTA: Essa rota não é recomendada para dar display nas informações dos usuários no FRONTEND. Ela está sendo documentada aqui para o propósito de testes.**

---
**PUT:** Edite as informações do usuário em http://localhost:8080/usuario/{id} e formate seu JSON da seguinte maneira:
```sh
{
  "name": "Charlie Brown",
  "email": "charlie.brown@example.com",
  "password": "Charlie789!2025",
  "cpf": "23456789012",
  "title": "COLABORADOR",
  "department": "HR",
  "workJourneyType": "Full-time",
  "employeeNumber": "EMP1003",
  "bankOfHours": 10.0,
  "dailyHours": 8
}
```
---
**PUT:** Edite a senha do usuário em http://localhost:8080/usuario/{id}/senha e formate seu JSON da seguinte maneira:
```sh
{

  "oldPassword": "Charlie789!2025",
  "newPassword":" newPassword@123"
}
```
---
### Rota Login/Autenticação

**Post:** Autentique um usuário em http://localhost:8080/usuario/auth




formate seu JSON da seguinte maneira para autenticar o usuário: (esse usuário gerente é padrão do sistema)
```sh
{
    "cpf": "00000000000",
    "password": "Necto-123"
}
```

**Post:** verifique o código de autenticação enviado para seu email em : http://localhost:8080/usuario/verify
 
❗️ **NOTA: Essa rota retornará um cookie(expira em 2 horas) de autenticação para o FrontEnd que será necessário para todas as requisições.** 

formate seu JSON da seguinte maneira para autenticar o usuário:
```sh
{
    "userId":"1",
    "verificationCode":"403036"
}

```
 
   <summary>  Clique para ver o JSON retornado </summary>
<details>

```sh
{
    "id_sessao": "67d75d8fc80bb31e40d611a5",
    "id_colaborador": 1,
    "dados_usuario": {
        "cargo": "COLABORADOR",
        "departamento": "Engineering",
        "status": null
    },
    "jornada_trabalho": {
        "tipo_jornada": "Full-time",
        "banco_de_horas": 2.0,
        "horas_diarias": 8,
        "jornada_atual": {
            "batida_atual": "ENTRADA",
            "ultima_entrada": "2025-03-16T23:24:41.914Z"
        }
    },
    "alertas_usuario": [
        {
            "id_aviso": "67d75dcfc80bb31e40d611ac",
            "tipo_aviso": "PONTOS_IMPAR",
            "data_aviso": "2025-03-16T23:25:03.632Z",
            "status_aviso": "PENDENDE"
        }
    ]
}
```

</details>

---
---

### Rotas sessão usuário

**GET:** Puxe uma lista paginada com todos os usuários em http://localhost:8080/sessao/usuario
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "content": [
        {
            "id_sessao": "67d75d8fc80bb31e40d611a5",
            "id_colaborador": 1,
            "dados_usuario": {
                "cargo": "COLABORADOR",
                "departamento": "Engineering",
                "status": null
            },
            "jornada_trabalho": {
                "tipo_jornada": "Full-time",
                "banco_de_horas": 2.0,
                "horas_diarias": 8,
                "jornada_atual": {
                    "batida_atual": "ENTRADA",
                    "ultima_entrada": "2025-03-16T23:24:41.914Z"
                }
            },
            "alertas_usuario": [
                {
                    "id_aviso": "67d75dcfc80bb31e40d611ac",
                    "tipo_aviso": "PONTOS_IMPAR",
                    "data_aviso": "2025-03-16T23:25:03.632Z",
                    "status_aviso": "PENDENDE"
                }
            ]
        },
        {
            "id_sessao": "67d75d92c80bb31e40d611a6",
            "id_colaborador": 2,
            "dados_usuario": {
                "cargo": "COLABORADOR",
                "departamento": "Sales",
                "status": null
            },
            "jornada_trabalho": {
                "tipo_jornada": "Part-time",
                "banco_de_horas": 6.5,
                "horas_diarias": 6,
                "jornada_atual": {
                    "batida_atual": "ENTRADA",
                    "ultima_entrada": "2025-03-16T23:24:45.121Z"
                }
            },
            "alertas_usuario": []
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 3,
    "size": 5,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "numberOfElements": 3,
    "first": true,
    "empty": false
}
```

</details>

❔ **Parâmetros:** ``` (int page), (int size) ```

---
**GET:** Puxe as informações de um usuário específico em http://localhost:8080/sessao/usuario/{id}
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "id_sessao": "67d75d8fc80bb31e40d611a5",
    "id_colaborador": 1,
    "dados_usuario": {
        "cargo": "COLABORADOR",
        "departamento": "Engineering",
        "status": null
    },
    "jornada_trabalho": {
        "tipo_jornada": "Full-time",
        "banco_de_horas": 2.0,
        "horas_diarias": 8,
        "jornada_atual": {
            "batida_atual": "ENTRADA",
            "ultima_entrada": "2025-03-16T23:24:41.914Z"
        }
    },
    "alertas_usuario": [
        {
            "id_aviso": "67d75dcfc80bb31e40d611ac",
            "tipo_aviso": "PONTOS_IMPAR",
            "data_aviso": "2025-03-16T23:25:03.632Z",
            "status_aviso": "PENDENDE"
        }
    ]
}
```

</details>

---
**GET:** Puxe as informações do usuário logado em http://localhost:8080/sessao/usuario/me
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "id_sessao": "67d75d8fc80bb31e40d611a5",
    "id_colaborador": 1,
    "dados_usuario": {
        "cargo": "COLABORADOR",
        "departamento": "Engineering",
        "status": null
    },
    "jornada_trabalho": {
        "tipo_jornada": "Full-time",
        "banco_de_horas": 2.0,
        "horas_diarias": 8,
        "jornada_atual": {
            "batida_atual": "ENTRADA",
            "ultima_entrada": "2025-03-16T23:24:41.914Z"
        }
    },
    "alertas_usuario": [
        {
            "id_aviso": "67d75dcfc80bb31e40d611ac",
            "tipo_aviso": "PONTOS_IMPAR",
            "data_aviso": "2025-03-16T23:25:03.632Z",
            "status_aviso": "PENDENDE"
        }
    ]
}
```

</details>

---
---
### Rotas dos pontos

**POST:** Marque um ponto com a rota http://localhost:8080/turno/bater-ponto/{id_colaborador}

---
**POST:** Para um gerente corrigir pontos ímpares use a rota http://localhost:8080/turno/bater-ponto/correcao e formate seu JSON da seguinte maneira:

```sh
{
    "id_colaborador": 1,
    "data_hora": "2025-03-16T23:55:03.632Z",
    "dados_ticket": {
        "id_ticket": "67d76026c80bb31e40d611b0",
        "id_aviso": "67d75dcfc80bb31e40d611ac"
    }
}
```

- **dados_ticket** é obrigatório!

---
**GET:** Puxe as informações de um ponto específico em http://localhost:8080/turno/{id}
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "id_ponto": "67d7837f103ad8099bb0ca31",
    "id_colaborador": 2,
    "tipo_ponto": "SAIDA",
    "data_hora": "2025-03-17T02:05:51.543Z",
    "horas_trabalhadas": 0
}
```

</details>

---
**GET:** Puxe o histórico de todos os turnos com filtros por parâmetros. Exemplo: http://127.0.0.1:8080/turno/historico
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "content": [
        {
            "id_ponto": "67d75db9c80bb31e40d611a8",
            "id_colaborador": 1,
            "tipo_ponto": "ENTRADA",
            "data_hora": "2025-03-16T23:24:41.914Z",
            "horas_trabalhadas": null
        },
        {
            "id_ponto": "67d75dbdc80bb31e40d611a9",
            "id_colaborador": 2,
            "tipo_ponto": "ENTRADA",
            "data_hora": "2025-03-16T23:24:45.121Z",
            "horas_trabalhadas": null
        },
        {
            "id_ponto": "67d75dbfc80bb31e40d611aa",
            "id_colaborador": 3,
            "tipo_ponto": "ENTRADA",
            "data_hora": "2025-03-16T23:24:47.330Z",
            "horas_trabalhadas": null
        },
        {
            "id_ponto": "67d75dc2c80bb31e40d611ab",
            "id_colaborador": 2,
            "tipo_ponto": "SAIDA",
            "data_hora": "2025-03-16T23:24:50.529Z",
            "horas_trabalhadas": 0
        },
        {
            "id_ponto": "67d78a01ffb1ba1013e574a8",
            "id_colaborador": 1,
            "tipo_ponto": "SAIDA",
            "data_hora": "2025-03-16T23:55:03.632Z",
            "horas_trabalhadas": 30
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 5,
    "size": 5,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 5,
    "empty": false
}
```

</details>

❔ **Parâmetros:** ```(int page), (int size), (Date startDate), (Date endDate), (str statusTurno), (int id_colaborador) ```
```sh
Enum statusTurno = TRABALHANDO, INTERVALO, ENCERRADO, NAO_COMPARECEU, IRREGULAR
```

❗️ **NOTA: Formate a data corretamente para mandar no parâmetro, mais especificamente usando ISO 8601.**
```sh
Ex: const now = new Date().toISOString();
```

---
---
### Rotas dos alertas

**GET:** Puxe um alerta específico na rota http://127.0.0.1:8080/alertas/{id}
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "id_aviso": "67d75dcfc80bb31e40d611ac",
    "id_colaborador": 1,
    "tipo_aviso": "PONTOS_IMPAR",
    "data_aviso": "2025-03-16T23:25:03.632Z",
    "status_aviso": "RESOLVIDO",
    "mensagem": null,
    "pontos_marcados": [
        {
            "id_ponto": "67d75db9c80bb31e40d611a8",
            "id_colaborador": 1,
            "tipo_ponto": "ENTRADA",
            "data_hora": "2025-03-16T23:24:41.914Z",
            "horas_trabalhadas": null
        },
        {
            "id_ponto": null,
            "id_colaborador": 1,
            "tipo_ponto": "SAIDA",
            "data_hora": "2025-03-16T23:55:03.632Z",
            "horas_trabalhadas": 30
        }
    ]
}
```

</details>

---
**GET:** Puxe todos os alertas com filtros por parâmetro na rota http://127.0.0.1:8080/alertas/listar
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "content": [
        {
            "id_aviso": "67d75dcfc80bb31e40d611ac",
            "id_colaborador": 1,
            "tipo_aviso": "PONTOS_IMPAR",
            "data_aviso": "2025-03-16T23:25:03.632Z",
            "status_aviso": "RESOLVIDO",
            "mensagem": null,
            "pontos_marcados": [
                {
                    "id_ponto": "67d75db9c80bb31e40d611a8",
                    "id_colaborador": 1,
                    "tipo_ponto": "ENTRADA",
                    "data_hora": "2025-03-16T23:24:41.914Z",
                    "horas_trabalhadas": null
                },
                {
                    "id_ponto": null,
                    "id_colaborador": 1,
                    "tipo_ponto": "SAIDA",
                    "data_hora": "2025-03-16T23:55:03.632Z",
                    "horas_trabalhadas": 30
                }
            ]
        },
        {
            "id_aviso": "67d75dcfc80bb31e40d611ad",
            "id_colaborador": 3,
            "tipo_aviso": "PONTOS_IMPAR",
            "data_aviso": "2025-03-16T23:25:03.700Z",
            "status_aviso": "PENDENDE",
            "mensagem": null,
            "pontos_marcados": [
                {
                    "id_ponto": "67d75dbfc80bb31e40d611aa",
                    "id_colaborador": 3,
                    "tipo_ponto": "ENTRADA",
                    "data_hora": "2025-03-16T23:24:47.330Z",
                    "horas_trabalhadas": null
                }
            ]
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 2,
    "size": 5,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 2,
    "empty": false
}
```

</details>

❔ **Parâmetros:** ``` (int page), (int size), (Date startDate), (Date endDate), (str statusAviso), (str tipoAviso), (int id_colaborador) ```
```sh
Enum statusAviso = PENDENTE, EM_AGUARDO, RESOLVIDO
Enum tipoAviso = PONTOS_IMPAR, SEM_ALMOCO
```

---
---
### Rotas dos tickets

**POST:** Mande um ticket usando a rota http://127.0.0.1:8080/tickets/postar e formate o JSON da seguinte forma:

```sh
{
    "id_colaborador": 3,
    "tipo_ticket": "PONTOS_IMPAR",
    "mensagem": "LET ME IN !!!!!",
    "id_aviso": "67d75dcfc80bb31e40d611ad"
}
```

- **id_colaborador** é obrigatório!
- **tipo_ticket** é um enum obrigatório! (No momento PONTOS_IMPAR é o único valor possível para o enum)
- **mensagem** é opcional.
- **id_aviso** é obrigatório se tipo_ticket for PONTOS_IMPAR, caso contrário pode ser opcional.

---
**GET:** Puxe um ticket específico em http://127.0.0.1:8080/tickets/{id}
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "id_ticket": "67d79704ffb1ba1013e574af",
    "id_colaborador": 3,
    "tipo_ticket": "PONTOS_IMPAR",
    "data_ticket": "2025-03-17T03:29:08.685Z",
    "status_ticket": "EM_AGUARDO",
    "id_aviso": "67d75dcfc80bb31e40d611ad",
    "mensagem": "LET ME IN !!!!!"
}
```

</details>

---
**GET:** Puxe todos os tickets paginados e com filtro de data em http://127.0.0.1:8080/tickets/listar
<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "content": [
        {
            "id_ticket": "67d76026c80bb31e40d611b0",
            "id_colaborador": 1,
            "tipo_ticket": "PONTOS_IMPAR",
            "data_ticket": "2025-03-16T23:35:02.939Z",
            "status_ticket": "RESOLVIDO",
            "id_aviso": "67d75dcfc80bb31e40d611ac",
            "mensagem": null
        },
        {
            "id_ticket": "67d79704ffb1ba1013e574af",
            "id_colaborador": 3,
            "tipo_ticket": "PONTOS_IMPAR",
            "data_ticket": "2025-03-17T03:29:08.685Z",
            "status_ticket": "EM_AGUARDO",
            "id_aviso": "67d75dcfc80bb31e40d611ad",
            "mensagem": "LET ME IN !!!!!"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 5,
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 2,
    "size": 5,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 2,
    "empty": false
}
```

</details>

❔ **Parâmetros:** ``` (int page), (int size), (Date startDate), (Date endDate), (str statusTicket), (str tipoTicket), (int id_colaborador) ```
```sh
Enum statusTicket = PENDENTE, EM_AGUARDO, RESOLVIDO
Enum tipoTicket = PONTOS_IMPAR, SEM_ALMOCO
```

---
---

## 📅 Sistema de Feriados

O Sistema de Feriados permite gerenciar calendários de trabalho, estabelecendo períodos de folga globais (para todos os usuários) ou específicos (para usuários selecionados).

### Rotas de Feriados

```sh
POST   http://localhost:8080/feriados/              # Cria um novo feriado
PUT    http://localhost:8080/feriados/{id}          # Atualiza um feriado existente
DELETE http://localhost:8080/feriados/{id}          # Remove um feriado
GET    http://localhost:8080/feriados/{id}          # Busca um feriado pelo ID
GET    http://localhost:8080/feriados/              # Lista todos os feriados
GET    http://localhost:8080/feriados/listar        # Lista feriados com paginação e filtros
GET    http://localhost:8080/feriados/verificar     # Verifica se hoje é feriado
GET    http://localhost:8080/feriados/verificar-data # Verifica se uma data é feriado
GET    http://localhost:8080/feriados/verificar-usuario # Verifica se uma data é feriado para um usuário
POST   http://localhost:8080/feriados/aplicar       # Aplica status de feriado para hoje
POST   http://localhost:8080/feriados/aplicar-data  # Aplica status de feriado para uma data específica
POST   http://localhost:8080/feriados/folga-usuario/{userId} # Aplica status de folga para um usuário específico
```

---

### Criando um Feriado

**POST:** Cadastre um feriado em http://localhost:8080/feriados/ usando o seguinte formato JSON:

```sh
{
  "name": "Natal",
  "startDate": "2025-12-25",
  "endDate": "2025-12-25",
  "description": "Comemoração de Natal",
  "repeatsYearly": true,
  "userIds": []
}
```

- **name**: Nome do feriado (obrigatório)
- **startDate**: Data de início do feriado (obrigatório)
- **endDate**: Data de término do feriado (obrigatório)
- **description**: Descrição opcional do feriado
- **repeatsYearly**: Define se o feriado se repete anualmente (mesmo dia/mês em todos os anos)
- **userIds**: Lista de IDs de usuários para os quais este feriado se aplica. Se vazio, o feriado é global (aplica-se a todos)

---

### Atualizando um Feriado

**PUT:** Atualize um feriado existente em http://localhost:8080/feriados/{id} com o mesmo formato JSON usado para criação.

---

### Listando Feriados

**GET:** Busque feriados com filtros e paginação em http://localhost:8080/feriados/listar

<details>
    <summary>Clique para ver o JSON retornado</summary>

```sh
{
    "content": [
        {
            "id": "65a4b7d7f741b53e157f3c55",
            "name": "Natal",
            "startDate": "2025-12-25",
            "endDate": "2025-12-25",
            "description": "Comemoração de Natal",
            "repeatsYearly": true,
            "userIds": []
        },
        {
            "id": "65a4b800f741b53e157f3c56",
            "name": "Dia do Trabalho",
            "startDate": "2025-05-01",
            "endDate": "2025-05-01",
            "description": "Feriado Nacional",
            "repeatsYearly": true,
            "userIds": []
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 2,
    "totalPages": 1,
    "size": 10,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "first": true,
    "numberOfElements": 2,
    "empty": false
}
```

</details>

❔ **Parâmetros:** `(int page), (int size), (string nome), (LocalDate data), (boolean repeticaoAnual)`

---

### Verificando Feriados

**GET:** Verifique se uma data é um feriado em http://localhost:8080/feriados/verificar-data?data=2025-12-25

<details>
    <summary>Clique para ver o retorno</summary>

```sh
true
```

</details>

---

**GET:** Verifique se uma data é feriado para um usuário específico em http://localhost:8080/feriados/verificar-usuario?data=2025-12-25&userId=1

<details>
    <summary>Clique para ver o retorno</summary>

```sh
true
```

</details>

---

### Aplicando Status de Feriado

**POST:** Aplique status de feriado para todos os usuários elegíveis para uma data específica em http://localhost:8080/feriados/aplicar-data?data=2025-12-25

<details>
    <summary>Clique para ver o retorno</summary>

```sh
"Status de feriado aplicado aos usuários!"
```

</details>

---

### Funcionamento Interno do Sistema de Feriados

O sistema de feriados tem as seguintes características importantes:

1. **Feriados Globais vs. Específicos**
   - Feriados globais (userIds vazio) aplicam-se a todos os usuários
   - Feriados específicos (userIds preenchido) aplicam-se apenas aos usuários listados

2. **Feriados Anuais**
   - Se repeatsYearly=true, o feriado repete-se anualmente no mesmo dia/mês
   - Útil para feriados fixos como Natal, Ano Novo, etc.

3. **Aplicação Automática**
   - À meia-noite (00:00), o sistema verifica se o dia atual é feriado e configura os usuários para FOLGA
   - Às 22:00, o sistema verifica se o próximo dia é feriado e prepara os status de usuário
   - Às 06:00, o sistema atualiza o status dos usuários, respeitando o calendário de feriados

4. **Suporte a Períodos**
   - Um feriado pode durar vários dias (startDate até endDate)
   - Útil para períodos como Carnaval, Semana Santa, recesso, etc.




---
---
