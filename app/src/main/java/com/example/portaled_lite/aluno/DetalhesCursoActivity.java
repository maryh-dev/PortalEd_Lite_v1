package com.example.portaled_lite.aluno;

import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portaled_lite.R;
import com.example.portaled_lite.adaptador.AulaAdapter;
import com.example.portaled_lite.modelo.Aula;
import com.example.portaled_lite.modelo.Curso;
import com.example.portaled_lite.modelo.Matricula;
import com.example.portaled_lite.utilitarios.Constantes;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.GerenciadorSessao;

import java.util.List;

public class DetalhesCursoActivity extends BaseAlunoActivity {

    private String cursoId;
    private ImageView ivVoltar, ivFavoritar;
    private TextView tvNome, tvProfessor, tvDescricao;
    private Button btnMatricular;
    private RecyclerView rvAulas;
    private AulaAdapter aulaAdapter;
    private boolean estaFavoritado;
    private boolean estaMatriculado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalhes_curso);

        cursoId = getIntent().getStringExtra(Constantes.EXTRA_CURSO_ID);
        if (cursoId == null) { finish(); return; }

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
        ivVoltar = findViewById(R.id.ivVoltar);
        ivFavoritar = findViewById(R.id.ivFavoritar);
        tvNome = findViewById(R.id.tvNomeCurso);
        tvProfessor = findViewById(R.id.tvProfessorCurso);
        tvDescricao = findViewById(R.id.tvDescricaoCurso);
        btnMatricular = findViewById(R.id.btnMatricular);
        rvAulas = findViewById(R.id.rvAulas);
        rvAulas.setLayoutManager(new LinearLayoutManager(this));
    }

    private void carregarDados() {
        Curso curso = GerenciadorDados.getInstance().buscarCursoPorId(cursoId);
        if (curso == null) { finish(); return; }

        tvNome.setText(curso.getTitulo());
        tvProfessor.setText(curso.getCategoria()); // Exemplo
        tvDescricao.setText(curso.getDescricao());

        String userId = GerenciadorSessao.getInstance().getUsuarioLogado().getId();
        
        // Matrícula
        estaMatriculado = GerenciadorDados.getInstance().verificarMatricula(userId, cursoId) != null;
        btnMatricular.setText(estaMatriculado ? "Continuar Estudando" : "Matricular-se");

        // Favorito
        estaFavoritado = GerenciadorDados.getInstance().verificarFavorito(userId, cursoId);
        ivFavoritar.setImageResource(estaFavoritado ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);

        // Aulas
        List<Aula> aulas = GerenciadorDados.getInstance().buscarAulasPorCurso(cursoId);
        aulaAdapter = new AulaAdapter(position -> {
            if (estaMatriculado) {
                Aula aula = aulas.get(position);
                Intent intent = new Intent(this, AulaActivity.class);
                intent.putExtra(Constantes.EXTRA_AULA_ID, aula.getId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "Matricule-se para acessar as aulas", Toast.LENGTH_SHORT).show();
            }
        });
        aulaAdapter.atualizarLista(aulas);
        rvAulas.setAdapter(aulaAdapter);
    }

    private void configurarListeners() {
        ivVoltar.setOnClickListener(v -> finish());

        ivFavoritar.setOnClickListener(v -> {
            String userId = GerenciadorSessao.getInstance().getUsuarioLogado().getId();
            if (estaFavoritado) {
                GerenciadorDados.getInstance().desfavoritar(userId, cursoId);
                estaFavoritado = false;
                Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show();
            } else {
                GerenciadorDados.getInstance().favoritar(userId, cursoId);
                estaFavoritado = true;
                Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show();
            }
            ivFavoritar.setImageResource(estaFavoritado ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        });

        btnMatricular.setOnClickListener(v -> {
            if (!estaMatriculado) {
                String userId = GerenciadorSessao.getInstance().getUsuarioLogado().getId();
                GerenciadorDados.getInstance().matricular(userId, cursoId);
                estaMatriculado = true;
                btnMatricular.setText("Continuar Estudando");
                aulaAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Matrícula realizada!", Toast.LENGTH_SHORT).show();
            } else {
                // Navega para a primeira aula não concluída
                List<Aula> aulas = GerenciadorDados.getInstance().buscarAulasPorCurso(cursoId);
                if (!aulas.isEmpty()) {
                    Intent intent = new Intent(this, AulaActivity.class);
                    intent.putExtra(Constantes.EXTRA_AULA_ID, aulas.get(0).getId());
                    startActivity(intent);
                }
            }
        });
    }
}
