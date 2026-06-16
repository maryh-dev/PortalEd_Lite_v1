package com.example.portaled_lite.aluno;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

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
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
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
        boolean mostrarApenasFavoritos = getIntent().getBooleanExtra("EXTRA_FAVORITOS", false);
        listaCompleta = GerenciadorDados.getInstance().listarCursos();
        
        List<Curso> listaExibida;
        if (mostrarApenasFavoritos) {
            String userId = GerenciadorSessao.getInstance().getUsuarioLogado().getId();
            listaExibida = GerenciadorDados.getInstance().listarFavoritosPorUsuario(userId);
        } else if (categoriaFiltro != null) {
            listaExibida = listaCompleta.stream()
                    .filter(c -> c.getCategoria().equalsIgnoreCase(categoriaFiltro))
                    .collect(Collectors.toList());
        } else {
            listaExibida = new ArrayList<>(listaCompleta);
        }

        cursoAdapter = new CursoAdapter(position -> {
            Curso c = listaExibida.get(position);
            Intent intent = new Intent(this, DetalhesCursoActivity.class);
            intent.putExtra(Constantes.EXTRA_CURSO_ID, c.getId());
            startActivity(intent);
        });
        
        cursoAdapter.atualizarLista(listaExibida);
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
