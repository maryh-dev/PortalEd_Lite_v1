package com.example.portaled_lite.autenticacao;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.portaled_lite.R;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.Validador;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarSenhaActivity extends AppCompatActivity {

    private TextInputLayout tilEmail;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recuperar_senha);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarViews();
        configurarListeners();
    }

    private void inicializarViews() {
        tilEmail = findViewById(R.id.tilEmailRecuperacao);
        btnEnviar = findViewById(R.id.btnEnviar);
    }

    private void configurarListeners() {
        btnEnviar.setOnClickListener(v -> realizarRecuperacao());
        
        if (tilEmail.getEditText() != null) {
            tilEmail.getEditText().addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    tilEmail.setError(null);
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void realizarRecuperacao() {
        if (tilEmail.getEditText() == null) return;

        String email = tilEmail.getEditText().getText().toString().trim();

        if (!Validador.validarEmail(email)) {
            tilEmail.setError("E-mail inválido");
            return;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Email de recuperação enviado!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    tilEmail.setError("Email não encontrado");
                });
    }
}
