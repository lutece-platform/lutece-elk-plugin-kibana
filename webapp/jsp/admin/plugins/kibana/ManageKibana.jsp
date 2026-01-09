<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.plugins.kibana.web.ManageKibanaJspBean"%>

${ manageKibanaJspBean.init( pageContext.request, ManageKibanaJspBean.RIGHT_MANAGEKIBANA ) }
${ manageKibanaJspBean.getManageKibanaHome( pageContext.request ) }

<%@ include file="../../AdminFooter.jsp" %>
