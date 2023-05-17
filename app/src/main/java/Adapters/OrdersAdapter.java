package Adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easyway.ComplexOrderView;
import com.example.easyway.ComplexProductView;
import com.example.easyway.R;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {

    static List<Order> orders;

    public OrdersAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_example, null,false);
        OrdersAdapter.OrdersViewHolder holder = new OrdersViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        Order order = orders.get(position);

        //Establecemos el texto en la vista predeterminada de la lista
        holder.id.setText(order.getId());
        holder.fecha.setText(order.getFecha());
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder{

        TextView id, fecha;
        Button orderButton;

        public OrdersViewHolder(View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.orderName);
            fecha = itemView.findViewById(R.id.orderDate);
            orderButton = itemView.findViewById(R.id.orderBtn);

            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Obtenemos la posicion del item donde pulsa el usuario
                    int position = getAdapterPosition();

                    //Obtenemos el producto correspondiente a la posicion pulsada
                    Order order = orders.get(position);

                    //Creamos un objeto Bundle para pasar los datos a la pantalla del pedido correspondiente
                    Bundle args = new Bundle();
                    args.putString("orderID", order.getId());
                    args.putString("nombre", order.getNombreCliente());
                    args.putString("fecha", order.getFecha());
                    args.putString("telefono", order.getTelefono());
                    args.putString("direccion", order.getDireccion());
                    args.putString("productos", order.getProducts().toString());
                    args.putDouble("precioTotal", order.getPrecioTotal());

                    //Establecemos el fragment de destino
                    Fragment destinationFragment = new ComplexOrderView();
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
