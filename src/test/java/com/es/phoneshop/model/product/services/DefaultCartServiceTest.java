package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DefaultCartServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long PRODUCT_ID_WHICH_NOT_EXIST = 4L;
    private static final int STOCK = 10;
    private static final int QUANTITY = 5;
    private static final int QUANTITY_1 = 3;
    private static final int QUANTITY_2 = 2;
    private static final int QUANTITY_3 = 6;
    private static final int QUANTITY_SUM_1_2 = QUANTITY_1 + QUANTITY_2 ;
    private static final int QUANTITY_SUM_1_2_3 = QUANTITY_1 + QUANTITY_2 +QUANTITY_3;

    private static final int STOCK_1 = 20;
    private static final int STOCK_2 = 15;
    private static final int STOCK_3 = 10;


    private static final BigDecimal PRICE_1 = new BigDecimal(100);
    private static final BigDecimal PRICE_2 = new BigDecimal(200);
    private static final BigDecimal PRICE_3 = new BigDecimal(300);
    private static final BigDecimal TOTAL_COST =
            PRICE_1.multiply(new BigDecimal(QUANTITY_1))
                    .add(PRICE_2.multiply(new BigDecimal(QUANTITY_2)))
                    .add(PRICE_3.multiply(new BigDecimal(QUANTITY_3)));

    @Mock
    private ProductDao productDao;

    private Product testProduct;
    private Cart testCart;
    private DefaultCartService cartService;

    @BeforeEach
    void setUpTestProduct() {
        testProduct = new Product(PRODUCT_ID,"testCode","testDescription", BigDecimal.TEN,null,STOCK,"testImageUrl");
        cartService = new DefaultCartService(productDao);
    }

    @BeforeEach
    void setUpTestCart() {
        testCart = new Cart();
    }

    @Test
    void add_whichNotExist() throws OutOfStockException {
        when(productDao.getProduct(any(Long.class))).thenReturn(testProduct);

        assertEquals(testCart.getItems().size(), 0);

        cartService.add(testCart,PRODUCT_ID,QUANTITY);

        assertEquals(testCart.getItems().get(0).getProduct(), testProduct);
        assertEquals(testCart.getItems().size(), 1);
    }

    @Test
    void add_whichExist() throws OutOfStockException {
        when(productDao.getProduct(any(Long.class))).thenReturn(testProduct);

        assertEquals(testCart.getItems().size(), 0);
        cartService.add(testCart,PRODUCT_ID,QUANTITY_1);
        assertEquals(testCart.getItems().get(0).getQuantity(), QUANTITY_1);
        cartService.add(testCart,PRODUCT_ID,QUANTITY_2);
        assertEquals(testCart.getItems().get(0).getQuantity(), QUANTITY_SUM_1_2);
        assertEquals(testCart.getItems().size(), 1);
    }

    @Test
    void delete_whichExist() throws OutOfStockException {
        when(productDao.getProduct(any(Long.class))).thenReturn(testProduct);

        cartService.add(testCart,PRODUCT_ID,QUANTITY);
        assertEquals(testCart.getItems().size(), 1);

        cartService.delete(testCart, PRODUCT_ID);
        assertEquals(testCart.getItems().size(), 0);
    }

    @Test
    void delete_whichNotExist() throws OutOfStockException {
        when(productDao.getProduct(any(Long.class))).thenReturn(testProduct);

        cartService.add(testCart,PRODUCT_ID,QUANTITY);
        assertEquals(testCart.getItems().size(), 1);

        cartService.delete(testCart, PRODUCT_ID_WHICH_NOT_EXIST);
        assertEquals(testCart.getItems().size(), 1);
        assertEquals(testCart.getItems().get(0).getProduct(), testProduct);
    }

    @Test
    void update_whichNotExist() throws OutOfStockException {
        when(productDao.getProduct(any(Long.class))).thenReturn(testProduct);

        assertEquals(testCart.getItems().size(), 0);

        cartService.update(testCart,PRODUCT_ID,QUANTITY);

        assertEquals(testCart.getItems().get(0).getProduct(), testProduct); ;
    }

    @Test
    void update_whichExist() throws OutOfStockException {
        when(productDao.getProduct(any(Long.class))).thenReturn(testProduct);

        assertEquals(testCart.getItems().size(), 0);
        cartService.update(testCart,PRODUCT_ID,QUANTITY_1);
        assertEquals(testCart.getItems().get(0).getQuantity(), QUANTITY_1);
        assertEquals(testCart.getItems().size(), 1);
        assertEquals(testCart.getItems().get(0).getProduct(), testProduct);

        cartService.update(testCart,PRODUCT_ID,QUANTITY_2);
        assertEquals(testCart.getItems().get(0).getQuantity(), QUANTITY_2);
        assertEquals(testCart.getItems().size(), 1);
        assertEquals(testCart.getItems().get(0).getProduct(), testProduct);
    }

    @Test
    void checkRecalculate() {
        Currency currency = Currency.getInstance("USD");
        Product product1 = new Product( 1L,"sgs", "Samsung Galaxy S", PRICE_1, currency, STOCK_1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", null);
        Product product2 = new Product( 2L,"sgs2", "Samsung Galaxy S II", PRICE_2, currency, STOCK_2, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", null);
        Product product3 = new Product( 3L, "sgs3", "Samsung Galaxy S III", PRICE_3, currency, STOCK_3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", null);

        testCart.add(new CartItem(product1, QUANTITY_1));
        testCart.add(new CartItem(product2, QUANTITY_2));
        testCart.add(new CartItem(product3, QUANTITY_3));

        assertEquals(testCart.getTotalCost(), BigDecimal.ZERO);
        assertEquals(testCart.getTotalQuantity(), 0);

        cartService.delete(testCart,PRODUCT_ID_WHICH_NOT_EXIST);

        assertEquals(testCart.getTotalCost(), TOTAL_COST);
        assertEquals(testCart.getTotalQuantity(), QUANTITY_SUM_1_2_3);
    }

}
