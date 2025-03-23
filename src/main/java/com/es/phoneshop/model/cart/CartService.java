package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.cart.storage.CartStorage;

/*
в лекции у нас тут был метод Cart getCart(HttpServletRequest request)
и проблема была в жёсткой зависимости
ведь идея CartService была в его потенциальной переиспользуемости
а мы явно делали его зависимым от ServletApi

и решением такого недуга стал интерфейс CartStorage (мне кажется, что его название не олицетворяет его суть полностью)
и реализация этого интерфейса, что уже и зависела от ServletApi
 */
public interface CartService {
    Cart getCartFromCartStorage(CartStorage storage);
    void add(Cart cart, Long productId, int quantity) throws OutOfStockException;
}
