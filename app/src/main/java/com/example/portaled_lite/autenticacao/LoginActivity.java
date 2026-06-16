package com.example.portaled_lite.autenticacao;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.portaled_lite.R;
import com.example.portaled_lite.administrador.DashboardActivity;
import com.example.portaled_lite.aluno.HomeActivity;
import com.example.portaled_lite.modelo.Usuario;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.GerenciadorSessao;
import com.example.portaled_lite.utilitarios.Validador;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tilEmail, tilSenha;
    private Button btnEntrar;
    private TextView tvEsqueciSenha, tvCriarConta;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        // 1. Verificar sessão ativa no Firebase
        if (auth.getCurrentUser() != null) {
            navegarParaHomeAutomatico();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarViews();
        configurarListeners();
    }

    private void inicializarViews() {
        tilEmail = findViewById(R.id.tilEmail);
        tilSenha = findViewById(R.id.tilSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha);
        tvCriarConta = findViewById(R.id.tvCriarConta);
    }

    private void configurarListeners() {
        btnEntrar.setOnClickListener(v -> realizarLogin());
        
        tvCriarConta.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroActivity.class));
        });

        tvEsqueciSenha.setOnClickListener(v -> {
            startActivity(new Intent(this, RecuperarSenhaActivity.class));
        });

        // Limpar erros ao digitar
        if (tilEmail.getEditText() != null) adicionarTextWatcher(tilEmail);
        if (tilSenha.getEditText() != null) adicionarTextWatcher(tilSenha);
    }

    private void realizarLogin() {
        if (tilEmail.getEditText() == null || tilSenha.getEditText() == null) return;

        String email = tilEmail.getEditText().getText().toString().trim();
        String senha = tilSenha.getEditText().getText().toString().trim();

        boolean valido = true;

        if (!Validador.validarEmail(email)) {
            tilEmail.setError("E-mail inválido");
            valido = false;
        }

        if (Validador.validarCampoVazio(senha)) {
            tilSenha.setError("Campo obrigatório");
            valido = false;
        }

        if (!valido) return;

        btnEntrar.setEnabled(false);

        auth.signInWithEmailAndPassword(email, senha)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("usuarios")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(documento -> {
                                String tipo = documento.getString("tipo");

                                if ("admin".equals(tipo)) {
                                    startActivity(new Intent(this, DashboardActivity.class));
                                } else {
                                    startActivity(new Intent(this, HomeActivity.class));
                                }
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnEntrar.setEnabled(true);
                                Toast.makeText(this, "Erro ao buscar dados do usuário", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnEntrar.setEnabled(true);
                    Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
                });
    }

    private void navegarParaHomeAutomatico() {
        // Ao abrir o app já logado, verificamos o tipo no Firestore
        String uid = auth.getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(documento -> {
                    String tipo = documento.getString("tipo");
                    if ("admin".equals(tipo)) {
                        startActivity(new Intent(this, DashboardActivity.class));
                    } else {
                        startActivity(new Intent(this, HomeActivity.class));
                    }
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Se falhar ao buscar o tipo, por segurança desloga ou vai para login
                    auth.signOut();
                    recreate();
                });
    }

    private void navegarParaHome(Usuario usuario) {
        Intent intent;
        if (usuario.isAdmin()) {
            intent = new Intent(this, DashboardActivity.class);
        } else {
            intent = new Intent(this, HomeActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void adicionarTextWatcher(TextInputLayout til) {
        if (til.getEditText() == null) return;
        til.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                til.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}
