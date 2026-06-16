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
import com.example.portaled_lite.utilitarios.GerenciadorDados;

public class GerenciarUsuariosActivity extends AppCompatActivity {

    private RecyclerView rvUsuarios;
    private UsuarioAdapter adapter;
    private EditText etBuscar;
    private ImageView ivVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gerenciar_usuarios);

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
                confirmarRemocao(position);
            }
        });
        adapter.atualizarLista(GerenciadorDados.getInstance().listarUsuarios());
        rvUsuarios.setAdapter(adapter);
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

    private void confirmarRemocao(int position) {
        Usuario usuario = GerenciadorDados.getInstance().listarUsuarios().get(position);
        
        new AlertDialog.Builder(this)
                .setTitle("Remover Usuário")
                .setMessage("Deseja realmente remover o usuário '" + usuario.getNome() + "'?")
                .setPositiveButton("Remover", (dialog, which) -> {
                    GerenciadorDados.getInstance().removerUsuario(usuario.getId());
                    adapter.atualizarLista(GerenciadorDados.getInstance().listarUsuarios());
                    Toast.makeText(this, "Usuário removido", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
