package com.example.portaled_lite.perfil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.portaled_lite.R;
import com.example.portaled_lite.aluno.BaseAlunoActivity;
import com.example.portaled_lite.autenticacao.LoginActivity;
import com.example.portaled_lite.modelo.Usuario;
import com.example.portaled_lite.utilitarios.Constantes;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.GerenciadorSessao;
import com.example.portaled_lite.utilitarios.Validador;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PerfilConfigActivity extends BaseAlunoActivity {

    private TextView tvNomePerfil, tvEmailPerfil;
    private TextInputLayout tilNome, tilEmail;
    private TextInputEditText etNome, etEmail;
    private Button btnSalvar, btnAlterarSenha, btnSair;
    private SwitchCompat switchNotificacoes, switchModoEscuro;
    private SharedPreferences preferences;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        usuarioLogado = GerenciadorSessao.getInstance().getUsuarioLogado();
        if (usuarioLogado == null) {
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("PortalEdPrefs", Context.MODE_PRIVATE);

        inicializarViews();
        configurarBottomNav(findViewById(R.id.bottomNav), R.id.nav_perfil);
        preencherDados();
        configurarListeners();
    }

    private void inicializarViews() {
        tvNomePerfil = findViewById(R.id.tvNomePerfil);
        tvEmailPerfil = findViewById(R.id.tvEmailPerfil);
        tilNome = findViewById(R.id.tilNomePerfil);
        tilEmail = findViewById(R.id.tilEmailPerfil);
        etNome = findViewById(R.id.etNomePerfil);
        etEmail = findViewById(R.id.etEmailPerfil);
        btnSalvar = findViewById(R.id.btnSalvarPerfil);
        btnAlterarSenha = findViewById(R.id.btnAlterarSenha);
        btnSair = findViewById(R.id.btnSair);
        switchNotificacoes = findViewById(R.id.switchNotificacoes);
        switchModoEscuro = findViewById(R.id.switchModoEscuro);
    }

    private void preencherDados() {
        tvNomePerfil.setText(usuarioLogado.getNome());
        tvEmailPerfil.setText(usuarioLogado.getEmail());
        etNome.setText(usuarioLogado.getNome());
        etEmail.setText(usuarioLogado.getEmail());

        switchNotificacoes.setChecked(preferences.getBoolean(Constantes.PREF_NOTIFICACOES, true));
        switchModoEscuro.setChecked(preferences.getBoolean(Constantes.PREF_MODO_ESCURO, false));
    }

    private void configurarListeners() {
        btnSalvar.setOnClickListener(v -> salvarAlteracoes());
        btnAlterarSenha.setOnClickListener(v -> exibirDialogAlterarSenha());
        btnSair.setOnClickListener(v -> sairDaConta());

        switchNotificacoes.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(Constantes.PREF_NOTIFICACOES, isChecked).apply();
        });

        switchModoEscuro.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(Constantes.PREF_MODO_ESCURO, isChecked).apply();
            Toast.makeText(this, "Modo escuro será aplicado ao reiniciar", Toast.LENGTH_SHORT).show();
        });
    }

    private void salvarAlteracoes() {
        String nome = etNome.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        boolean valido = true;
        if (Validador.validarCampoVazio(nome)) {
            tilNome.setError("Nome não pode ser vazio");
            valido = false;
        }
        if (!Validador.validarEmail(email)) {
            tilEmail.setError("Email inválido");
            valido = false;
        }

        if (valido) {
            usuarioLogado.setNome(nome);
            usuarioLogado.setEmail(email);
            tvNomePerfil.setText(nome);
            tvEmailPerfil.setText(email);
            tilNome.setError(null);
            tilEmail.setError(null);
            Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        }
    }

    private void exibirDialogAlterarSenha() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_alterar_senha, null);
        TextInputLayout tilAtual = view.findViewById(R.id.tilSenhaAtual);
        TextInputLayout tilNova = view.findViewById(R.id.tilNovaSenha);
        TextInputLayout tilConfirmar = view.findViewById(R.id.tilConfirmarNovaSenha);
        TextInputEditText etAtual = view.findViewById(R.id.etSenhaAtual);
        TextInputEditText etNova = view.findViewById(R.id.etNovaSenha);
        TextInputEditText etConfirmar = view.findViewById(R.id.etConfirmarNovaSenha);

        new AlertDialog.Builder(this)
                .setTitle("Alterar Senha")
                .setView(view)
                .setPositiveButton("Alterar", (dialog, which) -> {
                    String atual = etAtual.getText().toString();
                    String nova = etNova.getText().toString();
                    String confirmar = etConfirmar.getText().toString();

                    if (!usuarioLogado.getSenha().equals(atual)) {
                        Toast.makeText(this, "Senha atual incorreta", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!Validador.validarSenha(nova)) {
                        Toast.makeText(this, "Nova senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!nova.equals(confirmar)) {
                        Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    usuarioLogado.setSenha(nova);
                    Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void sairDaConta() {
        GerenciadorSessao.getInstance().encerrarSessao();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
