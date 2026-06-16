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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 1. Detalhes do Curso
        db.collection("cursos").document(cursoId).get().addOnSuccessListener(documento -> {
            if (documento.exists()) {
                Curso curso = documento.toObject(Curso.class);
                if (curso != null) {
                    tvNome.setText(curso.getTitulo());
                    tvProfessor.setText(curso.getProfessorNome());
                    tvDescricao.setText(curso.getDescricao());
                }
            } else {
                finish();
            }
        });

        // 2. Verificar Matrícula
        db.collection("matriculas")
                .whereEqualTo("usuarioId", uid)
                .whereEqualTo("cursoId", cursoId)
                .get()
                .addOnSuccessListener(query -> {
                    estaMatriculado = !query.isEmpty();
                    btnMatricular.setText(estaMatriculado ? "Continuar Estudando" : "Matricular-se");
                    carregarAulas(db);
                });

        // 3. Verificar Favorito
        verificarFavorito(uid, cursoId, db);
    }

    private void carregarAulas(FirebaseFirestore db) {
        db.collection("cursos")
                .document(cursoId)
                .collection("aulas")
                .orderBy("ordem")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Aula> listaAulas = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Aula aula = doc.toObject(Aula.class);
                        if (aula != null) {
                            aula.setId(doc.getId());
                            aula.setCursoId(cursoId);
                            listaAulas.add(aula);
                        }
                    }
                    aulaAdapter = new AulaAdapter(position -> {
                        if (estaMatriculado) {
                            Aula aula = listaAulas.get(position);
                            Intent intent = new Intent(this, AulaActivity.class);
                            intent.putExtra(Constantes.EXTRA_CURSO_ID, cursoId);
                            intent.putExtra(Constantes.EXTRA_AULA_ID, aula.getId());
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Matricule-se para acessar as aulas", Toast.LENGTH_SHORT).show();
                        }
                    });
                    aulaAdapter.atualizarLista(listaAulas);
                    rvAulas.setAdapter(aulaAdapter);
                });
    }

    private void verificarFavorito(String uid, String cursoId, FirebaseFirestore db) {
        db.collection("favoritos")
                .whereEqualTo("usuarioId", uid)
                .whereEqualTo("cursoId", cursoId)
                .get()
                .addOnSuccessListener(query -> {
                    estaFavoritado = !query.isEmpty();
                    ivFavoritar.setImageResource(estaFavoritado ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
                    // Usando cor como sugerido na Parte 16
                    ivFavoritar.setColorFilter(estaFavoritado ? getColor(R.color.azul_secundario) : getColor(R.color.texto_secundario));
                    ivFavoritar.setTag(estaFavoritado ? "favoritado" : "nao_favoritado");
                });
    }

    private void configurarListeners() {
        ivVoltar.setOnClickListener(v -> finish());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        ivFavoritar.setOnClickListener(v -> alternarFavorito(uid, cursoId, db));

        btnMatricular.setOnClickListener(v -> {
            if (!estaMatriculado) {
                Map<String, Object> matricula = new HashMap<>();
                matricula.put("usuarioId", uid);
                matricula.put("cursoId", cursoId);
                matricula.put("progresso", 0);

                db.collection("matriculas")
                        .add(matricula)
                        .addOnSuccessListener(ref -> {
                            estaMatriculado = true;
                            btnMatricular.setText("Continuar Estudando");
                            Toast.makeText(this, "Matriculado com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarAulas(db); // Recarrega para garantir listener atualizado
                        });
            } else {
                // Ir para a última aula ou primeira
                db.collection("cursos").document(cursoId).collection("aulas")
                        .orderBy("ordem").limit(1).get().addOnSuccessListener(q -> {
                            if (!q.isEmpty()) {
                                Intent intent = new Intent(this, AulaActivity.class);
                                intent.putExtra(Constantes.EXTRA_CURSO_ID, cursoId);
                                intent.putExtra(Constantes.EXTRA_AULA_ID, q.getDocuments().get(0).getId());
                                startActivity(intent);
                            }
                        });
            }
        });
    }

    private void alternarFavorito(String uid, String cursoId, FirebaseFirestore db) {
        if ("favoritado".equals(ivFavoritar.getTag())) {
            db.collection("favoritos")
                    .whereEqualTo("usuarioId", uid)
                    .whereEqualTo("cursoId", cursoId)
                    .get()
                    .addOnSuccessListener(query -> {
                        for (DocumentSnapshot doc : query.getDocuments()) {
                            doc.getReference().delete();
                        }
                        estaFavoritado = false;
                        ivFavoritar.setColorFilter(getColor(R.color.texto_secundario));
                        ivFavoritar.setImageResource(android.R.drawable.btn_star_big_off);
                        ivFavoritar.setTag("nao_favoritado");
                        Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Map<String, Object> favorito = new HashMap<>();
            favorito.put("usuarioId", uid);
            favorito.put("cursoId", cursoId);

            db.collection("favoritos")
                    .add(favorito)
                    .addOnSuccessListener(ref -> {
                        estaFavoritado = true;
                        ivFavoritar.setColorFilter(getColor(R.color.azul_secundario));
                        ivFavoritar.setImageResource(android.R.drawable.btn_star_big_on);
                        ivFavoritar.setTag("favoritado");
                        Toast.makeText(this, "Adicionado aos favoritos!", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
