package com.es.phoneshop.model.product.services;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.recentlyviewed.DefaultRecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.LinkedListRecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.storage.RecentlyViewedProductsStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
public class DefaultRecentlyViewedProductsServiceTest {
    private static final String RVP_SERVICE_FIELD_INSTANCE = "instance";
    private static final Long PRODUCT_ID = 1L;
    private static final int PRODUCT_STOCK = 10;

    @Mock
    private ProductDao productDao;

    @Mock
    private RecentlyViewedProductsStorage storage;

    @InjectMocks
    private DefaultRecentlyViewedProductsService service;

    private RecentlyViewedProducts testRecentlyViewedProducts;
    private static Product testProduct;


    @BeforeAll
    static void setTestProduct() {
        testProduct = new Product();
        testProduct.setId(PRODUCT_ID);
        testProduct.setStock(PRODUCT_STOCK);
    }

    @BeforeEach
    void setTestRecentlyViewedProducts() {
        testRecentlyViewedProducts = new LinkedListRecentlyViewedProducts();
    }

    @BeforeEach
    void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        var field = DefaultRecentlyViewedProductsService.class.getDeclaredField(RVP_SERVICE_FIELD_INSTANCE);
        field.setAccessible(true);
        field.set(null, null);
        service = (DefaultRecentlyViewedProductsService) DefaultRecentlyViewedProductsService.getInstance(productDao);
    }

    @Test
    void getInstance_returnSameInstance() {
        RecentlyViewedProductsService service1 = DefaultRecentlyViewedProductsService.getInstance(productDao);
        RecentlyViewedProductsService service2 = DefaultRecentlyViewedProductsService.getInstance(productDao);
        assertSame(service1, service2);
    }

    @Test
    void getInstance_throwNullPointerExceptionWhenProductDaoIsNull() {
        assertThrows(NullPointerException.class, () -> DefaultRecentlyViewedProductsService.getInstance(null));
    }

    @Test
    void getRecentlyViewedProductsFromStorage_returnExistObject() {
        Mockito.when(storage.getRecentlyViewedProducts()).thenReturn(testRecentlyViewedProducts);

        RecentlyViewedProducts products = service.getRecentlyViewedProductsFromStorage(storage);

        assertSame(testRecentlyViewedProducts, products);
        Mockito.verify(storage, Mockito.never()).saveRecentlyViewedProducts(Mockito.any());
    }

    @Test
    void getRecentlyViewedProductsFromStorage_createAndSaveNewRVPIfNull() {
        Mockito.when(storage.getRecentlyViewedProducts()).thenReturn(null).thenAnswer(invocationOnMock -> new LinkedListRecentlyViewedProducts());

        RecentlyViewedProducts products = service.getRecentlyViewedProductsFromStorage(storage);

        assertNotNull(products);
        Mockito.verify(storage).saveRecentlyViewedProducts(Mockito.any());
    }

    @Test
    void add_productToRecentlyViewedProducts() {
        int initialSize = testRecentlyViewedProducts.getRecentlyViewedProductsList().size();

        service.add(testRecentlyViewedProducts, testProduct);

        assertEquals(initialSize + 1, testRecentlyViewedProducts.getRecentlyViewedProductsList().size());
        assertEquals(testProduct, testRecentlyViewedProducts.getRecentlyViewedProductsList().
                get(testRecentlyViewedProducts.getRecentlyViewedProductsList().size() - 1));
    }

    @Test
    void add_productNotNull() {
        assertDoesNotThrow(() -> service.add(testRecentlyViewedProducts, testProduct));
    }

    @Test
    void add_productCorrect() {
        service.add(testRecentlyViewedProducts, testProduct);
        assertEquals(testProduct.getId(), testRecentlyViewedProducts.getRecentlyViewedProductsList().get(0).getId());
    }

}
