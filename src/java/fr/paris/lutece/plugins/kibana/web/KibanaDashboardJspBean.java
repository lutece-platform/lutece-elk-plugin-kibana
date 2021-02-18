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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.kibana.business.Dashboard;
import fr.paris.lutece.plugins.kibana.service.DashboardService;
import fr.paris.lutece.plugins.kibana.service.NoElasticSearchServerException;
import fr.paris.lutece.plugins.kibana.service.NoKibanaIndexException;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;

/**
 * KibanaDashboard JSP Bean abstract class for JSP Bean
 */
@Controller( controllerJsp = "KibanaDashboard.jsp", controllerPath = "jsp/admin/plugins/kibana/", right = KibanaDashboardJspBean.RIGHT_KIBANADASHBOARD )
public class KibanaDashboardJspBean extends MVCAdminJspBean
{
    // Right
    public static final String RIGHT_KIBANADASHBOARD = "KIBANA_MANAGEMENT";

    // Uid
    private static final long serialVersionUID = -8829869449480096316L;

    // Templates
    private static final String TEMPLATE_DASHBOARD = "/admin/plugins/kibana/kibana_dashboard.html";
    private static final String TEMPLATE_DASHBOARD_LIST = "/admin/plugins/kibana/kibana_dashboard_list.html";
    private static final String TEMPLATE_ELASTICSEARH_ERROR = "/admin/plugins/kibana/elasticsearch_error.html";
    private static final String TEMPLATE_NO_DASHBOARD = "/admin/plugins/kibana/no_dashboard.html";

    // Views
    private static final String VIEW_DASHBOARD = "dashboard";
    private static final String VIEW_DASHBOARD_LIST = "dashboardList";

    // Properties
    private static final String PROPERTY_PAGE_TITLE_DASHBOARD = "kibana.adminFeature.KibanaDashboard.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_DASHBOARD_LIST = "kibana.adminFeature.KibanaDashboard.list.pageTitle";
    private static final String PROPERTY_DASHBOARD_USER_TEXT = "kibana.dashboard.user.text";

    // Freemarker
    private static final String MARK_KIBANA_SERVER_URL = "kibana_server_url";
    private static final String MARK_KIBANA_SERVER_USER_LOGIN = "kibana_server_user_login";
    private static final String MARK_KIBANA_SERVER_USER_PWD = "kibana_server_user_pwd";
    private static final String MARK_KIBANA_SERVER_SPACE_ID = "kibana_server_space_id";
    private static final String MARK_DASHBOARDS_LIST = "dashboards_list";
    private static final String MARK_DASHBOARD = "dashboard";
    private static final String MARK_ERROR_MESSAGE = "error_message";
    private static final String PARAMETER_TAB = "tab";

    /**
     * Returns the content of the page kibana.
     * 
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_DASHBOARD )
    public String getKibanaDashboard( HttpServletRequest request )
    {
        try
        {
            List<Dashboard> listDashboards = DashboardService.getInstance( ).getDashboards( );
            listDashboards = DashboardService.getInstance( ).filterDashboardListRBAC( listDashboards, request );
            String strIdDashboard = request.getParameter( PARAMETER_TAB );
            Optional<Dashboard> dashboard = listDashboards.stream( ).filter( o -> o.getIdKibanaDashboard( ).equals( strIdDashboard ) ).findFirst( );

            if ( dashboard.isPresent( ) )
            {
                Map<String, Object> model = getModel( );
                model.put( MARK_DASHBOARD, dashboard.get( ) );
                model.put( MARK_KIBANA_SERVER_URL, DashboardService.getInstance( ).getKibanaServerUrl( ) );
                model.put( MARK_KIBANA_SERVER_SPACE_ID, DashboardService.getInstance( ).getKibanaServerSpaceId( ) );

                if ( DashboardService.getInstance( ).isKibanaServerUserShow( ) )
                {
                    model.put( MARK_KIBANA_SERVER_USER_LOGIN, DashboardService.getInstance( ).getKibanaServerUserLogin( ) );
                    model.put( MARK_KIBANA_SERVER_USER_PWD, DashboardService.getInstance( ).getKibanaServerUserPassword( ) );
                }

                return getPage( PROPERTY_PAGE_TITLE_DASHBOARD, TEMPLATE_DASHBOARD, model );
            }
            else
            {
                Map<String, Object> model = getModel( );
                model.put( MARK_KIBANA_SERVER_URL, DashboardService.getInstance( ).getKibanaServerUrl( ) );
                return getPage( PROPERTY_PAGE_TITLE_DASHBOARD, TEMPLATE_NO_DASHBOARD, model );
            }
        }
        catch( NoKibanaIndexException ex )
        {
            Map<String, Object> model = getModel( );
            model.put( MARK_KIBANA_SERVER_URL, DashboardService.getInstance( ).getKibanaServerUrl( ) );
            return getPage( PROPERTY_PAGE_TITLE_DASHBOARD, TEMPLATE_NO_DASHBOARD, model );
        }
        catch( NoElasticSearchServerException ex )
        {
            Map<String, Object> model = getModel( );
            model.put( MARK_ERROR_MESSAGE, ex.getMessage( ) );

            return getPage( PROPERTY_PAGE_TITLE_DASHBOARD, TEMPLATE_ELASTICSEARH_ERROR, model );
        }
    }

    /**
     * Returns the content of the page kibana.
     * 
     * @param request
     *            The HTTP request
     * @return The view
     */
    @View( value = VIEW_DASHBOARD_LIST, defaultView = true )
    public String getKibanaDashboardList( HttpServletRequest request )
    {
        try
        {
            List<Dashboard> listDashboards = DashboardService.getInstance( ).getDashboards( );
            listDashboards = DashboardService.getInstance( ).filterDashboardListRBAC( listDashboards, request );
            if ( !listDashboards.isEmpty( ) )
            {
                Map<String, Object> model = getModel( );
                model.put( MARK_DASHBOARDS_LIST, listDashboards );
                return getPage( PROPERTY_PAGE_TITLE_DASHBOARD_LIST, TEMPLATE_DASHBOARD_LIST, model );
            }
            else
            {
                Map<String, Object> model = getModel( );
                model.put( MARK_KIBANA_SERVER_URL, DashboardService.getInstance( ).getKibanaServerUrl( ) );
                return getPage( PROPERTY_PAGE_TITLE_DASHBOARD_LIST, TEMPLATE_NO_DASHBOARD, model );
            }
        }
        catch( NoKibanaIndexException ex )
        {
            Map<String, Object> model = getModel( );
            model.put( MARK_KIBANA_SERVER_URL, DashboardService.getInstance( ).getKibanaServerUrl( ) );
            return getPage( PROPERTY_PAGE_TITLE_DASHBOARD_LIST, TEMPLATE_NO_DASHBOARD, model );
        }
        catch( NoElasticSearchServerException ex )
        {
            Map<String, Object> model = getModel( );
            model.put( MARK_ERROR_MESSAGE, ex.getMessage( ) );

            return getPage( PROPERTY_PAGE_TITLE_DASHBOARD_LIST, TEMPLATE_ELASTICSEARH_ERROR, model );
        }
    }
}
