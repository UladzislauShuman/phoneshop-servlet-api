package com.es.phoneshop.model.cart.storage;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.DefaultCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class HttpSessionCartStorage implements CartStorage {
    public static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    private final HttpSession session;

    public HttpSessionCartStorage(HttpServletRequest request) {
        this.session = request.getSession();
    }

    @Override
    public void saveCart(Cart cart) {
        session.setAttribute(CART_SESSION_ATTRIBUTE, cart);
    }

    @Override
    public Cart getCart() {
        return (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
    }
}
