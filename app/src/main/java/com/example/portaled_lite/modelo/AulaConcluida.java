package com.example.portaled_lite.modelo;

public class AulaConcluida {
    private String id;
    private String usuarioId;
    private String aulaId;

    public AulaConcluida(String id, String usuarioId, String aulaId) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.aulaId = aulaId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public String getAulaId() { return aulaId; }
    public void setAulaId(String aulaId) { this.aulaId = aulaId; }
}
