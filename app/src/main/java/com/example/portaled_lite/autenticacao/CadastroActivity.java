package com.example.portaled_lite.autenticacao;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.portaled_lite.R;
import com.example.portaled_lite.modelo.Usuario;
import com.example.portaled_lite.utilitarios.Constantes;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.Validador;
import com.google.android.material.textfield.TextInputLayout;

public class CadastroActivity extends AppCompatActivity {

    private TextInputLayout tilNome, tilEmail, tilSenha, tilConfirmarSenha, tilCodigoCriador;
    private RadioGroup rgTipoConta;
    private Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarViews();
        configurarListeners();
    }

    private void inicializarViews() {
        tilNome = findViewById(R.id.tilNome);
        tilEmail = findViewById(R.id.tilEmail);
        tilSenha = findViewById(R.id.tilSenha);
        tilConfirmarSenha = findViewById(R.id.tilConfirmarSenha);
        tilCodigoCriador = findViewById(R.id.tilCodigoCriador);
        rgTipoConta = findViewById(R.id.rgTipoConta);
        btnCadastrar = findViewById(R.id.btnCadastrar);
    }

    private void configurarListeners() {
        rgTipoConta.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbAdmin) {
                tilCodigoCriador.setVisibility(View.VISIBLE);
            } else {
                tilCodigoCriador.setVisibility(View.GONE);
            }
        });

        btnCadastrar.setOnClickListener(v -> realizarCadastro());

        if (tilNome.getEditText() != null) adicionarTextWatcher(tilNome);
        if (tilEmail.getEditText() != null) adicionarTextWatcher(tilEmail);
        if (tilSenha.getEditText() != null) adicionarTextWatcher(tilSenha);
        if (tilConfirmarSenha.getEditText() != null) adicionarTextWatcher(tilConfirmarSenha);
        if (tilCodigoCriador.getEditText() != null) adicionarTextWatcher(tilCodigoCriador);
    }

    private void realizarCadastro() {
        if (tilNome.getEditText() == null || tilEmail.getEditText() == null ||
            tilSenha.getEditText() == null || tilConfirmarSenha.getEditText() == null ||
            tilCodigoCriador.getEditText() == null) return;

        String nome = tilNome.getEditText().getText().toString().trim();
        String email = tilEmail.getEditText().getText().toString().trim();
        String senha = tilSenha.getEditText().getText().toString().trim();
        String confirmarSenha = tilConfirmarSenha.getEditText().getText().toString().trim();
        String codigo = tilCodigoCriador.getEditText().getText().toString().trim();
        String tipo = rgTipoConta.getCheckedRadioButtonId() == R.id.rbAdmin ? Constantes.TIPO_ADMIN : Constantes.TIPO_ALUNO;

        boolean valido = true;

        if (Validador.validarCampoVazio(nome)) {
            tilNome.setError("Campo obrigatório");
            valido = false;
        }

        if (!Validador.validarEmail(email)) {
            tilEmail.setError("E-mail inválido");
            valido = false;
        }

        if (!Validador.validarSenha(senha)) {
            tilSenha.setError("Mínimo de 6 caracteres");
            valido = false;
        }

        if (!Validador.validarConfirmacaoSenha(senha, confirmarSenha)) {
            tilConfirmarSenha.setError("As senhas não coincidem");
            valido = false;
        }

        if (tipo.equals(Constantes.TIPO_ADMIN) && !Validador.validarCodigoCriador(codigo)) {
            tilCodigoCriador.setError("Código inválido");
            valido = false;
        }

        if (!valido) return;

        Usuario novoUsuario = new Usuario(null, nome, email, senha, tipo);
        if (GerenciadorDados.getInstance().cadastrarUsuario(novoUsuario)) {
            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            tilEmail.setError("E-mail já cadastrado");
        }
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
