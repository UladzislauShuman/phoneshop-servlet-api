package com.es.phoneshop.model.product;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

/*
прочитав и посмотрев примеры документации JUnit для
я решил опробовать некоторые возможности (по среднему уровню здравости)
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class ArrayListProductDaoTest
{
    private ProductDao productDao;
    private List<Product> products;
    private Product product;
    private Product existsProduct;
    private Product productWithStockLessZero;
    private Product productNullPrice;

    private Product productWithSomeNullFields;
    public ArrayListProductDaoTest(Product product) {
        this.productWithSomeNullFields = product;
    }

    @Parameterized.Parameters
    public static Collection<Product> data_save() {
        Currency usd = Currency.getInstance("USD");
        return Arrays.asList(
                new Product("sgs", null, new BigDecimal(100), usd, 100, null),
                new Product(null, "description", null, null, 9, "url")
        );
    }

    @Before // BeforeClass
    public void setup() {
        Currency usd = Currency.getInstance("USD");
        this.existsProduct = new Product(1L,"sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        this.productWithStockLessZero = new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, -40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        this.productNullPrice = new Product("simsxg75", "Siemens SXG75", null, usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg");
        this.products = Arrays.asList(
                new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"),
                new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"),
                new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"),
                new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"),
                new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"),
                new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"),
                new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"),
                new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"),
                new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"),
                new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"),
                new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"),
                new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"),
                new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"),
                this.productWithStockLessZero,
                this.productNullPrice
        );
        this.productDao = new ArrayListProductDao(this.products);
        this.product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
    }

    //delete
    @Test
    public void testDeleteExisting() {
        int amount = this.productDao.findProducts().size();
        this.productDao.delete(1L);
        assertEquals(amount - 1, this.productDao.findProducts().size());
    }

    @Test
    public void testDeleteThatWasDeleted() {
        int amount = this.productDao.findProducts().size();
        this.productDao.delete(1L);
        assertEquals(amount - 1, this.productDao.findProducts().size());
        amount = this.productDao.findProducts().size();
        this.productDao.delete(1L);
        assertEquals(amount, this.productDao.findProducts().size());
    }

    @Test
    public void testDeleteNonExisting() {
        int amount = this.productDao.findProducts().size();
        this.productDao.delete(-1L); // -1L? сделать константой?
        assertEquals(amount, this.productDao.findProducts().size());
    }

    @Test
    public void testDeleteNull() {
        int amount = this.productDao.findProducts().size();
        this.productDao.delete(null);
        assertEquals(amount, this.productDao.findProducts().size());
    }

    @Test
    public void testDeleteFromDAOWithOneElement() {
        this.productDao = new ArrayListProductDao(
                Arrays.asList(this.existsProduct)
        );
        assertEquals(1, this.productDao.findProducts().size());
        this.productDao.delete(this.existsProduct.getId());
        assertTrue(this.productDao.findProducts().isEmpty());
    }

    //findProducts
    @Test
    public void testFindProductsUnEmpty() {
        assertFalse(productDao.findProducts().isEmpty());
    }

    @Test
    public void testFindProductsCheckStockLessThanZero() {
        this.productDao = new ArrayListProductDao(
                Arrays.asList(
                        this.productWithStockLessZero,
                        this.productNullPrice
                )
        );
        assertTrue(this.productDao.findProducts().isEmpty());
    }

    @Test
    public void testFindProductsCheckNullPrice() {
        assertTrue(
                this.productDao.findProducts().size()
                        <
                        this.products.size()
        );
    }

    //save
    @Test
    public void testSaveNewProduct() throws ProductNotFoundException { //
        this.productDao.save(this.product);
        assertTrue(this.product.getId() > 0);
        Product result = this.productDao.getProduct(Long.valueOf(this.product.getId()));
        assertNotNull(result);
        assertEquals("test-product", result.getCode());
    }

    @Test
    public void testSaveNewProductWithSomeNullFields() throws ProductNotFoundException {
        Currency usd = Currency.getInstance("USD");
        this.productDao = new ArrayListProductDao();
        this.productDao.save(
                this.productWithSomeNullFields
        );
        assertFalse(this.productDao.findProducts().isEmpty());
    }

    @Test
    public void testSaveExitingProduct() throws ProductNotFoundException {
        this.changeProduct(this.existsProduct);
        this.productDao.save(this.existsProduct);
        assertEquals(
                this.existsProduct,
                this.productDao.getProduct(this.existsProduct.getId())
                );
    }
    private void changeProduct(Product product) {
        product.setDescription("changed");
        product.setImageUrl("changed");
        product.setPrice(new BigDecimal(10));
        product.setCurrency(Currency.getInstance("USD"));
        product.setStock(12);
        product.setCode("changed");
    }

    @Test(expected = ProductNotFoundException.class)
    public void testSaveNullProduct() throws ProductNotFoundException {
        this.productDao.save(null);
    }

    // getProduct
    @Test
    public void testGetProductThatExisting() throws ProductNotFoundException {
        Product result = this.productDao.getProduct(this.existsProduct.getId());
        assertNotNull(result);
        assertEquals(this.existsProduct, result);
    }

    @Test (expected = ProductNotFoundException.class)
    public void testGetNonExistingProduct() throws ProductNotFoundException {
        this.productDao.getProduct(-1L);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetProductWithNullId() throws ProductNotFoundException {
        this.productDao.getProduct(null);
    }

    //Concurrent
    @Test //
    public void testConcurrentAccess() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(()  -> {
                try {
                    Currency usd = Currency.getInstance("USD");
                    Product product_ = new Product("test-product", "Test Product", new BigDecimal(100), usd, 10, "test.jpg");
                    productDao.save(product_);

                    List<Product> products = productDao.findProducts();
                    if (!products.isEmpty()) {
                        productDao.delete(products.get(0).getId());
                    }
                    if (!products.isEmpty()) {
                        try {
                            productDao.getProduct(products.get(0).getId());
                        } catch (ProductNotFoundException ignored) {}
                    }
                    productDao.findProducts();
                } catch (ProductNotFoundException e) {
                    return; //
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        assertFalse(productDao.findProducts().contains(null));
        assertTrue(productDao.findProducts().size() >= 0);
    }
}

