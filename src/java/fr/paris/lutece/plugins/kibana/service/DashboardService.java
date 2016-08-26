/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.util.ArrayList;
import java.util.List;


/**
 * DashboardService
 */
public class DashboardService
{
    private static final String NOT_FOUND = "404";
    private static final String KIBANA_SERVER_URL_PROPERTY = "kibana.kibana_server_url";
    private static String _strUrl = getKibanaServerUrl(  ) + "/elasticsearch/.kibana/dashboard/_search";

    public static List<Dashboard> getDashboard(  ) throws NoKibanaIndexException, NoElasticSearchServerException
    {
        List<Dashboard> listDashboards = new ArrayList<Dashboard>(  );

        HttpAccess httpAccess = new HttpAccess(  );

        try
        {
            String strJSON = httpAccess.doGet( _strUrl );
            listDashboards = getListDashboard( strJSON );
        }
        catch ( HttpAccessException ex )
        {
            
            if( ex.getMessage(  ).indexOf( NOT_FOUND ) > 0 )
            {
                throw new NoKibanaIndexException( ex.getMessage(  ) );
            }
            else
            {
                throw new NoElasticSearchServerException(ex.getMessage(  ) );
            }
        }

        return listDashboards;
    }

    public static List<Dashboard> getListDashboard( String strJSON )
    {
        List<Dashboard> listDashBoard = new ArrayList<Dashboard>(  );

        JSONObject obj = (JSONObject) JSONSerializer.toJSON( strJSON );
        JSONArray arr = obj.getJSONObject( "hits" ).getJSONArray( "hits" );

        for ( int i = 0; i < arr.size(  ); i++ )
        {
            JSONObject document = arr.getJSONObject( i );

            if ( ( document != null ) && "dashboard".equals( document.getString( "_type" ) ) )
            {
                Dashboard dashboard = new Dashboard(  );
                dashboard.setId( document.getString( "_id" ) );
                dashboard.setTitle( document.getJSONObject( "_source" ).getString( "title" ) );
                listDashBoard.add( dashboard );
            }
        }

        return ( listDashBoard );
    }

    public static String getKibanaServerUrl(  ) {
        return AppPropertiesService.getProperty(KIBANA_SERVER_URL_PROPERTY, "http://localhost:5601");
    }
}
