<%@ attribute name="queryParamName" required="false" type="java.lang.String" description="Name of the query parameter" %>
<%@ attribute name="buttonText" required="false" type="java.lang.String" description="Text to display on the button" %>

<form>
    <input name="${empty queryParamName ? 'query' : queryParamName}" value="${param[empty queryParamName ? 'query' : queryParamName]}">
    <button>${empty buttonText ? 'Search' : buttonText}</button>
</form>
