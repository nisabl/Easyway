package com.example.easyway;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Adapters.Product;
import Adapters.ProductsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductsMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductsMenu extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageButton backButton;
    private TextView categoryMenu;
    RecyclerView recyclerProducts;
    List<Product> products;

    ProductsAdapter adapter;

    public ProductsMenu() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductsMenu.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductsMenu newInstance(String param1, String param2) {
        ProductsMenu fragment = new ProductsMenu();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_products_menu, container, false);

        Bundle args = getArguments();

        backButton = (ImageButton) view.findViewById(R.id.backButton);
        categoryMenu = (TextView) view.findViewById(R.id.categoryName);

        recyclerProducts = (RecyclerView) view.findViewById(R.id.productsList);
        recyclerProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        products = new ArrayList<>();

        adapter = new ProductsAdapter(products); //establecemos la lista de productos en el adaptador

        recyclerProducts.setAdapter(adapter); //establecemos el adaptador en nuestro recycler view (lista)

        categoryMenu.setText(args.getString("categoryName"));

        //obtenemos la posicion del boton pulsado en el adaptador
        int categoryID = args.getInt("categoryID");

        //llamamos a la funcion para obtener los productos y le sumamos 1 para que coincida con las categorias de los productos
        getProducts(categoryID);

        //Vuelve a la pantalla anterior
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    //Función que obtiene todos los productos equivalentes a una categoria
    public void getProducts(int category){
        db.collection("Productos")
                .whereEqualTo("idCategoria", category)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        products.removeAll(products);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = new Product(document.getString("nombre"), document.getDouble("precio"), document.getString("img"), document.getId(), document.getString("descripcion"), document.getDouble("stock"));
                                if(product.getStock() > 0){
                                    products.add(product); //guardamos los productos en una lista de productos
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }else{
                                    Log.d(TAG, "No queda stock del producto => " + document.getId());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}