/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.kibana.service;

import fr.paris.lutece.plugins.kibana.business.Dashboard;
import fr.paris.lutece.plugins.kibana.business.DashboardHome;
import fr.paris.lutece.plugins.kibana.utils.constants.KibanaConstants;
import fr.paris.lutece.portal.business.rbac.RBACHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * DashboardService
 */
public class DashboardService {
    private static final String NOT_FOUND = "404";
    private static final String PROPERTY_KIBANA_SERVER_URL = "kibana.kibana_server_url";
    private static final String DEFAULT_KIBANA_URL = "http://localhost:5601";
    private static final String KIBANA_SERVER_URL = AppPropertiesService.getProperty(PROPERTY_KIBANA_SERVER_URL,
            DEFAULT_KIBANA_URL);
    private static final String PROPERTY_ELASTIC_DASHBOARD_QUERY_URL = "kibana.elastic.dashboard_query_url";
    private static final String ELASTIC_DASHBOARDS_URL = AppPropertiesService
            .getProperty(PROPERTY_ELASTIC_DASHBOARD_QUERY_URL);

    private static DashboardService _instance = null;

    /** Private constructor */
    private DashboardService() {
    }

    public static DashboardService getInstance() {
        if (_instance == null) {
            _instance = new DashboardService();
        }
        return _instance;
    }

    /**
     * Get the list of all dashboards
     * 
     * @return The list of dashboards
     * @throws NoKibanaIndexException         if no Kibana index was found
     * @throws NoElasticSearchServerException if no Elastic server was found
     */
    public List<Dashboard> getDashboards() throws NoKibanaIndexException, NoElasticSearchServerException {
        List<Dashboard> listDashboards = new ArrayList<Dashboard>();

        HttpAccess httpAccess = new HttpAccess();
        try {
            Map<String, String> mapParams = new HashMap<>();
            String strJSON = httpAccess.doPost(ELASTIC_DASHBOARDS_URL, mapParams );
            listDashboards = getListDashboard(strJSON);
        } catch (HttpAccessException ex) 
        {

            if (ex.getMessage().indexOf(NOT_FOUND) > 0) {
                throw new NoKibanaIndexException(ex.getMessage());
            } else {
                throw new NoElasticSearchServerException(ex.getMessage());
            }
        }

        return listDashboards;
    }

    /**
     * Get the list of all dashboard
     * 
     * @param strJSON The list of dashboard as JSON provided by Elastic
     * @return The list
     */
    public List<Dashboard> getListDashboard(String strJSON) {
        List<Dashboard> listDashBoard = new ArrayList<Dashboard>();

        JSONObject obj = (JSONObject) JSONSerializer.toJSON(strJSON);

        JSONArray arr = obj.getJSONObject("hits").getJSONArray("hits");

        for (int i = 0; i < arr.size(); i++) {
            JSONObject document = arr.getJSONObject(i);

            if ((document != null) && "doc".equals(document.getString("_type")))
            {
                Dashboard dashboard = new Dashboard();
                dashboard.setIdKibanaDashboard( document.getString("_id").replace( "dashboard:", "") );
                dashboard.setTitle(document.getJSONObject("_source").getJSONObject("dashboard").getString("title") );
                listDashBoard.add(dashboard);
            }
        }

        return listDashBoard;
    }

    /**
     * Get Kibana server URL
     * 
     * @return The URL
     */
    public String getKibanaServerUrl() {
        return KIBANA_SERVER_URL;
    }

    /**
     * Insert all dashboard to database
     */
    public void createAllDashboards() {
        try {
            List<Dashboard> listDashboards = getDashboards();
            for (Dashboard dashboard : listDashboards) {
                DashboardHome.createOrUpdate(dashboard);
            }
        } catch (NoElasticSearchServerException e) {
            AppLogService.error("Unable to connect to Elasticsearch server", e);
        } catch (NoKibanaIndexException e) {
            AppLogService.error("Unable to find Kibana index", e);
        }

    }

    /**
     * Return a dashboard list filtered by RBAC authorizations.
     * 
     * @param listDashboard
     * @param request
     * @return a new dashboard list, according to RBAC authorizations.
     */
    public List<Dashboard> filterDashboardListRBAC(List<Dashboard> listDashboard, HttpServletRequest request) {
        List<Dashboard> listFilteredDashboards = new ArrayList<Dashboard>();
        AdminUser user = AdminUserService.getAdminUser(request);

        for (Dashboard dashboard : listDashboard) {
            Dashboard storedDashboard = DashboardHome.findByKibanaId(dashboard.getIdKibanaDashboard());
            if (RBACService.isAuthorized(storedDashboard, KibanaConstants.DASHBOARD_PERMISSION_VIEW, user)) {
                listFilteredDashboards.add(dashboard);
            }
        }
        return listFilteredDashboards;
    }

    /**
     * Delete given dashboard and RBACs related to it
     * 
     * @param nIdDashboard
     */
    public void deleteDashboard(int nIdDashboard) {
        // First delete RBAC Resource related to given id dashboard
        RBACHome.removeForResource(KibanaConstants.DASHBOARD_RESOURCE_TYPE, Integer.toString(nIdDashboard));

        // Then delete Dashboard
        DashboardHome.delete(nIdDashboard);
    }

}
