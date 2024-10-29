package sv.edu.catolica.dam_smartmoney;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Intro extends AppCompatActivity {

    boolean Boton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.layout_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Handler manejador = new Handler();

        manejador.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!Boton){
                    DatabaseHelper db = new DatabaseHelper(Intro.this);
                    boolean verificacion = db.verificarnuevousuario();
                    if (verificacion){
                        Intent ventana1 = new Intent(Intro.this,MainActivity.class);
                        startActivity(ventana1);
                        finish();
                    } else {
                        Intent ventana_crear_monto = new Intent(Intro.this, welcome.class);
                        startActivity(ventana_crear_monto);
                        finish();
                    }
                }

            }
        }, 3000);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Boton = true;
    }
}
