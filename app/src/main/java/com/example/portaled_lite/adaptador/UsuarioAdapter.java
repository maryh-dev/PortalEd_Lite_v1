package com.example.portaled_lite.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portaled_lite.R;
import com.example.portaled_lite.modelo.Usuario;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UsuarioAdapter extends BaseAdapter<Usuario, UsuarioAdapter.UsuarioViewHolder> {

    private final OnAcaoAdminListener listener;

    public UsuarioAdapter(OnAcaoAdminListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaExibida.get(position);
        holder.tvNome.setText(usuario.getNome());
        holder.tvEmail.setText(usuario.getEmail());
        holder.tvTipo.setText(usuario.getTipo());

        if (usuario.isAdmin()) {
            holder.ivRemover.setVisibility(View.GONE);
        } else {
            holder.ivRemover.setVisibility(View.VISIBLE);
            holder.ivRemover.setOnClickListener(v -> {
                if (listener != null) listener.onExcluirClick(position);
            });
        }
    }

    @Override
    public void filtrarPorTexto(String texto) {
        if (texto == null || texto.isEmpty()) {
            listaExibida = new ArrayList<>(listaOriginal);
        } else {
            String query = texto.toLowerCase();
            listaExibida = listaOriginal.stream()
                    .filter(u -> u.getNome().toLowerCase().contains(query) || 
                                u.getEmail().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, ivRemover;
        TextView tvNome, tvEmail, tvTipo;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivAvatarUsuario);
            ivRemover = itemView.findViewById(R.id.ivRemoverUsuario);
            tvNome = itemView.findViewById(R.id.tvNomeUsuario);
            tvEmail = itemView.findViewById(R.id.tvEmailUsuario);
            tvTipo = itemView.findViewById(R.id.tvTipoUsuario);
        }
    }
}
