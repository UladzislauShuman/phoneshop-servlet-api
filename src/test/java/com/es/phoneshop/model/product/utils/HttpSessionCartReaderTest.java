package com.es.phoneshop.model.product.utils;


import com.es.phoneshop.model.cart.Cart;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.es.phoneshop.utils.HttpSessionCartReader;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class HttpSessionCartReaderTest {

    @Mock
    private HttpSession session;

    @BeforeEach
    void setUp() {}

    @Test
    void getCartFromSession_returnsCartFromSession() {
        Cart existingCart = new Cart();
        when(session.getAttribute(HttpSessionCartReader.CART_SESSION_ATTRIBUTE)).thenReturn(existingCart);

        Cart cart = HttpSessionCartReader.getCartFromSession(session);

        assertSame(existingCart, cart);
    }

    @Test
    void getCartFromSession_createsNewCartAndReturnsIt() {
        when(session.getAttribute(HttpSessionCartReader.CART_SESSION_ATTRIBUTE)).thenReturn(null);

        Cart cart = HttpSessionCartReader.getCartFromSession(session);

        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    void saveCartToSession_savesCartToSession() {
        Cart cart = new Cart();
        HttpSessionCartReader.saveCartToSession(session, cart);

        verify(session).setAttribute(HttpSessionCartReader.CART_SESSION_ATTRIBUTE, cart);
    }

    @Test
    void saveCartToSession_savesNullCartToSession() {
        HttpSessionCartReader.saveCartToSession(session, null);

        verify(session).setAttribute(HttpSessionCartReader.CART_SESSION_ATTRIBUTE, null);
    }

}
