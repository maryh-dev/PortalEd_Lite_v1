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

import com.example.portaled_lite.R;
import com.example.portaled_lite.modelo.Aula;
import com.example.portaled_lite.utilitarios.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AulaActivity extends BaseAlunoActivity {

    private String aulaId, cursoId;
    private Aula aulaAtual;
    private TextView tvTitulo, tvDescricao;
    private Button btnConcluir, btnProxima;
    private ImageView ivVoltar;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aula);

        db = FirebaseFirestore.getInstance();
        aulaId = getIntent().getStringExtra(Constantes.EXTRA_AULA_ID);
        cursoId = getIntent().getStringExtra(Constantes.EXTRA_CURSO_ID);
        
        if (aulaId == null || cursoId == null) { finish(); return; }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
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
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 1. Buscar dados da aula
        db.collection("cursos").document(cursoId).collection("aulas").document(aulaId)
                .get().addOnSuccessListener(documento -> {
                    aulaAtual = documento.toObject(Aula.class);
                    if (aulaAtual != null) {
                        aulaAtual.setId(documento.getId());
                        aulaAtual.setCursoId(cursoId);
                        tvTitulo.setText(aulaAtual.getTitulo());
                        tvDescricao.setText(aulaAtual.getDescricao());
                        verificarProximaAula();
                    }
                });

        // 2. Verificar se já concluiu
        db.collection("aulasConcluidas")
                .whereEqualTo("usuarioId", uid)
                .whereEqualTo("aulaId", aulaId)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        btnConcluir.setEnabled(false);
                        btnConcluir.setText("Aula Concluída");
                    }
                });
    }

    private void verificarProximaAula() {
        db.collection("cursos").document(cursoId).collection("aulas")
                .whereEqualTo("ordem", aulaAtual.getOrdem() + 1)
                .limit(1).get().addOnSuccessListener(q -> {
                    if (!q.isEmpty()) {
                        btnProxima.setVisibility(View.VISIBLE);
                        final String proximaId = q.getDocuments().get(0).getId();
                        btnProxima.setOnClickListener(v -> {
                            Intent intent = new Intent(this, AulaActivity.class);
                            intent.putExtra(Constantes.EXTRA_CURSO_ID, cursoId);
                            intent.putExtra(Constantes.EXTRA_AULA_ID, proximaId);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        btnProxima.setVisibility(View.GONE);
                    }
                });
    }

    private void configurarListeners() {
        ivVoltar.setOnClickListener(v -> finish());

        btnConcluir.setOnClickListener(v -> {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Map<String, Object> conclusao = new HashMap<>();
            conclusao.put("usuarioId", uid);
            conclusao.put("cursoId", cursoId);
            conclusao.put("aulaId", aulaId);

            db.collection("aulasConcluidas").add(conclusao).addOnSuccessListener(ref -> {
                btnConcluir.setEnabled(false);
                btnConcluir.setText("Aula Concluída");
                Toast.makeText(this, "Aula concluída!", Toast.LENGTH_SHORT).show();
                atualizarProgresso(uid, cursoId);
            });
        });
    }

    private void atualizarProgresso(String uid, String cursoId) {
        db.collection("cursos").document(cursoId).collection("aulas").get()
                .addOnSuccessListener(todasAulas -> {
                    int totalAulas = todasAulas.size();

                    db.collection("aulasConcluidas")
                            .whereEqualTo("usuarioId", uid)
                            .whereEqualTo("cursoId", cursoId)
                            .get()
                            .addOnSuccessListener(concluidas -> {
                                int totalConcluidas = concluidas.size();
                                int progresso = (int) ((totalConcluidas * 100.0) / totalAulas);

                                db.collection("matriculas")
                                        .whereEqualTo("usuarioId", uid)
                                        .whereEqualTo("cursoId", cursoId)
                                        .get()
                                        .addOnSuccessListener(matriculas -> {
                                            if (!matriculas.isEmpty()) {
                                                String mId = matriculas.getDocuments().get(0).getId();
                                                db.collection("matriculas").document(mId)
                                                        .update("progresso", progresso)
                                                        .addOnSuccessListener(u -> {
                                                            if (progresso == 100) {
                                                                Toast.makeText(this, "Parabéns! Curso concluído!", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            }
                                        });
                            });
                });
    }
}
