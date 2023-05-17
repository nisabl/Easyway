package com.example.easyway;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComplexOrderView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComplexOrderView extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView orderID, customerData, address, productsList, orderDate, total;
    private ImageButton backButton;

    public ComplexOrderView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComplexOrderView.
     */
    // TODO: Rename and change types and number of parameters
    public static ComplexOrderView newInstance(String param1, String param2) {
        ComplexOrderView fragment = new ComplexOrderView();
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

        View view = inflater.inflate(R.layout.fragment_complex_order_view, container, false);

        Bundle args = new Bundle();

        //Obtenemos los datos que llegan por Bundle
        args = getArguments();

        //Declaramos los elementos visuales
        orderID = view.findViewById(R.id.orderID);
        customerData = view.findViewById(R.id.customerData);
        address = view.findViewById(R.id.address);
        productsList = view.findViewById(R.id.productsList);
        orderDate = view.findViewById(R.id.orderDate);
        total = view.findViewById(R.id.totalPrice);

        backButton = (ImageButton) view.findViewById(R.id.backButton);

        //Establecemos los datos que nos llegan por parámetro dentro de los elementos visuales
        orderID.setText(args.getString("orderID"));
        customerData.setText(args.getString("nombre") + " - " +  args.getString("telefono"));
        address.setText(args.getString("direccion"));
        productsList.setText(args.getString("productos"));
        orderDate.setText(args.getString("fecha"));
        total.setText(String.valueOf(args.getDouble("precioTotal")) + "€");

        //Boton que vuelve a la pantalla anterior
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

}