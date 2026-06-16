package com.example.portaled_lite.utilitarios;

import com.example.portaled_lite.modelo.Usuario;

public class GerenciadorSessao {
    private static GerenciadorSessao instance;
    private Usuario usuarioLogado;

    private GerenciadorSessao() {}

    public static synchronized GerenciadorSessao getInstance() {
        if (instance == null) {
            instance = new GerenciadorSessao();
        }
        return instance;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public boolean hasSessaoAtiva() {
        return usuarioLogado != null;
    }

    public void encerrarSessao() {
        usuarioLogado = null;
    }
}
