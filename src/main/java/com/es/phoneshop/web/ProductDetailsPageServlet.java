package com.es.phoneshop.web;

import com.es.phoneshop.model.product.*;
import com.es.phoneshop.model.product.exceptions.ProductNotFoundException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ProductDetailsPageServlet extends HttpServlet {
    public static final String ATTRIBUTE_PRODUCT = "product";
    public static final String ATTRIBUTE_PRODUCTCODE = "productCode";
    public static final String PRODUCT_JSP_PATH = "/WEB-INF/pages/product.jsp";
    public static final String INVALID_ID_FORMAT = "Invalid product ID format";

    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.productDao == null) {
            productDao = HashMapProductDao.getInstance();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String productId = request.getPathInfo().substring(1);
            Product product = this.productDao.getProduct(
                    Long.valueOf(productId)
            );
            request.setAttribute(ATTRIBUTE_PRODUCT, product);
            request.getRequestDispatcher(PRODUCT_JSP_PATH).forward(request, response);

        } catch (ProductNotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            request.setAttribute(ATTRIBUTE_PRODUCTCODE, request.getPathInfo().substring(1));
            request.getRequestDispatcher(ErrorPagePath.ERROR_404.toString()).forward(request, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, INVALID_ID_FORMAT);
        }
    }
}
