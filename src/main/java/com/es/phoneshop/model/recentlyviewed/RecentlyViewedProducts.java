package com.es.phoneshop.model.recentlyviewed;

/*
мне важно, чтобы
    - элементы были уникальными (что-то близкое по Set)
    - сохранялся порядок очереди (Queue, конкретно -- LinkedList)
    - если мы добавляем существующий элемент в очередь, то мы удаляем его из неё И добавляем в начало
    мой выбор пал на LinkedHashSet и LinkedList
    но для работы с Queue и тк у нас 3 элемента, то я реализую на LinkedList
 */

import com.es.phoneshop.model.product.Product;

import java.util.List;

public interface RecentlyViewedProducts {
    List<Product> getRecentlyViewedProductsList();
    void add(Product product);
}
