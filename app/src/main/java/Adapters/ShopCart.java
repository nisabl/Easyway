package Adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ShopCart {

    List<Product> products;

    public ShopCart(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product){
        products.add(product);
    }

    public Product getProduct(int position){
        return products.get(position);
    }

    public int getCartSize(){
        return products.size();
    }

    public void removeProduct(int position){
        products.remove(position);
    }

    public void removeAll(){
        products.removeAll(products);
    }

    public double calcularPrecioTotal(){
        double precioTotal = 0;
        for(int i = 0; i < products.size(); i++){
            precioTotal += products.get(i).getPrice();
        }
        return precioTotal;
    }

    public List<String> getNames(){
        List<String> names = new ArrayList<>();
        for(int i = 0; i < getCartSize(); i++){
            names.add(getProduct(i).getName());
        }
        return names;
    }

    public List<Double> getPrecios(){
        List<Double> precios = new ArrayList<>();
        for(int i = 0; i < getCartSize(); i++){
            precios.add(getProduct(i).getPrice());
        }
        return precios;
    }

}
