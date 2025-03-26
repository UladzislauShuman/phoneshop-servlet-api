package com.es.phoneshop.model.cart.storage;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.DefaultCartService;
import jakarta.servlet.http.HttpSession;

public class HttpSessionCartReader {
    public static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    public static Cart getCartFromSession(HttpSession session) {
        synchronized (session) {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
            }
            return cart;
        }
    }

    public static void saveCartToSession(HttpSession session ,Cart cart) {
        session.setAttribute(CART_SESSION_ATTRIBUTE, cart);
    }
}
