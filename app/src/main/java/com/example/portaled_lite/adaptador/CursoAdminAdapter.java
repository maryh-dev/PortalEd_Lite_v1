package com.example.portaled_lite.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portaled_lite.R;
import com.example.portaled_lite.modelo.Curso;

public class CursoAdminAdapter extends BaseAdapter<Curso, CursoAdminAdapter.AdminViewHolder> {

    private final OnAcaoAdminListener listener;

    public CursoAdminAdapter(OnAcaoAdminListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Curso curso = listaExibida.get(position);
        holder.tvTitulo.setText(curso.getTitulo());
        holder.tvCategoria.setText(curso.getCategoria());

        holder.ivEditar.setOnClickListener(v -> {
            if (listener != null) listener.onEditarClick(position);
        });

        holder.ivExcluir.setOnClickListener(v -> {
            if (listener != null) listener.onExcluirClick(position);
        });
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcone, ivEditar, ivExcluir;
        TextView tvTitulo, tvCategoria;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcone = itemView.findViewById(R.id.ivIconeCursoAdmin);
            ivEditar = itemView.findViewById(R.id.ivEditarCurso);
            ivExcluir = itemView.findViewById(R.id.ivExcluirCurso);
            tvTitulo = itemView.findViewById(R.id.tvTituloCursoAdmin);
            tvCategoria = itemView.findViewById(R.id.tvCategoriaCursoAdmin);
        }
    }
}
