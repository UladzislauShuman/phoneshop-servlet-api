<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ attribute name="product" required="true" type="com.es.phoneshop.model.product.Product" description="Product object to display" %>

<tr>
  <td>
    <img class="product-tile" src="${product.imageUrl}">
  </td>
  <td class="product-container">
    <a href="${pageContext.servletContext.contextPath}/products/${product.id}" class="product-link">
      ${product.description}
    </a>
  </td>
  <td class="price">
    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
    <tags:priceHistory priceHistories="${product.productHistories}"/>
  </td>
</tr>
