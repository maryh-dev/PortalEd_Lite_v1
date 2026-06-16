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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CursoAdapter extends BaseAdapter<Curso, CursoAdapter.CursoViewHolder> {

    private final OnItemClickListener listener;

    public CursoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CursoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso_recomendado, parent, false);
        return new CursoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoViewHolder holder, int position) {
        Curso curso = listaExibida.get(position);
        holder.tvTitulo.setText(curso.getTitulo());
        holder.tvProfessor.setText(curso.getCategoria());

        // Carregar imagem baseada na categoria ou na string armazenada no modelo (imagemUrl)
        String capa = curso.getImagemUrl();
        if (capa != null && capa.toLowerCase().contains("figma")) {
            holder.ivCapa.setImageResource(R.drawable.ic_colors);
        } else if (capa != null && (capa.toLowerCase().contains("java") || capa.toLowerCase().contains("android") || capa.toLowerCase().contains("backend") || capa.toLowerCase().contains("laptop"))) {
            holder.ivCapa.setImageResource(R.drawable.ic_laptop);
        } else if (curso.getCategoria().equalsIgnoreCase("Design")) {
            holder.ivCapa.setImageResource(R.drawable.ic_colors);
        } else {
            holder.ivCapa.setImageResource(R.drawable.ic_laptop);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    @Override
    public void filtrarPorTexto(String texto) {
        if (texto == null || texto.isEmpty()) {
            listaExibida = new ArrayList<>(listaOriginal);
        } else {
            String query = texto.toLowerCase();
            listaExibida = listaOriginal.stream()
                    .filter(c -> c.getTitulo().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }
        notifyDataSetChanged();
    }

    static class CursoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCapa;
        TextView tvTitulo, tvProfessor, tvAvaliacao;

        public CursoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCapa = itemView.findViewById(R.id.ivCapaCurso);
            tvTitulo = itemView.findViewById(R.id.tvTituloCurso);
            tvProfessor = itemView.findViewById(R.id.tvProfessorCurso);
            tvAvaliacao = itemView.findViewById(R.id.tvAvaliacaoCurso);
        }
    }
}
