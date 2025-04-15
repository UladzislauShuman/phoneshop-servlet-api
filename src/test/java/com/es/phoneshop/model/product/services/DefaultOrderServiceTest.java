package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.model.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultOrderServiceTest {
    private static final BigDecimal PRICE_1 = new BigDecimal("10.00");
    private static final BigDecimal PRICE_2 = new BigDecimal("5.00");
    private static final BigDecimal TOTAL_COST = PRICE_1.add(PRICE_2);

    private static final Product product1;
    private static final Product product2;
    private static final CartItem cartItem1;
    private static final CartItem cartItem2;

    public static final int QUANTITY_1 = 2;
    public static final int QUANTITY_2 = 1;
    public static final int TOTAL_QUANTITY = QUANTITY_1 + QUANTITY_2;

    public static final long PRODUCT_ID_1 = 1L;
    public static final long PRODUCT_ID_2 = 2L;
    public static final BigDecimal DELIVERY_COST = new BigDecimal("5");
    public static final BigDecimal RESULT_COST = DELIVERY_COST.add(TOTAL_COST);

    static {
        product1 = new Product();
        product1.setId(PRODUCT_ID_1);
        product1.setPrice(PRICE_1);

        product2 = new Product();
        product2.setId(PRODUCT_ID_2);
        product2.setPrice(PRICE_2);

        cartItem1 = new CartItem(product1, QUANTITY_1);
        cartItem2 = new CartItem(product2, QUANTITY_2);
    }

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private DefaultOrderService orderService;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();

        Map<Long, CartItem> itemsMap = new HashMap<>();
        itemsMap.put(PRODUCT_ID_1, cartItem1);
        itemsMap.put(PRODUCT_ID_2, cartItem2);

        cart.setItems(itemsMap);
        cart.setTotalCost(TOTAL_COST);
        cart.setTotalQuantity(TOTAL_QUANTITY);
    }

    @Test
    void getOrder_validCart() throws CloneNotSupportedException {
        Order order = orderService.getOrder(cart);

        assertNotNull(order);
        assertEquals(TOTAL_COST, order.getSubtotal());
        assertEquals(DELIVERY_COST, order.getDeliveryCost());
        assertEquals(RESULT_COST, order.getTotalCost());
        assertEquals(TOTAL_QUANTITY, order.getTotalQuantity());

        assertNotNull(order.getItems());
        assertEquals(2, order.getItems().size());
        assertTrue(order.getItemsMap().containsKey(PRODUCT_ID_1));
        assertTrue(order.getItemsMap().containsKey(PRODUCT_ID_2));

        assertEquals(QUANTITY_1, order.getItemsMap().get(PRODUCT_ID_1).getQuantity());
        assertEquals(QUANTITY_2, order.getItemsMap().get(PRODUCT_ID_2).getQuantity());
    }


    @Test
    void getPaymentMethods_returnPaymentMethods() {
        List<PaymentMethod> paymentMethods = orderService.getPaymentMethods();

        assertNotNull(paymentMethods);
        assertEquals(PaymentMethod.values().length, paymentMethods.size());
        assertArrayEquals(PaymentMethod.values(), paymentMethods.toArray());
    }

    @Test
    void placeOrder_validOrder() {
        Order order = new Order();

        orderService.placeOrder(order);

        assertNotNull(order.getSecureId());
        assertTrue(isValidUUID(order.getSecureId()));
        verify(orderDao, times(1)).save(order);
    }

    private boolean isValidUUID(String uuidString) {
        try {
            UUID.fromString(uuidString);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
