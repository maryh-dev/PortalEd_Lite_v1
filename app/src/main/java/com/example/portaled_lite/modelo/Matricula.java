package com.example.portaled_lite.modelo;

public class Matricula {
    private String id;
    private String usuarioId;
    private String cursoId;
    private int progresso;

    public Matricula(String id, String usuarioId, String cursoId, int progresso) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
        this.progresso = progresso;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getCursoId() { return cursoId; }
    public void setCursoId(String cursoId) { this.cursoId = cursoId; }

    public int getProgresso() { return progresso; }
    public void setProgresso(int progresso) { this.progresso = progresso; }
}
