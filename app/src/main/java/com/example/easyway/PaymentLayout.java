package com.example.easyway;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import Adapters.MiSingleton;
import Adapters.ShopCart;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentLayout#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentLayout extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText countryName, provinceName, cityName, streetName, contactName, contactPhone, titular, cvv, cardNumber, caducidad;

    private ImageButton backButton;
    private TextView totalPrice;

    private Button finishPayment;

    ShopCart shopCart;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    RelativeLayout relativeLayout;

    boolean canBuy;

    public PaymentLayout() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentLayout.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentLayout newInstance(String param1, String param2) {
        PaymentLayout fragment = new PaymentLayout();
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
        View view = inflater.inflate(R.layout.fragment_payment_layout, container, false);

        backButton = (ImageButton) view.findViewById(R.id.backButton);
        totalPrice = (TextView) view.findViewById(R.id.totalPrice);
        finishPayment = (Button) view.findViewById(R.id.payButton);
        relativeLayout = view.findViewById(R.id.totalLayout);

        countryName = view.findViewById(R.id.countryName);
        cityName = view.findViewById(R.id.cityName);
        streetName = view.findViewById(R.id.streetName);
        contactName = view.findViewById(R.id.contactName);
        contactPhone = view.findViewById(R.id.contactPhone);
        //titular = view.findViewById(R.id.titular);
        //cvv = view.findViewById(R.id.cvv);
        //cardNumber = view.findViewById(R.id.cardNumber);
        //caducidad = view.findViewById(R.id.caducidad);

        shopCart = MiSingleton.getShopCart();

        Bundle args;

        //Obtenemos los argumentos almacenados en el objeto Bundle
        args = getArguments();

        //Agregamos el valor del precio total a un String
        String precioTotal = args.getString("precioTotal");

        //Establecemos el texto del precio total en un TextView
        totalPrice.setText(precioTotal);

        finishPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Si los campos de información estan vacios se mostrara un mensaje emergente informandonos de eso
                if(countryName.getText().toString().isEmpty() || cityName.getText().toString().isEmpty() || streetName.getText().toString().isEmpty() || contactName.getText().toString().isEmpty() || contactPhone.getText().toString().isEmpty()){
                   Toast.makeText(getContext(), "No puede haber datos en blanco", Toast.LENGTH_SHORT).show();
                }else{
                    //Sino, se abrirá un Pop-up
                    PopupWindow popupWindow = new PopupWindow(getContext());
                    View popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
                    popupWindow.setContentView(popupView);
                    popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                    popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
                    popupWindow.setFocusable(true);
                    relativeLayout.setVisibility(View.INVISIBLE); //Establecemos el layout como invisible

                    //Declaramos los botones dentro del Pop-up
                    Button confirm, cancel;
                    confirm = popupView.findViewById(R.id.confirm);
                    cancel = popupView.findViewById(R.id.cancelar);

                    //Establecemos todo lo que pasara cuando cliquemos sobre el botón de "Aceptar"
                    confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Obtenemos el texto de los campos de texto
                            String calle = streetName.getText().toString();
                            String nombreContacto = contactName.getText().toString();
                            String telefonoContacto = contactPhone.getText().toString();

                            /*CODIGO PARA ACTUALIZAR EL STOCK*/
                            for (int i = 0; i < shopCart.getCartSize(); i++) { //Por cada producto dentro del carrito

                                String productName = shopCart.getProduct(i).getName(); //Guardamos el nombre en una variable
                                int finalI = i;

                                countDocumentsByName(productName) //Ejecutamos la función que cuenta la cantidad de documentos con un mismo nombre y le pasamos la variable anterior creada
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) { //Si la task tiene éxito
                                                int count = task.getResult(); //Guardamos el valor de salida en una variable
                                                System.out.println("Count for " + productName + ": " + count);

                                                //Creamos una variable donde obtenemos el nuevo stock que habra después de comprar
                                                double newStock = shopCart.getProduct(finalI).getStock() - count;
                                                String id = shopCart.getProduct(finalI).getId(); //Obtenemos el id del producto

                                                //Restamos el id diferencial al id del producto del carrito para obtener el id del producto
                                                String productId = shopCart.getProduct(finalI).getId().substring(0, id.length() - 5);

                                                System.out.println(productId + " : " + shopCart.getProduct(finalI).getName());

                                              if(newStock < 0){ //Si el nuevo stock es menor a 0, el cliente no podra comprar
                                                    Toast.makeText(getContext(), "Solo quedan " + (int) shopCart.getProduct(finalI).getStock() + " existencias" , Toast.LENGTH_SHORT).show();
                                                    canBuy = false;
                                              }else{
                                                  //Sino, se ejecutará la función que actualizará el stock
                                                  canBuy = true;
                                                  updateStock(newStock, productId);
                                                }

                                            } else {
                                                //Si la task no tiene éxito,
                                                Exception e = task.getException();
                                                System.err.println("Error counting documents for " + productName + ": " + e);
                                            }
                                        });
                            }
                            /*FIN DE ACTUALIZACION DE STOCK*/
                            //Realizamos el pedido, pasandole los datos de contacto y envio además del precio total del producto
                            if(canBuy == true){
                                makeOrder(shopCart.calcularPrecioTotal(), nombreContacto, telefonoContacto, calle);
                                //Cerramos el pop-up
                                popupWindow.dismiss();
                            }

                        }
                    });

                    //Si se pulsa en el boton de cancelar
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Se cierra el pop-up
                            popupWindow.dismiss();
                            //Se vuelve a poner visible el layout
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                    });

                    //Muestra el Popup en el centro de la pantalla
                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                }
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

    //AÑADIMOS LOS PRODUCTOS AL PEDIDO
    public void makeOrder(double price, String nombre, String telefono, String direccion){

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        //Mapeado de los datos que se agregaran al pedido
        Map<String, Object> pedido = new HashMap<>();
        pedido.put("precio", price);
        pedido.put("nombre", nombre);
        pedido.put("telefono", telefono);
        pedido.put("direccion", direccion);
        pedido.put("usuario", mAuth.getCurrentUser().getEmail());
        pedido.put("fecha", dateFormat.format(date));
        pedido.put("Productos", shopCart.getNames());
        pedido.put("Precios", shopCart.getPrecios());

        //Añadimos el documento a la base de datos
        DocumentReference docRef = db.collection("Pedidos").document();
        docRef.set(pedido).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(getContext(), "¡Se ha realizado el pedido!", Toast.LENGTH_SHORT).show();
                        borrarCarrito(); //Borramos el carrito
                        refreshUI(); //Actualizamos la pantalla
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        Toast.makeText(getContext(), "Ha ocurrido un error al crear el pedido", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Función que elimina todos los productos del carrito del usuario
    public void borrarCarrito(){
        //Referencia a la coleccion de productos dentro del carrito
        CollectionReference collectionRef = db.collection("Carrito de compra").document(mAuth.getCurrentUser().getEmail()).collection("Productos");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    //Función que cambia el fragment por el de configuración de usuario
    public void refreshUI(){
        Fragment newFragment = new UserConfFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout , newFragment);
        fragmentTransaction.commit();
    }

    //Función que devuelve el numero de documentos con un mismo nombre
    public Task<Integer> countDocumentsByName(String name) {
        //Una Task es una tarea que se ejecuta de forma asíncrona y devuelve o un resultado o un error

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Referencia a la coleccion de productos dentro del carrito
        CollectionReference collectionRef = db.collection("Carrito de compra").document(mAuth.getCurrentUser().getEmail()).collection("Productos");

        //Consultamos los documentos dentro de la colección donde el nombre coincida con el que le pasamos por parámetro
        Query query = collectionRef.whereEqualTo("nombre", name);

        return query.get().continueWith(task -> {
            if (task.isSuccessful()) { //Si la task tiene éxito devuelve la cuenta de documentos
                QuerySnapshot querySnapshot = task.getResult();
                int count = querySnapshot.size();
                return count;
            } else { //Sino devuelve un error
                Exception e = task.getException();
                System.err.println("Error al contar documentos: " + e);
                throw e;
            }
        });
    }

    public void updateStock(double newStock, String docID){
        //Referencia al documento deseado mediante el id pasado por parámetro
        DocumentReference docRef = db.collection("Productos").document(docID);

        //Actualizamos el nuevo stock con el valor que entra como parámetro
        docRef.update("stock", newStock)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

}