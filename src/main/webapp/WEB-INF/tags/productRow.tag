<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ attribute name="product" required="true" type="com.es.phoneshop.model.product.Product" description="Product object to display" %>

<form method="post" action="${pageContext.servletContext.contextPath}/cart/addCartItem?productId=${product.id}&">
<tr>
  <td>
    <img class="product-tile" src="${product.imageUrl}">
  </td>
  <td class="product-container">
    <a href="${pageContext.servletContext.contextPath}/products/${product.id}" class="product-link">
      ${product.description}
    </a>
  </td>
  <td>
      <fmt:formatNumber value="${product.stock}" var="stock"/>
      <input name="quantity" value="1" class="quantity"/>
      <c:if test="${param.productId == product.id}">
        <c:if test="${not empty param.message}">
            <div class="success">
                ${param.message}
            </div>
        </c:if>
        <c:if test="${not empty param.error}">
            <div class="error">
                ${param.error}
            </div>
        </c:if>
        </c:if>
  </td>
  <td class="price">
    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
    <tags:priceHistory priceHistories="${product.productHistories}"/>
  </td>
  <td>
    <button>
        Add to cart
    </button>
  </td>
</tr>
</form>
