package com.example.easyway;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import Adapters.MiSingleton;
import Adapters.Product;
import Adapters.ShopCart;
import Adapters.ShopCartAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingCartFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button finishPurchase;

    ImageButton backButton;

    TextView totalPrice;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private RecyclerView recyclerCart;
    List<Product> products;
    ShopCartAdapter adapter;

    ShopCart shopCart;

    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingCartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingCartFragment newInstance(String param1, String param2) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        recyclerCart = (RecyclerView) view.findViewById(R.id.productsList);
        recyclerCart.setLayoutManager(new LinearLayoutManager(getContext()));
        totalPrice = view.findViewById(R.id.totalPrice);

        //Lista de productos que se mostraran en el carrito
        products = new ArrayList<>();

        //Creamos una instancia global para el carrito, que se utilizara para realizar los pedidos en la pantalla siguiente
        shopCart = MiSingleton.getShopCart();

        adapter = new ShopCartAdapter(products);

        recyclerCart.setAdapter(adapter);

        //Cargamos los productos que se añaden al ArrayList
        getProducts();

        //Cuando se haga clic en el boton se va a la pantalla de finalizar compra
        finishPurchase = (Button) view.findViewById(R.id.finishPurchase);
        finishPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Si el carrito esta vacio, muestra un mensaje informando que no se puede continuar la compra
                if(adapter.getItemCount() == 0){
                    Toast.makeText(getContext(), "No hay nada en el carrito", Toast.LENGTH_SHORT).show();
                }else{
                    //Sino, se envia el precio total a la pantalla de pago mediante un objeto Bundle
                    Bundle args = new Bundle();

                    //Enviamos mediante el bundle el precio total transformado en string a la pantalla siguiente
                    args.putString("precioTotal", totalPrice.getText().toString());

                    //Establecemos el fragmento de destino y los argumentos que se enviaran a este
                    Fragment destinationFragment = new PaymentLayout();
                    destinationFragment.setArguments(args);

                    //Reemplazamos el fragmento actual con el de destino
                    FragmentManager fragmentManager = ((FragmentActivity) getContext()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout, destinationFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });

        backButton = (ImageButton) view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Volvemos a la pantalla anterior
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    //Obtiene todos los productos de la base de datos y los añade a la lista de productos que rellenara el adaptador
    public void getProducts(){
        db.collection("Carrito de compra").document(mAuth.getCurrentUser().getEmail()).collection("Productos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        products.removeAll(products);
                        shopCart.removeAll();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = new Product(document.getString("nombre"), document.getDouble("precio"), document.getString("img"), document.getId(), document.getString("descripcion"), document.getDouble("stock"));
                                shopCart.addProduct(product); //Añade productos al objeto carrito
                                products.add(product);
                                System.out.println(shopCart.getCartSize());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        //Establecemos el precio total del carrito en el TextView "totalPrice"
                        totalPrice.setText(String.valueOf(shopCart.calcularPrecioTotal()) + "€");
                    }
                });
    }

}