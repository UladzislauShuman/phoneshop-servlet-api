<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
    <c:if test="${not empty param.error_empty_cart}">
        <div class="error">
            ${param.error_empty_cart}
        </div>
      </c:if>

    <c:if test="${not empty param.error}">
        <div class="error">
            ${param.error}
        </div>
      </c:if>

    <c:if test="${not empty param.message}">
      <div class="success">
          ${param.message}
      </div>
    </c:if>

  <c:if test="${not empty param.errors}">
    <div class="error">
        Error
    </div>
  </c:if>

  <form method="post" action="${pageContext.servletContext.contextPath}/cart">
      <table>
        <thead>
          <tr>
            <td>
                Image
            </td>
            <td>
                Description
            </td>
            <td class="quantity">
                Quantity
            </td>
            <td class="price">
                Price
            </td>
            <td></td>
          </tr>
        </thead>
        <c:forEach var="cartItem" items="${cart.items}" varStatus="status">

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
              <c:set var="error" value="${errors[cartItem.product.id]}"/>
              <input name="quantity" value="${not empty error ? paramValues['quantity'][status.index] : cartItem.quantity}" class="quantity"/>
              <c:if test="${not empty error}">
                  <div class="error">
                      ${errors[cartItem.product.id]}
                  </div>
              </c:if>
              <input type="hidden" name="productId" value="${cartItem.product.id}"/>
            </td>
            <td class="price">
              <fmt:formatNumber value="${cartItem.product.price}" type="currency" currencySymbol="${cartItem.product.currency.symbol}"/>
              <tags:priceHistory priceHistories="${cartItem.product.productHistories}"/>
            </td>
            <td>
                <button form="deleteCartItem" formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${cartItem.product.id}">
                    Delete
                </button>
            </td>
          </tr>

        </c:forEach>
        <tr>
            <td></td>
            <td></td>
            <td class="quantity">
                ${cart.totalQuantity}
            </td>
            <td class="cost">
                <fmt:formatNumber value="${cart.totalCost}" type="currency" currencySymbol="$"/>
            </td>
            <td></td>
        </tr>
      </table>
      <p>
        <button>Update</button>
      </p>
  </form>
  <form action="${pageContext.servletContext.contextPath}/checkout" method="GET">
    <button>Checkout</button>
  </form>
  <form id="deleteCartItem" method="post"></form>
</tags:master>
