<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="managekibana" scope="session" class="fr.paris.lutece.plugins.kibana.web.ManageKibanaJspBean" />

<% managekibana.init( request, managekibana.RIGHT_MANAGEKIBANA ); %>
<%= managekibana.getManageKibanaHome ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
