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
import com.example.portaled_lite.utilitarios.GerenciadorSessao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseAlunoActivity {

    private ImageView ivMenu, ivNotificacoes;
    private View cardContinuarCurso;
    private TextView tvNomeCursoAtual, tvInfoCursoAtual, tvNomeApp, tvContinueEstudando;
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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        inicializarViews();
        configurarBottomNav(findViewById(R.id.bottomNav), R.id.nav_home);
        carregarDados();
        carregarNomeUsuario();
        configurarListeners();
    }

    private void carregarNomeUsuario() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() == null) return;
        
        String uid = auth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(documento -> {
                    if (documento.exists()) {
                        String nome = documento.getString("nome");
                        if (nome != null) {
                            tvNomeApp.setText("Olá, " + nome);
                        }
                    }
                });
    }

    private void inicializarViews() {
        ivMenu = findViewById(R.id.ivMenu);
        ivNotificacoes = findViewById(R.id.ivNotificacoes);
        tvNomeApp = findViewById(R.id.tvNomeApp);
        cardContinuarCurso = findViewById(R.id.cardContinuarCurso);
        tvNomeCursoAtual = findViewById(R.id.tvNomeCursoAtual);
        tvInfoCursoAtual = findViewById(R.id.tvInfoCursoAtual);
        tvContinueEstudando = findViewById(R.id.tvContinueEstudando);
        pbProgressoCursoAtual = findViewById(R.id.pbProgressoCursoAtual);
        rvRecomendados = findViewById(R.id.rvRecomendados);

        rvRecomendados.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void carregarDados() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 1. Carregar curso em andamento real do Firestore
        db.collection("matriculas")
                .whereEqualTo("usuarioId", uid)
                .whereLessThan("progresso", 100)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot matricula = query.getDocuments().get(0);
                        String cursoId = matricula.getString("cursoId");
                        Long progressoLong = matricula.getLong("progresso");
                        int progresso = (progressoLong != null) ? progressoLong.intValue() : 0;

                        db.collection("cursos").document(cursoId).get().addOnSuccessListener(cursoDoc -> {
                            if (cursoDoc.exists()) {
                                cardContinuarCurso.setVisibility(View.VISIBLE);
                                tvContinueEstudando.setVisibility(View.VISIBLE);
                                tvNomeCursoAtual.setText(cursoDoc.getString("titulo"));
                                tvInfoCursoAtual.setText(cursoDoc.getString("professorNome"));
                                pbProgressoCursoAtual.setProgress(progresso);

                                cardContinuarCurso.setOnClickListener(v -> {
                                    Intent intent = new Intent(this, DetalhesCursoActivity.class);
                                    intent.putExtra(Constantes.EXTRA_CURSO_ID, cursoId);
                                    startActivity(intent);
                                });
                            }
                        });
                    } else {
                        cardContinuarCurso.setVisibility(View.GONE);
                        tvContinueEstudando.setVisibility(View.GONE);
                    }
                });

        // 2. Carregar recomendações reais do Firestore
        db.collection("cursos").limit(10).get().addOnSuccessListener(querySnapshot -> {
            List<Curso> recomendados = new ArrayList<>();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Curso c = doc.toObject(Curso.class);
                if (c != null) {
                    c.setId(doc.getId());
                    recomendados.add(c);
                }
            }
            cursoAdapter = new CursoAdapter(position -> {
                Curso c = recomendados.get(position);
                abrirDetalhesCurso(c.getId());
            });
            cursoAdapter.atualizarLista(recomendados);
            rvRecomendados.setAdapter(cursoAdapter);
        });
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
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // Clique nas categorias corrigido para Tecnologia e Design
        findViewById(R.id.ivCategoriaTecnologia).setOnClickListener(v -> abrirCursosComFiltro("Tecnologia"));
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
