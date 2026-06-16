package com.example.portaled_lite.administrador;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portaled_lite.R;
import com.example.portaled_lite.adaptador.CursoAdminAdapter;
import com.example.portaled_lite.adaptador.OnAcaoAdminListener;
import com.example.portaled_lite.modelo.Curso;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.Validador;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class GerenciarCursosActivity extends AppCompatActivity {

    private RecyclerView rvCursos;
    private CursoAdminAdapter adapter;
    private FloatingActionButton fabAdicionar;
    private ImageView ivVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gerenciar_cursos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarViews();
        configurarAdapter();
        configurarListeners();
    }

    private void inicializarViews() {
        rvCursos = findViewById(R.id.rvCursosAdmin);
        fabAdicionar = findViewById(R.id.fabAdicionarCurso);
        ivVoltar = findViewById(R.id.ivVoltar);
    }

    private void configurarAdapter() {
        adapter = new CursoAdminAdapter(new OnAcaoAdminListener() {
            @Override
            public void onEditarClick(int position) {
                exibirDialogCurso(GerenciadorDados.getInstance().listarCursos().get(position));
            }

            @Override
            public void onExcluirClick(int position) {
                confirmarExclusao(position);
            }
        });
        adapter.atualizarLista(GerenciadorDados.getInstance().listarCursos());
        rvCursos.setAdapter(adapter);
    }

    private void configurarListeners() {
        ivVoltar.setOnClickListener(v -> finish());
        fabAdicionar.setOnClickListener(v -> exibirDialogCurso(null));
    }

    private void exibirDialogCurso(Curso cursoExistente) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_curso, null);
        TextInputEditText etTitulo = view.findViewById(R.id.etTituloCurso);
        TextInputEditText etCategoria = view.findViewById(R.id.etCategoriaCurso);
        TextInputEditText etDescricao = view.findViewById(R.id.etDescricaoCurso);

        if (cursoExistente != null) {
            etTitulo.setText(cursoExistente.getTitulo());
            etCategoria.setText(cursoExistente.getCategoria());
            etDescricao.setText(cursoExistente.getDescricao());
        }

        String acao = cursoExistente == null ? "Adicionar" : "Salvar";

        new AlertDialog.Builder(this)
                .setTitle(acao + " Curso")
                .setView(view)
                .setPositiveButton(acao, (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String categoria = etCategoria.getText().toString().trim();
                    String descricao = etDescricao.getText().toString().trim();

                    if (Validador.validarCampoVazio(titulo) || Validador.validarCampoVazio(categoria)) {
                        Toast.makeText(this, "Título e Categoria são obrigatórios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (cursoExistente == null) {
                        GerenciadorDados.getInstance().adicionarCurso(new Curso(null, titulo, descricao, categoria, ""));
                        Toast.makeText(this, "Curso adicionado", Toast.LENGTH_SHORT).show();
                    } else {
                        cursoExistente.setTitulo(titulo);
                        cursoExistente.setCategoria(categoria);
                        cursoExistente.setDescricao(descricao);
                        GerenciadorDados.getInstance().editarCurso(cursoExistente);
                        Toast.makeText(this, "Curso atualizado", Toast.LENGTH_SHORT).show();
                    }
                    adapter.atualizarLista(GerenciadorDados.getInstance().listarCursos());
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarExclusao(int position) {
        Curso curso = GerenciadorDados.getInstance().listarCursos().get(position);
        
        new AlertDialog.Builder(this)
                .setTitle("Excluir Curso")
                .setMessage("Deseja realmente excluir o curso '" + curso.getTitulo() + "'?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    GerenciadorDados.getInstance().excluirCurso(curso.getId());
                    adapter.atualizarLista(GerenciadorDados.getInstance().listarCursos());
                    
                    Snackbar.make(rvCursos, "Curso excluído", Snackbar.LENGTH_LONG)
                            .setAction("Desfazer", v -> {
                                GerenciadorDados.getInstance().adicionarCurso(curso);
                                adapter.atualizarLista(GerenciadorDados.getInstance().listarCursos());
                            }).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
