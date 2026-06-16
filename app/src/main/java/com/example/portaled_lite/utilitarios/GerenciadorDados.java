package com.example.portaled_lite.utilitarios;

import com.example.portaled_lite.modelo.Aula;
import com.example.portaled_lite.modelo.AulaConcluida;
import com.example.portaled_lite.modelo.Curso;
import com.example.portaled_lite.modelo.Favorito;
import com.example.portaled_lite.modelo.Matricula;
import com.example.portaled_lite.modelo.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GerenciadorDados {
    private static GerenciadorDados instance;

    private List<Usuario> usuarios;
    private List<Curso> cursos;
    private List<Aula> aulas;
    private List<Matricula> matriculas;
    private List<Favorito> favoritos;
    private List<AulaConcluida> aulasConcluidas;

    private GerenciadorDados() {
        usuarios = new ArrayList<>();
        cursos = new ArrayList<>();
        aulas = new ArrayList<>();
        matriculas = new ArrayList<>();
        favoritos = new ArrayList<>();
        aulasConcluidas = new ArrayList<>();
        popularDadosExemplo();
    }

    public static synchronized GerenciadorDados getInstance() {
        if (instance == null) {
            instance = new GerenciadorDados();
        }
        return instance;
    }

    private void popularDadosExemplo() {
        // Admin
        usuarios.add(new Usuario("1", "Admin PortalEd", "admin@portaled.com", "admin123", Constantes.TIPO_ADMIN));
        
        // Alunos
        usuarios.add(new Usuario("2", "Ana Aluna", "ana@email.com", "123456", Constantes.TIPO_ALUNO));
        usuarios.add(new Usuario("3", "Bruno Aluno", "bruno@email.com", "123456", Constantes.TIPO_ALUNO));

        // Cursos
        cursos.add(new Curso("c1", "Java", "Base sólida para criar sistemas eficientes e escaláveis.", "Desenvolvimento", "java"));
        cursos.add(new Curso("c2", "Figma", "Transforme ideias em interfaces bonitas e bem pensadas.", "Design", "figma"));
        cursos.add(new Curso("c3", "Front-end", "Crie interfaces rápidas, responsivas e envolventes.", "Desenvolvimento", "laptop"));
        cursos.add(new Curso("c4", "Back-end", "Construa a lógica que faz tudo funcionar por trás.", "Desenvolvimento", "backend"));
        cursos.add(new Curso("c5", "Android", "Desenvolva apps modernos e funcionais para o mundo real.", "Desenvolvimento", "android"));
        cursos.add(new Curso("c6", "Lógica de Programação", "Aprenda a pensar como um programador e resolver problemas com eficiência.", "Desenvolvimento", "laptop"));

        // Aulas para o curso c1
        aulas.add(new Aula("a1", "c1", "Introdução ao Java", "url1", 1));
        aulas.add(new Aula("a2", "c1", "Variáveis e Tipos", "url2", 2));
        aulas.add(new Aula("a3", "c1", "Estruturas de Controle", "url3", 3));

        // Aulas para outros cursos (exemplo rápido)
        for (int i = 2; i <= 4; i++) {
            String cid = "c" + i;
            aulas.add(new Aula("a" + i + "1", cid, "Aula Inicial do Curso " + i, "url", 1));
            aulas.add(new Aula("a" + i + "2", cid, "Conceitos Intermediários", "url", 2));
            aulas.add(new Aula("a" + i + "3", cid, "Projeto Prático", "url", 3));
        }

        // Matrícula de exemplo (Ana no curso Android)
        matriculas.add(new Matricula("m1", "2", "c1", 33));
        aulasConcluidas.add(new AulaConcluida("ac1", "2", "a1"));
        
        // Favorito de exemplo
        favoritos.add(new Favorito("f1", "2", "c1"));
    }

    // --- Métodos de Autenticação ---
    public Usuario autenticar(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        return null;
    }

    public boolean cadastrarUsuario(Usuario usuario) {
        if (buscarUsuarioPorEmail(usuario.getEmail()) != null) return false;
        if (usuario.getId() == null) usuario.setId(UUID.randomUUID().toString());
        usuarios.add(usuario);
        return true;
    }

    public Usuario buscarUsuarioPorEmail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    // --- Métodos de Cursos ---
    public List<Curso> listarCursos() { return new ArrayList<>(cursos); }
    
    public Curso buscarCursoPorId(String id) {
        for (Curso c : cursos) { if (c.getId().equals(id)) return c; }
        return null;
    }

    public void adicionarCurso(Curso c) {
        if (c.getId() == null) c.setId(UUID.randomUUID().toString());
        cursos.add(c);
    }

    public void editarCurso(Curso cursoEditado) {
        for (int i = 0; i < cursos.size(); i++) {
            if (cursos.get(i).getId().equals(cursoEditado.getId())) {
                cursos.set(i, cursoEditado);
                return;
            }
        }
    }

    public void excluirCurso(String id) {
        cursos.removeIf(c -> c.getId().equals(id));
        aulas.removeIf(a -> a.getCursoId().equals(id));
    }

    // --- Métodos de Aulas ---
    public List<Aula> buscarAulasPorCurso(String cursoId) {
        List<Aula> result = new ArrayList<>();
        for (Aula a : aulas) {
            if (a.getCursoId().equals(cursoId)) result.add(a);
        }
        result.sort((a1, a2) -> Integer.compare(a1.getOrdem(), a2.getOrdem()));
        return result;
    }

    public Aula buscarAulaPorId(String id) {
        for (Aula a : aulas) { if (a.getId().equals(id)) return a; }
        return null;
    }

    // --- Matrículas e Progresso ---
    public void matricular(String usuarioId, String cursoId) {
        if (verificarMatricula(usuarioId, cursoId) == null) {
            matriculas.add(new Matricula(UUID.randomUUID().toString(), usuarioId, cursoId, 0));
        }
    }

    public Matricula verificarMatricula(String usuarioId, String cursoId) {
        for (Matricula m : matriculas) {
            if (m.getUsuarioId().equals(usuarioId) && m.getCursoId().equals(cursoId)) return m;
        }
        return null;
    }

    public List<Matricula> listarMatriculasPorUsuario(String usuarioId) {
        List<Matricula> result = new ArrayList<>();
        for (Matricula m : matriculas) {
            if (m.getUsuarioId().equals(usuarioId)) result.add(m);
        }
        return result;
    }

    public void concluirAula(String usuarioId, String aulaId) {
        if (!estaAulaConcluida(usuarioId, aulaId)) {
            aulasConcluidas.add(new AulaConcluida(UUID.randomUUID().toString(), usuarioId, aulaId));
            Aula aula = buscarAulaPorId(aulaId);
            if (aula != null) atualizarProgresso(usuarioId, aula.getCursoId());
        }
    }

    public boolean estaAulaConcluida(String usuarioId, String aulaId) {
        for (AulaConcluida ac : aulasConcluidas) {
            if (ac.getUsuarioId().equals(usuarioId) && ac.getAulaId().equals(aulaId)) return true;
        }
        return false;
    }

    private void atualizarProgresso(String usuarioId, String cursoId) {
        Matricula m = verificarMatricula(usuarioId, cursoId);
        if (m == null) return;

        List<Aula> aulasDoCurso = buscarAulasPorCurso(cursoId);
        if (aulasDoCurso.isEmpty()) return;

        int concluidas = 0;
        for (Aula a : aulasDoCurso) {
            if (estaAulaConcluida(usuarioId, a.getId())) concluidas++;
        }

        int progresso = (concluidas * 100) / aulasDoCurso.size();
        m.setProgresso(progresso);
    }

    // --- Favoritos ---
    public void favoritar(String usuarioId, String cursoId) {
        if (!verificarFavorito(usuarioId, cursoId)) {
            favoritos.add(new Favorito(UUID.randomUUID().toString(), usuarioId, cursoId));
        }
    }

    public void desfavoritar(String usuarioId, String cursoId) {
        favoritos.removeIf(f -> f.getUsuarioId().equals(usuarioId) && f.getCursoId().equals(cursoId));
    }

    public boolean verificarFavorito(String usuarioId, String cursoId) {
        for (Favorito f : favoritos) {
            if (f.getUsuarioId().equals(usuarioId) && f.getCursoId().equals(cursoId)) return true;
        }
        return false;
    }

    public List<Curso> listarFavoritosPorUsuario(String usuarioId) {
        List<Curso> result = new ArrayList<>();
        for (Favorito f : favoritos) {
            if (f.getUsuarioId().equals(usuarioId)) {
                Curso c = buscarCursoPorId(f.getCursoId());
                if (c != null) result.add(c);
            }
        }
        return result;
    }

    // --- Administração e Contagens ---
    public int contarAlunos() {
        int count = 0;
        for (Usuario u : usuarios) { if (u.getTipo().equals(Constantes.TIPO_ALUNO)) count++; }
        return count;
    }

    public int contarCursos() { return cursos.size(); }

    public List<Usuario> listarUsuarios() { return new ArrayList<>(usuarios); }

    public void removerUsuario(String id) {
        usuarios.removeIf(u -> u.getId().equals(id) && !u.isAdmin());
    }
}
