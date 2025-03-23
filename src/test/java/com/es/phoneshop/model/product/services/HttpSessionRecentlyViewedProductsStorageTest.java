package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.recentlyviewed.LinkedListRecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.storage.HttpSessionRecentlyViewedProductsStorage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpSessionRecentlyViewedProductsStorageTest {

    private static final String RECENTLY_VIEWED_SESSION_ATTRIBUTE =
            HttpSessionRecentlyViewedProductsStorage.RECENTLY_VIEWED_SESSION_ATTRIBUTE;

    @Mock
    private HttpSession session;
    @Mock
    private HttpServletRequest request;
    @Mock
    private RecentlyViewedProductsService service;

    private HttpSessionRecentlyViewedProductsStorage storage;

    private RecentlyViewedProducts recentlyViewedProducts;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        when(request.getSession()).thenReturn(session);

        productList = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        productList.add(product1);
        recentlyViewedProducts = new LinkedListRecentlyViewedProducts(productList);

        storage = new HttpSessionRecentlyViewedProductsStorage(service, request);
    }

    @Test
    void getRecentlyViewedProducts_sessionAttributeExists() {
        when(session.getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE)).thenReturn(productList);
        when(service.createRecentlyViewedProducts(productList)).thenReturn(recentlyViewedProducts);

        RecentlyViewedProducts result = storage.getRecentlyViewedProducts();

        assertEquals(recentlyViewedProducts, result);
        verify(session).getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE);
        verify(service).createRecentlyViewedProducts(productList);
    }

    @Test
    void getRecentlyViewedProducts_sessionAttributeNotExist() {
        when(session.getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE)).thenReturn(null);
        when(service.createRecentlyViewedProducts(any())).thenReturn(new LinkedListRecentlyViewedProducts());

        RecentlyViewedProducts result = storage.getRecentlyViewedProducts();

        assertEquals(0, result.getRecentlyViewedProductsList().size());
        verify(session).getAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE);
        verify(service).createRecentlyViewedProducts(any());
    }

    @Test
    void saveRecentlyViewedProducts_productsSavedToSession() {

        storage.saveRecentlyViewedProducts(recentlyViewedProducts);

        verify(session).setAttribute(RECENTLY_VIEWED_SESSION_ATTRIBUTE, productList);
    }
}
