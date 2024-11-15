package sv.edu.catolica.dam_smartmoney.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import sv.edu.catolica.dam_smartmoney.Classes.Categoria;
import sv.edu.catolica.dam_smartmoney.R;

public class VerCategoriaAdapter extends RecyclerView.Adapter<VerCategoriaAdapter.CategoriaViewHolder>  {

    private List<Categoria> categorias;
    private Context context;
    private OnCategoriaDeleteListener deleteListener;

    public VerCategoriaAdapter(Context context, List<Categoria> categorias) {
        this.context = context;
        this.categorias = categorias;
    }

    public VerCategoriaAdapter(Context context, List<Categoria> categorias, OnCategoriaDeleteListener deleteListener) {
        this.context = context;
        this.categorias = categorias;
        this.deleteListener = deleteListener;
    }

    public interface OnCategoriaDeleteListener {
        void onDelete(Categoria categoria);
    }

    @Override
    public CategoriaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categoria, parent, false);
        return new CategoriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoriaViewHolder holder, int position) {
        Categoria categoria = categorias.get(position);
        holder.nombreTextView.setText(categoria.getNombre());

        String imagenURI = categoria.getImagenURI();
        // Verificar si el URI de la imagen está vacío o es null
        if (imagenURI != null && !imagenURI.isEmpty()) {
            Glide.with(context).load(imagenURI).into(holder.imagenImageView); // Cargar la imagen desde el URI
        } else {
            holder.imagenImageView.setImageResource(R.drawable.user); // Imagen predeterminada en drawable
        }

        // Configurar el botón de eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(categoria);
                Log.d("VerCategoriaAdapter", "Botón eliminar presionado para categoría: " + categoria.getNombre());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageView imagenImageView;
        ImageButton btnEliminar;

        public CategoriaViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            imagenImageView = itemView.findViewById(R.id.imagenImageView);
            btnEliminar = itemView.findViewById(R.id.btn_delete_category);
        }
    }
}
