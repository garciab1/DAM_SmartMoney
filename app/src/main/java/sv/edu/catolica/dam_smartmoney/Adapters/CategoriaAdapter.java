package sv.edu.catolica.dam_smartmoney.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

import sv.edu.catolica.dam_smartmoney.Classes.Categoria;
import sv.edu.catolica.dam_smartmoney.R;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder> {
    private List<Categoria> categorias;
    private Context context;

    public CategoriaAdapter(Context context, List<Categoria> categorias) {
        this.context = context;
        this.categorias = categorias;
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
    }

    @Override
    public int getItemCount() {
        return categorias.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {
        TextView nombreTextView;
        ImageView imagenImageView;

        public CategoriaViewHolder(View itemView) {
            super(itemView);
            nombreTextView = itemView.findViewById(R.id.nombreTextView);
            imagenImageView = itemView.findViewById(R.id.imagenImageView);
        }
    }
}