package com.es.phoneshop.model.product.OrderDaoTests;

import com.es.phoneshop.model.dao.HashMapOrderDao;
import com.es.phoneshop.model.exceptions.OrderNotFoundException;
import com.es.phoneshop.model.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class HashMapOrderDaoTest {
    public static final String TEST_SECURE_ID = "test_secureId";
    public static final long TEST_ORDER_ID = 1L;
    public static final long ORDER_ID_1 = 1L;
    public static final long ORDER_ID_2 = 2L;
    public static final String TEST_NON_EXISTING_SECURE_ID = "nonExistingSecureId";

    private HashMapOrderDao orderDao = (HashMapOrderDao) HashMapOrderDao.getInstance();

    @BeforeEach
    public void setup() {
        orderDao.clear();
    }

    @Test
    public void testGetInstance() {
        HashMapOrderDao instance1 = (HashMapOrderDao) HashMapOrderDao.getInstance();
        HashMapOrderDao instance2 = (HashMapOrderDao) HashMapOrderDao.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    public void testGetItem_existingOrder() throws OrderNotFoundException {
        Order order = createTestOrder(1L);
        orderDao.save(order);

        Order retrievedOrder = orderDao.getItem(1L);

        assertEquals(order, retrievedOrder);
    }

    @Test
    public void testGetItem_nonExistingOrder() {
        assertThrows(OrderNotFoundException.class, () -> orderDao.getItem(1L));
    }

    @Test
    public void testGetItem_nullId() {
        assertThrows(OrderNotFoundException.class, () -> orderDao.getItem(null));
    }

    @Test
    public void testSave_newOrder() throws OrderNotFoundException {
        Order order = createTestOrder(null);

        orderDao.save(order);

        assertNotNull(order.getId());
        Order retrievedOrder = orderDao.getItem(order.getId());
        assertEquals(order, retrievedOrder);
    }

    @Test
    public void testSave_existingOrder() throws OrderNotFoundException {
        Order order = createTestOrder(1L);
        orderDao.save(order);

        order.setSecureId(TEST_SECURE_ID);
        orderDao.save(order);
        Order retrievedOrder = orderDao.getItem(1L);

        assertEquals(TEST_SECURE_ID, retrievedOrder.getSecureId());
    }


    @Test
    public void testSave_nullOrder() {
        assertThrows(OrderNotFoundException.class, () -> orderDao.save(null));
    }

    @Test
    public void testDelete_existingOrder() throws OrderNotFoundException {
        Order order = createTestOrder(TEST_ORDER_ID);
        orderDao.save(order);

        orderDao.delete(TEST_ORDER_ID);

        assertThrows(OrderNotFoundException.class, () -> orderDao.getItem(TEST_ORDER_ID));
    }

    @Test
    public void testDelete_nonExistingOrder() {
        assertDoesNotThrow(() -> orderDao.delete(1L));
    }

    @Test
    public void testClear() throws OrderNotFoundException {
        Order order1 = createTestOrder(ORDER_ID_1);
        Order order2 = createTestOrder(ORDER_ID_2);
        orderDao.save(order1);
        orderDao.save(order2);

        orderDao.clear();

        assertTrue(orderDao.isEmpty());
    }

    @Test
    public void testIsEmpty_emptyDao() {
        assertTrue(orderDao.isEmpty());
    }

    @Test
    public void testIsEmpty_nonEmptyDao() throws OrderNotFoundException {
        Order order = createTestOrder(TEST_ORDER_ID);
        orderDao.save(order);

        assertFalse(orderDao.isEmpty());
    }

    @Test
    public void testGetOrderBySecureId_existingOrder() throws OrderNotFoundException {
        Order order = createTestOrder(TEST_ORDER_ID);
        order.setSecureId(TEST_SECURE_ID);
        orderDao.save(order);

        Order retrievedOrder = orderDao.getOrderBySecureId(TEST_SECURE_ID);

        assertEquals(order, retrievedOrder);
    }

    @Test
    public void testGetOrderBySecureId_nonExistingOrder() {
        assertThrows(OrderNotFoundException.class, () -> orderDao.getOrderBySecureId(TEST_NON_EXISTING_SECURE_ID));
    }

    private Order createTestOrder(Long id) {
        Order order = new Order();
        order.setId(id);
        return order;
    }
}
