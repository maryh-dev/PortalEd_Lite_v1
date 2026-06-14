package com.example.portaled_lite.autenticacao;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.portaled_lite.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvCriarConta = findViewById(R.id.tvCriarConta);
        TextView tvEsqueciSenha = findViewById(R.id.tvEsqueciSenha);
        Button btnEntrar = findViewById(R.id.btnEntrar);

        tvCriarConta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        tvEsqueciSenha.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecuperarSenhaActivity.class);
            startActivity(intent);
        });

        btnEntrar.setOnClickListener(v -> {
            // lógica de login vem depois
        });
    }
}