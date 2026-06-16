package com.example.portaled_lite.aluno;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portaled_lite.R;
import com.example.portaled_lite.adaptador.CursoAdapter;
import com.example.portaled_lite.modelo.Curso;
import com.example.portaled_lite.utilitarios.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FavoritosActivity extends BaseAlunoActivity {

    private EditText etBuscar;
    private RecyclerView rvFavoritos;
    private CursoAdapter cursoAdapter;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoritos);

        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            return;
        }
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Ajuste de insets removendo padding inferior para não empurrar o menu pra cima
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        inicializarViews();
        configurarBottomNav(findViewById(R.id.bottomNav), R.id.nav_favoritos);
        carregarFavoritos();
        configurarListeners();
    }

    private void inicializarViews() {
        etBuscar = findViewById(R.id.etBuscarFavorito);
        rvFavoritos = findViewById(R.id.rvFavoritos);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void carregarFavoritos() {
        db.collection("favoritos")
                .whereEqualTo("usuarioId", uid)
                .get()
                .addOnSuccessListener(favoritosQuery -> {
                    List<String> idsFavoritos = new ArrayList<>();
                    for (DocumentSnapshot doc : favoritosQuery.getDocuments()) {
                        idsFavoritos.add(doc.getString("cursoId"));
                    }

                    if (idsFavoritos.isEmpty()) {
                        atualizarAdapter(new ArrayList<>());
                        return;
                    }

                    List<Curso> cursosFavoritos = new ArrayList<>();
                    for (String cursoId : idsFavoritos) {
                        db.collection("cursos").document(cursoId).get().addOnSuccessListener(cursoDoc -> {
                            Curso curso = cursoDoc.toObject(Curso.class);
                            if (curso != null) {
                                curso.setId(cursoDoc.getId());
                                cursosFavoritos.add(curso);
                            }
                            if (cursosFavoritos.size() == idsFavoritos.size()) {
                                atualizarAdapter(cursosFavoritos);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar favoritos", Toast.LENGTH_SHORT).show();
                });
    }

    private void atualizarAdapter(List<Curso> listaFinal) {
        cursoAdapter = new CursoAdapter(position -> {
            Curso c = listaFinal.get(position);
            Intent intent = new Intent(this, DetalhesCursoActivity.class);
            intent.putExtra(Constantes.EXTRA_CURSO_ID, c.getId());
            startActivity(intent);
        });
        cursoAdapter.atualizarLista(listaFinal);
        rvFavoritos.setAdapter(cursoAdapter);
    }

    private void configurarListeners() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (cursoAdapter != null) {
                    cursoAdapter.filtrarPorTexto(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}
