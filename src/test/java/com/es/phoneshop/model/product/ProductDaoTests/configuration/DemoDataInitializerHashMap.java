
package com.es.phoneshop.model.product.ProductDaoTests.configuration;

import com.es.phoneshop.model.product.*;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

// я думал переложить эту ответственности на SuiteOfTests, но не много ли тогда у него ответственности
public class DemoDataInitializerHashMap {
    public final static ProductDao productDao;

    public final static Product existsProduct;
    public final static Product productWithStockLessZero;
    public final static Product productNullPrice;
    public final static Product product;
    public final static Product productWithSomeNullFields;

    static {
        Currency usd = Currency.getInstance("USD");
        List<ProductHistory> productHistories = Arrays.asList(
                new ProductHistory(usd, LocalDate.now(), new BigDecimal(100)),
                new ProductHistory(usd, LocalDate.now().minusDays(1), new BigDecimal(200)),
                new ProductHistory(usd, LocalDate.now().minusDays(2), new BigDecimal(150))
        );

        productDao = HashMapProductDao.getInstance();

        existsProduct = new Product(1L,"sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg",productHistories);
        productWithStockLessZero = new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, -40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg",productHistories);
        productNullPrice = new Product("simsxg75", "Siemens SXG75", null, usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg",productHistories);
        product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", productHistories);
        //price != null и stock>0
        productWithSomeNullFields = new Product(null, "Samsung Galaxy S", new BigDecimal(100) , null, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", productHistories);
    }


    public static void setup() {
        productDao.clear();
        getDemoData().stream().forEach(product -> saveToProductDao(product));
    }

    public static List<Product> getDemoData() {
        List<Product> result = new ArrayList<>();
        Currency usd = Currency.getInstance("USD");
        List<ProductHistory> productHistories = Arrays.asList(
                new ProductHistory(usd, LocalDate.now(), new BigDecimal(100)),
                new ProductHistory(usd, LocalDate.now().minusDays(1), new BigDecimal(200)),
                new ProductHistory(usd, LocalDate.now().minusDays(2), new BigDecimal(150))
        );
        result.add(new Product( "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", productHistories));
        result.add(new Product( "sgs2", "Samsung Galaxy S II", new BigDecimal(201), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( "sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", productHistories));
        result.add(new Product( "sgs4", "Samsung Galaxy S IV", new BigDecimal(202), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( "sss5", "Samsung Smth S V", new BigDecimal(203), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( "sga50", "Samsung Galaxy A50", new BigDecimal(204), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg", productHistories));
        result.add(new Product( "iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg", productHistories));
        result.add(new Product( "htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg", productHistories));
        result.add(new Product( "sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg", productHistories));
        result.add(new Product( "xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg", productHistories));
        result.add(new Product( "nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg", productHistories));
        result.add(new Product( "palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg", productHistories));
        result.add(new Product( "simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg", productHistories));
        result.add(new Product( "simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg", productHistories));
        result.add(new Product( "simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", productHistories));
        return result;
    }

    private static List<Product> getDemoDataWithIds() {
        List<Product> result = getDemoData();
        Long tempId = 1L;
        for (Product product : result) {
            product.setId(tempId++);
        }
        return result;
    }

    private static void saveToProductDao(Product product) {
        try {
            productDao.save(product);
        } catch (ProductNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void afterTest() {
        productDao.clear();
    }

    public static List<Product> getDemoDataDescriptionAsc(){
        return getDemoDataWithIds().stream().sorted(Comparator.comparing(Product::getDescription)).toList();
    }
    public static List<Product> getDemoDataDescriptionDesc() {
        return getDemoDataWithIds().stream().sorted(Comparator.comparing(Product::getDescription).reversed()).toList();
    }

    public static List<Product> getDemoDataPriceAsc(){
        return getDemoDataWithIds().stream().sorted(Comparator.comparing(Product::getPrice)).toList();
    }
    public static List<Product> getDemoDataPriceDesc(){
        return getDemoDataWithIds().stream().sorted(Comparator.comparing(Product::getPrice).reversed()).toList();
    }

    public static List<Product> getDemoData_Samsung_Galaxy_S_II() {
        List<Product> result = new ArrayList<>();
        Currency usd = Currency.getInstance("USD");
        List<ProductHistory> productHistories = Arrays.asList(
                new ProductHistory(usd, LocalDate.now(), new BigDecimal(100)),
                new ProductHistory(usd, LocalDate.now().minusDays(1), new BigDecimal(200)),
                new ProductHistory(usd, LocalDate.now().minusDays(2), new BigDecimal(150))
        );
        result.add(new Product( 1L,"sgs2", "Samsung Galaxy S II", new BigDecimal(201), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( 2L,"sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", productHistories));
        result.add(new Product( 3L,"sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", productHistories));
        result.add(new Product( 4L,"sgs4", "Samsung Galaxy S IV", new BigDecimal(202), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( 5L,"sss5", "Samsung Smth S V", new BigDecimal(203), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( 6L,"sga50", "Samsung Galaxy A50", new BigDecimal(204), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( 7L,"htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg", productHistories));
        result.add(new Product( 8L,"sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg", productHistories));
        result.add(new Product( 9L,"xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg", productHistories));
        result.add(new Product( 10L,"simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg", productHistories));
        result.add(new Product( 11L,"simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg", productHistories));
        result.add(new Product( 12L,"simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", productHistories));
        return result;
    }
    public static List<Product> getDemoData_Galaxy_S_PriceDesc() {
        List<Product> result = new ArrayList<>();
        Currency usd = Currency.getInstance("USD");
        List<ProductHistory> productHistories = Arrays.asList(
                new ProductHistory(usd, LocalDate.now(), new BigDecimal(100)),
                new ProductHistory(usd, LocalDate.now().minusDays(1), new BigDecimal(200)),
                new ProductHistory(usd, LocalDate.now().minusDays(2), new BigDecimal(150))
        );
        result.add(new Product( 2L,"sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg", productHistories));
        result.add(new Product( 3L,"htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg", productHistories));
        result.add(new Product( 4L,"sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg", productHistories));
        result.add(new Product( 5L,"sga50", "Samsung Galaxy A50", new BigDecimal(204), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( 6L,"sss5", "Samsung Smth S V", new BigDecimal(203), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( 7L,"sgs4", "Samsung Galaxy S IV", new BigDecimal(202), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( 8L,"sgs2", "Samsung Galaxy S II", new BigDecimal(201), usd, 1, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg", productHistories));
        result.add(new Product( 9L,"simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg", productHistories));
        result.add(new Product( 10L,"xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg", productHistories));
        result.add(new Product( 1L,"sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg", productHistories));
        result.add(new Product( 11L,"simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg", productHistories));
        result.add(new Product( 12L,"simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg", productHistories));
        return result;
    }
}

