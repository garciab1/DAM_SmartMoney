package sv.edu.catolica.dam_smartmoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Objects;

public class Planning extends AppCompatActivity {

    private EditText txtTotal, txtGastoFijo, txtAhorro, txtInversion, txtTotalSobrante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_planning);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Intent intent;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_search) {
                intent = new Intent(this, Expenses.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(this, Planning.class);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.navigation_about) {
                intent = new Intent(this, about.class);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });

        txtTotal = findViewById(R.id.txt_planning_dinero_total);
        txtGastoFijo = findViewById(R.id.txt_planning_gastos_fijos);
        txtAhorro = findViewById(R.id.txt_planning_ahorros);
        txtInversion = findViewById(R.id.txt_planning_inversion);
        txtTotalSobrante = findViewById(R.id.TotalSobrante);
        DineroTotal();
    }

    @SuppressLint("DefaultLocale")
    private void DineroTotal() {
        DatabaseHelper db = new DatabaseHelper(Planning.this);
        double total = db.getsaldototal();
        txtTotal.setText(String.format("%.2f", total));
    }

    public void CalcularGastos(View view) {
        DatabaseHelper db = new DatabaseHelper(Planning.this);

        // Obtener el dinero total desde el campo correspondiente
        double total = db.getsaldototal();

        // Obtener los valores de porcentaje ingresados en cada campo
        int gastoFijoPercent = txtGastoFijo.getText().toString().isEmpty() ? 0 : Integer.parseInt(txtGastoFijo.getText().toString());
        int ahorroPercent = txtAhorro.getText().toString().isEmpty() ? 0 : Integer.parseInt(txtAhorro.getText().toString());
        int inversionPercent = txtInversion.getText().toString().isEmpty() ? 0 : Integer.parseInt(txtInversion.getText().toString());

        // Calcular el valor real de cada categoría
        double gastoFijo = total * gastoFijoPercent / 100;
        double ahorro = total * ahorroPercent / 100;
        double inversion = total * inversionPercent / 100;

        // Verificar que el porcentaje total no exceda el 100%
        int totalPercentage = gastoFijoPercent + ahorroPercent + inversionPercent;
        if (totalPercentage > 100) {
            Toast.makeText(this, "El total de los porcentajes no puede exceder el 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar resultados en el PieChart
        PieChart pieChart = findViewById(R.id.piechart);
        pieChart.clearChart();

        pieChart.addPieSlice(new PieModel("Gasto Fijo", (float) gastoFijo, Color.parseColor("#FFA726")));
        pieChart.addPieSlice(new PieModel("Ahorro", (float) ahorro, Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(new PieModel("Inversión", (float) inversion, Color.parseColor("#EF5350")));

        pieChart.startAnimation();

        // Mostrar resultados en la lista
        ListView listaPlanning = findViewById(R.id.lista_planning);
        String[] items = {
                String.format("Gasto Fijo: %.2f", gastoFijo),
                String.format("Ahorro: %.2f", ahorro),
                String.format("Inversión: %.2f", inversion),
        };

        txtTotalSobrante.setText(String.format("Total Disponible: %.2f", total - (gastoFijo + ahorro + inversion)));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listaPlanning.setAdapter(adapter);
    }

    public void PantallaUpdate(View view) {
        Intent intent = new Intent(Planning.this, updatesalario.class);
        startActivity(intent);
    }
}