package com.example.portaled_lite.modelo;

public class Favorito {
    private String id;
    private String usuarioId;
    private String cursoId;

    public Favorito(String id, String usuarioId, String cursoId) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.cursoId = cursoId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getCursoId() { return cursoId; }
    public void setCursoId(String cursoId) { this.cursoId = cursoId; }
}
