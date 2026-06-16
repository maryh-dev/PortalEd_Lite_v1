package com.example.portaled_lite.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portaled_lite.R;
import com.example.portaled_lite.modelo.Aula;
import com.example.portaled_lite.utilitarios.GerenciadorDados;
import com.example.portaled_lite.utilitarios.GerenciadorSessao;

public class AulaAdapter extends BaseAdapter<Aula, AulaAdapter.AulaViewHolder> {

    private final OnItemClickListener listener;

    public AulaAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AulaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aula, parent, false);
        return new AulaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AulaViewHolder holder, int position) {
        Aula aula = listaExibida.get(position);
        holder.tvTitulo.setText(aula.getTitulo());
        
        String userId = GerenciadorSessao.getInstance().getUsuarioLogado().getId();
        boolean concluida = GerenciadorDados.getInstance().estaAulaConcluida(userId, aula.getId());
        
        if (concluida) {
            holder.ivStatus.setImageResource(android.R.drawable.checkbox_on_background);
        } else {
            holder.ivStatus.setImageResource(android.R.drawable.checkbox_off_background);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    static class AulaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStatus, ivPlay;
        TextView tvTitulo, tvDuracao;

        public AulaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStatus = itemView.findViewById(R.id.ivStatusAula);
            ivPlay = itemView.findViewById(R.id.ivPlayAula);
            tvTitulo = itemView.findViewById(R.id.tvTituloAula);
            tvDuracao = itemView.findViewById(R.id.tvDuracaoAula);
        }
    }
}
