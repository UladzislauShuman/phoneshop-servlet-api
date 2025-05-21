package com.es.phoneshop.web;

import com.es.phoneshop.model.exceptions.InvalidPriceException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.utils.LoggerHelper;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchPageServlet extends HttpServlet {
    public static final String EXCEPTIONS = "ServletException | IOException: %s";
    public static final String ATTRIBUTE_PRODUCTS = "products";

    public static final String PARAMETER_QUERY = "query";
    public static final String PARAMETER_SORT = "sort";
    public static final String PARAMETER_ORDER = "order";

    private static final String SERVLET_EXCEPTION_PRODUCT_DAO_NULL = "PLP: ProductDao == null";
    public static final String ATTRIBUTE_ERRORS = "errors";
    public static final String NOT_A_NUMBER_OR_EMPTY = "Not a number or empty";
    public static final String ERRO_MIN_PRICE = "min_price";

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
            Map<String, String> errors = new HashMap<>();
            setAttributesToRequest(request, errors);
            redirectRequest(request, response, errors);
            LoggerHelper.logDoGet(logger, LoggerHelper.SUCCESS);
        } catch (Exception e) {
            LoggerHelper.logDoGet(logger, e);
            handleDoGetExceptions(e, request, response);
        }
    }

    private void setAttributesToRequest(HttpServletRequest request, Map<String, String> errors) {

        String query = null, minPriceString = null, maxPriceString = null;
        getParameters(request, query, minPriceString, maxPriceString);
        BigDecimal minPrice = null, maxPrice = null;
        query = request.getParameter("description");
        minPriceString = request.getParameter("min_price");
        maxPriceString = request.getParameter("max_price");

        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();
        format.setParseBigDecimal(true);

        try{
            minPrice = (BigDecimal) format.parse(minPriceString);
        }catch (ParseException e) {
            LoggerHelper.logDoGet(logger, "Not a number");
            errors.put(ERRO_MIN_PRICE, NOT_A_NUMBER_OR_EMPTY);
        } catch (NullPointerException e) {
            minPrice = null;
        } catch (Exception e) {
            errors.put(ERRO_MIN_PRICE, e.getMessage());
        }

        try {
            maxPrice = (BigDecimal) format.parse(maxPriceString);
        } catch (ParseException e) {

            errors.put("max_price", NOT_A_NUMBER_OR_EMPTY);
        } catch (NullPointerException e) {
            maxPrice = null;
        } catch (Exception e) {
            errors.put("max_price", e.getMessage());
        }

        request.setAttribute(ATTRIBUTE_PRODUCTS, getProductFromProductDao(query, minPrice, maxPrice));
        request.setAttribute(ATTRIBUTE_ERRORS, errors);
    }
    private void redirectRequest(HttpServletRequest request, HttpServletResponse response, Map<String, String> errors) throws IOException, ServletException {
        //request.getSession().setAttribute(ATTRIBUTE_ERRORS, errors);
        request.getRequestDispatcher(SEARCHPAGE_JSP_PATH).forward(request, response);
    }

    private void getParameters(HttpServletRequest request, String query,String minPriceString,String maxPriceString) {

    }

    private List<Product> getProductFromProductDao(String query, BigDecimal minPrice, BigDecimal maxPrice) {
        return this.productDao.findProducts(query, minPrice, maxPrice);
    }


    private void handleDoGetExceptions(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setAttribute(ATTRIBUTE_PRODUCTS, Collections.emptyList());
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(EXCEPTIONS, e.getMessage()));
    }

}

