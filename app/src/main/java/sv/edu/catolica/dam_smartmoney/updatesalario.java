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

    EditText versalario, updatesalario, salario_actual;

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
        salario_actual = findViewById(R.id.txt_salario_actual);

        getsalario();
        getGastos();
    }

    private void getsalario(){
        DatabaseHelper db = new DatabaseHelper(updatesalario.this);
        Double salario = db.getsaldototal();

        versalario.setText(String.format("%.2f", salario));
    }

    private void getGastos(){
        DatabaseHelper db = new DatabaseHelper(this);
        Double dinero = db.get_saldo();
        salario_actual.setText(String.format("%.2f", dinero));
    }

    public void VolverExpenses(View view) {
        Intent intent = new Intent(updatesalario.this, Planning.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void SumarElSalario(View view) {
        DatabaseHelper db = new DatabaseHelper(updatesalario.this);

        // Verificar si el campo está vacío
        if (updatesalario.getText().toString().isEmpty()) {
            mensajes(getString(R.string.el_campo_del_salario_no_puede_estar_vac_o));
            return;
        }

        Double nuevosalario = Double.valueOf(updatesalario.getText().toString());

        // Verificar que el valor no sea menor o igual a 0
        if (nuevosalario <= 0) {
            mensajes(getString(R.string.el_salario_no_puede_ser_0_o_menor_de_0));
            return;
        }

        boolean salarionuevo = db.UpdateSalario(nuevosalario, "SUMA");

        if (salarionuevo) {
            mensajes(getString(R.string.salario_modificado));
            getsalario();
            getGastos();
        } else {
            mensajes(getString(R.string.no_se_pudo_actualizar_el_salario));
        }
    }


    public void RestarElSalario(View view) {
        DatabaseHelper db = new DatabaseHelper(updatesalario.this);

        // Verificar si el campo está vacío
        if (updatesalario.getText().toString().isEmpty()) {
            mensajes(getString(R.string.el_campo_del_salario_no_puede_estar_vac_o));
            return;
        }

        Double nuevosalario = Double.valueOf(updatesalario.getText().toString());

        // Verificar que el valor no sea menor o igual a 0
        if (nuevosalario <= 0) {
            mensajes(getString(R.string.el_salario_no_puede_ser_0_o_menor_de_0));
            return;
        }

        Double dineroActual = db.get_saldo();

        // Verificar si el monto a restar es mayor que el saldo actual
        if (nuevosalario > dineroActual) {
            mensajes(getString(R.string.esta_restando_m_s_de_lo_que_tiene_actualmente));
            return;
        }

        boolean salarionuevo = db.UpdateSalario(nuevosalario, "RESTA");

        if (salarionuevo) {
            mensajes(getString(R.string.salario_modificado));
            getsalario();
            getGastos();
        } else {
            mensajes(getString(R.string.no_se_pudo_actualizar_el_salario));
        }
    }

    public void mensajes(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(updatesalario.this, Planning.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}