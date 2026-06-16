package com.example.portaled_lite.aluno;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portaled_lite.R;
import com.example.portaled_lite.adaptador.CursoAdapter;
import com.example.portaled_lite.autenticacao.LoginActivity;
import com.example.portaled_lite.modelo.Curso;
import com.example.portaled_lite.modelo.Matricula;
import com.example.portaled_lite.modelo.Usuario;
import com.example.portaled_lite.perfil.PerfilConfigActivity;
import com.example.portaled_lite.utilitarios.Constantes;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.GerenciadorSessao;

import java.util.List;

public class HomeActivity extends BaseAlunoActivity {

    private ImageView ivMenu, ivNotificacoes;
    private View cardContinuarCurso;
    private TextView tvNomeCursoAtual, tvInfoCursoAtual;
    private ProgressBar pbProgressoCursoAtual;
    private RecyclerView rvRecomendados;
    private CursoAdapter cursoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarViews();
        configurarBottomNav(findViewById(R.id.bottomNav), R.id.nav_home);
        carregarDados();
        configurarListeners();
    }

    private void inicializarViews() {
        ivMenu = findViewById(R.id.ivMenu);
        ivNotificacoes = findViewById(R.id.ivNotificacoes);
        cardContinuarCurso = findViewById(R.id.cardContinuarCurso);
        tvNomeCursoAtual = findViewById(R.id.tvNomeCursoAtual);
        tvInfoCursoAtual = findViewById(R.id.tvInfoCursoAtual);
        pbProgressoCursoAtual = findViewById(R.id.pbProgressoCursoAtual);
        rvRecomendados = findViewById(R.id.rvRecomendados);

        rvRecomendados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void carregarDados() {
        Usuario usuario = GerenciadorSessao.getInstance().getUsuarioLogado();
        if (usuario == null) return;

        // 1. Carregar curso em andamento
        List<Matricula> matriculas = GerenciadorDados.getInstance().listarMatriculasPorUsuario(usuario.getId());
        Matricula cursoEmAndamento = null;
        for (Matricula m : matriculas) {
            if (m.getProgresso() < 100) {
                cursoEmAndamento = m;
                break;
            }
        }

        if (cursoEmAndamento != null) {
            Curso curso = GerenciadorDados.getInstance().buscarCursoPorId(cursoEmAndamento.getCursoId());
            if (curso != null) {
                cardContinuarCurso.setVisibility(View.VISIBLE);
                tvNomeCursoAtual.setText(curso.getTitulo());
                tvInfoCursoAtual.setText(curso.getCategoria());
                pbProgressoCursoAtual.setProgress(cursoEmAndamento.getProgresso());
                
                final String cursoId = curso.getId();
                cardContinuarCurso.setOnClickListener(v -> abrirDetalhesCurso(cursoId));
            }
        } else {
            cardContinuarCurso.setVisibility(View.GONE);
        }

        // 2. Carregar recomendações
        List<Curso> recomendados = GerenciadorDados.getInstance().listarCursos();
        cursoAdapter = new CursoAdapter(position -> {
            Curso c = recomendados.get(position);
            abrirDetalhesCurso(c.getId());
        });
        cursoAdapter.atualizarLista(recomendados);
        rvRecomendados.setAdapter(cursoAdapter);
    }

    private void configurarListeners() {
        ivMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, ivMenu);
            popup.getMenuInflater().inflate(R.menu.menu_lateral_aluno, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_perfil) {
                    startActivity(new Intent(this, PerfilConfigActivity.class));
                    return true;
                } else if (id == R.id.menu_sair) {
                    GerenciadorSessao.getInstance().encerrarSessao();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Clique nas categorias
        findViewById(R.id.ivCategoriaTecnologia).setOnClickListener(v -> abrirCursosComFiltro("Desenvolvimento"));
        findViewById(R.id.ivCategoriaDesign).setOnClickListener(v -> abrirCursosComFiltro("Design"));
    }

    private void abrirDetalhesCurso(String cursoId) {
        Intent intent = new Intent(this, DetalhesCursoActivity.class);
        intent.putExtra(Constantes.EXTRA_CURSO_ID, cursoId);
        startActivity(intent);
    }

    private void abrirCursosComFiltro(String categoria) {
        Intent intent = new Intent(this, CursosActivity.class);
        intent.putExtra("EXTRA_CATEGORIA", categoria);
        startActivity(intent);
    }
}
