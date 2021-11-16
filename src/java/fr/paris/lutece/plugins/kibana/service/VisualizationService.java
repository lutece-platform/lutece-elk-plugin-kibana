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
package fr.paris.lutece.plugins.kibana.service;

import java.util.UUID;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class VisualizationService
{
    /**
     * create a standard visualisation object to fill
     * 
     * @param visualization
     *            visualisation
     */
    public static JSONObject getVisualizationObject( String strTitle, String strIdVisualization, String strIdIndexPattern, String queryFilter )
    {
        JSONObject visualizationJSON = new JSONObject( );
        JSONObject searchSourceJSON = new JSONObject( );
        searchSourceJSON.put( "searchSourceJSON", "{\\\"query\\\":{\\\"query\\\":\\\"" + queryFilter
                + "\\\",\\\"language\\\":\\\"kuery\\\"},\\\"filter\\\":[],\\\"indexRefName\\\":\\\"kibanaSavedObjectMeta.searchSourceJSON.index\\\"}" );
        JSONObject migrationVersion = new JSONObject( );
        migrationVersion.put( "visualization", "7.8.0" );
        JSONObject reference = new JSONObject( );
        reference.put( "id", strIdIndexPattern );
        reference.put( "name", "kibanaSavedObjectMeta.searchSourceJSON.index" );
        reference.put( "type", "index-pattern" );
        JSONArray references = new JSONArray( );
        references.add( reference );
        JSONObject attributes = new JSONObject( );
        attributes.put( "title", strTitle.replace( ".keyword", "" ) );
        attributes.put( "description", "" );
        attributes.put( "version", "1" );
        attributes.put( "kibanaSavedObjectMeta", searchSourceJSON );
        attributes.put( "uiStateJSON", "" );
        visualizationJSON.put( "attributes", attributes );
        visualizationJSON.put( "id", strIdVisualization );
        visualizationJSON.put( "migrationVersion", migrationVersion );
        visualizationJSON.accumulate( "references", references );
        visualizationJSON.put( "type", "visualization" );
        visualizationJSON.put( "updated_at", "2020-10-02T20:04:58.470Z" );
        visualizationJSON.put( "version", "Wzc4OTUsN10=" );
        return visualizationJSON;
    }

    /**
     * create datatable with top values of a field
     * 
     * @param visualization
     *            visualisation
     */
    public static void createDataTableTopValueVisualization( String strTitle, String strFieldName, String strIdVisualization, String strIdIndexPattern,
            String queryFilter )
    {
        JSONObject dataTable = getVisualizationObject( strTitle, strIdVisualization, strIdIndexPattern, queryFilter );
        String visState = "{\\\"title\\\":\\\"" + strTitle
                + "\\\",\\\"type\\\":\\\"table\\\",\\\"aggs\\\":[{\\\"id\\\":\\\"1\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"count\\\",\\\"params\\\":{\\\"customLabel\\\":\\\"Nombre de réponses\\\"},\\\"schema\\\":\\\"metric\\\"},{\\\"id\\\":\\\"2\\\",\\\"enabled\\\":true,\\\"type\\\":\\\"terms\\\",\\\"params\\\":{\\\"field\\\":\\\""
                + strFieldName
                + "\\\",\\\"orderBy\\\":\\\"1\\\",\\\"order\\\":\\\"desc\\\",\\\"size\\\":500,\\\"otherBucket\\\":true,\\\"otherBucketLabel\\\":\\\"Autres\\\",\\\"missingBucket\\\":true,\\\"missingBucketLabel\\\":\\\"Inconnu\\\",\\\"customLabel\\\":\\\""
                + strTitle
                + "\\\"},\\\"schema\\\":\\\"bucket\\\"}],\\\"params\\\":{\\\"perPage\\\":500,\\\"showPartialRows\\\":true,\\\"showMetricsAtAllLevels\\\":false,\\\"sort\\\":{\\\"columnIndex\\\":null,\\\"direction\\\":null},\\\"showTotal\\\":true,\\\"totalFunc\\\":\\\"sum\\\",\\\"percentageCol\\\":\\\"Réponses\\\"}}";
        ( (JSONObject) dataTable.get( "attributes" ) ).put( "visState", visState );
        SavedObjectService.create( dataTable );
    }

    /**
     * create donut lens visualization
     * 
     * @param visualization
     *            visualisation
     */
    public static void createDonutLensVisualization( String strTitle, String strFieldName, String strIdVisualization, String strIdIndexPattern,
            String queryFilter )
    {
        String strGroupId = UUID.randomUUID( ).toString( );
        String strLayerId = UUID.randomUUID( ).toString( );
        String strColumnId = UUID.randomUUID( ).toString( );
        String strDonutObj = "{\"attributes\":{\"description\":\"\",\"state\":{\"datasourceStates\":{\"indexpattern\":{\"layers\":{\"" + strLayerId
                + "\":{\"columnOrder\":[\"" + strGroupId + "\",\"" + strColumnId + "\"],\"columns\":{\"" + strGroupId
                + "\":{\"dataType\":\"string\",\"isBucketed\":true,\"label\":\"" + strTitle
                + "\",\"operationType\":\"terms\",\"params\":{\"orderBy\":{\"columnId\":\"" + strColumnId
                + "\",\"type\":\"column\"},\"orderDirection\":\"desc\",\"size\":50},\"scale\":\"ordinal\",\"sourceField\":\"" + strFieldName + "\"},\""
                + strColumnId
                + "\":{\"dataType\":\"number\",\"isBucketed\":false,\"label\":\"Count of records\",\"operationType\":\"count\",\"scale\":\"ratio\",\"sourceField\":\"Records\"}}}}}},\"filters\":[],\"query\":{\"language\":\"kuery\",\"query\":\""
                + queryFilter + "\"},\"visualization\":{\"layers\":[{\"categoryDisplay\":\"default\",\"groups\":[\"" + strGroupId + "\"],\"layerId\":\""
                + strLayerId + "\",\"legendDisplay\":\"show\",\"legendPosition\":\"right\",\"metric\":\"" + strColumnId
                + "\",\"nestedLegend\":false,\"numberDisplay\":\"percent\"}],\"shape\":\"donut\"}},\"title\":\"" + strTitle
                + "\",\"visualizationType\":\"lnsPie\"},\"id\":\"" + strIdVisualization
                + "\",\"migrationVersion\":{\"lens\":\"7.10.0\"},\"references\":[{\"id\":\"" + strIdIndexPattern
                + "\",\"name\":\"indexpattern-datasource-current-indexpattern\",\"type\":\"index-pattern\"},{\"id\":\"" + strIdIndexPattern
                + "\",\"name\":\"indexpattern-datasource-layer-" + strLayerId
                + "\",\"type\":\"index-pattern\"}],\"type\":\"lens\",\"updated_at\":\"2020-12-27T09:57:38.881Z\",\"version\":\"WzQ3Mzk2LDRd\"}";
        SavedObjectService.create( JSONObject.fromObject( strDonutObj ) );
    }

    /**
     * create map visualization
     * 
     * @param visualization
     *            visualisation
     */
    public static void createMapVisualization( String strTitle, String strFieldName, String strIdVisualization, String strIdIndexPattern, String queryFilter )
    {
        String strMapObj = "{\"objects\":[{\"attributes\":{\"description\":\"\",\"layerListJSON\":\"[{\\\"sourceDescriptor\\\":{\\\"type\\\":\\\"EMS_TMS\\\",\\\"isAutoSelect\\\":true},\\\"id\\\":\\\"5f4845a6-1b12-4d48-a640-96bf4b68d805\\\",\\\"label\\\":null,\\\"minZoom\\\":0,\\\"maxZoom\\\":24,\\\"alpha\\\":1,\\\"visible\\\":true,\\\"style\\\":{\\\"type\\\":\\\"TILE\\\"},\\\"type\\\":\\\"VECTOR_TILE\\\"},{\\\"sourceDescriptor\\\":{\\\"geoField\\\":\\\""
                + strFieldName
                + "\\\",\\\"filterByMapBounds\\\":true,\\\"scalingType\\\":\\\"CLUSTERS\\\",\\\"topHitsSize\\\":1,\\\"id\\\":\\\"6cc4541c-73ad-46e0-b331-3a14ba5010f4\\\",\\\"type\\\":\\\"ES_SEARCH\\\",\\\"tooltipProperties\\\":[\\\""
                + strFieldName.replace( "elastic.geopoint", "address" )
                + "\\\"],\\\"sortField\\\":\\\"\\\",\\\"sortOrder\\\":\\\"desc\\\",\\\"indexPatternRefName\\\":\\\"layer_1_source_index_pattern\\\"},\\\"id\\\":\\\"b0506a1c-5963-4d75-ae45-049d48ef45be\\\",\\\"label\\\":\\\"Adresse\\\",\\\"minZoom\\\":0,\\\"maxZoom\\\":24,\\\"alpha\\\":0.75,\\\"visible\\\":false,\\\"style\\\":{\\\"type\\\":\\\"VECTOR\\\",\\\"properties\\\":{\\\"icon\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"value\\\":\\\"marker\\\"}},\\\"fillColor\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"color\\\":\\\"#54B399\\\"}},\\\"lineColor\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"color\\\":\\\"#41937c\\\"}},\\\"lineWidth\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"size\\\":1}},\\\"iconSize\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"size\\\":6}},\\\"iconOrientation\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"orientation\\\":0}},\\\"labelText\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"value\\\":\\\"\\\"}},\\\"labelColor\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"color\\\":\\\"#000000\\\"}},\\\"labelSize\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"size\\\":14}},\\\"labelBorderColor\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"color\\\":\\\"#FFFFFF\\\"}},\\\"symbolizeAs\\\":{\\\"options\\\":{\\\"value\\\":\\\"circle\\\"}},\\\"labelBorderSize\\\":{\\\"options\\\":{\\\"size\\\":\\\"SMALL\\\"}}},\\\"isTimeAware\\\":true},\\\"type\\\":\\\"BLENDED_VECTOR\\\",\\\"joins\\\":[]},{\\\"sourceDescriptor\\\":{\\\"type\\\":\\\"ES_GEO_GRID\\\",\\\"id\\\":\\\"2baa1738-6600-47b2-9c59-0122444fe6eb\\\",\\\"geoField\\\":\\\""
                + strFieldName
                + "\\\",\\\"metrics\\\":[{\\\"type\\\":\\\"count\\\",\\\"label\\\":\\\"Nombre de demandes\\\"},{\\\"type\\\":\\\"terms\\\",\\\"field\\\":\\\""
                + strFieldName.replace( "elastic.geopoint", "address.keyword" )
                + "\\\",\\\"label\\\":\\\"Top Adresse\\\"}],\\\"requestType\\\":\\\"point\\\",\\\"resolution\\\":\\\"COARSE\\\",\\\"indexPatternRefName\\\":\\\"layer_2_source_index_pattern\\\"},\\\"style\\\":{\\\"type\\\":\\\"VECTOR\\\",\\\"properties\\\":{\\\"icon\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"value\\\":\\\"marker\\\"}},\\\"fillColor\\\":{\\\"type\\\":\\\"DYNAMIC\\\",\\\"options\\\":{\\\"color\\\":\\\"Blues\\\",\\\"colorCategory\\\":\\\"palette_0\\\",\\\"field\\\":{\\\"name\\\":\\\"doc_count\\\",\\\"origin\\\":\\\"source\\\"},\\\"fieldMetaOptions\\\":{\\\"isEnabled\\\":true,\\\"sigma\\\":3},\\\"type\\\":\\\"ORDINAL\\\"}},\\\"lineColor\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"color\\\":\\\"#FFF\\\"}},\\\"lineWidth\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"size\\\":0}},\\\"iconSize\\\":{\\\"type\\\":\\\"DYNAMIC\\\",\\\"options\\\":{\\\"minSize\\\":7,\\\"maxSize\\\":32,\\\"field\\\":{\\\"name\\\":\\\"doc_count\\\",\\\"origin\\\":\\\"source\\\"},\\\"fieldMetaOptions\\\":{\\\"isEnabled\\\":true,\\\"sigma\\\":3}}},\\\"iconOrientation\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"orientation\\\":0}},\\\"labelText\\\":{\\\"type\\\":\\\"DYNAMIC\\\",\\\"options\\\":{\\\"field\\\":{\\\"name\\\":\\\"doc_count\\\",\\\"origin\\\":\\\"source\\\"}}},\\\"labelColor\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"color\\\":\\\"#000000\\\"}},\\\"labelSize\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"size\\\":14}},\\\"labelBorderColor\\\":{\\\"type\\\":\\\"STATIC\\\",\\\"options\\\":{\\\"color\\\":\\\"#FFFFFF\\\"}},\\\"symbolizeAs\\\":{\\\"options\\\":{\\\"value\\\":\\\"circle\\\"}},\\\"labelBorderSize\\\":{\\\"options\\\":{\\\"size\\\":\\\"SMALL\\\"}}},\\\"isTimeAware\\\":true},\\\"id\\\":\\\"2f0bad2d-3e25-4ca1-ad97-c246b685ed9b\\\",\\\"label\\\":\\\"Nombre d'adresses\\\",\\\"minZoom\\\":0,\\\"maxZoom\\\":24,\\\"alpha\\\":0.75,\\\"visible\\\":true,\\\"type\\\":\\\"VECTOR\\\",\\\"joins\\\":[]},{\\\"sourceDescriptor\\\":{\\\"type\\\":\\\"ES_GEO_GRID\\\",\\\"id\\\":\\\"15628a81-26e3-44ab-8a67-2066e63d70c8\\\",\\\"geoField\\\":\\\""
                + strFieldName
                + "\\\",\\\"metrics\\\":[{\\\"type\\\":\\\"count\\\",\\\"label\\\":\\\"Nombre de demande\\\"}],\\\"requestType\\\":\\\"heatmap\\\",\\\"resolution\\\":\\\"COARSE\\\",\\\"indexPatternRefName\\\":\\\"layer_3_source_index_pattern\\\"},\\\"id\\\":\\\"6df9c480-2b22-433a-a77e-ec00157b93b1\\\",\\\"label\\\":\\\"Intensité de la demande\\\",\\\"minZoom\\\":0,\\\"maxZoom\\\":24,\\\"alpha\\\":0.75,\\\"visible\\\":false,\\\"style\\\":{\\\"type\\\":\\\"HEATMAP\\\",\\\"colorRampName\\\":\\\"theclassic\\\"},\\\"type\\\":\\\"HEATMAP\\\",\\\"joins\\\":[]}]\",\"mapStateJSON\":\"{\\\"zoom\\\":11.51,\\\"center\\\":{\\\"lon\\\":2.36938,\\\"lat\\\":48.85081},\\\"timeFilters\\\":{\\\"from\\\":\\\"now-3y\\\",\\\"to\\\":\\\"now\\\"},\\\"refreshConfig\\\":{\\\"isPaused\\\":true,\\\"interval\\\":10000},\\\"query\\\":{\\\"query\\\":\\\"documentTypeName.keyword : formResponse\\\",\\\"language\\\":\\\"kuery\\\"},\\\"filters\\\":[],\\\"settings\\\":{\\\"autoFitToDataBounds\\\":false,\\\"initialLocation\\\":\\\"LAST_SAVED_LOCATION\\\",\\\"fixedLocation\\\":{\\\"lat\\\":0,\\\"lon\\\":0,\\\"zoom\\\":2},\\\"browserLocation\\\":{\\\"zoom\\\":2},\\\"maxZoom\\\":24,\\\"minZoom\\\":0,\\\"showSpatialFilters\\\":true,\\\"spatialFiltersAlpa\\\":0.3,\\\"spatialFiltersFillColor\\\":\\\"#DA8B45\\\",\\\"spatialFiltersLineColor\\\":\\\"#DA8B45\\\"}}\",\"title\":\""
                + strTitle + "\",\"uiStateJSON\":\"{\\\"isLayerTOCOpen\\\":true,\\\"openTOCDetails\\\":[]}\"},\"id\":\"" + strIdVisualization
                + "\",\"migrationVersion\":{\"map\":\"7.10.0\"},\"references\":[{\"id\":\"" + strIdIndexPattern
                + "\",\"name\":\"layer_1_source_index_pattern\",\"type\":\"index-pattern\"},{\"id\":\"" + strIdIndexPattern
                + "\",\"name\":\"layer_2_source_index_pattern\",\"type\":\"index-pattern\"},{\"id\":\"" + strIdIndexPattern
                + "\",\"name\":\"layer_3_source_index_pattern\",\"type\":\"index-pattern\"}],\"type\":\"map\",\"updated_at\":\"2021-02-09T10:51:18.667Z\",\"version\":\"WzM2NTQ2Nyw4XQ==\"}],\"version\":\"1\"}";
        SavedObjectService.createFromString( strMapObj );
    }
}
