<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="priceHistories" required="true" type="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="tooltip">
    <strong>Price History</strong>
    <c:choose>
        <c:when test="${not empty priceHistories}">
            <table>
                <tr>
                    <th>Date</th>
                    <th>Price</th>
                </tr>
                <c:forEach var="history" items="${priceHistories}">
                    <tr>
                        <td><fmt:formatDate value="${history.dateAsUtilDate}" pattern="yyyy-MM-dd"/></td>
                        <td><fmt:formatNumber value="${history.price}" type="currency" currencySymbol="${history.currency.symbol}"/></td>
                    </tr>
                </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            <div>No price history available.</div>
        </c:otherwise>
    </c:choose>
</div>

