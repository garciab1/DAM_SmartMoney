package sv.edu.catolica.dam_smartmoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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
        adapter = new GastoAdapter(this, listaGastos, dbHelper){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Obtiene la vista del elemento de la lista
                View view = super.getView(position, convertView, parent);

                // Cambia el color del texto
                TextView textView = (TextView) view.findViewById(R.id.nombre_gasto);
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.textcolor));

                TextView textView2 = (TextView) view.findViewById(R.id.cantidad_gasto);
                textView2.setTextColor(ContextCompat.getColor(getContext(), R.color.textcolor));

                return view;
            }
        };
        listView.setAdapter(adapter);

    }

    public void VolverExpenses(View view) {
        Intent intent = new Intent(eliminargasto.this, Expenses.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(eliminargasto.this, Expenses.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}