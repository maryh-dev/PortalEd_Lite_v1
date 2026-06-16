package com.example.portaled_lite.modelo;

public class Aula {
    private String id;
    private String cursoId;
    private String titulo;
    private String descricao;
    private String videoUrl;
    private int ordem;

    public Aula() {}

    public Aula(String id, String cursoId, String titulo, String videoUrl, int ordem) {
        this.id = id;
        this.cursoId = cursoId;
        this.titulo = titulo;
        this.videoUrl = videoUrl;
        this.ordem = ordem;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCursoId() { return cursoId; }
    public void setCursoId(String cursoId) { this.cursoId = cursoId; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public int getOrdem() { return ordem; }
    public void setOrdem(int ordem) { this.ordem = ordem; }
}
