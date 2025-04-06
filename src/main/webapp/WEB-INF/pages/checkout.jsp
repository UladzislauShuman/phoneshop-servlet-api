<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Checkout">

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

  <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
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
        <c:forEach var="item" items="${order.items}" varStatus="status">

          <tr>
            <td>
              <img class="product-tile" src="${item.product.imageUrl}">
            </td>
            <td class="product-container">
              <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}" class="product-link">
                ${item.product.description}
              </a>
            </td>
            <td class="quantity">
              <fmt:formatNumber value="${item.quantity}" var="quantity"/>
              ${item.quantity}
            </td>
            <td class="price">
              <fmt:formatNumber value="${item.product.price}" type="currency" currencySymbol="${item.product.currency.symbol}"/>
              <tags:priceHistory priceHistories="${item.product.productHistories}"/>
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
        <tags:orderFormRow name="firstName" label="First name" order="${order}" errors="${errors}"></tags:orderFormRow>
        <tags:orderFormRow name="lastName" label="Last name" order="${order}" errors="${errors}"></tags:orderFormRow>
        <tags:orderFormRow name="phone" label="Phone" order="${order}" errors="${errors}"></tags:orderFormRow>

        <tr>
            <td>Delivery date<span style="color:red">*</span></td>
            <td>
                <c:set var="error" value="${errors['deliveryDate']}"/>
                <input type="date" name="deliveryDate" value="${not empty error ? param['deliveryDate'] : order['deliveryDate']}"/>
                <c:if test="${not empty error}">
                  <div class="error">
                      ${error}
                  </div>
                </c:if>
            </td>
        </tr>

        <tags:orderFormRow name="deliveryAddress" label="Delivery address" order="${order}" errors="${errors}"></tags:orderFormRow>

        <tr>
            <td>Payment method<span style="color:red">*</span></td>
            <td>
                <c:set var="error" value="${errors['paymentMethod']}"/>
                <select name="paymentMethod">
                    <option></option>
                    <c:forEach var="paymentMethod" items="${paymentMethods}">
                        <option value="${paymentMethod}"
                                ${(not empty error and param['paymentMethod'] == paymentMethod)
                                   or (empty error and order.paymentMethod == paymentMethod) ? 'selected' : ''}>
                            ${paymentMethod}
                        </option>
                    </c:forEach>
                </select>
                <c:if test="${not empty error}">
                    <div class="error">${error}</div>
                </c:if>
            </td>
        </tr>

      </table>

      <p>
        <button>Place order</button>
      </p>
  </form>
</tags:master>
