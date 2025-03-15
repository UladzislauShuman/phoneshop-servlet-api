package com.es.phoneshop.web;

import com.es.phoneshop.model.product.*;
import com.es.phoneshop.model.product.exceptions.InsertDemoDataException;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

public class ProductDemoDataServletContextListener implements ServletContextListener {
    public static final String CURRENCY = "USD";
    public static final String INSERT_DEMO_DATA = "insertDemoData";
    private static final String UNKNOWN_EXCEPTION_MESSAGE = "Unknown Exception %s";

    private ProductDao productDao;
    public ProductDemoDataServletContextListener() {
        this.productDao = HashMapProductDao.getInstance();
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            boolean insertDemoData = Boolean.valueOf(event.getServletContext().getInitParameter(INSERT_DEMO_DATA));
            if (insertDemoData)
                this.getSampleProducts().stream().forEach(product -> saveProductToProductDao(product));
        } catch (NullPointerException | IllegalArgumentException | ProductNotFoundException e) {
            throw new InsertDemoDataException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(String.format(UNKNOWN_EXCEPTION_MESSAGE, e.getMessage()));
        }
    }

    private void saveProductToProductDao(Product product) {
        try {
            this.productDao.save(product);
        } catch (ProductNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}

    public List<Product> getSampleProducts(){
        List<Product> result = new ArrayList<>();
        Currency usd = Currency.getInstance(CURRENCY);
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
}
