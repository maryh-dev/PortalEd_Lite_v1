package com.example.portaled_lite.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portaled_lite.R;
import com.example.portaled_lite.modelo.Matricula;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.modelo.Curso;

public class CursoProgressoAdapter extends BaseAdapter<Matricula, CursoProgressoAdapter.ProgressoViewHolder> {

    private final OnItemClickListener listener;

    public CursoProgressoAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProgressoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso_progresso, parent, false);
        return new ProgressoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgressoViewHolder holder, int position) {
        Matricula matricula = listaExibida.get(position);
        Curso curso = GerenciadorDados.getInstance().buscarCursoPorId(matricula.getCursoId());
        
        if (curso != null) {
            holder.tvNome.setText(curso.getTitulo());
            holder.tvInfo.setText(matricula.getProgresso() + "% concluído");
            holder.pbProgresso.setProgress(matricula.getProgresso());
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    static class ProgressoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcone;
        TextView tvNome, tvInfo;
        ProgressBar pbProgresso;

        public ProgressoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcone = itemView.findViewById(R.id.ivIconeCurso);
            tvNome = itemView.findViewById(R.id.tvNomeCurso);
            tvInfo = itemView.findViewById(R.id.tvInfoCurso);
            pbProgresso = itemView.findViewById(R.id.pbProgressoCurso);
        }
    }
}
