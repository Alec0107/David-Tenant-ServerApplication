package SERVER.Models;

import java.util.ArrayList;

public class CartResponse {
    private ArrayList<CartResponseProduct> cartResponseProducts = new ArrayList<>();


    public CartResponse(ArrayList<CartResponseProduct> cartResponseProducts) {
        this.cartResponseProducts = cartResponseProducts;
    }

    public ArrayList<CartResponseProduct> getCartResponseProducts() {
        return cartResponseProducts;
    }
}
