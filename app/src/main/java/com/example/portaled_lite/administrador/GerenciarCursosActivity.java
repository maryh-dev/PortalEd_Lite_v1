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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GerenciarCursosActivity extends AppCompatActivity {

    private RecyclerView rvCursos;
    private CursoAdminAdapter adapter;
    private FloatingActionButton fabAdicionar;
    private ImageView ivVoltar;
    private FirebaseFirestore db;
    private List<Curso> listaCursos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gerenciar_cursos);
        db = FirebaseFirestore.getInstance();

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
                exibirDialogCurso(listaCursos.get(position));
            }

            @Override
            public void onExcluirClick(int position) {
                confirmarExclusaoFirestore(listaCursos.get(position), position);
            }
        });
        rvCursos.setAdapter(adapter);
        carregarCursosFirestore();
    }

    private void carregarCursosFirestore() {
        db.collection("cursos").get().addOnSuccessListener(querySnapshot -> {
            listaCursos.clear();
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                Curso c = doc.toObject(Curso.class);
                if (c != null) {
                    c.setId(doc.getId());
                    listaCursos.add(c);
                }
            }
            adapter.atualizarLista(listaCursos);
        });
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
        TextInputEditText etProfessor = view.findViewById(R.id.etProfessorCurso);

        if (cursoExistente != null) {
            etTitulo.setText(cursoExistente.getTitulo());
            etCategoria.setText(cursoExistente.getCategoria());
            etDescricao.setText(cursoExistente.getDescricao());
            etProfessor.setText(cursoExistente.getProfessorNome());
        }

        String acao = cursoExistente == null ? "Adicionar" : "Salvar";

        new AlertDialog.Builder(this)
                .setTitle(acao + " Curso")
                .setView(view)
                .setPositiveButton(acao, (dialog, which) -> {
                    String titulo = etTitulo.getText().toString().trim();
                    String categoria = etCategoria.getText().toString().trim();
                    String descricao = etDescricao.getText().toString().trim();
                    String professor = etProfessor.getText().toString().trim();

                    if (Validador.validarCampoVazio(titulo) || Validador.validarCampoVazio(categoria)) {
                        Toast.makeText(this, "Título e Categoria são obrigatórios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Map<String, Object> dados = new HashMap<>();
                    dados.put("titulo", titulo);
                    dados.put("categoria", categoria);
                    dados.put("descricao", descricao);
                    dados.put("professorNome", professor);

                    if (cursoExistente == null) {
                        db.collection("cursos").add(dados).addOnSuccessListener(ref -> {
                            Toast.makeText(this, "Curso adicionado", Toast.LENGTH_SHORT).show();
                            carregarCursosFirestore();
                        });
                    } else {
                        db.collection("cursos").document(cursoExistente.getId()).set(dados).addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Curso atualizado", Toast.LENGTH_SHORT).show();
                            carregarCursosFirestore();
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarExclusaoFirestore(Curso curso, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir curso")
                .setMessage("Tem certeza? Essa ação não pode ser desfeita.")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    db.collection("cursos").document(curso.getId()).delete().addOnSuccessListener(unused -> {
                        listaCursos.remove(position);
                        adapter.atualizarLista(listaCursos);
                        Snackbar.make(findViewById(android.R.id.content), "Curso removido", Snackbar.LENGTH_LONG).show();
                    });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
