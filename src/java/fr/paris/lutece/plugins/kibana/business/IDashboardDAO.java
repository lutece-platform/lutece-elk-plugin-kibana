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
import java.util.List;

/**
 * IDashboardDAO Interface
 */
public interface IDashboardDAO
{
    /**
     * Insert a new record in the table.
     * 
     * @param dashboard
     *            instance of the Dashboard object to insert
     * @param plugin
     *            the Plugin
     */
    void insert( Dashboard dashboard, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param nKey
     *            The identifier of the dashboard
     * @param plugin
     *            the Plugin
     * @return The instance of the dashboard
     */
    Dashboard load( int nKey, Plugin plugin );

    /**
     * Load the data from the table
     * 
     * @param strIdKibana
     *            The identifier of the dashboard in Kibana
     * @param plugin
     *            the Plugin
     * @return The instance of the dashboard
     */
    Dashboard loadByKibanaId( String strIdKibana, Plugin plugin );

    /**
     * Load the data of all the dashboard objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the data of all the dashboard objects
     */
    List<Dashboard> selectDashboardsList( Plugin plugin );

    /**
     * Load the id of all the dashboard objects and returns them as a list
     * 
     * @param plugin
     *            the Plugin
     * @return The list which contains the id of all the dashboard objects
     */
    List<Integer> selectIdDashboardsList( Plugin plugin );

    /**
     * Load the data of all the dashboard objects and returns them as a referenceList
     * 
     * @param plugin
     *            the Plugin
     * @return The referenceList which contains the data of all the dashboard objects
     */
    ReferenceList selectDashboardsReferenceList( Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param dashboard
     *            the reference of the Dashboard
     * @param plugin
     *            the Plugin
     */
    void store( Dashboard dashboard, Plugin plugin );

    /**
     * Delete the given dashboard
     * 
     * @param nIdDashboard
     *            the dashboard id to delete
     * @param plugin
     *            the Plugin
     */
    void delete( int nIdDashboard, Plugin plugin );

    /**
     * Delete all the dashboards in the table
     * 
     * @param plugin
     */
    void deleteAll( Plugin plugin );

    /**
     * check if given dashboard exists, based on Kibana dashboard id
     * 
     * @param dashboard
     * @param plugin
     * @return true if exists, false otherwise
     */
    boolean isDashboardExists( Dashboard dashboard, Plugin plugin );

}
