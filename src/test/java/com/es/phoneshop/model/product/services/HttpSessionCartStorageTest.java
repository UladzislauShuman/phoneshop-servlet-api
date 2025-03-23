package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.storage.HttpSessionCartStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpSessionCartStorageTest {

    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartStorage.CART_SESSION_ATTRIBUTE; // Correct usage

    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;

    private HttpSessionCartStorage storage;

    private Cart cart;

    @BeforeEach
    void setUp() {
        when(request.getSession()).thenReturn(session);
        cart = new Cart();
        storage = new HttpSessionCartStorage(request);
    }

    @Test
    void saveCart_savesCartToSession() {
        storage.saveCart(cart);
        verify(session).setAttribute(CART_SESSION_ATTRIBUTE, cart);
    }

    @Test
    void getCart_returnsCartFromSession() {
        when(session.getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(cart);
        Cart retrievedCart = storage.getCart();
        assertEquals(cart, retrievedCart);
    }

    @Test
    void getCart_returnsNull() {
        when(session.getAttribute(CART_SESSION_ATTRIBUTE)).thenReturn(null);
        Cart retrievedCart = storage.getCart();
        assertNull(retrievedCart);
    }
}
