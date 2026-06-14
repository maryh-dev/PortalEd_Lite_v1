package com.example.portaled_lite.autenticacao;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import com.example.portaled_lite.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CadastroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        TextInputEditText etNome = findViewById(R.id.etNome);
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etSenha = findViewById(R.id.etSenha);
        TextInputEditText etConfirmarSenha = findViewById(R.id.etConfirmarSenha);
        TextInputEditText etCodigoCriador = findViewById(R.id.etCodigoCriador);
        TextInputLayout tilCodigoCriador = findViewById(R.id.tilCodigoCriador);

        RadioGroup rgTipoConta = findViewById(R.id.rgTipoConta);
        RadioButton rbAdmin = findViewById(R.id.rbAdmin);

        Button btnCadastrar = findViewById(R.id.btnCadastrar);

        // Mostra/esconde o campo Código do Criador
        rgTipoConta.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAdmin) {
                tilCodigoCriador.setVisibility(View.VISIBLE);
            } else {
                tilCodigoCriador.setVisibility(View.GONE);
            }
        });

        // Clique no botão Cadastrar
        btnCadastrar.setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String senha = etSenha.getText().toString().trim();
            String confirmarSenha = etConfirmarSenha.getText().toString().trim();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                // depois: mostrar mensagem de erro (Toast)
                return;
            }

            if (!senha.equals(confirmarSenha)) {
                // depois: Toast "Senhas não coincidem"
                return;
            }

            if (rbAdmin.isChecked()) {
                String codigo = etCodigoCriador.getText().toString().trim();
                // depois: validar código no backend
            }

            // depois: enviar dados pro backend (cadastro)
        });
    }
}