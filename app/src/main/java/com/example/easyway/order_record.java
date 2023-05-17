package com.example.easyway;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Adapters.Order;
import Adapters.OrdersAdapter;
import Adapters.Product;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link order_record#newInstance} factory method to
 * create an instance of this fragment.
 */
public class order_record extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ImageButton backButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    RecyclerView recyclerOrders;
    List<Order> orders;

    OrdersAdapter adapter;

    public order_record() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment order_record.
     */
    // TODO: Rename and change types and number of parameters
    public static order_record newInstance(String param1, String param2) {
        order_record fragment = new order_record();
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

        View view = inflater.inflate(R.layout.fragment_order_record, container, false);

        recyclerOrders = (RecyclerView) view.findViewById(R.id.orderList);
        recyclerOrders.setLayoutManager(new LinearLayoutManager(getContext()));

        //Inicializamos la lista de pedidos
        orders = new ArrayList<>();

        //Instanciamos el adaptador con la lista de pedidos
        adapter = new OrdersAdapter(orders);

        //Configuramos la lista con el adapter creado
        recyclerOrders.setAdapter(adapter);

        getOrders();

        backButton = (ImageButton) view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new UserConfFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout , newFragment);
                fragmentTransaction.commit();

            }
        });

        return view;
    }

    //Funcion para obtener todos los pedidos
    public void getOrders(){
        db.collection("Pedidos").whereEqualTo("usuario", mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        orders.removeAll(orders);
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Se guardan los datos de los pedidos en un objeto Order
                                Order order = new Order(document.getString("nombre"), document.getString("telefono"), document.getString("fecha"), document.getId(), (List<Product>) document.get("Productos"), document.getString("direccion"), document.getDouble("precio")); //RELLENAR EL OBJETO ORDER PARA MOSTRARLO EN EL ADAPTER
                                orders.add(order); //Se añade el objeto Order a la lista
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}