package com.es.phoneshop.web;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProducts;
import com.es.phoneshop.model.product.recentlyviewed.RecentlyViewedProductsService;
import com.es.phoneshop.model.product.recentlyviewed.storage.HttpSessionRecentlyViewedProductsStorage;
import com.es.phoneshop.web.listeners.DependenciesServletContextListener;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ProductListPageServlet extends HttpServlet {
    public static final String PRODUCTLIST_JSP_PATH = "/WEB-INF/pages/productList.jsp";
    public static final String EXCEPTIONS = "ServletException | IOException: %s";
    public static final String ATTRIBUTE_PRODUCTS = "products";
    public static final String ATTRIBUTE_RECENTLY_PRODUCTS = "recently_products";

    public static final String PARAMETER_QUERY = "query";
    public static final String PARAMETER_SORT = "sort";
    public static final String PARAMETER_ORDER = "order";

    private ProductDao productDao;
    private RecentlyViewedProductsService recentlyViewedProductsService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if (this.productDao == null) {
            ServletContext context = getServletContext();
            productDao = (ProductDao) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_PRODUCT_DAO);
            recentlyViewedProductsService = (RecentlyViewedProductsService) context.getAttribute(DependenciesServletContextListener.ATTRIBUTE_RECENTLY_VIEWED_PRODUCTS_SERVICE);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setAttribute(ATTRIBUTE_PRODUCTS,
                    this.productDao.findProducts(
                            request.getParameter(PARAMETER_QUERY),
                            request.getParameter(PARAMETER_SORT),
                            request.getParameter(PARAMETER_ORDER)
                    )
            );
            RecentlyViewedProducts recentlyViewedProducts = recentlyViewedProductsService
                    .getRecentlyViewedProductsFromStorage(
                            new HttpSessionRecentlyViewedProductsStorage(recentlyViewedProductsService,request)
                    );
            List<Product> recentlyProducts = recentlyViewedProducts
                    .getRecentlyViewedProductsList();

            request.setAttribute(ATTRIBUTE_RECENTLY_PRODUCTS, recentlyProducts);
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
            request.setAttribute(ATTRIBUTE_PRODUCTS, Collections.emptyList());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
