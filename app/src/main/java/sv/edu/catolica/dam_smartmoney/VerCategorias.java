package sv.edu.catolica.dam_smartmoney;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sv.edu.catolica.dam_smartmoney.Adapters.CategoriaAdapter;
import sv.edu.catolica.dam_smartmoney.Adapters.VerCategoriaAdapter;
import sv.edu.catolica.dam_smartmoney.Classes.Categoria;

public class VerCategorias extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_categorias);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CargarCategorias();
    }

    public void VolverExpenses(View view) {
        Intent intent = new Intent(VerCategorias.this, Expenses.class);
        startActivity(intent);
        finish();
    }

    private void CargarCategorias(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        DatabaseHelper database = new DatabaseHelper(this);
        List<Categoria> categorias = database.getCategoriasIMG();
        VerCategoriaAdapter adapter = new VerCategoriaAdapter(this, categorias, this::onDelete);
        recyclerView.setAdapter(adapter);
    }

    public void onDelete(Categoria categoria){
        String titulo = getString(R.string.confirmar_eliminacion_codigo);
        String mensaje = getString(R.string.confirmar_msg_codigo);

        new AlertDialog.Builder(this)
            .setTitle(titulo).setMessage(mensaje)
            .setPositiveButton(getString(R.string.btn_eliminar_codigo), (dialog, which) -> {
                // Eliminar la categoría y actualizar la lista solo si el usuario confirma
                DatabaseHelper database = new DatabaseHelper(this);
                database.deleteCategoria(categoria.getId()); // Método que elimina la categoría en DatabaseHelper
                CargarCategorias();
            }).setNegativeButton(getString(R.string.btn_cancelar_codigo), (dialog, which) -> {
               dialog.dismiss();
            }).show();
    }
}