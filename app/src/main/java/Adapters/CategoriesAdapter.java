package Adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.easyway.ProductsMenu;
import com.example.easyway.R;

import org.jetbrains.annotations.Nullable;

import java.util.List;


//Clase que sirve para rellenar la lista con los diferentes elementos
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>{

    static List<Category> categories;

    public CategoriesAdapter(List<Category> categories){
        this.categories = categories;
    }

    //Infla cada elemento que se mostrará en la lista
    @Override
    public CategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_example, null, false);
        CategoriesViewHolder holder = new CategoriesViewHolder(v);
        return holder;
    }

    //Método que sirve para configurar lo que se mostrara en pantalla por cada item
    @Override
    public void onBindViewHolder(@NonNull CategoriesViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.nombre.setText(category.getName());

        Uri imgUri = Uri.parse(category.getImg());

        // Cargar la imagen en el ImageView utilizando Glide
        holder.progressBar.setVisibility(View.VISIBLE);
        Glide.with(holder.img.getContext())
                .load(imgUri)
                .centerCrop()
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

    //Devuelve el numero de filas del recyclerView
    @Override
    public int getItemCount() {
        return categories.size();
    }

    //Clase para configurar los elementos dentro de los items de la lista
    public static class CategoriesViewHolder extends RecyclerView.ViewHolder{

        TextView nombre;
        ImageView img;
        Button categoryButton;

        ProgressBar progressBar;
        public CategoriesViewHolder(@NonNull View itemView) {
            super(itemView);

            nombre = (TextView) itemView.findViewById(R.id.categoryTitle);
            img = (ImageView) itemView.findViewById(R.id.categoryImage);
            categoryButton = (Button) itemView.findViewById(R.id.categoryButton);

            progressBar = itemView.findViewById(R.id.progressBar);

            categoryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Se obtiene la posicion del Adapter
                    int position = getAdapterPosition();

                    //Se obtiene el objeto de la lista en la posicion donde el usuario ha pulsado
                    Category category = categories.get(position);

                    //Se guardan el nombre de la categoria y la posicion en el objeto Bundle para pasarlo a otro fragment
                    Bundle args = new Bundle();
                    args.putString("categoryName", category.getName());
                    args.putInt("categoryID", category.getId());

                    //Se establece el fragment de destino y al que se enviaran los datos
                    Fragment destinationFragment = new ProductsMenu();
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
