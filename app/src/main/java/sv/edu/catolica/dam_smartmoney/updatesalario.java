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

public class updatesalario extends AppCompatActivity {

    EditText versalario, updatesalario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_updatesalario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        versalario = findViewById(R.id.txt_actualizar_salario_ver);
        updatesalario = findViewById(R.id.txt_actualizar_salario);

        getsalario();
    }

    private void getsalario(){
        DatabaseHelper db = new DatabaseHelper(updatesalario.this);
        Double salario = db.getsaldototal();

        versalario.setText(String.format("%.2f", salario));
    }

    public void VolverExpenses(View view) {
        Intent intent = new Intent(updatesalario.this, Planning.class);
        startActivity(intent);
        finish();
    }

    public void ActualizarElSalario(View view) {
        DatabaseHelper db = new DatabaseHelper(updatesalario.this);

        // Verificar si el campo está vacío
        if (updatesalario.getText().toString().isEmpty()) {
            mensajes("El campo del salario no puede estar vacío");
            return;
        }

        Double nuevosalario = Double.valueOf(updatesalario.getText().toString());

        // Verificar que el valor no sea menor o igual a 0
        if (nuevosalario <= 0) {
            mensajes("El salario no puede ser 0 o menor de 0");
            return;
        }

        boolean salarionuevo = db.UpdateSalario(nuevosalario);
        if (salarionuevo) {
            mensajes("Salario modificado");
            getsalario();
        } else {
            mensajes("No se pudo actualizar el salario");
        }
    }


    public void mensajes(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}