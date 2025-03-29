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
        <tags:sortLink sort="description" order="asc"/>
        <tags:sortLink sort="description" order="desc"/>
      </td>
      <td>
         Quantity
      </td>
      <td class="price">
        Price
        <tags:sortLink sort="price" order="asc"/>
        <tags:sortLink sort="price" order="desc"/>
      </td>
      <td></td>
    </tr>
  </thead>
  <c:forEach var="product" items="${products}">
    <tags:productRow product="${product}"/>
  </c:forEach>
</table>
