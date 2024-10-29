package sv.edu.catolica.dam_smartmoney;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class welcome extends AppCompatActivity {

    private EditText textonombre, textomonto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.welcome), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textonombre = findViewById(R.id.editTextText3);
        textomonto = findViewById(R.id.editTextText4);

        // Verificar si ya existe un usuario antes de crear uno nuevo
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        if (!dbHelper.verificarnuevousuario()) {
            crearusuario();
        } else {
            mensajes("Ya existe un usuario registrado.");
        }
    }

    public void crear(View v){
        crearusuario();
    }

    private void crearusuario() {
        String nombre = textonombre.getText().toString().trim();
        String montoString = textomonto.getText().toString().trim();

        // Validación de entradas
        if (nombre.isEmpty()) {
            mensajes("El campo de nombre está vacío");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoString);
        } catch (NumberFormatException e) {
            mensajes("Monto no válido");
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);
        boolean creacion = db.crear_usuario(nombre, monto);

        if (creacion) {
            mensajes("Creación de datos completada");
            Intent redirecion = new Intent(welcome.this, MainActivity.class);
            startActivity(redirecion);
            finish();
        } else {
            mensajes("Ocurrió un error al crear el usuario");
        }
    }

    private void mensajes(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
    }
}
