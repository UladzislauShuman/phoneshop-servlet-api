<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ attribute name="product" required="true" type="com.es.phoneshop.model.product.Product" description="Product object to display" %>

<div class="product-item">
    <img class="product-tile" src="${product.imageUrl}" alt="${product.description}">
    <div class="product-details">
        <a href="${pageContext.servletContext.contextPath}/products/${product.id}" class="product-link">
            <div clas="product-details">
                ${product.description}
            </div>
        </a>
        <div class="product-price">
            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
        </div>
    </div>
</div>
