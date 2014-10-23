<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="kibanadashboard" scope="session" class="fr.paris.lutece.plugins.kibana.web.KibanaDashboardJspBean" />

<% kibanadashboard.init( request, kibanadashboard.RIGHT_KIBANADASHBOARD ); %>
<%= kibanadashboard.getKibanaDashboardHome ( request ) %>

<%@ include file="../../AdminFooter.jsp" %>
