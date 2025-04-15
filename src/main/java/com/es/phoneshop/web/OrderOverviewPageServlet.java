package com.es.phoneshop.web;

import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.utils.LoggerHelper;
import com.es.phoneshop.web.listeners.DependenciesServletContextListener;
import com.es.phoneshop.utils.HttpSessionCartReader;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(OrderOverviewPageServlet.class);

    public static final String ATTRIBUTE_ORDER = "order";
    public static final String ORDER_OVERVIEW_JSP_PATH = "/WEB-INF/pages/orderOverview.jsp";
    public static final String EXCEPTION_ORDER_DAO_IS_NULL = "OrderDao is null";

    private OrderDao orderDao;

    public OrderOverviewPageServlet() {
        this.orderDao = null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        LoggerHelper.logInit(logger, LoggerHelper.BEGIN);
        super.init(config);
        ServletContext context = config.getServletContext();
        orderDao = (OrderDao) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_ORDER_DAO);
        throwIfNullAttributes();
        LoggerHelper.logInit(logger, LoggerHelper.SUCCESS);
    }

    private void throwIfNullAttributes() throws ServletException {
        if (orderDao == null) {
            LoggerHelper.logInit(logger, EXCEPTION_ORDER_DAO_IS_NULL);
            throw new ServletException(EXCEPTION_ORDER_DAO_IS_NULL);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            LoggerHelper.logDoGet(logger, LoggerHelper.BEGIN);
            String orderSecureId = parseSecureId(request);
            request.setAttribute(ATTRIBUTE_ORDER, orderDao.getOrderBySecureId(orderSecureId));

            HttpSessionCartReader.cleanCartFromSession(request.getSession());

            request.getRequestDispatcher(ORDER_OVERVIEW_JSP_PATH).forward(request, response);
            LoggerHelper.logDoGet(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoGet(logger, e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    private String parseSecureId(HttpServletRequest request) {
        return request.getPathInfo().substring(1);
    }
}
