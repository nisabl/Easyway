package Adapters;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.easyway.ComplexProductView;
import com.example.easyway.R;
import com.example.easyway.ShoppingCartFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartAdapter.ShopCartViewHolder>{

    static List<Product> products;

    public ShopCartAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public ShopCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_in_cart_template, null, false);
        ShopCartViewHolder holder = new ShopCartViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ShopCartViewHolder holder, int position) {

        //Obtenemos el producto guardado en la posicion X del ArrayList y lo guardamos en un objeto producto
        Product product = products.get(position);

        //Rellenamos los campos de texto con la información del producto
        holder.nombre.setText(product.getName());
        holder.precio.setText(String.valueOf(product.getPrice()));

        Uri imgUri = Uri.parse(product.getImg());

        //Cargamos la imagen usando Glide
        Glide.with(holder.img.getContext())
                .load(imgUri)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ShopCartViewHolder extends RecyclerView.ViewHolder {

        TextView nombre, precio;
        ImageView img;
        ImageButton deleteButton;
        Button productButton;

        public ShopCartViewHolder(@NonNull View itemView) {
            super(itemView);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();

            //Declaramos los campos de datos de cada producto
            nombre = itemView.findViewById(R.id.productTitle);
            precio = itemView.findViewById(R.id.productPrice);
            img = itemView.findViewById(R.id.productImage);

            //Obtenemos el carrito global
            ShopCart shopCart = MiSingleton.getShopCart();

            productButton = itemView.findViewById(R.id.productButton);

            productButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Obtenemos la posición del item del adaptador se pulsa
                    int position = getAdapterPosition();

                    //Obtenemos el producto de la lista de productos y lo guardamos en un objeto
                    Product product = products.get(position);

                    //Creamos un objeto Bundle para pasar los datos del objeto producto recien creado
                    Bundle args = new Bundle();
                    args.putString("productName", product.getName());
                    args.putDouble("productPrice", product.getPrice());
                    args.putString("productImg", product.getImg());
                    args.putString("productDesc", product.getDescripcion());

                    //Establecemos un fragmento de destino al que enviaremos los datos
                    Fragment destinationFragment = new ComplexProductView();
                    destinationFragment.setArguments(args);

                    //Reemplazamos el fragmento actual por el de destino
                    FragmentManager fragmentManager = ((FragmentActivity) itemView.getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, destinationFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            });

            deleteButton = (ImageButton) itemView.findViewById(R.id.deleteProduct);
            deleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //Obtenemos la posición del item del adaptador se pulsa
                    int position = getAdapterPosition();

                    Product product = products.get(position); //Obtenemos el producto en cuestión
                    shopCart.getProduct(position); //Obtenemos el producto del carrito
                    shopCart.removeProduct(position); //Eliminamos el producto del carrito
                    System.out.println(shopCart.getCartSize());

                    //Eliminamos el producto del carrito de la BBDD mediante el id del producto que se quiere eliminar
                    db.collection("Carrito de compra").document(mAuth.getCurrentUser().getEmail()).collection("Productos").document(product.getId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Muestra un mensaje de éxito
                                    Toast.makeText(deleteButton.getContext(), "Eliminado del carrito", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error deleting document", e);
                                }
                            });

                    //Recargamos el fragmento para actualizar el RecyclerView y que la vista del producto no aparezca
                    Fragment destinationFragment = new ShoppingCartFragment();
                    FragmentManager fragmentManager = ((FragmentActivity) itemView.getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, destinationFragment);
                    fragmentTransaction.commit();
                }


            });
        }

    }
}