package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.utils.LoggerHelper;
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
import java.util.Collections;
import java.util.List;

public class SearchPageServlet extends HttpServlet {
    public static final String EXCEPTIONS = "ServletException | IOException: %s";
    public static final String ATTRIBUTE_PRODUCTS = "products";

    public static final String PARAMETER_QUERY = "query";
    public static final String PARAMETER_SORT = "sort";
    public static final String PARAMETER_ORDER = "order";

    private static final String SERVLET_EXCEPTION_PRODUCT_DAO_NULL = "PLP: ProductDao == null";

    private ProductDao productDao;

    private static final Logger logger = LoggerFactory.getLogger(SearchPageServlet.class);
    public static final String SEARCHPAGE_JSP_PATH = "/WEB-INF/pages/searchpage.jsp";


    @Override
    public void init(ServletConfig config) throws ServletException {
        LoggerHelper.logInit(logger,LoggerHelper.BEGIN);
        super.init(config);
        ServletContext context = getServletContext();
        productDao = (ProductDao) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO);
        throwIfNullAttributes();
        LoggerHelper.logInit(logger, LoggerHelper.SUCCESS);
    }

    private void throwIfNullAttributes() throws ServletException {
        if (productDao == null) {
            LoggerHelper.logInit(logger, SERVLET_EXCEPTION_PRODUCT_DAO_NULL);
            throw new ServletException(SERVLET_EXCEPTION_PRODUCT_DAO_NULL);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LoggerHelper.logDoGet(logger, LoggerHelper.BEGIN);
        try {
            setAttributesToRequest(request);
            request.getRequestDispatcher(SEARCHPAGE_JSP_PATH).forward(request, response);
            LoggerHelper.logDoGet(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoGet(logger, e);
            handleDoGetExceptions(e, request, response);
        }
    }

    private void setAttributesToRequest(HttpServletRequest request) {
        request.setAttribute(ATTRIBUTE_PRODUCTS, getProductFromProductDao(request));
    }

    private List<Product> getProductFromProductDao(HttpServletRequest request) {
        return this.productDao.findProducts(
                request.getParameter(PARAMETER_QUERY),
                request.getParameter(PARAMETER_SORT),
                request.getParameter(PARAMETER_ORDER)
        );
    }


    private void handleDoGetExceptions(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setAttribute(ATTRIBUTE_PRODUCTS, Collections.emptyList());
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(EXCEPTIONS, e.getMessage()));
    }

}

