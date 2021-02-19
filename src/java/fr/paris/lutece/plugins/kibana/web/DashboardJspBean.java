/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.kibana.web;

import fr.paris.lutece.plugins.kibana.business.Dashboard;
import fr.paris.lutece.plugins.kibana.business.DashboardHome;
import fr.paris.lutece.plugins.kibana.service.DashboardService;
import fr.paris.lutece.plugins.kibana.utils.constants.KibanaConstants;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage Dashboard features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDashboards.jsp", controllerPath = "jsp/admin/plugins/kibana/", right = "KIBANA_MANAGEMENT" )
public class DashboardJspBean extends ManageKibanaJspBean
{
    // Uid
    private static final long serialVersionUID = 2675669204436156692L;

    // Templates
    private static final String TEMPLATE_MANAGE_DASHBOARDS = "/admin/plugins/kibana/manage_dashboards.html";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DASHBOARDS = "kibana.manage_dashboards.pageTitle";

    // Markers
    private static final String MARK_DASHBOARD_LIST = "dashboard_list";

    // JSP
    private static final String JSP_MANAGE_DASHBOARDS = "jsp/admin/plugins/kibana/ManageDashboards.jsp";

    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_DASHBOARD = "kibana.remove_dashboard.confirm";

    // Views
    private static final String VIEW_MANAGE_DASHBOARDS = "manageDashboards";

    // Actions
    private static final String ACTION_IMPORT_DASHBOARDS = "importDashboards";
    private static final String ACTION_CONFIRM_REMOVE_DASHBOARD = "confirmRemoveDashboard";
    private static final String ACTION_REMOVE_DASHBOARD = "removeDashboard";

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DASHBOARDS, defaultView = true )
    public String getManageDashboards( HttpServletRequest request )
    {
        List<Dashboard> listDashboards = DashboardHome.getDashboardsList( );
        Map<String, Object> model = getPaginatedListModel( request, MARK_DASHBOARD_LIST, listDashboards, JSP_MANAGE_DASHBOARDS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DASHBOARDS, TEMPLATE_MANAGE_DASHBOARDS, model );
    }

    /**
     * Import all dashboards from JSON to database. Insert only if it didnt exist.
     * 
     * @param request
     * @return
     */
    @Action( value = ACTION_IMPORT_DASHBOARDS )
    public String doImportDashboards( HttpServletRequest request )
    {
        DashboardService.getInstance( ).createAllDashboards( );
        return redirectView( request, VIEW_MANAGE_DASHBOARDS );
    }

    /**
     * Ask admin user for confirmation about removing dashboard
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DASHBOARD )
    public String getConfirmRemoveDashboard( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( KibanaConstants.PARAMETER_ID_DASHBOARD ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DASHBOARD ) );
        url.addParameter( KibanaConstants.PARAMETER_ID_DASHBOARD, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DASHBOARD, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Import all dashboards from JSON to database. Insert only if it didnt exist.
     * 
     * @param request
     * @return
     */
    @Action( value = ACTION_REMOVE_DASHBOARD )
    public String doDeleteDashboard( HttpServletRequest request )
    {
        int nIdDashboard = Integer.parseInt( request.getParameter( KibanaConstants.PARAMETER_ID_DASHBOARD ) );

        DashboardService.getInstance( ).deleteDashboard( nIdDashboard );

        return redirectView( request, VIEW_MANAGE_DASHBOARDS );
    }

}
