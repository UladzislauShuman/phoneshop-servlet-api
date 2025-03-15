<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>

  <tags:searchForm/>
  <tags:productTable products="${products}"/>

</tags:master>

<style>
  .tooltip {
    display: none;
    position: absolute;
    background: white;
    border: 1px solid #ccc;
    box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.2);
    padding: 10px;
    font-size: 14px;
    z-index: 1000;
    width: 250px;
    left: 100%;
  }

  .price {
    position: relative;
    cursor: pointer; /* Добавим, чтобы было понятно, что можно навести */
  }

  .price:hover .tooltip {
    display: block;
  }

</style>
