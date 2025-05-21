<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="SearchPage">
  <p>
    Advanced search
  </p>

    <c:if test="${not empty errors}">
        <div class="error">
            Error
        </div>
    </c:if>

  <form method="get" action="searchpage/">
      <label for="description">Description</label>
      <input type="text" id="description" name="description">
      <select name="description_param">
          <option>all words</option>
          <option>any words</option>
      </select>
      <br>

      <label for="min_price">Min price</label>
      <c:set var="error_min_price" value="${errors['min_price']}"/>
      <input type="text" id="min_price" name="min_price"><br>
      <c:if test="${not empty error_min_price}">
          <div class="error">${error_min_price}</div><br>
      </c:if>



      <label for="max_price">Max price</label>
      <c:set var="error_max_price" value="${errors['max_price']}"/>
      <input type="text" id="max_price" name="max_price"><br>
      <c:if test="${not empty error_max_price}">
          <div class="error">${error_max_price}</div><br>
      </c:if>

      <button>Search</button>
  </form>
  <tags:searchPageProductTable products="${products}"/>

</tags:master>
