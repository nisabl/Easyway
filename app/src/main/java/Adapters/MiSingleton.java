package Adapters;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase se utiliza para crear una instancia global del carrito, para asi guardar y poder acceder a su valor en todas las pantallas
 */
public class MiSingleton {

    static List<Product> products = new ArrayList<>();

    private static ShopCart shopCart;

    public static ShopCart getShopCart() {
        if (shopCart == null) {
            shopCart = new ShopCart(products);
        }
        return shopCart;
    }

}
