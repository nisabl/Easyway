package Adapters;


import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.easyway.ComplexProductView;
import com.example.easyway.ProductsMenu;
import com.example.easyway.R;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>{

    static List<Product> products;


    public ProductsAdapter(List<Product> products) {
        this.products = products;
    }

    //Layout con el que se rellenara el RecyclerView
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_list_template, null,false);
        ProductsViewHolder holder = new ProductsViewHolder(v);
        return holder;
    }

    //Lo que queramos hacer en el RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        Product product = products.get(position);

        holder.nombre.setText(product.getName());
        holder.precio.setText(String.valueOf(product.getPrice()) + "€");

        Uri imgUri = Uri.parse(product.getImg());

        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(holder.img.getContext())
                .load(imgUri)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.img);

    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    //Clase para configurar los items del RecyclerView
    public static class ProductsViewHolder extends RecyclerView.ViewHolder{

        TextView nombre, precio;
        ImageView img;
        Button productButton;

        ProgressBar progressBar;

        public ProductsViewHolder(View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.productTitle);
            precio = itemView.findViewById(R.id.productPrice);
            img = itemView.findViewById(R.id.productImage);
            productButton = itemView.findViewById(R.id.productButton);

            progressBar = itemView.findViewById(R.id.progressBar);

            //A partir de aqui se establece lo que pasara cuando se pulse encima de un producto
            productButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Obtenemos la posicion del item donde pulsa el usuario
                    int position = getAdapterPosition();

                    //Obtenemos el producto correspondiente a la posicion pulsada
                    Product product = products.get(position);

                    // Creamos un objeto Bundle para pasar los datos a la pantalla del producto para ver sus datos
                    Bundle args = new Bundle();
                    args.putString("productName", product.getName());
                    args.putDouble("productPrice", product.getPrice());
                    args.putString("productImg", product.getImg());
                    args.putString("productID", product.getId());
                    args.putString("productDesc", product.getDescripcion());
                    args.putDouble("productStock", product.getStock());

                    //Establecemos el fragment de destino
                    Fragment destinationFragment = new ComplexProductView();
                    destinationFragment.setArguments(args);

                    //Reemplazamos el fragmento actual con el fragmento de destino
                    FragmentManager fragmentManager = ((FragmentActivity) itemView.getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, destinationFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            });

        }
    }

}
