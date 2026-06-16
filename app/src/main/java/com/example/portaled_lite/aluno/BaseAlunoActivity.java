package com.example.portaled_lite.aluno;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import com.example.portaled_lite.R;
import com.example.portaled_lite.perfil.PerfilConfigActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseAlunoActivity extends AppCompatActivity {

    protected void configurarBottomNav(BottomNavigationView nav, int itemSelecionado) {
        nav.setSelectedItemId(itemSelecionado);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == itemSelecionado) return true;

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (id == R.id.nav_cursos) {
                startActivity(new Intent(this, CursosActivity.class));
                return true;
            } else if (id == R.id.nav_favoritos) {
                Intent intent = new Intent(this, CursosActivity.class);
                intent.putExtra("EXTRA_FAVORITOS", true);
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilConfigActivity.class));
                return true;
            }
            // Favoritos pode ser implementado em CursosActivity com filtro ou em uma nova Activity
            return false;
        });
    }
}
