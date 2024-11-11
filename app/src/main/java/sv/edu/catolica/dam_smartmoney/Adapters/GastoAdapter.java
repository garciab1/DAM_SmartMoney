package sv.edu.catolica.dam_smartmoney.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

import sv.edu.catolica.dam_smartmoney.Classes.Gasto;
import sv.edu.catolica.dam_smartmoney.DatabaseHelper;
import sv.edu.catolica.dam_smartmoney.R;

public class GastoAdapter extends ArrayAdapter<Gasto> {
    private final Context context;
    private final List<Gasto> gastos;
    private final DatabaseHelper dbHelper;

    public GastoAdapter(Context context, List<Gasto> gastos, DatabaseHelper dbHelper) {
        super(context, 0, gastos);
        this.context = context;
        this.gastos = gastos;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_gasto, parent, false);
        }

        Gasto gasto = gastos.get(position);

        TextView nombreGasto = convertView.findViewById(R.id.nombre_gasto);
        TextView cantidadGasto = convertView.findViewById(R.id.cantidad_gasto);
        ImageButton btnEliminar = convertView.findViewById(R.id.btn_eliminar);

        nombreGasto.setText(gasto.getNombreGasto());
        cantidadGasto.setText(String.format("%.2f", gasto.getCantidad()));

        // Configurar el botón de eliminar con confirmación
        btnEliminar.setOnClickListener(view -> new AlertDialog.Builder(context)
                .setTitle(R.string.confirmar_eliminaci_n)
                .setMessage(R.string.est_s_seguro_de_que_deseas_eliminar_este_gasto)
                .setPositiveButton(R.string.s, (dialog, which) -> {
                    // Eliminar el gasto de la base de datos
                    dbHelper.eliminarGasto(gasto.getId());
                    gastos.remove(position); // Eliminar de la lista
                    notifyDataSetChanged(); // Actualizar la vista
                })
                .setNegativeButton(R.string.no, null)
                .show());

        return convertView;
    }
}
