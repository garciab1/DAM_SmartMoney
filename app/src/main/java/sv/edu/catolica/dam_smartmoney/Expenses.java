package sv.edu.catolica.dam_smartmoney;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Expenses extends AppCompatActivity {

    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expenses);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (Objects.requireNonNull(item.getTitle()).toString()) {
                case "Home":
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
                case "Search":
                    return true;
                case "Profile":
                    startActivity(new Intent(this, Planning.class));
                    return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
        verDatos();
    }

    public void Crear_Gasto(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View vista = inflater.inflate(R.layout.add_expense, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(vista);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        Button btnCancelar = vista.findViewById(R.id.btn_cancel_expence);
        Button btnCrearExpence = vista.findViewById(R.id.btn_ok_expence);
        TextView fecha_expence = vista.findViewById(R.id.fecha_expence);
        Spinner spinnerCategoria = vista.findViewById(R.id.spinner_categoria);

        // Obtener las categorías desde la base de datos
        DatabaseHelper db = new DatabaseHelper(this);
        List<String> categorias = db.getCategorias();

        // Crear un adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);

        fecha_expence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(Expenses.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        fecha_expence.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
                    }
                }, 2024, 11, 1);
                dialog.show();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btnCrearExpence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nombre = vista.findViewById(R.id.txt_expence_name);
                EditText cantidadField = vista.findViewById(R.id.txt_cantidad_expence);

                String nombreGasto = nombre.getText().toString();
                String fechaText = fecha_expence.getText().toString();
                String tipoPago = "No importante";
                String categoria = spinnerCategoria.getSelectedItem().toString();


                if (nombreGasto.isEmpty()) {
                    mensajemanager("Por favor, ingresa un nombre para el gasto");
                    return;
                }

                double cantidad = 0;
                try {
                    cantidad = Double.parseDouble(cantidadField.getText().toString());
                } catch (NumberFormatException e) {
                    mensajemanager("Por favor, ingresa una cantidad válida");
                    return;
                }

                // Convertir la fecha a un objeto Date
                Date fecha = null;
                try {
                    // Cambiar el formato "dd/MM/yyyy" y asegurarse de pasar 'fechaText' (la fecha introducida)
                    fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(fechaText);
                } catch (java.text.ParseException e) {
                    mensajemanager("Fecha inválida");
                    return;
                }

                // Llamar al método crear_gasto con los datos obtenidos
                DatabaseHelper db = new DatabaseHelper(Expenses.this);
                boolean crear = db.crear_gasto(fecha, nombreGasto, cantidad, tipoPago, categoria);
                if (crear){
                    nombre.setText("");
                    cantidadField.setText("");
                    fecha_expence.setText("dd/MM/yyyy");
                    mensajemanager("Gasto creado correctamente");
                } else {
                    mensajemanager("Ocurrio un error al crear el gasto");
                }

            }
        });
    }

    private void mensajemanager(String mesage){
        Toast.makeText(this, mesage, Toast.LENGTH_SHORT).show();
    }

    public void AbrirCategorias(View view) {
        Intent intent = new Intent(Expenses.this, VerCategorias.class);
        startActivity(intent);
    }

    //La grafica :O
    public void verDatos(){
        PieChart pieChart = findViewById(R.id.piechart);
        DatabaseHelper gastoRepository = new DatabaseHelper(Expenses.this);
        // Obtiene el gasto total y por categoría
        float totalGasto = gastoRepository.GetTotalGasto();
        Map<String, Float> gastoPorCategoria = gastoRepository.getGastoPorCategoria();

        // Si el gasto total es cero, evita la división por cero
        if (totalGasto == 0) {
            // Añadir una sección con 100% asignado a "Sin Gastos"
            pieChart.addPieSlice(new PieModel("Sin Gastos", 100, Color.GRAY));
            pieChart.startAnimation();
            return;
        }

        // Agrega cada categoría al gráfico de pastel
        for (Map.Entry<String, Float> entry : gastoPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            float cantidad = entry.getValue();
            float porcentaje = (cantidad / totalGasto) * 100;

            // Agrega la entrada al gráfico con un color específico
            pieChart.addPieSlice(new PieModel(categoria, porcentaje, getColorForCategory(categoria)));
        }

        // Inicia la animación del gráfico de pastel
        pieChart.startAnimation();
    }


    private int[] colors = {
            R.color.color1,
            R.color.color2,
            R.color.color3,
            R.color.color4,
            R.color.color5
    };

    private int getColorForCategory(String categoryName) {
        int index = Math.abs(categoryName.hashCode()) % colors.length;
        return getResources().getColor(colors[index]);
    }
}