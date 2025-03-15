package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;

import com.es.phoneshop.model.product.HashMapProductDao;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

public class ProductListPageServlet extends HttpServlet {
    public static final String PRODUCTLIST_JSP_PATH = "/WEB-INF/pages/productList.jsp";
    public static final String EXCEPTIONS = "ServletException | IOException: %s";
    public static final String ATTRIBUTE = "products";

    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.productDao == null) {
            productDao = HashMapProductDao.getInstance();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute(ATTRIBUTE,
                    this.productDao.findProducts(
                            request.getParameter("query"),
                            request.getParameter("sort"),
                            request.getParameter("order")
                    )
            );
            request.getRequestDispatcher(PRODUCTLIST_JSP_PATH).forward(request, response);
        }
        catch (
                ServletException |
                IOException
                    e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(
                    EXCEPTIONS, e.getMessage()
            ));
        } catch (Exception e) {
            request.setAttribute(ATTRIBUTE, Collections.emptyList());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
