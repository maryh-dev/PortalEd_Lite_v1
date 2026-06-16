package com.example.portaled_lite.aluno;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;

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
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.GerenciadorSessao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CursosActivity extends BaseAlunoActivity {

    private EditText etBuscar;
    private RecyclerView rvCursos;
    private CursoAdapter cursoAdapter;
    private List<Curso> listaCompleta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cursos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        inicializarViews();
        configurarBottomNav(findViewById(R.id.bottomNav), R.id.nav_cursos);
        carregarDados();
        configurarListeners();

    }

    private void inicializarViews() {
        etBuscar = findViewById(R.id.etBuscarCurso);
        rvCursos = findViewById(R.id.rvCursos);
        rvCursos.setLayoutManager(new LinearLayoutManager(this));
    }

    private void carregarDados() {
        String categoriaFiltro = getIntent().getStringExtra("EXTRA_CATEGORIA");
        
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("cursos");

        if (categoriaFiltro != null) {
            query = query.whereEqualTo("categoria", categoriaFiltro);
        }

        query.get().addOnSuccessListener(querySnapshot -> {
            List<Curso> listaParaExibir = new ArrayList<>();
            for (DocumentSnapshot documento : querySnapshot.getDocuments()) {
                Curso curso = documento.toObject(Curso.class);
                if (curso != null) {
                    curso.setId(documento.getId());
                    listaParaExibir.add(curso);
                }
            }
            atualizarAdapter(listaParaExibir);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Erro ao carregar cursos", Toast.LENGTH_SHORT).show();
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
        rvCursos.setAdapter(cursoAdapter);
    }

    private void configurarListeners() {
        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                cursoAdapter.filtrarPorTexto(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

}
