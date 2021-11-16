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
package fr.paris.lutece.plugins.kibana.service.listener;

import fr.paris.lutece.plugins.elasticdata.business.DataSource;
import fr.paris.lutece.plugins.elasticdata.service.DataSourceService;
import fr.paris.lutece.plugins.elasticdata.service.DataSourceUtils;
import fr.paris.lutece.plugins.kibana.service.IDataVisualizerService;
import fr.paris.lutece.plugins.kibana.service.SavedObjectService;
import fr.paris.lutece.portal.business.event.EventRessourceListener;
import fr.paris.lutece.portal.business.event.ResourceEvent;
import fr.paris.lutece.portal.service.spring.SpringContextService;

public class DataSourceEventListener implements EventRessourceListener
{
    private static final String CONSTANT_FORM_RESPONSE_LISTENER_NAME = "DataSourceEventListener";

    @Override
    public String getName( )
    {
        return CONSTANT_FORM_RESPONSE_LISTENER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addedResource( ResourceEvent event )
    {
        if ( checkResourceType( event ) )
        {
            SavedObjectService.initEnvironnement( );

            String strIdResource = event.getIdResource( );
            DataSource dataSource = DataSourceService.getDataSource( strIdResource );
            for ( IDataVisualizerService dataVisualizerService : SpringContextService.getBeansOfType( IDataVisualizerService.class ) )
            {
                if ( dataVisualizerService.isExistDataSourceDataVisualizer( dataSource ) )
                {
                    SavedObjectService.doRefreshKibanaIndexPattern( dataSource.getTargetIndexName( ) );
                    dataVisualizerService.createOrUpdate( event, dataSource );
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deletedResource( ResourceEvent event )
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updatedResource( ResourceEvent event )
    {
    }

    /**
     * Check ressource type
     * 
     * @param event
     *            resource event
     * 
     */
    private boolean checkResourceType( ResourceEvent event )
    {
        return DataSourceUtils.RESOURCE_TYPE_INDEXING.equals( event.getTypeResource( ) );
    }
}
