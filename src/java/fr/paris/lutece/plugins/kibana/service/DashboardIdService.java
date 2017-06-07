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
import fr.paris.lutece.plugins.kibana.business.DashboardHome;
import fr.paris.lutece.plugins.kibana.utils.constants.KibanaConstants;
import fr.paris.lutece.portal.service.rbac.Permission;
import fr.paris.lutece.portal.service.rbac.ResourceIdService;
import fr.paris.lutece.portal.service.rbac.ResourceType;
import fr.paris.lutece.portal.service.rbac.ResourceTypeManager;
import fr.paris.lutece.util.ReferenceList;
import java.util.List;
import java.util.Locale;

public class DashboardIdService extends ResourceIdService
{
    private static final String PROPERTY_LABEL_DASHBOARD_RESOURCE_TYPE = "kibana.rbac.dashboard.resourceType";
    private static final String PROPERTY_LABEL_VIEW = "kibana.rbac.dashboard.permission.view";
    

    /**
     * {@inheritDoc }
     */
    @Override
    public void register(  )
    {
        ResourceType rt = new ResourceType(  );
        rt.setResourceIdServiceClass( DashboardIdService .class.getName(  ) );
        rt.setPluginName( KibanaConstants.PLUGIN_NAME );
        rt.setResourceTypeKey( KibanaConstants.DASHBOARD_RESOURCE_TYPE );
        rt.setResourceTypeLabelKey( PROPERTY_LABEL_DASHBOARD_RESOURCE_TYPE );

        Permission p = new Permission(  );
        p.setPermissionKey( KibanaConstants.DASHBOARD_PERMISSION_VIEW );
        p.setPermissionTitleKey( PROPERTY_LABEL_VIEW );
        rt.registerPermission( p );

        ResourceTypeManager.registerResourceType( rt );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ReferenceList getResourceIdList( Locale locale )
    {
        List<Dashboard>  listDashboards = DashboardHome.getDashboardsList( );

        return ReferenceList.convert( listDashboards, "id", "title", true );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getTitle( String strId, Locale locale )
    {
        Dashboard dashboard = DashboardHome.findByPrimaryKey( Integer.parseInt( strId ) );

        return dashboard.getTitle(  );
    }
}
