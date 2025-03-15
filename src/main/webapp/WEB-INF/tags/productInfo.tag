<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="product" required="true" type="com.es.phoneshop.model.product.Product" description="Product object to display" %>

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
        <td>
            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
        </td>
    </tr>
</table>

