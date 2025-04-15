<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Order overview">
      <h1>Order overview</h1>
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
          </tr>
        </thead>
        <c:forEach var="cartItem" items="${order.items}" varStatus="status">
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
              ${cartItem.quantity}
            </td>
            <td class="price">
              <fmt:formatNumber value="${cartItem.product.price}" type="currency" currencySymbol="${cartItem.product.currency.symbol}"/>
              <tags:priceHistory priceHistories="${cartItem.product.productHistories}"/>
            </td>
          </tr>

        </c:forEach>
        <tr>
            <td></td>
            <td class="quantity">
                Total quantity
            </td>
            <td class="quantity">
                ${order.totalQuantity}
            </td>
            <td class="cost"></td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td class="quantity">
                Subtotal
            </td>
            <td class="cost">
                <p>
                    <fmt:formatNumber value="${order.subtotal}" type="currency" currencySymbol="$"/>
                </p>
            </td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td class="quantity">
                Delivery cost
            </td>
            <td class="cost">
                <p>
                    <fmt:formatNumber value="${order.deliveryCost}" type="currency" currencySymbol="$"/>
                </p>
            </td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td class="quantity">
                Total cost
            </td>
            <td class="cost">
                <p>
                    <fmt:formatNumber value="${order.totalCost}" type="currency" currencySymbol="$"/>
                </p>
            </td>
        </tr>
      </table>

      <h2>Your details</h2>
      <table>
        <tags:orderOverviewRow name="firstName" label="First name" order="${order}"></tags:orderOverviewRow>
        <tags:orderOverviewRow name="lastName" label="Last name" order="${order}"></tags:orderOverviewRow>
        <tags:orderOverviewRow name="phone" label="Phone" order="${order}"></tags:orderOverviewRow>

        <tr>
            <td>Delivery date</td>
            <td type="date">
                ${order.deliveryDate}
            </td>
        </tr>


        <tags:orderOverviewRow name="deliveryAddress" label="Delivery address" order="${order}"></tags:orderOverviewRow>

        <tr>
            <td>Payment method</td>
            <td>
                ${order.paymentMethod}
            </td>
        </tr>

      </table>


</tags:master>
