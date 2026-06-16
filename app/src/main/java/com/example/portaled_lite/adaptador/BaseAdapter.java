package com.example.portaled_lite.adaptador;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> listaOriginal = new ArrayList<>();
    protected List<T> listaExibida = new ArrayList<>();

    public void atualizarLista(List<T> novaLista) {
        this.listaOriginal = new ArrayList<>(novaLista);
        this.listaExibida = new ArrayList<>(novaLista);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listaExibida.size();
    }

    public void filtrarPorTexto(String texto) {
        // A ser sobrescrito pelos filhos se necessário
    }
}
