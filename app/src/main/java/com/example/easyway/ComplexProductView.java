package com.example.easyway;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import Adapters.MiSingleton;
import Adapters.Product;
import Adapters.ShopCart;
import Adapters.ShopCartAdapter;
import auxFunctions.FuncionesAux;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComplexProductView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComplexProductView extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton backButton;
    private TextView productName, productPrice;
    private ImageView productImg;
    private EditText productDescription;

    private Button addToCart;

    Bundle args = new Bundle();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore db;

    FuncionesAux aux = new FuncionesAux();

    public ComplexProductView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComplexProductView.
     */
    // TODO: Rename and change types and number of parameters
    public static ComplexProductView newInstance(String param1, String param2) {
        ComplexProductView fragment = new ComplexProductView();
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
        View view = inflater.inflate(R.layout.fragment_complex_product_view, container, false);

        db = FirebaseFirestore.getInstance();

        //Declaramos elementos visuales
        backButton = (ImageButton) view.findViewById(R.id.backButton);
        productName = (TextView) view.findViewById(R.id.productName);
        productPrice = (TextView) view.findViewById(R.id.productPrice);
        productDescription = view.findViewById(R.id.productDescription);
        productImg = (ImageView) view.findViewById(R.id.productImage);
        addToCart = (Button) view.findViewById(R.id.addToCartBtn);

        //Obtenemos los elementos del Bundle y los guardamos en variables
        args = getArguments();
        String name = args.getString("productName");
        double price = args.getDouble("productPrice");
        String img = args.getString("productImg");
        String id = args.getString("productID");
        String description = args.getString("productDesc");
        double stock = args.getDouble("productStock");

        //Editamos los campos de texto para que muestren la información correspondiente
        productName.setText(name);
        productPrice.setText(String.valueOf(price) + "€");
        productDescription.setText(description);

        //Convertimos el String a Uri
        Uri imgUri = Uri.parse(img);

        // Cargar la imagen en el ImageView utilizando Glide
        Glide.with(productImg.getContext())
                .load(imgUri)
                .into(productImg);

        //Función para volver a la pantalla anterior
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //Cuando se pulse este botón se activara la función insertToCart para añadir un producto al carrito
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int idDiferencia = aux.generateID(); //generamos un ID para ponerselo al producto y asi poder comprar productos diferentes
                insertToCart(name, price, img, id + String.valueOf(idDiferencia), description, stock);
            }
        });

        return view;
    }

    //Funcion que añade un producto al carrito
    public void insertToCart(String name, double price, String img, String docID, String description, double stock){

        //Mapeado de los datos que se agregaran al producto del carrito
        Map<String, Object> producto = new HashMap<>();
        producto.put("nombre", name);
        producto.put("precio", price);
        producto.put("img", img);
        producto.put("id", docID);
        producto.put("descripcion", description);
        producto.put("stock", stock);

        //Ruta donde se creara el nuevo documento (Producto)
        DocumentReference docRef = db.collection("Carrito de compra").document(mAuth.getCurrentUser().getEmail()).collection("Productos").document(docID);

        //Creacion del documento
        docRef.set(producto).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(getContext(), "Se ha añadido el producto al carrito", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(getContext(), "Ha ocurrido al añadir el producto", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}