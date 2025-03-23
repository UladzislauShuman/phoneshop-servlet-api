package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

public class Product implements Cloneable, Serializable {
    private Long id;
    private String code;
    private String description;
    /** null means there is no price because the product is outdated or new */
    private BigDecimal price;
    /** can be null if the price is null */
    private Currency currency;
    private int stock;
    private String imageUrl;

    List<ProductHistory> productHistories;

    public Product() {
    }

    public Product(Long id, String code, String description, BigDecimal price, Currency currency, int stock, String imageUrl) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.productHistories = new ArrayList<>();
    }

    public Product(String code, String description, BigDecimal price, Currency currency, int stock, String imageUrl) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.productHistories = new ArrayList<>();
    }

    public Product(String code, String description, BigDecimal price, Currency currency, int stock, String imageUrl,
                   List<ProductHistory> productHistories) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.productHistories = productHistories;
    }

    public Product(Long id, String code, String description, BigDecimal price, Currency currency, int stock, String imageUrl,
                   List<ProductHistory> productHistories) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.productHistories = productHistories;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return this.stock == product.stock &&
                Objects.equals(this.id, product.id) &&
                Objects.equals(this.code, product.code) &&
                Objects.equals(this.price, product.price) &&
                Objects.equals(this.currency, product.currency) &&
                Objects.equals(this.description, product.description) &&
                Objects.equals(this.imageUrl, product.imageUrl) &&
                Objects.equals(this.productHistories, product.productHistories);
    }

    public boolean equalsWithOutId(Product product) {
        return this.stock == product.stock &&
                Objects.equals(this.code, product.code) &&
                Objects.equals(this.price, product.price) &&
                Objects.equals(this.currency, product.currency) &&
                Objects.equals(this.description, product.description) &&
                Objects.equals(this.imageUrl, product.imageUrl) &&
                Objects.equals(this.productHistories, product.productHistories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.id,
                this.code,
                this.price,
                this.description,
                this.currency,
                this.imageUrl,
                this.stock,
                this.productHistories
        );
    }

    public List<ProductHistory> getProductHistories() {
        return productHistories;
    }

    public void setProductHistories(List<ProductHistory> productHistories) {
        this.productHistories = productHistories;
    }

    @Override
    public Product clone() {
        Product clonedProduct = new Product();

        clonedProduct.setId(this.id);
        clonedProduct.setCode(this.code);
        clonedProduct.setDescription(this.description);
        clonedProduct.setPrice(this.price);
        clonedProduct.setCurrency(this.currency);
        clonedProduct.setStock(this.stock);
        clonedProduct.setImageUrl(this.imageUrl);

        if (this.productHistories != null) {
            List<ProductHistory> clonedHistories = new ArrayList<>();
            for (ProductHistory history : this.productHistories) {
                clonedHistories.add(history.clone());
            }
            clonedProduct.setProductHistories(clonedHistories);
        } else {
            clonedProduct.setProductHistories(new ArrayList<>()); // or null, depending on desired behavior
        }

        return clonedProduct;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", currency=" + currency +
                ", stock=" + stock +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}