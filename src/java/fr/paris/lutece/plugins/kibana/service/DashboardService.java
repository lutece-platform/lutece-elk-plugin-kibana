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

import fr.paris.lutece.plugins.grustorageelastic.business.ElasticConnexion;
import fr.paris.lutece.plugins.kibana.business.Dashboard;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;



/**
 * DashboardService
 */
public class DashboardService
{
    private static final String NOT_FOUND = "404";

    private static String _strUrl ;
    private static String _strUrlDashboard ;
    
    //PROPERTIES
    private static final String PROPERTY_KIBANA_URL = "kibana.url";
    private static final String PROPERTY_KIBANA_URL_DASHBOARD= "kibana.UrlDashboard";
    	

    public static List<Dashboard> getDashboard(  ) throws NoKibanaIndexException, NoElasticSearchServerException
    {
        List<Dashboard> listDashboards = new ArrayList<Dashboard>(  );
        
        String json, retour = StringUtils.EMPTY ;
        _strUrl = AppPropertiesService.getProperty( PROPERTY_KIBANA_URL ) ;
        _strUrlDashboard = AppPropertiesService.getProperty( PROPERTY_KIBANA_URL_DASHBOARD ) ;
        AppLogService.info( "URI : " +_strUrl);
        
        try
        {

        	HashMap<String, String> mapParam = new HashMap<String, String>(  );
            mapParam.put( "_type", "dashboard" );

            json = ElasticConnexion.formatExactSearch( mapParam );
            retour = ElasticConnexion.sentToElasticPOST( _strUrl, json );
            
            AppLogService.info( "retour : " +retour);
            
            List<String> listDashboardNames = getListDashboard( retour );
            
            Map<String,String> mapIframe = new HashMap<String,String>( );
            
            JsonNode jsonRetour = ElasticConnexion.setJsonToJsonTree( retour );
            List<JsonNode> tmp = jsonRetour.findValues( "_source" );
                        
            for ( JsonNode node : tmp )
            {
            	String strTitle = node.findValue( "title" ).asText(  ) ;
            	strTitle = strTitle.replaceAll(" ", "-");
                if ( node != null )
                {
                	mapIframe.put( strTitle, generateIframe ( node )  );
                }
            }

            int nIndex = 1;

            for ( String strDashboardName : listDashboardNames )
            {
                Dashboard dashboard = new Dashboard(  );
                dashboard.setId( nIndex );
                dashboard.setName( strDashboardName );
                dashboard.setIframe( mapIframe.get( strDashboardName ));
                listDashboards.add( dashboard );
                nIndex++;
            }
        }
        catch ( IOException ex )
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

    public static List<String> getListDashboard( String strJSON )
    {
        List<String> listDashBoard = new ArrayList<String>(  );

        JSONObject obj = (JSONObject) JSONSerializer.toJSON( strJSON );
        JSONArray arr = obj.getJSONObject( "hits" ).getJSONArray( "hits" );

        for ( int i = 0; i < arr.size(  ); i++ )
        {
            JSONObject document = arr.getJSONObject( i );

            if ( ( document != null ) && "dashboard".equals( document.getString( "_type" ) ) )
            {
                listDashBoard.add( document.getString( "_id" ) );
            }
        }
        AppLogService.info( "getListDashboard : " +listDashBoard.size( ) );
        return ( listDashBoard );
    }
    
    private static String generateIframe ( JsonNode node ) throws JsonProcessingException, IOException
    {
    	String strTitle = "", strDarkTheme ="",strAnalyzeWildCard = "", strQuery = "", strPanels="", strPrefix="";
    	
    	StringBuilder strBuilder = new StringBuilder( _strUrlDashboard );
    	AppLogService.info( "generateIframe BEGIN ");
    	try
    	{
    		strTitle =  node.findValue( "title" ).asText(  );
    		strTitle = strTitle.replaceAll(" ", "-");
    		String  strPanelsJsonNode =  node.findValue( "panelsJSON" ).asText();
    		
    		org.codehaus.jettison.json.JSONArray array = new org.codehaus.jettison.json.JSONArray(strPanelsJsonNode); 
    		
    		for (int i = 0; i < array.length(); i++) 
    			{
    				org.codehaus.jettison.json.JSONObject tmpNode = array.getJSONObject(i) ;
    				
    				StringBuilder strVisualisation = new StringBuilder( "(col:" );
    				strVisualisation.append( tmpNode.getString("col")) ;
    				strVisualisation.append( ",id:") ;
    				strVisualisation.append( tmpNode.getString("id") ) ;
    				strVisualisation.append( ",panelIndex:") ;
    				strVisualisation.append( tmpNode.getString("panelIndex") ) ;
    				strVisualisation.append( ",row:") ;
    				strVisualisation.append( tmpNode.getString("row") ) ;
    				strVisualisation.append( ",size_x:") ;
    				strVisualisation.append( tmpNode.getString("size_x") ) ;
    				strVisualisation.append( ",size_y:") ;
    				strVisualisation.append( tmpNode.getString("size_y") ) ;
    				strVisualisation.append( ",type:") ;
    				strVisualisation.append( tmpNode.getString("type") ) ;
    				strVisualisation.append( ")") ;
    				
    				strPanels +=  strPrefix + strVisualisation.toString( )   ;
    				strPrefix = "," ;
				}
    		
    		ObjectMapper mapper = new ObjectMapper();
    		
    		JsonNode optionsJsonNode = mapper.readTree( node.findValue( "optionsJSON" ).asText(  )  ) ;
    		
    		strDarkTheme =  optionsJsonNode.findValue( "darkTheme" ).asText(  );
    		
			JsonNode kibanaSavedObjectNode = node.path("kibanaSavedObjectMeta");
			String searchSource = kibanaSavedObjectNode.findValue( "searchSourceJSON" ).asText(  ) ;
			JsonNode searchSourceJsonNode = mapper.readTree(searchSource  ) ;
			
			JsonNode filterNode =  searchSourceJsonNode.path("filter");
				
			for ( JsonNode tmpNode : filterNode) 
				{
					JsonNode queryNode = tmpNode.path("query");
					JsonNode queryStringNode = queryNode.path("query_string");
					strAnalyzeWildCard = queryStringNode.findValue("analyze_wildcard").asText(  ) ;
					strQuery = queryStringNode.findValue("query").asText(  ) ;
				}
			
        }
        catch( NullPointerException ex)
        {
        	AppLogService.error( "Parsing dashboard fail"+ node.toString( ), ex );
        	Response.status( Response.Status.INTERNAL_SERVER_ERROR ).entity( "{ \"status\": \"Error : Parsing dashboard fail"+ node.toString() + "\" }" ).build(  );
        } 
    	catch ( JSONException e ) 
    	{	
    		AppLogService.error( "Parsing dashboard fail"+ node.toString( ), e);
		}
    	
    	strDarkTheme = strDarkTheme.equals("true") ? "t" : "f" ;
    	strAnalyzeWildCard = strAnalyzeWildCard.equals("true") ? "t" : "f" ;
    	
    	strBuilder.append( strTitle )
    	.append("?").append("_g=(refreshInterval:(display:Off,pause:!f,value:0),time:(from:now-60d,mode:quick,to:now))")
    	.append("&_a=(filters:!(),options:(darkTheme:!")
    	.append( strDarkTheme )
    	.append("),panels:!(")
    	.append(strPanels)
    	.append("),query:(query_string:(analyze_wildcard:!")
    	.append(strAnalyzeWildCard)
    	.append(",query:'")
    	.append(strQuery)
    	.append("')), title:")
    	.append(strTitle)
    	.append(",uiState:())");
    	
    	AppLogService.info( "generateIframe END "+ strBuilder.toString( ));
    	return strBuilder.toString( ) ;
    }
}
