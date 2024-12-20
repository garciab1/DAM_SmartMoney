package sv.edu.catolica.dam_smartmoney;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import sv.edu.catolica.dam_smartmoney.Adapters.CategoriaAdapter;
import sv.edu.catolica.dam_smartmoney.Classes.Categoria;

public class Expenses extends AppCompatActivity {

    private PieChart pieChart;
    private ListView Lista;

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
        Lista = findViewById(R.id.ListaExpences);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);

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
                return true;
            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(this, Planning.class);
                startActivity(intent);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

        verDatos();
        CargarLista();
    }

    private void CargarLista() {
        DatabaseHelper db = new DatabaseHelper(Expenses.this);
        List<String> ListaExpenses = db.getCategoriaExpences();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ListaExpenses){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Obtiene la vista del elemento de la lista
                View view = super.getView(position, convertView, parent);

                // Cambia el color del texto
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(getResources().getColor(R.color.textcolor)); // Usa el color adaptativo

                return view;
            }
        };

        Lista.setAdapter(adapter);
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
                // Obtener la fecha actual
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Expenses.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        fecha_expence.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
                    }
                }, year, month, day);
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
                String tipoPago = getString(R.string.no_importante);
                String categoria = spinnerCategoria.getSelectedItem() != null ? spinnerCategoria.getSelectedItem().toString() : "";

                DatabaseHelper db = new DatabaseHelper(Expenses.this);
                double DineroTotal = db.get_saldo();

                if (nombreGasto.isEmpty()) {
                    mensajemanager(getString(R.string.por_favor_ingresa_un_nombre_para_el_gasto));
                    return;
                }

                double cantidad = 0;
                try {
                    cantidad = Double.parseDouble(cantidadField.getText().toString());
                } catch (NumberFormatException e) {
                    mensajemanager(getString(R.string.por_favor_ingresa_una_cantidad_v_lida));
                    return;
                }

                if (cantidad > DineroTotal){
                    mensajemanager(getString(R.string.no_hay_suficiente_saldo_para_procesar_la_transaccion));
                    return;
                }

                if (categoria.isEmpty()) {
                    mensajemanager(getString(R.string.la_categoria_no_puede_estar_vacia));
                    return;
                }

                // Convertir la fecha a un objeto Date
                Date fecha = null;
                try {
                    // Cambiar el formato "dd/MM/yyyy" y asegurarse de pasar 'fechaText' (la fecha introducida)
                    fecha = new java.text.SimpleDateFormat("dd/MM/yyyy").parse(fechaText);
                } catch (java.text.ParseException e) {
                    mensajemanager(getString(R.string.fecha_inv_lida));
                    return;
                }

                // Llamar al método crear_gasto con los datos obtenidos
                boolean crear = db.crear_gasto(fecha, nombreGasto, cantidad, tipoPago, categoria);
                if (crear){
                    nombre.setText("");
                    cantidadField.setText("");
                    fecha_expence.setText("dd/MM/yyyy");

                    mensajemanager(getString(R.string.gasto_creado_correctamente));
                    dialog.dismiss();
                    CargarLista();
                    verDatos();
                    //startActivity(new Intent(Expenses.this, Expenses.class));
                    //finish();
                } else {
                    mensajemanager(getString(R.string.ocurrio_un_error_al_crear_el_gasto));
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
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    //La grafica :O
    public void verDatos() {
        PieChart pieChart = findViewById(R.id.piechart);
        pieChart.clearChart();
        DatabaseHelper gastoRepository = new DatabaseHelper(Expenses.this);

        float totalGasto = gastoRepository.GetTotalGasto();
        Map<String, Float> gastoPorCategoria = gastoRepository.getGastoPorCategoria();
        List<Categoria> listaCategorias = new ArrayList<>();

        // Inicializar el mapa de colores
        int[] categoriaColores = new int[colors.length];

        if (totalGasto == 0) {
            pieChart.addPieSlice(new PieModel(getString(R.string.sin_gastos), 100, Color.GRAY));
            pieChart.startAnimation();
            return;
        }

        for (Map.Entry<String, Float> entry : gastoPorCategoria.entrySet()) {
            String categoria = entry.getKey();
            float cantidad = entry.getValue();
            float porcentaje = (cantidad / totalGasto) * 100;

            // Obtener el color para la categoría
            int color = getColorForCategory(categoria);
            pieChart.addPieSlice(new PieModel(categoria, porcentaje, color));

            // Agregar el color al mapa
            listaCategorias.add(new Categoria(null, categoria, null)); // Asumimos que aquí no tienes URI de imagen
            categoriaColores[listaCategorias.size() - 1] = color; // Guardar el color correspondiente
        }

        // Crear y asignar adaptador
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCategorias);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CategoriaAdapter adapter = new CategoriaAdapter(this, listaCategorias, categoriaColores);
        recyclerView.setAdapter(adapter);

    }

    // Colores predefinidos
    private int[] colors = {
            R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5,
            R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10,
            R.color.color11, R.color.color12, R.color.color13, R.color.color14, R.color.color15,
            R.color.color16, R.color.color17, R.color.color18, R.color.color19, R.color.color20,
            R.color.color21, R.color.color22, R.color.color23, R.color.color24, R.color.color25,
            R.color.color26, R.color.color27, R.color.color28, R.color.color29, R.color.color30
    };

    private int getColorForCategory(String categoryName) {
        int index = Math.abs(categoryName.hashCode()) % colors.length;
        return getResources().getColor(colors[index]);
    }

    public void VistaEliminar(View view) {
        Intent intent = new Intent(Expenses.this, eliminargasto.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}