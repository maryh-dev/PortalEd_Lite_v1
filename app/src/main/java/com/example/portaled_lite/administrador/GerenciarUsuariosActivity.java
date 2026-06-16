package com.example.portaled_lite.administrador;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
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
import com.example.portaled_lite.adaptador.OnAcaoAdminListener;
import com.example.portaled_lite.adaptador.UsuarioAdapter;
import com.example.portaled_lite.modelo.Usuario;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class GerenciarUsuariosActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;
    private UsuarioAdapter adapter;
    private EditText etBuscar;
    private ImageView ivVoltar;
    private FirebaseFirestore db;
    private List<Usuario> listaUsuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gerenciar_usuarios);
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
        rvUsuarios = findViewById(R.id.rvUsuarios);
        etBuscar = findViewById(R.id.etBuscarUsuario);
        ivVoltar = findViewById(R.id.ivVoltar);
    }

    private void configurarAdapter() {
        adapter = new UsuarioAdapter(new OnAcaoAdminListener() {
            @Override
            public void onEditarClick(int position) {
                // Não implementado para usuários nesta fase
            }

            @Override
            public void onExcluirClick(int position) {
                removerUsuario(listaUsuarios.get(position).getId(), position);
            }
        });
        rvUsuarios.setAdapter(adapter);
        carregarUsuarios();
    }

    private void carregarUsuarios() {
        db.collection("usuarios")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    listaUsuarios.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Usuario usuario = doc.toObject(Usuario.class);
                        if (usuario != null) {
                            usuario.setId(doc.getId());
                            listaUsuarios.add(usuario);
                        }
                    }
                    adapter.atualizarLista(listaUsuarios);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar usuários", Toast.LENGTH_SHORT).show();
                });
    }

    private void configurarListeners() {
        ivVoltar.setOnClickListener(v -> finish());

        etBuscar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filtrarPorTexto(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void removerUsuario(String usuarioId, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Remover usuário")
                .setMessage("Tem certeza que deseja remover este usuário?")
                .setPositiveButton("Remover", (dialog, which) -> {
                    db.collection("usuarios")
                            .document(usuarioId)
                            .delete()
                            .addOnSuccessListener(unused -> {
                                listaUsuarios.remove(position);
                                adapter.atualizarLista(listaUsuarios);
                                Toast.makeText(this, "Usuário removido", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Erro ao remover usuário", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
