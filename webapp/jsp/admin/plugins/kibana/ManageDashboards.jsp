<jsp:useBean id="managekibanaDashboard" scope="session" class="fr.paris.lutece.plugins.kibana.web.DashboardJspBean" />
<% String strContent = managekibanaDashboard.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
