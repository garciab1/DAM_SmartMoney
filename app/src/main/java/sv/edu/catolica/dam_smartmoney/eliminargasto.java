package sv.edu.catolica.dam_smartmoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import sv.edu.catolica.dam_smartmoney.Adapters.GastoAdapter;
import sv.edu.catolica.dam_smartmoney.Classes.Gasto;

public class eliminargasto extends AppCompatActivity {

    private ListView listView;
    private GastoAdapter adapter;
    private DatabaseHelper dbHelper;
    private List<Gasto> listaGastos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_eliminargasto);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listViewGastos);
        dbHelper = new DatabaseHelper(this);

        // Obtener lista de gastos desde la base de datos
        DatabaseHelper db = new DatabaseHelper(eliminargasto.this);
        listaGastos = db.obtenerGastos();
        adapter = new GastoAdapter(this, listaGastos, dbHelper);
        listView.setAdapter(adapter);

    }

    public void VolverExpenses(View view) {
        Intent intent = new Intent(eliminargasto.this, Expenses.class);
        startActivity(intent);
    }
}