package com.example.portaled_lite.aluno;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.portaled_lite.R;
import com.example.portaled_lite.modelo.Aula;
import com.example.portaled_lite.utilitarios.Constantes;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.GerenciadorSessao;

import java.util.List;

public class AulaActivity extends BaseAlunoActivity {

    private String aulaId;
    private Aula aulaAtual;
    private TextView tvTitulo, tvDescricao;
    private Button btnConcluir, btnProxima;
    private ImageView ivVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aula);

        aulaId = getIntent().getStringExtra(Constantes.EXTRA_AULA_ID);
        if (aulaId == null) { finish(); return; }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarViews();
        carregarDados();
        configurarListeners();
    }

    private void inicializarViews() {
        tvTitulo = findViewById(R.id.tvTituloAula);
        tvDescricao = findViewById(R.id.tvDescricaoAula);
        btnConcluir = findViewById(R.id.btnConcluirAula);
        btnProxima = findViewById(R.id.btnProximaAula);
        ivVoltar = findViewById(R.id.ivVoltar);
    }

    private void carregarDados() {
        aulaAtual = GerenciadorDados.getInstance().buscarAulaPorId(aulaId);
        if (aulaAtual == null) { finish(); return; }

        tvTitulo.setText(aulaAtual.getTitulo());
        tvDescricao.setText("Esta é a aula " + aulaAtual.getOrdem() + " do curso.");

        String userId = GerenciadorSessao.getInstance().getUsuarioLogado().getId();
        boolean concluida = GerenciadorDados.getInstance().estaAulaConcluida(userId, aulaId);
        
        if (concluida) {
            btnConcluir.setEnabled(false);
            btnConcluir.setText("Aula Concluída");
        }

        // Verificar próxima aula
        List<Aula> aulasCurso = GerenciadorDados.getInstance().buscarAulasPorCurso(aulaAtual.getCursoId());
        Aula proxima = null;
        for (Aula a : aulasCurso) {
            if (a.getOrdem() == aulaAtual.getOrdem() + 1) {
                proxima = a;
                break;
            }
        }

        if (proxima == null) {
            btnProxima.setVisibility(View.GONE);
        } else {
            btnProxima.setVisibility(View.VISIBLE);
            final String proximaId = proxima.getId();
            btnProxima.setOnClickListener(v -> {
                getIntent().putExtra(Constantes.EXTRA_AULA_ID, proximaId);
                recreate();
            });
        }
    }

    private void configurarListeners() {
        ivVoltar.setOnClickListener(v -> finish());

        btnConcluir.setOnClickListener(v -> {
            String userId = GerenciadorSessao.getInstance().getUsuarioLogado().getId();
            GerenciadorDados.getInstance().concluirAula(userId, aulaId);
            
            btnConcluir.setEnabled(false);
            btnConcluir.setText("Aula Concluída");
            Toast.makeText(this, "Parabéns por concluir mais uma aula!", Toast.LENGTH_SHORT).show();
            
            // Se não houver próxima aula, dar parabéns pelo curso
            List<Aula> aulasCurso = GerenciadorDados.getInstance().buscarAulasPorCurso(aulaAtual.getCursoId());
            boolean temProxima = false;
            for (Aula a : aulasCurso) {
                if (a.getOrdem() == aulaAtual.getOrdem() + 1) {
                    temProxima = true;
                    break;
                }
            }
            
            if (!temProxima) {
                Toast.makeText(this, "Você concluiu todas as aulas deste curso!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
