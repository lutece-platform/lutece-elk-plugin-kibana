/*
 * Copyright (c) 2002-2021, City of Paris
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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides Data Access methods for Dashboard objects
 */
public final class DashboardDAO implements IDashboardDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_dashboard, idKibanaDashboard, title, dataSourceName FROM kibana_dashboard WHERE id_dashboard = ?";
    private static final String SQL_QUERY_SELECT_BY_KIBANA_ID = "SELECT id_dashboard, idKibanaDashboard, title, dataSourceName FROM kibana_dashboard WHERE idKibanaDashboard = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO kibana_dashboard ( idKibanaDashboard, title, dataSourceName ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_dashboard, idKibanaDashboard, title, dataSourceName FROM kibana_dashboard";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_dashboard FROM kibana_dashboard";
    private static final String SQL_QUERY_DELETE = "DELETE FROM kibana_dashboard WHERE id_dashboard = ?";
    private static final String SQL_QUERY_DELETE_ALL = "DELETE FROM kibana_dashboard";
    private static final String SQL_QUERY_COUNT_DASHBOARD_KIBANA_ID = "SELECT count(*) FROM kibana_dashboard WHERE idKibanaDashboard = ?";
    private static final String SQL_QUERY_UPDATE = "UPDATE kibana_dashboard SET id_dashboard = ?, idKibanaDashboard = ?, title = ? WHERE id_dashboard = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( Dashboard dashboard, Plugin plugin )
    {
    	try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, dashboard.getIdKibanaDashboard( ) );
            daoUtil.setString( nIndex++, dashboard.getTitle( ) );
            daoUtil.setString( nIndex++, dashboard.getIdDataSource( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                dashboard.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Dashboard load( int nKey, Plugin plugin )
    {
        Dashboard dashboard = null;
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                dashboard = new Dashboard( );
                int nIndex = 1;

                dashboard.setId( daoUtil.getInt( nIndex++ ) );
                dashboard.setIdKibanaDashboard( daoUtil.getString( nIndex++ ) );
                dashboard.setTitle( daoUtil.getString( nIndex++ ) );
                dashboard.setIdDataSource( daoUtil.getString( nIndex++ ) );
            }
        }
        return dashboard;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Dashboard loadByKibanaId( String strIdKibana, Plugin plugin )
    {
    	Dashboard dashboard = null;
    	try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_KIBANA_ID, plugin ) )
        {
            daoUtil.setString( 1, strIdKibana );
            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                dashboard = new Dashboard( );
                int nIndex = 1;

                dashboard.setId( daoUtil.getInt( nIndex++ ) );
                dashboard.setIdKibanaDashboard( daoUtil.getString( nIndex++ ) );
                dashboard.setTitle( daoUtil.getString( nIndex++ ) );
                dashboard.setIdDataSource( daoUtil.getString( nIndex++ ) );
            }
        }
        return dashboard;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Dashboard> selectDashboardsList( Plugin plugin )
    {
        List<Dashboard> dashboardList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                Dashboard dashboard = new Dashboard( );
                int nIndex = 1;

                dashboard.setId( daoUtil.getInt( nIndex++ ) );
                dashboard.setIdKibanaDashboard( daoUtil.getString( nIndex++ ) );
                dashboard.setTitle( daoUtil.getString( nIndex++ ) );
                dashboard.setIdDataSource( daoUtil.getString( nIndex++ ) );

                dashboardList.add( dashboard );
            }
        }
        return dashboardList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdDashboardsList( Plugin plugin )
    {
        List<Integer> dashboardList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                dashboardList.add( daoUtil.getInt( 1 ) );
            }
        }
        return dashboardList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList selectDashboardsReferenceList( Plugin plugin )
    {
        ReferenceList dashboardList = new ReferenceList( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                dashboardList.addItem( daoUtil.getInt( 1 ), daoUtil.getString( 2 ) );
            }
        }
        return dashboardList;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nIdDashboard, Plugin plugin )
    {
    	try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ))
        {
            daoUtil.setInt( 1, nIdDashboard );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteAll( Plugin plugin )
    {
    	try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_ALL ) )
        {
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isDashboardExists( Dashboard dashboard, Plugin plugin )
    {
    	boolean bExist = false;
    	try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_COUNT_DASHBOARD_KIBANA_ID, plugin ) )
    	{
            daoUtil.setString( 1, dashboard.getIdKibanaDashboard( ) );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                if ( daoUtil.getInt( 1 ) >= 1 )
                {
                    bExist = true;
                }
            }
    	}
        return bExist;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( Dashboard dashboard, Plugin plugin )
    {
    	try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin )  )
    	{	
            int nIndex = 1;

            daoUtil.setInt( nIndex++, dashboard.getId( ) );
            daoUtil.setString( nIndex++, dashboard.getIdKibanaDashboard( ) );
            daoUtil.setString( nIndex++, dashboard.getTitle( ) );
            daoUtil.setInt( nIndex, dashboard.getId( ) );

            daoUtil.executeUpdate( );
    	}
    }
}
