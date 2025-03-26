<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ attribute name="recently_products" required="true" type="java.util.List" description="recently products display" %>

<p style="font-weight: bold;">
    Recently viewed phones
</p>

<div style="display: flex; flex-direction: row;">
    <c:forEach var="product" items="${recently_products}">
        <tags:productSquare product="${product}"/>
    </c:forEach>
</div>
