package sv.edu.catolica.dam_smartmoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.navigation_search) {
                intent = new Intent(this, Expenses.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.navigation_profile) {
                return true;
            } else if (itemId == R.id.navigation_about) {
                intent = new Intent(this, about.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        CargarUltimosDatos();
    }

    private void CargarUltimosDatos(){
        DatabaseHelper db = new DatabaseHelper(this);
        Cursor cursor = db.getLastPlanning();

        if (cursor != null && cursor.moveToFirst()) {
            // Extraer los datos del último registro
            double gastoFijo = cursor.getDouble(cursor.getColumnIndexOrThrow("gastos_fijos"));
            double ahorro = cursor.getDouble(cursor.getColumnIndexOrThrow("ahorros"));
            double inversion = cursor.getDouble(cursor.getColumnIndexOrThrow("inversion"));
            double restante = cursor.getDouble(cursor.getColumnIndexOrThrow("restante"));

            // Mostrar los datos en los campos de texto
            txtGastoFijo.setText(String.format("%.2f", gastoFijo));
            txtAhorro.setText(String.format("%.2f", ahorro));
            txtInversion.setText(String.format("%.2f", inversion));
            txtTotalSobrante.setText(String.format(getString(R.string.total_disponible_2f), restante));

            // Mostrar los datos en el PieChart
            MostrarResultados(gastoFijo, ahorro, inversion, restante);
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @SuppressLint("DefaultLocale")
    private void DineroTotal() {
        DatabaseHelper db = new DatabaseHelper(Planning.this);
        double total = db.getsaldototal();
        txtTotal.setText(String.format("%.2f", total));
    }

    @SuppressLint("DefaultLocale")
    public void CalcularGastosdinero(View view) {
        DatabaseHelper db = new DatabaseHelper(Planning.this);

        // Obtener el dinero total desde el campo correspondiente
        double total = db.getsaldototal();

        // Obtener los valores de dinero ingresados
        double gastoFijo = txtGastoFijo.getText().toString().isEmpty() ? 0 : Double.parseDouble(txtGastoFijo.getText().toString());
        double ahorro = txtAhorro.getText().toString().isEmpty() ? 0 : Double.parseDouble(txtAhorro.getText().toString());
        double inversion = txtInversion.getText().toString().isEmpty() ? 0 : Double.parseDouble(txtInversion.getText().toString());

        if (gastoFijo + ahorro + inversion > total) {
            Toast.makeText(this, "Los valores ingresados no pueden superar el total", Toast.LENGTH_SHORT).show();
            return;
        }

        double restante = total - (gastoFijo + ahorro + inversion);

        // Guardar en la base de datos
        db.insertPlanning(gastoFijo, ahorro, inversion, restante);

        // Mostrar resultados
        MostrarResultados(gastoFijo, ahorro, inversion, restante);
    }

    private void MostrarResultados(double gastoFijo, double ahorro, double inversion, double restante) {
        // Mostrar en el PieChart
        PieChart pieChart = findViewById(R.id.piechart);
        pieChart.clearChart();

        pieChart.addPieSlice(new PieModel(getString(R.string.gasto_fijo), (float) gastoFijo, Color.parseColor("#FFA726")));
        pieChart.addPieSlice(new PieModel(getString(R.string.ahorro), (float) ahorro, Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(new PieModel(getString(R.string.inversion1), (float) inversion, Color.parseColor("#EF5350")));

        pieChart.startAnimation();

        // Mostrar en el ListView
        ListView listaPlanning = findViewById(R.id.lista_planning);
        String[] items = {
                String.format(getString(R.string.gasto_fijo_2f), gastoFijo),
                String.format(getString(R.string.ahorro_2f), ahorro),
                String.format(getString(R.string.inversi_n_2f), inversion),
        };

        txtTotalSobrante.setText(String.format(getString(R.string.total_disponible_2f), restante));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        listaPlanning.setAdapter(adapter);
    }

    public void PantallaUpdate(View view) {
        Intent intent = new Intent(Planning.this, updatesalario.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}