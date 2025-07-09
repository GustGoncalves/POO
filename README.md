# Sistema de Gerenciamento de Eventos Acadêmicos (SGEA) + Interface Gráfica

Este projeto é uma aplicação Java com interface gráfica voltada para a organização de eventos acadêmicos como semanas de curso, congressos e feiras científicas. O sistema oferece controle completo de eventos, atividades, inscrições e pagamentos, com foco em usabilidade, segurança e extensibilidade.

## Funcionalidades

### Administrador
- Autenticação com usuário e senha
- Criação, edição e exclusão de eventos acadêmicos
- Cadastro de atividades (palestras, simpósios, minicursos etc.)
- Confirmação manual de pagamentos
- Acesso ao painel administrativo com:
  - Lista de eventos cadastrados
  - Participantes inscritos por evento/atividade
  - Status de pagamento de cada participante
  - Configuração de valores de inscrição por tipo de participante

### Participante
- Registro no sistema como:
  - Aluno
  - Professor
  - Profissional
- Inscrição em eventos e atividades específicas
- Pagamento conforme o tipo de perfil
- Acesso à área pessoal com:
  - Histórico de inscrições
  - Status de pagamento
  - Cancelamento de inscrições pendentes

## Estrutura de Eventos e Atividades
- Um evento pode conter várias atividades vinculadas
- Cada atividade possui:
  - Nome, descrição, data, tipo e limite de inscritos
- Inscrição em eventos e em atividades separadamente
- Controle de vagas com validação automática de lotação

## Pagamento
- Participantes informam o pagamento via sistema
- Administradores confirmam o pagamento manualmente
- Valores são configuráveis de acordo com o perfil do participante

## Tecnologias Utilizadas
- **Linguagem**: Java 21
- **Interface Gráfica**: Java Swing
- **Banco de Dados**: SQLite (persistência local)
- **Paradigma**: Programação Orientada a Objetos (POO)
- **Padrões e Técnicas**:
  - Herança, composição, agregação, interfaces, polimorfismo
  - Tratamento de exceções com `try-catch`
  - Exceções personalizadas para regras de negócio (ex: atividade lotada, inscrição duplicada)

## Funcionalidades Técnicas
- CRUD completo para:
  - Usuários (administradores e participantes)
  - Eventos
  - Atividades
  - Inscrições
  - Pagamentos
- Controle de acesso com tela de login
- Validação de dados e mensagens de erro amigáveis
- Interface intuitiva com separação clara entre áreas de administrador e participante

## Banco de Dados
- Desenvolvido em **SQLite**
- Tabelas: `usuarios`, `eventos`, `atividades`, `inscricoes`, `pagamentos`
- Relacionamentos bem definidos e normalizados

## Diagramas
- Diagrama de Casos de Uso
- Diagrama de Classes
- Modelo Relacional do Banco de Dados

## Demonstração
Fluxo completo de funcionamento:
1. Login como administrador ou participante
2. Cadastro de eventos e atividades
3. Inscrição de participante
4. Simulação de pagamento e confirmação manual
5. Visualização de dados no painel

## Estrutura do Projeo

sgea/
├── lib/
│   ├── sqlite-jdbc-3.50.1.0.jar
├── out/
├── src/
│   ├── dao/
│   │   ├── AdministradorDAO.java
│   │   ├── AtividadeDAO.java
│   │   ├── EventoDAO.java
│   │   ├── InscricaoDAO.java
│   │   ├── Pagamento.java
│   │   ├── Participante.java
│   │   ├── ValorInscricao.java
│   ├── exceptions/
│   │   ├── AtividadeLotadaException.java
│   │   ├── EntidadeNaoEncontradaException.java
│   │   ├── InscricaoDuplicadaException.java
│   │   ├── PagamentoInvalidoException.java
│   ├── model/
│   │   ├── Administrador.java
│   │   ├── Aluno.java
│   │   ├── Atividade.java
│   │   ├── Database.java
│   │   ├── Evento.java
│   │   ├── Inscricao.java
│   │   ├── Pagamento.java
│   │   ├── Participante.java
│   │   ├── Professor.java
│   │   └── Profissional.java
│   ├── Main.java
│   └── MainGUI.java
└── eventos.db

## Como Executar
1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/sistema-eventos.git

2. Abra o projeto em sua IDE Java (Ex: IntelliJ, Eclipse ou VS Code)

3. Certifique-se de ter o driver JDBC do SQLite

4. Execute a classe Main.java ou MainGUI.java

5. O banco de dados eventos.db será criado automaticamente (ou populado, se já existir)

## Diferenciais
- Interface gráfica completa e responsiva

- Validação de regras de negócio com exceções customizadas

- Código modular e bem organizado

- Fácil manutenção e expansão

## Released under MIT License

Copyright (c) 2013 Mark Otto.

Copyright (c) 2017 Andrew Fong.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
