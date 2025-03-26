<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="product" required="true" type="com.es.phoneshop.model.product.Product" description="Product object to display" %>

<form method="post">
    <table>
        <tr>
            <td>Image</td>
            <td>
                <img src="${product.imageUrl}">
            </td>
        </tr>
        <tr>
            <td>code</td>
            <td>
                ${product.code}
            </td>
        </tr>
        <tr>
            <td>stock</td>
            <td>
                ${product.stock}
            </td>
        </tr>
        <tr>
            <td>Price</td>
            <td class="price">
                <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
            </td>
        </tr>
        <tr>
            <td>quantity</td>
            <td class="quantity">
                <input name="quantity" value="${not empty param.error ? param.quantity : 1}" class="quantity">
                <c:if test="${not empty param.error}">
                    <div class="error">
                        ${param.error}
                    </div>
                </c:if>
            </td>
        </tr>
    </table>
    <p>
        <button>Add to cart</button>
    </p>
</form>

