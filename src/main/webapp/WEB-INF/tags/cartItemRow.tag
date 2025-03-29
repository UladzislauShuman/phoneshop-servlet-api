<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ attribute name="cartItem" required="true" type="com.es.phoneshop.model.cart.CartItem" description="CartItem object to display" %>

<tr>
  <td>
    <img class="product-tile" src="${cartItem.product.imageUrl}">
  </td>
  <td class="product-container">
    <a href="${pageContext.servletContext.contextPath}/products/${cartItem.product.id}" class="product-link">
      ${cartItem.product.description}
    </a>
  </td>
  <td class="quantity">
    <fmt:formatNumber value="${cartItem.quantity}" var="quantity"/>
    <c:set var="error" value="errors[cartItem.product.id]}"/>
    <input name="quantity" value="${not empty error ?  quantity}" class="quantity"/>
    <c:if test="${not empty errors[cartItem.product.id]}">
        <div class="error">
            ${error}
        </div>
    </c:if>
    <input type="hidden" name="productId" value="${cartItem.product.id}"/>
  </td>
  <td class="price">
    <fmt:formatNumber value="${cartItem.product.price}" type="currency" currencySymbol="${cartItem.product.currency.symbol}"/>
    <tags:priceHistory priceHistories="${cartItem.product.productHistories}"/>
  </td>
</tr>
