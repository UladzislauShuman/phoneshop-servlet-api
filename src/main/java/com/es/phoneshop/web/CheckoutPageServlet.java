package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.utils.HttpSessionCartReader;
import com.es.phoneshop.model.enums.PaymentMethod;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import com.es.phoneshop.utils.LoggerHelper;
import com.es.phoneshop.utils.PhoneValidator;
import com.es.phoneshop.utils.RedirectPathFormater;
import com.es.phoneshop.web.listeners.DependenciesServletContextListener;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class CheckoutPageServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CheckoutPageServlet.class);

    public static final String ATTRIBUTE_ERRORS = "errors";
    public static final String ATTRIBUTE_ORDER = "order";
    public static final String CHECKOUT_JSP_PATH = "/WEB-INF/pages/checkout.jsp";
    public static final String ATTRIBUTE_PAYMENT_METHODS = "paymentMethods";
    public static final String ORDER_FIRST_NAME = "firstName";
    public static final String ORDER_LAST_NAME = "lastName";
    public static final String ORDER_PHONE = "phone";
    public static final String ORDER_DELIVERY_ADDRESS = "deliveryAddress";
    public static final String MESSAGE_WHEN_EMPTY_INPUT = "Value is required";
    public static final String PARAMETER_PAYMENT_METHOD = "paymentMethod";
    public static final String PARAMETER_DELIVERY_DATE = "deliveryDate";
    public static final String MESSAGE_BAD_DATE = "Bad date";
    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String REDIRECT_ORDER_OVERVIEW = "/order/overview/%s";
    public static final String ERROR_CHECKOUT_PAGE_CART_SERVICE_NULL = "Checkout Page: cartService == null";
    public static final String ERROR_CHECKOUT_PAGE_ORDER_SERVICE_NULL = "Checkout Page: orderService == null";
    public static final String MESSAGE_DELIVERY_DATE_IN_PAST = "Bad date (We will send your order no earlier than tomorrow.)";
    public static final String INVALID_PHONE_NUMBER = "Invalid phone number";
    public static final int MIN_DELIVERY_DAYS = 1;
    public static final String REDIRECT_ERROR_EMPTY_CART = "/cart?error_empty_cart=%s";
    public static final String ERROR_YOUR_CART_IS_EMPTY = "Your cart is empty";
    public static final String ERRORS_PUT_PARAMETER_S_E_S = "errors.put: parameter = %s, e: %s ";
    public static final String ATTRIBUTE_CART = "cart";

    private CartService cartService;
    private OrderService orderService;

    public CheckoutPageServlet(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    public CheckoutPageServlet() {
        this.cartService = null;
        this.orderService = null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        LoggerHelper.logInit(logger,LoggerHelper.BEGIN);
        ServletContext context = config.getServletContext();
        cartService = (CartService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_CART_SERVICE);
        orderService = (OrderService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_ORDER_SERVICE);
        throwIfNullAttributes();
        LoggerHelper.logInit(logger, LoggerHelper.SUCCESS);
    }

    private void throwIfNullAttributes() throws ServletException {
        if (cartService == null) {
            LoggerHelper.logInit(logger, ERROR_CHECKOUT_PAGE_CART_SERVICE_NULL);
            throw new ServletException(ERROR_CHECKOUT_PAGE_CART_SERVICE_NULL);
        } else if (orderService == null) {
            LoggerHelper.logInit(logger, ERROR_CHECKOUT_PAGE_ORDER_SERVICE_NULL);
            throw new ServletException(ERROR_CHECKOUT_PAGE_ORDER_SERVICE_NULL);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            LoggerHelper.logDoGet(logger, LoggerHelper.BEGIN);
            if (setAttributesToRequestIfCartNotEmpty(request, response)) {
                request.getRequestDispatcher(CHECKOUT_JSP_PATH).forward(request, response);
            }
            LoggerHelper.logDoGet(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoGet(logger, e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private boolean setAttributesToRequestIfCartNotEmpty(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(RedirectPathFormater.formatErrorPath(request.getContextPath(), REDIRECT_ERROR_EMPTY_CART,
                    ERROR_YOUR_CART_IS_EMPTY));
            return false;
        }
        request.setAttribute(ATTRIBUTE_CART, cart);
        LoggerHelper.logDoGet(logger, cart.toString());

        request.setAttribute(ATTRIBUTE_ORDER, orderService.getOrder(cart));
        request.setAttribute(ATTRIBUTE_PAYMENT_METHODS, orderService.getPaymentMethods());
        return true;
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoggerHelper.logDoPost(logger, LoggerHelper.BEGIN);
        try {
            Cart cart = HttpSessionCartReader.getCartFromSession(request.getSession());
            Order order = orderService.getOrder(cart);

            Map<String, String> errors = new HashMap<>();
            setParametersToOrder(request,order,errors);
            LoggerHelper.logDoPost(logger, cart.toString());

            redirectRequest(request, response, errors, order);
            LoggerHelper.logDoPost(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoPost(logger, e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }

    }

    private void setParametersToOrder(HttpServletRequest request, Order order, Map<String, String> errors) {
        setRequiredParameter(request, ORDER_FIRST_NAME, errors, order::setFirstName);
        setRequiredParameter(request, ORDER_LAST_NAME, errors, order::setLastName);
        setPhone(request, errors, order);
        setRequiredParameter(request, ORDER_DELIVERY_ADDRESS, errors, order::setDeliveryAddress);
        request.setAttribute(ATTRIBUTE_ORDER, order);
        setDeliveryDate(request, errors, order);
        setPaymentMethod(request, errors, order);
    }

    private void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors,
                                      Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            LoggerHelper.logDoPost(logger,
                    String.format(ERRORS_PUT_PARAMETER_S_E_S, parameter, MESSAGE_WHEN_EMPTY_INPUT));
            errors.put(parameter, MESSAGE_WHEN_EMPTY_INPUT);
        } else {
            consumer.accept(value);
        }
    }

    private void setPhone(HttpServletRequest request, Map<String, String> errors, Order order) {
        String phone = request.getParameter(ORDER_PHONE);
        if (phone == null || phone.isEmpty()) {
            LoggerHelper.logDoPost(logger,
                    String.format(ERRORS_PUT_PARAMETER_S_E_S, ORDER_PHONE, MESSAGE_WHEN_EMPTY_INPUT));
            errors.put(ORDER_PHONE, MESSAGE_WHEN_EMPTY_INPUT);
        } else {
            if (PhoneValidator.isValidNumber(phone)) {
                order.setPhone(phone);
            } else {
                LoggerHelper.logDoPost(logger,
                        String.format(ERRORS_PUT_PARAMETER_S_E_S, ORDER_PHONE, INVALID_PHONE_NUMBER));
                errors.put(ORDER_PHONE, INVALID_PHONE_NUMBER);
            }
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        setValueFromRequest(request, errors, PARAMETER_PAYMENT_METHOD, MESSAGE_WHEN_EMPTY_INPUT,
                PaymentMethod::valueOf, order::setPaymentMethod, MESSAGE_WHEN_EMPTY_INPUT);
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        setValueFromRequest(request, errors, PARAMETER_DELIVERY_DATE, MESSAGE_BAD_DATE,
                dateString -> {
                    LocalDate parsedDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD));
                    LocalDate todayPlusOne = LocalDate.now().plusDays(MIN_DELIVERY_DAYS);
                    if (parsedDate.isBefore(todayPlusOne)) {
                        throw new IllegalArgumentException(MESSAGE_DELIVERY_DATE_IN_PAST);
                    }
                    return parsedDate;
                },
                order::setDeliveryDate, MESSAGE_DELIVERY_DATE_IN_PAST);
    }

    private <T> void setValueFromRequest(HttpServletRequest request, Map<String, String> errors,
             String parameter, String errorMessageEmpty, Function<String, T> valueConverter,
            Consumer<T> orderSetter, String errorMessageConversion) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            LoggerHelper.logDoPost(logger,
                    String.format(ERRORS_PUT_PARAMETER_S_E_S, parameter, errorMessageEmpty));
            errors.put(parameter, errorMessageEmpty);
        } else {
            try {
                T convertedValue = valueConverter.apply(value);
                orderSetter.accept(convertedValue);
            } catch (Exception e) {
                LoggerHelper.logDoPost(logger,
                        String.format(ERRORS_PUT_PARAMETER_S_E_S, parameter,
                                errorMessageConversion == null ? e.getMessage() : errorMessageConversion));
                errors.put(parameter, errorMessageConversion == null ? e.getMessage() : errorMessageConversion);
            }
        }
    }

    private void redirectRequest(HttpServletRequest request, HttpServletResponse response, Map<String, String> errors,
                                 Order order) throws IOException, ServletException {
        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            response.sendRedirect(RedirectPathFormater.formatSuccessPath(
                    request.getContextPath(), REDIRECT_ORDER_OVERVIEW, order.getSecureId()));
        } else {
            request.setAttribute(ATTRIBUTE_ERRORS, errors);
            request.setAttribute(ATTRIBUTE_PAYMENT_METHODS, orderService.getPaymentMethods());
            request.getRequestDispatcher(CHECKOUT_JSP_PATH).forward(request, response);
        }
    }
}
