<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="SearchPage">
  <p>
    Advanced search
  </p>

  <form method="get" action="searchpage/">
      <label for="description">Description</label>
      <input type="text" id="description" name="description">
      <select name="paymentMethod">
          <option>all words</option>
          <option>any words</option>
      </select>
      <br>

      <label for="min_price">Min price</label>
      <input type="text" id="min_price" name="min_price"><br>

      <label for="max_price">Max price</label>
      <input type="text" id="max_price" name="max_price"><br>

      <button>Search</button>
  </form>
  <tags:searchPageProductTable products="${products}"/>

</tags:master>
