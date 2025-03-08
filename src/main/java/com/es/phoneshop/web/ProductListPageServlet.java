package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;

import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class ProductListPageServlet extends HttpServlet {
    private ProductDao productDao;

    @Override
    public void init(ServletConfig config) throws ServletException { // гарантировано выполняется в одном потоке(?)
        super.init(config);
        if (this.productDao == null) {
            productDao = new ArrayListProductDao();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("products", this.productDao.findProducts());
        } catch (Exception e) {
            request.setAttribute("products", Collections.emptyList());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "exception in this.productDao.findProducts()");
        }

        try {
            request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
        } catch (
                ServletException |
                IOException
                    e)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ServletException | IOException");
        }
    }
}
