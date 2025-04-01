package com.es.phoneshop.model.product.utils;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.storage.HttpSessionRVPReader;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class HttpSessionRVPReaderTest {
    @Mock
    private HttpSession session;
    @Mock
    private RecentlyViewedProductsService service;
    @Mock
    private RecentlyViewedProducts recentlyViewedProducts;

    @BeforeEach
    void setUp() {}

    @Test
    void getRecentlyViewProductsFromSession_returnsProductsFromSessionWhichHasProducts() {
        List<Product> existingProducts = new ArrayList<>();
        existingProducts.add(new Product());
        when(session.getAttribute(HttpSessionRVPReader.RECENTLY_VIEWED_SESSION_ATTRIBUTE)).thenReturn(existingProducts);
        when(service.createRecentlyViewedProducts(existingProducts)).thenReturn(recentlyViewedProducts);

        RecentlyViewedProducts rvp = HttpSessionRVPReader.getRecentlyViewProductsFromSession(session, service);

        assertSame(recentlyViewedProducts, rvp);
    }

    @Test
    void getRecentlyViewProductsFromSession_createsNewProductsAndReturnsIt() {
        when(session.getAttribute(HttpSessionRVPReader.RECENTLY_VIEWED_SESSION_ATTRIBUTE)).thenReturn(null);
        when(service.createRecentlyViewedProducts(Collections.emptyList())).thenReturn(recentlyViewedProducts);

        RecentlyViewedProducts rvp = HttpSessionRVPReader.getRecentlyViewProductsFromSession(session, service);

        assertSame(recentlyViewedProducts, rvp);
    }

    @Test
    void saveRecentlyViewedProducts_savesProductsToSession() {
        List<Product> products = new ArrayList<>();
        when(recentlyViewedProducts.getRecentlyViewedProductsList()).thenReturn(products);

        HttpSessionRVPReader.saveRecentlyViewedProducts(session, recentlyViewedProducts);

        verify(session).setAttribute(HttpSessionRVPReader.RECENTLY_VIEWED_SESSION_ATTRIBUTE, products);
    }
}
