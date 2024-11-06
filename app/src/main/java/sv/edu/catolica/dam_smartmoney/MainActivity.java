package sv.edu.catolica.dam_smartmoney;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.LayoutInflaterCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import android.widget.DatePicker;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;
    private TextView textViewFecha;
    private TextView saldo_total;
    public ListView Lista_pagos;
    public CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        Lista_pagos = findViewById(R.id.list_pagos);
        calendarView = findViewById(R.id.calendarViewMain);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                // Formatear la fecha seleccionada como "yyyy-MM-dd"
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String fechaSeleccionada = dateFormat.format(calendar.getTime());

                // Consultar si hay un evento en la fecha seleccionada
                DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                String evento = db.ObtenerEventoFecha(fechaSeleccionada);
                if (evento != null) {
                    // Si hay evento, abrir la actividad de detalle
                    Intent intent = new Intent(MainActivity.this, activity_detalle_evento.class);
                    intent.putExtra("fecha", fechaSeleccionada);
                    intent.putExtra("evento", evento);
                    startActivity(intent);
                } else {
                    // Si no hay evento, mostrar Toast
                    int duracion = 800;
                    Toast toast = Toast.makeText(MainActivity.this, "Nada en este día", Toast.LENGTH_SHORT);
                    toast.show();
                    new Handler().postDelayed(toast::cancel, duracion);
                }
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (Objects.requireNonNull(item.getTitle()).toString()) {
                case "Home":
                    return true;
                case "Search":
                    startActivity(new Intent(this, Expenses.class));
                    return true;
                case "Profile":
                    startActivity(new Intent(this, Planning.class));
                    return true;
                case "About":
                    startActivity(new Intent(this, about.class));
                    return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        saldo_total = findViewById(R.id.textView16);
        get_money();
        get_important_expences();
        FechaActual();
    }

    private void FechaActual(){
        long fechaactual = System.currentTimeMillis();
        calendarView.setDate(fechaactual, false, true);
    }

    @SuppressLint("DefaultLocale")
    private void get_money(){
        DatabaseHelper db = new DatabaseHelper(this);
        Double dinero = db.get_saldo();
        saldo_total.setText(String.format("%.2f", dinero));
    }

    public void ShowCategoryCreate(View v){
        LayoutInflater inflater = getLayoutInflater();
        View vista = inflater.inflate(R.layout.add_categories, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(vista);

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        //botones del dialogo personalizado
        Button btnCancelar = vista.findViewById(R.id.btn_cancelar);
        Button btnCreate = vista.findViewById(R.id.btn_aceptar);
        Button btnImagen = vista.findViewById(R.id.btn_add_image);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickimage();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db = new DatabaseHelper(MainActivity.this);
                EditText btncategoria = vista.findViewById(R.id.txt_categoria_nombre);
                EditText btntipo = vista.findViewById(R.id.txt_tipo_gasto);

                String categoria = btncategoria.getText().toString();
                String tipo = btntipo.getText().toString();
                String uri = (selectedImageUri != null) ? selectedImageUri.toString() : null;

                boolean verificacion_crear_cat = db.crear_categorias(categoria, tipo, uri);

                if (verificacion_crear_cat){
                    mensajemanager("La creacion de la categoria a sido exitosa");
                    btncategoria.setText("");
                    btntipo.setText("");
                    selectedImageUri = null;
                }else{
                    mensajemanager("Ocurrio un error al mostrar la categoria");
                    selectedImageUri = null;
                }
            }
        });
    }

    public void show_important_expences(View v){
        LayoutInflater inflater = getLayoutInflater();
        View vista = inflater.inflate(R.layout.add_important_expenses, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(vista);

        // Crear el dialogo
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        Button btn_cancel = vista.findViewById(R.id.btn_important_expences_cancel);
        Button btn_ok = vista.findViewById(R.id.btn_important_expences_ok);
        textViewFecha = vista.findViewById(R.id.textView_fecha);

        textViewFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener la fecha actual
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        textViewFecha.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
                    }
                }, year, month, day);
                dialog.show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper db = new DatabaseHelper(MainActivity.this);

                TextView nombre_add = vista.findViewById(R.id.txt_nombre_important_expence);
                EditText cantidadField = vista.findViewById(R.id.txt_cantidad_important_expence);

                String nombreGasto = nombre_add.getText().toString();
                String fechaText = textViewFecha.getText().toString();
                String tipoPago = "Importante"; // Aquí lo ajustas si tienes otro valor en mente
                double SaldoTotal = db.get_saldo();

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

                if (cantidad > SaldoTotal){
                    mensajemanager("No hay suficiente saldo para procesar la transaccion");
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
                boolean crear = db.crear_gasto(fecha, nombreGasto, cantidad, tipoPago, "Importante");
                if (crear){
                    nombre_add.setText("");
                    cantidadField.setText("");
                    textViewFecha.setText("dd/MM/yyyy");
                    mensajemanager("Gasto creado correctamente");

                    get_important_expences();
                    get_money();
                } else {
                    mensajemanager("Ocurrio un error al crear el gasto");
                }
            }
        });
    }

    private void get_important_expences(){
        DatabaseHelper db = new DatabaseHelper(MainActivity.this);
        List<String> pagosImportantes = db.get_pagos_importantes();

        // Crear un ArrayAdapter para mostrar los pagos en la ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pagosImportantes);
        Lista_pagos.setAdapter(adapter);
    }

    //Metodos aparte
    private void pickimage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.selectedImageUri = data.getData();  // Actualiza la variable de nivel de clase
            saveImageToAppFolder(selectedImageUri);  // Guarda la imagen en la carpeta de la app
        }
    }

    private void saveImageToAppFolder(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            // Obtener la carpeta privada de la aplicación
            File appFolder = new File(getFilesDir(), "images");
            if (!appFolder.exists()) {
                appFolder.mkdirs();
            }

            // Crear un archivo único para guardar la imagen
            File imageFile = new File(appFolder, "selected_image_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(imageFile);

            // Copiar los datos del archivo de la URI seleccionada al archivo de destino
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Guardar la URI en la variable de clase
            this.selectedImageUri = Uri.fromFile(imageFile);  // Actualiza selectedImageUri
            Toast.makeText(this, "Imagen guardada en: " + selectedImageUri.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void mensajemanager(String mensaje){
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}