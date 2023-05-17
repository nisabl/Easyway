package com.example.easyway;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapters.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserConfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserConfFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ImageButton orderList, backButton;

    private Button saveButton;

    private EditText nameBox, phoneBox, surnameBox, emailBox;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserConfFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserConfFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserConfFragment newInstance(String param1, String param2) {
        UserConfFragment fragment = new UserConfFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_conf, container, false);

        //Declaramos los elementos visuales
        orderList = (ImageButton) view.findViewById(R.id.orderList);
        backButton = (ImageButton) view.findViewById(R.id.backButton);
        saveButton = (Button) view.findViewById(R.id.saveChanges);

        nameBox = (EditText) view.findViewById(R.id.contactName);
        phoneBox = (EditText) view.findViewById(R.id.contactPhone);
        surnameBox = (EditText) view.findViewById(R.id.contactSurname);
        emailBox = (EditText) view.findViewById(R.id.contactMail);

        //Obtenemos el mail del usuario conectado
        String email = mAuth.getCurrentUser().getEmail();

        emailBox.setText(email);

        //Obtenemos los datos del usuario desde la base de datos
        loadData(email, new OnDataLoadedListener() {
            @Override //Cuando el listener comunica que ya se han cargado los datos, se ejecuta el codigo debajo
            public void onDataLoaded(User user) {
                //Una vez los hayamos obtenido, los establecemos en los campos de texto
                nameBox.setText(user.getNombre());
                surnameBox.setText(user.getApellidos());
                phoneBox.setText(user.getTelefono());
            }
        });

        //Boton para guardar los datos que modifiquemos
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, surname, phone;

                //Obtenemos el texto de los campos de texto
                name = nameBox.getText().toString().trim();
                surname = surnameBox.getText().toString().trim();
                phone = phoneBox.getText().toString().trim();

                //Guardamos los nuevos datos
                saveContactData(name, surname, phone,  email);

                //Obtenemos los datos modificados
                loadData(email, new OnDataLoadedListener() {
                    @Override
                    public void onDataLoaded(User user) {
                        nameBox.setText(user.getNombre());
                        surnameBox.setText(user.getApellidos());
                        phoneBox.setText(user.getTelefono());
                        emailBox.setText(email);
                    }
                });

            }
        });

        //Boton para ir al historial de pedidos
        orderList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new order_record();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout , newFragment);
                fragmentTransaction.commit();
            }
        });

        //Boton para volver a la pantalla anterior
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return view;
    }

    //Función para cargar los datos del usuario desde la base de datos
    public void loadData(String email, OnDataLoadedListener listener){
        db.collection("Usuarios").document(email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        //Guardamos los datos en un objeto User
                        User user = new User(document.getString("Nombre"),document.getString("Apellidos"), document.getString("Telefono"), document.getString("Email"));
                        listener.onDataLoaded(user); //Listener que devuelve el objeto User cuando los datos se han cargado correctamente
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    //Función para guardar los datos de usuario en la base de datos
    public void saveContactData(String name, String surname, String phone, String email){
        Map<String, Object> user = new HashMap<>();
        user.put("Nombre", name);
        user.put("Apellidos",  surname);
        user.put("Telefono",  phone);
        user.put("admin", false);

        //Se actualiza el documento
        db.collection("Usuarios").document(email)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(getContext(), "Se han guardado los cambios", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    //Interfaz que comunica cuando los datos del usuario han sido cargados
    public interface OnDataLoadedListener {
        void onDataLoaded(User user);
    }




}