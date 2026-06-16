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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PerfilConfigActivity extends BaseAlunoActivity {

    private TextView tvNomePerfil, tvEmailPerfil;
    private TextInputLayout tilNome, tilEmail;
    private TextInputEditText etNome, etEmail;
    private Button btnSalvar, btnAlterarSenha, btnSair;
    private SwitchCompat switchNotificacoes, switchModoEscuro;
    private SharedPreferences preferences;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
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
        db.collection("usuarios").document(uid).get().addOnSuccessListener(documento -> {
            if (documento.exists()) {
                String nome = documento.getString("nome");
                String email = documento.getString("email");
                tvNomePerfil.setText(nome);
                tvEmailPerfil.setText(email);
                etNome.setText(nome);
                etEmail.setText(email);
            }
        });

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

        if (nome.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dados = new HashMap<>();
        dados.put("nome", nome);
        dados.put("email", email);

        db.collection("usuarios").document(uid).update(dados).addOnSuccessListener(unused -> {
            tvNomePerfil.setText(nome);
            tvEmailPerfil.setText(email);
            Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void exibirDialogAlterarSenha() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_alterar_senha, null);
        TextInputEditText etNova = view.findViewById(R.id.etNovaSenha);
        TextInputEditText etConfirmar = view.findViewById(R.id.etConfirmarNovaSenha);

        new AlertDialog.Builder(this)
                .setTitle("Alterar Senha")
                .setView(view)
                .setPositiveButton("Salvar", (dialog, which) -> {
                    String nova = etNova.getText().toString().trim();
                    String confirmar = etConfirmar.getText().toString().trim();

                    if (nova.isEmpty() || !nova.equals(confirmar)) {
                        Toast.makeText(this, "As senhas não coincidem ou estão vazias", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseAuth.getInstance().getCurrentUser().updatePassword(nova)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Senha alterada!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erro ao alterar senha. Faça login novamente.", Toast.LENGTH_LONG).show();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void sairDaConta() {
        GerenciadorSessao.getInstance().encerrarSessao();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
