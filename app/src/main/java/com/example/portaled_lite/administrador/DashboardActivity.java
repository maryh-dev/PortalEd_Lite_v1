package com.example.portaled_lite.administrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.portaled_lite.R;
import com.example.portaled_lite.autenticacao.LoginActivity;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.GerenciadorSessao;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotalAlunos, tvTotalCursos;
    private Button btnGerenciarCursos, btnGerenciarUsuarios;
    private ImageView ivSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarViews();
        carregarEstatisticas();
        configurarListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarEstatisticas();
    }

    private void inicializarViews() {
        tvTotalAlunos = findViewById(R.id.tvTotalAlunos);
        tvTotalCursos = findViewById(R.id.tvTotalCursos);
        btnGerenciarCursos = findViewById(R.id.btnGerenciarCursos);
        btnGerenciarUsuarios = findViewById(R.id.btnGerenciarUsuarios);
        ivSair = findViewById(R.id.ivSair);
    }

    private void carregarEstatisticas() {
        tvTotalAlunos.setText(String.valueOf(GerenciadorDados.getInstance().contarAlunos()));
        tvTotalCursos.setText(String.valueOf(GerenciadorDados.getInstance().contarCursos()));
    }

    private void configurarListeners() {
        btnGerenciarCursos.setOnClickListener(v -> {
            startActivity(new Intent(this, GerenciarCursosActivity.class));
        });

        btnGerenciarUsuarios.setOnClickListener(v -> {
            startActivity(new Intent(this, GerenciarUsuariosActivity.class));
        });

        ivSair.setOnClickListener(v -> {
            GerenciadorSessao.getInstance().encerrarSessao();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
