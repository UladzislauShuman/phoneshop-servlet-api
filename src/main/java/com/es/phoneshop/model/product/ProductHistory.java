package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Objects;
import java.util.Date;

public class ProductHistory implements Cloneable, Serializable {
    private LocalDate date;
    private BigDecimal price;
    private Currency currency;

    public ProductHistory(Currency currency, LocalDate date, BigDecimal price) {
        this.currency = currency;
        this.date = date;
        this.price = price;
    }

    public Date getDateAsUtilDate() {
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public ProductHistory() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ProductHistory productHistory = (ProductHistory) o;
        return Objects.equals(this.date, productHistory.date) &&
                Objects.equals(this.price, productHistory.price) &&
                Objects.equals(this.currency, productHistory.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.currency, this.date, this.price);
    }

    @Override
    public ProductHistory clone() {
        return new ProductHistory(this.currency, this.date, this.price);
    }
}
