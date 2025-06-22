<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ attribute name="products" required="true" type="java.util.List" description="List of products to display" %>

<table>
  <thead>
    <tr>
      <td>Image</td>
      <td>
        Description
      </td>
      <td class="price">
        Price
      </td>
    </tr>
  </thead>
  <c:forEach var="product" items="${products}">
    <tags:searchPageProductRow product="${product}"/>
  </c:forEach>
</table>
