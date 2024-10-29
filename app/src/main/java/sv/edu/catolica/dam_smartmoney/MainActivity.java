package sv.edu.catolica.dam_smartmoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.LayoutInflaterCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri = null;
    private TextView saldo_total;

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
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        saldo_total = findViewById(R.id.textView16);
        get_money();
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
                    selectedImageUri = null;
                }else{
                    mensajemanager("Ocurrio un error al mostrar la categoria");
                    selectedImageUri = null;
                }
            }
        });
    }

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