package com.example.portaled_lite.utilitarios;

import android.util.Patterns;

public class Validador {

    public static boolean validarEmail(String email) {
        return !validarCampoVazio(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean validarSenha(String senha) {
        return !validarCampoVazio(senha) && senha.length() >= 6;
    }

    public static boolean validarCampoVazio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean validarConfirmacaoSenha(String senha, String confirmacao) {
        return senha != null && senha.equals(confirmacao);
    }

    public static boolean validarCodigoCriador(String codigo) {
        return Constantes.CODIGO_CRIADOR_ADMIN.equals(codigo);
    }
}
