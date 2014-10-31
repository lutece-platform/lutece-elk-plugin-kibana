/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
import fr.paris.lutece.plugins.kibana.service.DashboardService;
import fr.paris.lutece.plugins.kibana.service.NoElasticSearchServerException;
import fr.paris.lutece.plugins.kibana.service.NoKibanaIndexException;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.portal.util.mvc.xpage.MVCApplication;
import fr.paris.lutece.portal.util.mvc.xpage.annotations.Controller;
import fr.paris.lutece.portal.web.xpages.XPage;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * This class provides a simple implementation of an XPage
 */
@Controller( xpageName = "kibana", pageTitleI18nKey = "kibana.xpage.kibana.pageTitle", pagePathI18nKey = "kibana.xpage.kibana.pagePathLabel" )
public class KibanaApp extends MVCApplication
{
    private static final String TEMPLATE_XPAGE = "/skin/plugins/kibana/kibana.html";
    private static final String TEMPLATE_ELASTICSEARH_ERROR = "/skin/plugins/kibana/elasticsearch_error.html";
    private static final String TEMPLATE_NO_DASHBOARD = "/skin/plugins/kibana/no_dashboard.html";
    private static final String VIEW_HOME = "home";
    private static final String MARK_DASHBOARDS_LIST = "dashboards_list";
    private static final String MARK_CURRENT = "current";
    private static final String MARK_ERROR_MESSAGE = "error_message";
    private static final String PARAMETER_TAB = "tab";

    /**
     * Returns the content of the page kibana.
     * @param request The HTTP request
     * @return The view
     */
    @View( value = VIEW_HOME, defaultView = true )
    public XPage viewHome( HttpServletRequest request )
    {
        try
        {
            List<Dashboard> listDashboards = DashboardService.getDashboard(  );

            if ( listDashboards.size(  ) > 0 )
            {
                int nCurrent = listDashboards.get( 0 ).getId(  );
                String strTab = request.getParameter( PARAMETER_TAB );

                if ( strTab != null )
                {
                    try 
                    { 
                        nCurrent = Integer.parseInt( strTab );
                    }
                    catch( NumberFormatException ex )
                    {
                        nCurrent = listDashboards.get( 0 ).getId(  );
                    }
                }

                Map<String, Object> model = getModel(  );
                model.put( MARK_DASHBOARDS_LIST, listDashboards );
                model.put( MARK_CURRENT, nCurrent );

                return getXPage( TEMPLATE_XPAGE, request.getLocale(  ), model );
            }
            else
            {
                return getXPage( TEMPLATE_NO_DASHBOARD, request.getLocale(  ) );
            }
        }
        catch ( NoKibanaIndexException ex )
        {
            return getXPage( TEMPLATE_NO_DASHBOARD, request.getLocale(  ) );
        }
        catch (NoElasticSearchServerException ex)
        {
            Map<String, Object> model = getModel(  );
            model.put( MARK_ERROR_MESSAGE, ex.getMessage() );

            return getXPage( TEMPLATE_ELASTICSEARH_ERROR, request.getLocale(  ), model );
        }
    }
}
