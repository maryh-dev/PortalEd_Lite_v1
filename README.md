# 📚 PortalEd_Lite

<p align="center">
  <img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black"/>
  <img src="https://img.shields.io/badge/Firestore-FF6F00?style=for-the-badge&logo=firebase&logoColor=white"/>
  <img src="https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=material-design&logoColor=white"/>
  <img src="https://img.shields.io/badge/Status-Em%20desenvolvimento-yellow?style=for-the-badge"/>
</p>

---

## 📋 Descrição

O **PortalEd_Lite** é uma plataforma educacional mobile desenvolvida para facilitar o acesso ao aprendizado online de forma simples, organizada e intuitiva. O sistema permite que alunos visualizem cursos, acompanhem seu progresso e realizem seus estudos em um ambiente digital acessível.

Esta versão **Lite v1.0** contém as funcionalidades essenciais para autenticação via Firebase, gerenciamento de cursos com Firestore e acompanhamento do aprendizado, servindo como base para futuras expansões do projeto.

---

## 🎯 Objetivo

Desenvolver uma plataforma educacional mobile que permita aos usuários:

- ✅ Realizar cadastro e login via Firebase Authentication
- ✅ Recuperar senha por email
- ✅ Visualizar cursos disponíveis
- ✅ Matricular-se em cursos
- ✅ Acompanhar o progresso de aprendizagem
- ✅ Favoritar cursos
- ✅ Assistir aulas
- ✅ Gerenciar configurações e perfil da conta

---

## 🛠️ Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| Java | Linguagem principal do app |
| XML | Layouts das telas |
| Android Studio | IDE de desenvolvimento |
| Firebase Authentication | Cadastro, login e recuperação de senha |
| Cloud Firestore | Banco de dados em nuvem (NoSQL) |
| Material Design 3 | Componentes visuais e sistema de temas |
| Poppins (Google Fonts) | Tipografia do app |
| Git / GitHub | Controle de versão |

---

## 📱 Telas do Aplicativo

### Módulo de Autenticação
| Tela | Descrição |
|---|---|
| Login | Entrada com email/senha via Firebase Auth, verificação de sessão ativa |
| Cadastro | Criação de conta para aluno ou administrador com código do criador |
| Recuperar Senha | Envio automático de link de recuperação por email via Firebase |

### Módulo do Aluno
| Tela | Descrição |
|---|---|
| Home | Painel com curso em andamento, categorias (Tecnologia e Design) e cursos recomendados |
| Cursos | Listagem completa de cursos com busca em tempo real |
| Detalhes do Curso | Informações, matrícula, favoritar e lista de aulas do curso |
| Aula | Player de vídeo com descrição, controle de conclusão e progresso |
| Perfil / Configurações | Dados pessoais, alterar senha, notificações e modo escuro |

### Módulo do Administrador
| Tela | Descrição |
|---|---|
| Dashboard | Painel com totais reais de alunos e cursos do Firestore |
| Gerenciar Cursos | Criar, editar e excluir cursos com dialogs e FAB |
| Gerenciar Usuários | Listar e remover usuários com busca em tempo real |

### Layouts de Item (RecyclerViews)
| Arquivo | Uso |
|---|---|
| `item_curso_recomendado.xml` | Cards de cursos na Home e na tela de Cursos |
| `item_curso_progresso.xml` | Card do curso em andamento com barra de progresso |
| `item_aula.xml` | Itens da lista de aulas em DetalhesCurso |
| `item_curso_admin.xml` | Itens da lista de cursos com botões editar/excluir |
| `item_usuario.xml` | Itens da lista de usuários com botão remover |

### Dialogs
| Arquivo | Uso |
|---|---|
| `dialog_curso.xml` | Dialog para criar e editar cursos (admin) |
| `dialog_alterar_senha.xml` | Dialog para alteração de senha no perfil |

---

## 🏗️ Estrutura do Projeto

```
app/src/main/java/com/example/portaled_lite/
│
├── autenticacao/
│   ├── LoginActivity.java
│   ├── CadastroActivity.java
│   └── RecuperarSenhaActivity.java
│
├── aluno/
│   ├── HomeActivity.java
│   ├── CursosActivity.java
│   ├── DetalhesCursoActivity.java
│   └── AulaActivity.java
│
├── administrador/
│   ├── DashboardActivity.java
│   ├── GerenciarCursosActivity.java
│   └── GerenciarUsuariosActivity.java
│
├── perfil/
│   └── PerfilConfigActivity.java
│
├── modelo/
│   ├── Usuario.java
│   ├── Curso.java
│   ├── Aula.java
│   ├── Matricula.java
│   ├── Favorito.java
│   └── AulaConcluida.java
│
├── adaptador/
│   ├── BaseAdapter.java
│   ├── CursoAdapter.java
│   ├── CursoProgressoAdapter.java
│   ├── AulaAdapter.java
│   ├── CursoAdminAdapter.java
│   └── UsuarioAdapter.java
│
└── utilitarios/
    ├── GerenciadorSessao.java
    ├── Validador.java
    └── Constantes.java

app/src/main/res/
│
├── layout/
│   ├── activity_login.xml
│   ├── activity_cadastro.xml
│   ├── activity_recuperar_senha.xml
│   ├── activity_home.xml
│   ├── activity_cursos.xml
│   ├── activity_detalhes_curso.xml
│   ├── activity_aula.xml
│   ├── activity_perfil.xml
│   ├── activity_dashboard.xml
│   ├── activity_gerenciar_cursos.xml
│   ├── activity_gerenciar_usuarios.xml
│   ├── dialog_curso.xml
│   ├── dialog_alterar_senha.xml
│   ├── item_curso_recomendado.xml
│   ├── item_curso_progresso.xml
│   ├── item_aula.xml
│   ├── item_curso_admin.xml
│   └── item_usuario.xml
│
├── values/
│   ├── colors.xml
│   ├── styles.xml
│   └── themes.xml
│
├── values-night/
│   └── colors.xml
│
├── font/
│   ├── poppins_regular.ttf
│   └── poppins_bold.ttf
│
├── color/
│   ├── box_stroke_color.xml
│   ├── hint_text_color.xml
│   ├── radio_button_color.xml
│   └── nav_item_color.xml
│
├── drawable/
│   ├── gradiente_topo.xml
│   ├── ic_menu.xml
│   ├── ic_search.xml
│   ├── ic_notificacoes.xml
│   ├── ic_voltar.xml
│   ├── ic_favorito.xml
│   ├── ic_laptop.xml
│   ├── ic_colors.xml
│   ├── ic_default_java.xml
│   ├── ic_perfil.xml
│   └── logo_portaled.xml
│
└── menu/
    └── bottom_nav_menu.xml
```

---

## 🔥 Estrutura do Firestore

```
usuarios (coleção)
  └── {uid} (documento)
        ├── nome: String
        ├── email: String
        └── tipo: "aluno" | "admin"

cursos (coleção)
  └── {cursoId} (documento)
        ├── titulo: String
        ├── descricao: String
        ├── categoria: String
        ├── professorNome: String
        └── aulas (subcoleção)
              └── {aulaId} (documento)
                    ├── titulo: String
                    ├── descricao: String
                    ├── videoUrl: String
                    └── ordem: Number

matriculas (coleção)
  └── {id} (documento)
        ├── usuarioId: String
        ├── cursoId: String
        └── progresso: Number (0–100)

aulasConcluidas (coleção)
  └── {id} (documento)
        ├── usuarioId: String
        ├── cursoId: String
        └── aulaId: String

favoritos (coleção)
  └── {id} (documento)
        ├── usuarioId: String
        └── cursoId: String
```

---

## 🔄 Fluxo de Navegação

```
LOGIN
├── Criar conta → CADASTRO → LOGIN
├── Esqueceu senha → RECUPERAR SENHA → LOGIN
└── Entrar
     ├── [aluno]  → HOME
     │    ├── Curso em andamento → DETALHES DO CURSO → AULA
     │    ├── Categoria → CURSOS (filtrado)
     │    ├── Curso recomendado → DETALHES DO CURSO
     │    └── ☰ Menu → PERFIL / SAIR
     └── [admin] → DASHBOARD
          ├── Gerenciar Cursos (criar, editar, excluir)
          └── Gerenciar Usuários (listar, remover)
```

---

## ⚙️ Regras de Negócio

1. O aluno deve possuir cadastro para acessar o sistema
2. O e-mail deve ser único por usuário (gerenciado pelo Firebase Auth)
3. Apenas administradores podem cadastrar, editar ou excluir cursos
4. Um aluno pode se matricular em vários cursos
5. O progresso é calculado automaticamente: (aulas concluídas ÷ total de aulas) × 100
6. Cursos favoritados ficam acessíveis na área de favoritos
7. O administrador não pode ser criado livremente — requer código do criador
8. O código do criador é definido como constante no código e não é exposto ao usuário

---

## 🎨 Design

| Item | Detalhe |
|---|---|
| Fonte | Poppins Regular e Bold |
| Cor primária | `#0D47A1` |
| Cor secundária | `#1565C0` |
| Fundo claro | `#F5F7FA` |
| Superfície claro | `#FFFFFF` |
| Fundo escuro | `#0F172A` |
| Superfície escuro | `#1E293B` |
| Tema | Material Design 3 com suporte a modo claro e escuro |
| Gradiente | Diagonal 135° de `azul_primario` para `azul_secundario` |
| Bordas | Cards com 12dp, botões com 24dp (estilo pílula) |
| Elevação | Zero elevação — diferenciação por cor de fundo |
| Botões | Texto em 16sp para acessibilidade |
| BottomNav | Fundo azul com ícones brancos/semitransparentes |

---

## 🚀 Como Executar

### Pré-requisitos
- Android Studio Hedgehog ou superior
- JDK 11 ou superior
- Dispositivo ou emulador com Android API 24+
- Conta Google para acesso ao Firebase Console

### Passos

1. Clone o repositório:
```bash
git clone https://github.com/maryh-dev/PortalEd_Lite_v1.git
```

2. Abra o projeto no Android Studio

3. Aguarde a sincronização do Gradle

4. Certifique-se de que o arquivo `google-services.json` está na pasta `app/`

5. Execute no emulador ou dispositivo físico

> ⚠️ O arquivo `google-services.json` não está incluído no repositório por segurança. Para executar o projeto, crie um projeto no [Firebase Console](https://console.firebase.google.com), ative **Authentication (Email/Senha)** e **Firestore Database**, e baixe o arquivo de configuração.

### Credenciais de demonstração

| Tipo | Email | Senha |
|---|---|---|
| Administrador | admin@portaled.com | admin123 |
| Aluno | aluno@portaled.com | aluno123 |

> As contas acima devem ser criadas no Firebase Authentication ou via cadastro no app (admin requer código do criador definido em `Constantes.java`).

---

## 📦 Dependências principais

```kotlin
// Firebase
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth")
implementation("com.google.firebase:firebase-firestore")

// Material Design 3
implementation("com.google.android.material:material:1.x.x")

// RecyclerView
implementation("androidx.recyclerview:recyclerview:1.x.x")

// ConstraintLayout
implementation("androidx.constraintlayout:constraintlayout:2.x.x")
```

---

## 📌 Versão

**PortalEd_Lite v1.0**
Versão inicial com autenticação via Firebase Auth, banco de dados Firestore, suporte a modo claro/escuro, tipografia Poppins, sistema de styles e gerenciamento completo de cursos e usuários.

---

## 👩‍💻 Desenvolvedores

Projeto desenvolvido para fins acadêmicos no curso de **Desenvolvimento de Sistemas** — **SENAI**

> Disciplinas envolvidas: Programação de Aplicativos, Banco de Dados.

---

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos e de aprendizado.
