<jsp:useBean id="kibanadashboard" scope="session" class="fr.paris.lutece.plugins.kibana.web.KibanaDashboardJspBean" />
<% String strContent = kibanadashboard.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
