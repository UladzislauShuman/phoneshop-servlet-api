package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.DefaultCartService;
import com.es.phoneshop.model.cart.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class DefaultCartServiceTest {

    private static final Long PRODUCT_ID = 1L;
    private static final int STOCK = 10;
    private static final int QUANTITY = 5;
    private static final int BIGGER_QUANTITY = 15;
    private static final int QUANTITY_1 = 3;
    private static final int QUANTITY_2 = 2;
    private static final int QUANTITY_SUM = QUANTITY_1 + QUANTITY_2 ;

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private DefaultCartService cartService; // Remove static

    private Product testProduct;
    private Cart testCart;


    @BeforeEach
    void setUpTestProduct() {
        testProduct = new Product();
        testProduct.setId(PRODUCT_ID);
        testProduct.setStock(STOCK);
    }

    @BeforeEach
    void setUpTestCart() {
        testCart = new Cart();
    }


    @Test
    void add_addProductToCart() throws ProductNotFoundException, OutOfStockException {
        Mockito.when(productDao.getProduct(PRODUCT_ID)).thenReturn(testProduct);

        cartService.add(testCart, PRODUCT_ID, QUANTITY);

        assertEquals(1, testCart.getItems().size());
        assertEquals(QUANTITY, testCart.getItems().get(0).getQuantity());
    }

    @Test
    void add_throwProductNotFoundException() throws ProductNotFoundException {
        Mockito.when(productDao.getProduct(PRODUCT_ID)).thenThrow(new ProductNotFoundException(PRODUCT_ID));

        assertThrows(ProductNotFoundException.class, () -> cartService.add(testCart, PRODUCT_ID, QUANTITY));
    }

    @Test
    void add_throwOutOfStockException() throws ProductNotFoundException {
        Mockito.when(productDao.getProduct(PRODUCT_ID)).thenReturn(testProduct);

        assertThrows(OutOfStockException.class, () -> cartService.add(testCart, PRODUCT_ID, BIGGER_QUANTITY));
    }

    @Test
    void add_throwOutOfStockException_cartContainsProduct() throws ProductNotFoundException, OutOfStockException {
        Mockito.when(productDao.getProduct(PRODUCT_ID)).thenReturn(testProduct);

        cartService.add(testCart, PRODUCT_ID, QUANTITY);

        assertThrows(OutOfStockException.class, () -> cartService.add(testCart, PRODUCT_ID, QUANTITY + 1));
    }

    @Test
    void add_updateQuantity_productInCart() throws ProductNotFoundException, OutOfStockException {
        Mockito.when(productDao.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        cartService.add(testCart, PRODUCT_ID, QUANTITY_1);
        cartService.add(testCart, PRODUCT_ID, QUANTITY_2);

        assertEquals(1, testCart.getItems().size());
        assertEquals(QUANTITY_SUM, testCart.getItems().get(0).getQuantity());
    }

    @Test
    void getCurrentAvailableStock_calculatesCorrectStock() throws ProductNotFoundException, OutOfStockException{
        Mockito.when(productDao.getProduct(PRODUCT_ID)).thenReturn(testProduct);
        cartService.add(testCart, PRODUCT_ID, QUANTITY_1);
        int availableStock = testProduct.getStock() - testCart.getQuantity(testProduct);

        assertEquals(STOCK - 3, availableStock);
    }
}
