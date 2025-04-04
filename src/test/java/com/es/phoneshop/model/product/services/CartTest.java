package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartTest {
    private static Product product1;
    private static Product product2;

    private static Long PRODUCT_1_ID = 1L;
    private static Long PRODUCT_2_ID = 2L;
    private static String PRODUCT_1_DESCRIPTION = "Product 1";
    private static String PRODUCT_2_DESCRIPTION = "Product 2";
    private static BigDecimal PRODUCT_1_PRICE = BigDecimal.valueOf(100);
    private static BigDecimal PRODUCT_2_PRICE = BigDecimal.valueOf(200);
    private static int QUANTITY_1 = 2;
    private static int QUANTITY_2 = 3;
    private static int QUANTITY_SUM = QUANTITY_1 + QUANTITY_2;



    private Cart cart;

    @BeforeAll
    static void setUp() {
        product1 = createTestProduct(PRODUCT_1_ID, PRODUCT_1_DESCRIPTION, PRODUCT_1_PRICE);
        product2 = createTestProduct(PRODUCT_2_ID, PRODUCT_2_DESCRIPTION, PRODUCT_2_PRICE);
    }

    @BeforeEach
    void setUpCart() {
        cart = new Cart();
    }

    @Test
    void add_validCartItem() {
        CartItem cartItem = new CartItem(product1, QUANTITY_1);

        cart.add(cartItem);

        List<CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        assertEquals(cartItem, items.get(0));
    }

    @Test
    void add_nullCartItem() {
        int initialSize = cart.getItems().size();
        cart.add(null);
        assertEquals(initialSize, cart.getItems().size());
    }

    @Test
    void add_sameProductTwice() {
        CartItem cartItem1 = new CartItem(product1, QUANTITY_1);
        CartItem cartItem2 = new CartItem(product1, QUANTITY_2);

        cart.add(cartItem1);
        cart.add(cartItem2);

        List<CartItem> items = cart.getItems();
        assertEquals(1, items.size());
        assertEquals(QUANTITY_SUM, items.get(0).getQuantity());
    }

    @Test
    void getQuantity_validProductInCart_returnsQuantity() {
        CartItem cartItem = new CartItem(product1, QUANTITY_1);
        cart.add(cartItem);

        int quantity = cart.getQuantity(product1);

        assertEquals(QUANTITY_1, quantity);
    }

    @Test
    void getQuantity_productNotInCart_returnsZero() {
        int quantity = cart.getQuantity(product1);

        assertEquals(0, quantity);
    }

    @Test
    void getQuantity_nullProduct_returnsZero() {
        int quantity = cart.getQuantity(null);
        assertEquals(0, quantity);
    }

    @Test
    void getItems_returnsCorrectItems() {
        CartItem cartItem1 = new CartItem(product1, QUANTITY_1);
        CartItem cartItem2 = new CartItem(product2, QUANTITY_2);
        cart.add(cartItem1);
        cart.add(cartItem2);

        List<CartItem> items = cart.getItems();

        assertEquals(2, items.size());
        assertTrue(items.contains(cartItem1));
        assertTrue(items.contains(cartItem2));
    }

    private static Product createTestProduct(Long id, String description, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setDescription(description);
        product.setPrice(price);
        return product;
    }
}
