package fr.paris.lutece.plugins.kibana.service;

import fr.paris.lutece.plugins.elasticdata.business.DataSource;
import fr.paris.lutece.plugins.elasticdata.service.DataSourceService;
import fr.paris.lutece.plugins.kibana.business.Dashboard;
import fr.paris.lutece.plugins.kibana.business.Gird;
import fr.paris.lutece.plugins.libraryelastic.util.Elastic;
import fr.paris.lutece.plugins.libraryelastic.util.ElasticClientException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.httpaccess.HttpAccess;
import fr.paris.lutece.util.httpaccess.HttpAccessException;
import fr.paris.lutece.util.signrequest.BasicAuthorizationAuthenticator;
import fr.paris.lutece.util.signrequest.RequestAuthenticator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SavedObjectService
{
    private static final String NOT_FOUND = "404";
    private static final String PROPERTY_KIBANA_SERVER_URL = "kibana.kibana_server_url";
    private static final String DEFAULT_KIBANA_URL = "http://localhost:5601";
    private static final String KIBANA_SERVER_URL = AppPropertiesService.getProperty( PROPERTY_KIBANA_SERVER_URL, DEFAULT_KIBANA_URL );
    private static final String PROPERTY_KIBANA_SERVER_SPACE_ID = "kibana.kibana_server.space.id";
    private static final String PROPERTY_KIBANA_SERVER_LOGIN = "kibana.kibana_server.login";
    private static final String PROPERTY_KIBANA_SERVER_PWD = "kibana.kibana_server.pwd";
    private static final String KIBANA_SERVER_SPACE_ID = AppPropertiesService.getProperty( PROPERTY_KIBANA_SERVER_SPACE_ID );
    private static final String KIBANA_SERVER_LOGIN = AppPropertiesService.getProperty( PROPERTY_KIBANA_SERVER_LOGIN );
    private static final String KIBANA_SERVER_PWD = AppPropertiesService.getProperty( PROPERTY_KIBANA_SERVER_PWD );
    private static final String KIBANA_SERVER_REFRESH_INDEX_PATTERN_NAME = "/api/index_patterns/_fields_for_wildcard?pattern=";
    private static final String KIBANA_SERVER_REFRESH_INDEX_PATTERN_PARAMETERS = "&meta_fields=_source&meta_fields=_id&meta_fields=_type&meta_fields=_index&meta_fields=_score";
    private static final String KIBANA_SERVER_INDEX_API_URL = "/api/saved_objects/index-pattern/";
    private static final String KIBANA_SERVER_DASHBOARD_OBJECT_API_URL = "/api/saved_objects/dashboard/";
    private static RequestAuthenticator _authenticator = new BasicAuthorizationAuthenticator( KIBANA_SERVER_LOGIN, KIBANA_SERVER_PWD );

    /** Package constructor */
    SavedObjectService( )
    {
    }

    /**
     * Get dashboard
     * 
     * @param strIdIndexPattern
     *            The index pattern id
     */
    public static JSONObject getDashboard( String strIdDashboard )
    {
        HttpAccess httpAccess = new HttpAccess( );
        // header
        Map<String, String> headers = new HashMap<>( );
        headers.put( "kbn-xsrf", "true" );
        String strFieldsJSON;
        try
        {
            strFieldsJSON = httpAccess.doGet( KIBANA_SERVER_URL + "/s/" + KIBANA_SERVER_SPACE_ID + KIBANA_SERVER_DASHBOARD_OBJECT_API_URL + strIdDashboard,
                    _authenticator, null, headers );
            JSONObject jsonResult = (JSONObject) JSONSerializer.toJSON( strFieldsJSON );
            Object statusCode = jsonResult.get( "statusCode" );
            if ( statusCode != null )
            {
                return null;
            }
            else
            {
                return jsonResult;
            }
        }
        catch( HttpAccessException e )
        {
            return null;
        }
    }

    /**
     * Refresh kibana index pattern
     * 
     * @param strIdIndexPattern
     *            The index pattern id
     */
    public static void doRefreshKibanaIndexPattern( String strIdIndexPattern )
    {
        HttpAccess httpAccess = new HttpAccess( );
        try
        {
            // header
            Map<String, String> headers = new HashMap<>( );
            headers.put( "kbn-xsrf", "true" );
            // get current index of elk
            String strFieldsJSON = httpAccess.doGet(
                    KIBANA_SERVER_URL + KIBANA_SERVER_REFRESH_INDEX_PATTERN_NAME + strIdIndexPattern + KIBANA_SERVER_REFRESH_INDEX_PATTERN_PARAMETERS,
                    _authenticator, null, headers );
            // get fields attributes
            JSONObject obj = (JSONObject) JSONSerializer.toJSON( strFieldsJSON );
            Object objFields = obj.get( "fields" );
            String strFields = objFields.toString( );
            // build json to fit to kibana api
            JSONObject updateFieldsJSON = new JSONObject( );
            updateFieldsJSON.put( "title", strIdIndexPattern );
            updateFieldsJSON.put( "fields", escape( strFields ) );
            updateFieldsJSON.put( "timeFieldName", "timestamp" );
            JSONObject updateAttributeJSON = new JSONObject( );
            updateAttributeJSON.accumulate( "attributes", updateFieldsJSON );
            String strAttributes = updateAttributeJSON.toString( );
            strAttributes = removeUnusedBackSlash( strAttributes );
            httpAccess.doPutJSON( KIBANA_SERVER_URL + "/s/" + KIBANA_SERVER_SPACE_ID + KIBANA_SERVER_INDEX_API_URL + strIdIndexPattern, strAttributes,
                    _authenticator, null, headers, null );
        }
        catch( HttpAccessException ex )
        {
            AppLogService.error( "Unable to connect to Elasticsearch / Kibana servers", ex );
        }
    }

    /**
     * create a kibana object
     * 
     * @param object
     *            The json object
     */
    public static void create( JSONObject object )
    {
        doPostSavedObject( object );
    }

    /**
     * create a kibana object from string
     * 
     * @param strObject
     *            The string object
     */
    public static void createFromString( String strObject )
    {
        doPostSavedStringObject( strObject );
    }

    /**
     * delete a dashboard
     * 
     * @param strIdDashboard
     *            The dashboard id
     */
    public static void deleteDashboard( String strIdDashboard )
    {
        JSONObject dashboard = getDashboard( strIdDashboard );
        if ( dashboard != null )
        {
            JSONArray references = dashboard.getJSONArray( "references" );
            for ( int i = 0; i < references.size( ); i++ )
            {
                JSONObject ref = references.getJSONObject( i );
                String idVisualization = ref.getString( "id" );
                String typeVisualization = ref.getString( "type" );
                doDeleteSavedObject( typeVisualization, idVisualization );
            }
            doDeleteSavedObject( "dashboard", strIdDashboard );
        }
    }

    /**
     * Create a dashboard
     * 
     * @param strIdDashboard
     *            the id of dashboard
     * @param strTitleDashboard
     *            the title of dashboard
     * @param strIdIndexPattern
     *            the index pattern id
     * @param jsonArrayReference
     *            the reference array
     * @param jsonArraypanelsJSON
     *            the panels array
     * 
     */
    public static void createDashboard( String strIdDashboard, String strTitleDashboard, String strIdIndexPattern, JSONArray jsonArrayReference,
            JSONArray jsonArraypanelsJSON, DataSource dataSource )
    {
        JSONObject searchSourceJSON = new JSONObject( );
        searchSourceJSON.put( "searchSourceJSON", "{\\\"query\":{\\\"query\\\":\\\"\\\",\\\"language\\\":\\\"lucene\\\"},\\\"filter\\\":[]}" );
        JSONObject attributes = new JSONObject( );
        attributes.put( "title", strTitleDashboard );
        attributes.put( "hits", 0 );
        attributes.put( "version", "1" );
        attributes.put( "kibanaSavedObjectMeta", searchSourceJSON );
        attributes.put( "panelsJSON", escape( jsonArraypanelsJSON.toString( ) ) );
        attributes.put( "optionsJSON", "{\\\"useMargins\\\":true,\\\"hidePanelTitles\\\":false}" );
        JSONObject migrationVersion = new JSONObject( );
        migrationVersion.put( "dashboard", "7.3.0" );
        JSONObject dashboard = new JSONObject( );
        dashboard.put( "id", strIdDashboard );
        dashboard.put( "type", "dashboard" );
        dashboard.put( "updated_at", "2018-09-07T18:41:05.887Z" );
        dashboard.put( "version", "1" );
        dashboard.put( "timeFrom", "now-30d" );
        dashboard.put( "timeTo", "now" );
        dashboard.put( "timeRestore", true );
        dashboard.put( "attributes", attributes );
        dashboard.put( "migrationVersion", migrationVersion );
        dashboard.put( "references", jsonArrayReference );
        doPostSavedObject( dashboard );
        Dashboard dashboardItem = new Dashboard( );
        dashboardItem.setIdDataSource( dataSource.getId( ) );
        dashboardItem.setIdKibanaDashboard( strIdDashboard );
        dashboardItem.setTitle( strTitleDashboard );
        DashboardService.getInstance( ).createDashboard( dashboardItem );
    }

    /**
     * Create a dashboard visualization panel object
     * 
     * @param strIdVisualisation
     *            The visualization id
     * @param gird
     *            The gird of the visualization
     * @return PanelJSON object
     */
    public static JSONObject createPanelJson( String panelId, Gird gird )
    {
        JSONObject createPanelJson = new JSONObject( );
        JSONObject gridData = new JSONObject( );
        String paneIndex = UUID.randomUUID( ).toString( );
        gridData.put( "w", gird.getW( ) );
        gridData.put( "h", gird.getH( ) );
        gridData.put( "x", gird.getX( ) );
        gridData.put( "y", gird.getY( ) );
        gridData.put( "i", paneIndex );
        createPanelJson.put( "gridData", gridData );
        createPanelJson.put( "embeddableConfig", new JSONObject( ) );
        createPanelJson.put( "version", "7.9.1" );
        createPanelJson.put( "panelIndex", paneIndex );
        createPanelJson.put( "panelRefName", panelId );
        return createPanelJson;
    }

    /**
     * Post a saved object
     * 
     * @param savedObject
     *            Object to save
     */
    private static final void doDeleteSavedObject( String strType, String strIdObject )
    {
        Map<String, String> headers = new HashMap<>( );
        headers.put( "kbn-xsrf", "true" );
        HttpAccess httpAccess = new HttpAccess( );
        try
        {
            httpAccess.doDelete( KIBANA_SERVER_URL + "/s/" + KIBANA_SERVER_SPACE_ID + "/api/saved_objects/" + strType + "/" + strIdObject, _authenticator, null,
                    headers, null );
        }
        catch( HttpAccessException ex )
        {
            AppLogService.error( "Unable to connect to Kibana server", ex );
        }
    }

    /**
     * Get index pattern fields mapping
     * 
     * @param strIdIndexPattern
     *            index pattern id
     * @return a reference list of fields mapping
     */
    private static ReferenceList getIndexPatternFieldsMapping( String strIdIndexPattern )
    {
        HttpAccess httpAccess = new HttpAccess( );
        ReferenceList fieldList = new ReferenceList( );
        try
        {
            // header
            Map<String, String> headers = new HashMap<>( );
            headers.put( "kbn-xsrf", "true" );
            // get current index of elk
            String strFieldsJSON = httpAccess.doGet( KIBANA_SERVER_URL + "/s/" + KIBANA_SERVER_SPACE_ID + KIBANA_SERVER_REFRESH_INDEX_PATTERN_NAME
                    + strIdIndexPattern + KIBANA_SERVER_REFRESH_INDEX_PATTERN_PARAMETERS, _authenticator, null, headers );
            // get fields attributes
            JSONObject obj = (JSONObject) JSONSerializer.toJSON( strFieldsJSON );
            Object objFields = obj.get( "fields" );
            JSONArray fields = JSONArray.fromObject( objFields );
            for ( int n = 0; n < fields.size( ); n++ )
            {
                JSONObject object = fields.getJSONObject( n );
                ReferenceItem fieldItem = new ReferenceItem( );
                String fieldName = object.getString( "name" );
                String fieldType = object.getString( "type" );
                fieldItem.setName( fieldName );
                fieldItem.setCode( fieldType );
                fieldList.add( fieldItem );
            }
        }
        catch( HttpAccessException ex )
        {
            AppLogService.error( "Unable to connect to Kibana server", ex );
        }
        return fieldList;
    }

    /**
     * Get parent list of elk documents
     * 
     * @param strIndexId
     *            The index id
     * @return a reference list of parents
     */
    public static ReferenceList getParentList( String strIndexId )
    {
        ReferenceList parentList = new ReferenceList( );
        String bodyJSON = "{\"size\":0,\"aggs\":{\"langs\":{\"composite\":{\"sources\":[{\"parentName\":{\"terms\":{\"field\":\"parentName.keyword\"}}},{\"parentId\":{\"terms\":{\"field\":\"parentId.keyword\"}}}]}}}}";
        Elastic elastic = DataSourceService.getElastic( );
        try
        {
            String response = elastic.search( strIndexId, bodyJSON.replaceAll( "/\\/", "" ) );
            JSONObject obj = (JSONObject) JSONSerializer.toJSON( response );
            JSONObject aggr = JSONObject.fromObject( obj.get( "aggregations" ) );
            JSONObject langs = JSONObject.fromObject( aggr.get( "langs" ) );
            JSONArray values = JSONArray.fromObject( langs.get( "buckets" ) );
            for ( int i = 0; i < values.size( ); i++ )
            {
                JSONObject value = (JSONObject) JSONSerializer.toJSON( values.get( i ) );
                JSONObject key = (JSONObject) JSONSerializer.toJSON( value.get( "key" ) );
                parentList.addItem( key.getString( "parentId" ).toString( ), key.get( "parentName" ).toString( ) );
            }
        }
        catch( ElasticClientException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace( );
        }
        return parentList;
    }

    /**
     * Get distinct values of a field
     * 
     * @param strIndexId
     *            The index id
     * @param strFieldName
     *            the field name
     * @param nMaxSize
     *            the max size
     * @return a list of values
     */
    public static List<String> getFieldDistinctValues( String strIndexId, String strFieldName, int nMaxSize )
    {
        List<String> distinctValuesList = new ArrayList<>( );
        String bodyJSON = "{\"size\":0,\"aggs\":{\"langs\":{\"terms\":{\"field\":\"" + strFieldName + "\",\"size\":" + nMaxSize + "}}}}";
        Elastic elastic = DataSourceService.getElastic( );
        try
        {
            String response = elastic.search( strIndexId, bodyJSON.replaceAll( "/\\/", "" ) );
            JSONObject obj = (JSONObject) JSONSerializer.toJSON( response );
            JSONObject aggr = JSONObject.fromObject( obj.get( "aggregations" ) );
            JSONObject langs = JSONObject.fromObject( aggr.get( "langs" ) );
            JSONArray values = JSONArray.fromObject( langs.get( "buckets" ) );
            for ( int i = 0; i < values.size( ); i++ )
            {
                String value = values.get( i ).toString( );
                distinctValuesList.add( value );
            }
        }
        catch( ElasticClientException e )
        {
            AppLogService.error( "Unable to connect to Elastic server", e );
        }
        return distinctValuesList;
    }

    /**
     * Remove backslash
     * 
     * @param jsString
     *            string to clean
     * @return string without multiple backslash
     */
    public static String removeUnusedBackSlash( String jsString )
    {
        jsString = jsString.replace( "\\\\\\\"", "\\\"" );
        return jsString;
    }

    /**
     * escape json string
     * 
     * @param jsString
     *            string to escape
     * @return string escaped
     */
    public static String escape( String jsString )
    {
        jsString = jsString.replace( "\\", "\\\\" );
        jsString = jsString.replace( "\"", "\\\"" );
        jsString = jsString.replace( "\b", "\\b" );
        jsString = jsString.replace( "\f", "\\f" );
        jsString = jsString.replace( "\n", "\\n" );
        jsString = jsString.replace( "\r", "\\r" );
        jsString = jsString.replace( "\t", "\\t" );
        jsString = jsString.replace( "/", "\\/" );
        return jsString;
    }

    /**
     * Post a saved object
     * 
     * @param savedObject
     *            Object to save
     */
    private static final void doPostSavedObject( JSONObject savedObject )
    {
        Map<String, String> headers = new HashMap<>( );
        headers.put( "kbn-xsrf", "true" );
        HttpAccess httpAccess = new HttpAccess( );
        JSONObject objCreate = new JSONObject( );
        JSONArray jsonArray = new JSONArray( );
        jsonArray.add( savedObject.toString( ) );
        objCreate.accumulate( "objects", jsonArray );
        objCreate.put( "version", "1" );
        String strObject = objCreate.toString( );
        strObject = removeUnusedBackSlash( strObject );
        try
        {
            httpAccess.doPostJSON( KIBANA_SERVER_URL + "/s/" + KIBANA_SERVER_SPACE_ID + "/api/kibana/dashboards/import?force=true", strObject, _authenticator,
                    null, headers, null );
        }
        catch( HttpAccessException ex )
        {
            AppLogService.error( "Unable to connect to Kibana server", ex );
        }
    }

    /**
     * Post a saved object
     * 
     * @param savedObject
     *            Object to save
     */
    public static final void doPostSavedObject( List<JSONObject> savedObjectList )
    {
        Map<String, String> headers = new HashMap<>( );
        headers.put( "kbn-xsrf", "true" );
        HttpAccess httpAccess = new HttpAccess( );
        JSONObject objCreate = new JSONObject( );
        JSONArray jsonArray = new JSONArray( );
        for ( JSONObject savedObject : savedObjectList )
        {
            jsonArray.add( savedObject.toString( ) );
        }
        objCreate.accumulate( "objects", jsonArray );
        objCreate.put( "version", "1" );
        String strObject = objCreate.toString( );
        strObject = removeUnusedBackSlash( strObject );
        try
        {
            httpAccess.doPostJSON( KIBANA_SERVER_URL + "/s/" + KIBANA_SERVER_SPACE_ID + "/api/kibana/dashboards/import?force=true", strObject, _authenticator,
                    null, headers, null );
        }
        catch( HttpAccessException ex )
        {
            AppLogService.error( "Unable to connect to Kibana server", ex );
        }
    }

    /**
     * Post a saved object
     * 
     * @param strObject
     *            Object string to save
     */
    private static final void doPostSavedStringObject( String strObject )
    {
        Map<String, String> headers = new HashMap<>( );
        headers.put( "kbn-xsrf", "true" );
        HttpAccess httpAccess = new HttpAccess( );
        strObject = removeUnusedBackSlash( strObject );
        try
        {
            httpAccess.doPostJSON( KIBANA_SERVER_URL + "/s/" + KIBANA_SERVER_SPACE_ID + "/api/kibana/dashboards/import?force=true", strObject, _authenticator,
                    null, headers, null );
        }
        catch( HttpAccessException ex )
        {
            AppLogService.error( "Unable to connect to Kibana server", ex );
        }
    }
}
