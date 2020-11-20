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
package fr.paris.lutece.plugins.kibana.business;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Dashboard objects
 */
public final class DashboardHome
{
    // Static variable pointed at the DAO instance
    private static IDashboardDAO _dao = SpringContextService.getBean( "kibana.dashboardDAO" );
    private static Plugin _plugin = PluginService.getPlugin( "kibana" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DashboardHome( )
    {
    }

    /**
     * Create an instance of the dashboard class
     * 
     * @param dashboard
     *            The instance of the Dashboard which contains the informations to store
     * @return The instance of dashboard which has been created with its primary key.
     */
    public static Dashboard create( Dashboard dashboard )
    {
        _dao.insert( dashboard, _plugin );

        return dashboard;
    }

    /**
     * Returns an instance of a dashboard whose identifier is specified in parameter
     * 
     * @param nKey
     *            The dashboard primary key
     * @return an instance of Dashboard
     */
    public static Dashboard findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, _plugin );
    }

    /**
     * Load the data of all the dashboard objects and returns them as a list
     * 
     * @return the list which contains the data of all the dashboard objects
     */
    public static List<Dashboard> getDashboardsList( )
    {
        return _dao.selectDashboardsList( _plugin );
    }

    /**
     * Load the id of all the dashboard objects and returns them as a list
     * 
     * @return the list which contains the id of all the dashboard objects
     */
    public static List<Integer> getIdDashboardsList( )
    {
        return _dao.selectIdDashboardsList( _plugin );
    }

    /**
     * Load the data of all the dashboard objects and returns them as a referenceList
     * 
     * @return the referenceList which contains the data of all the dashboard objects
     */
    public static ReferenceList getDashboardsReferenceList( )
    {
        return _dao.selectDashboardsReferenceList( _plugin );
    }

    /**
     * Delete the given dashboard
     * 
     * @param nIdDashboard
     *            The dashboard id
     */
    public static void delete( int nIdDashboard )
    {
        _dao.delete( nIdDashboard, _plugin );
    }

    /**
     * Delete all kibanas dashboards
     */

    public static void deleteAllKibanaDasboards( )
    {
        _dao.deleteAll( _plugin );
    }

    /**
     * Create or update given dashboard
     * 
     * @param dashboard
     * @return the created or updated dashboard
     */
    public static Dashboard createOrUpdate( Dashboard dashboard )
    {
        if ( _dao.isDashboardExists( dashboard, _plugin ) )
        {
            _dao.store( dashboard, _plugin );
        }
        else
        {
            _dao.insert( dashboard, _plugin );
        }
        return dashboard;
    }

    /**
     * Find Kibana dashboard with given kibana dashboard id
     * 
     * @param strIdKibana
     * @return the dashboard found
     */
    public static Dashboard findByKibanaId( String strIdKibana )
    {
        return _dao.loadByKibanaId( strIdKibana, _plugin );
    }
}
