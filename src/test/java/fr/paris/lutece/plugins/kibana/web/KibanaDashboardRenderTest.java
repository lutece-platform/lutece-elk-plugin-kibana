/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.kibana.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.service.template.HtmlAttributesMethod;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.HTMLOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;

/**
 * Renders the dashboard templates with HTML auto-escaping and the back office macros (LUT-33153), as
 * KibanaDashboardJspBean builds their model: the texts are escaped, the Kibana server URL goes through safeUrl and the
 * button attributes are a params map. The core macros are read from the exploded webapp (lutece:exploded).
 */
public class KibanaDashboardRenderTest
{
    private static final String TEMPLATES_ROOT = "webapp/WEB-INF/templates";
    private static final String CORE_TEMPLATES_ROOT = "target/lutece/WEB-INF/templates";
    private static final String COMMONS = "commons_bs5_tabler.html";
    private static final String TEMPLATE_DASHBOARD = "admin/plugins/kibana/kibana_dashboard.html";
    private static final String TEMPLATE_DASHBOARD_LIST = "admin/plugins/kibana/kibana_dashboard_list.html";
    private static final String TITLE = "Stats <b>\"x\"</b>";
    private static final String TITLE_ESCAPED = "Stats &lt;b&gt;&quot;x&quot;&lt;/b&gt;";

    /**
     * Renders a template of the plugin with the back office macros auto-included.
     *
     * @param strTemplate
     *            the template
     * @param model
     *            the model
     * @return the HTML
     * @throws IOException
     *             if a template cannot be read
     * @throws TemplateException
     *             if the rendering fails
     */
    private static String render( String strTemplate, Map<String, Object> model ) throws IOException, TemplateException
    {
        assumeTrue( new File( CORE_TEMPLATES_ROOT, COMMONS ).exists( ), "core templates not exploded under " + CORE_TEMPLATES_ROOT );
        Configuration cfg = new Configuration( Configuration.VERSION_2_3_28 );
        // as AbstractFreeMarkerTemplateService configures the engine
        cfg.setNumberFormat( "0.######" );
        cfg.setLocalizedLookup( false );
        cfg.setTemplateLoader( new MultiTemplateLoader( new TemplateLoader [ ] {
                new FileTemplateLoader( new File( TEMPLATES_ROOT ) ), new FileTemplateLoader( new File( CORE_TEMPLATES_ROOT ) )
        } ) );
        cfg.addAutoInclude( COMMONS );
        cfg.setOutputFormat( HTMLOutputFormat.INSTANCE );
        cfg.setAutoEscapingPolicy( Configuration.ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY );
        cfg.setSharedVariable( "i18n", (TemplateMethodModelEx) args -> String.valueOf( args.get( 0 ) ) );
        cfg.setSharedVariable( "dskey", (TemplateMethodModelEx) args -> "DS" );
        cfg.setSharedVariable( "htmlAttributes", new HtmlAttributesMethod( true ) );
        StringWriter writer = new StringWriter( );
        cfg.getTemplate( strTemplate ).process( model, writer );

        return writer.toString( );
    }

    /**
     * The model of the dashboard view, with the rights of the indexing buttons.
     *
     * @param strServerUrl
     *            the Kibana server URL
     * @return the model
     */
    private static Map<String, Object> dashboardModel( String strServerUrl )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "dashboard", Map.of( "title", TITLE, "idDataSource", "FormsDataSource", "idKibanaDashboard", "forms_12" ) );
        model.put( "kibana_server_url", strServerUrl );
        model.put( "kibana_server_space_id", "default" );
        model.put( "right_elasticdata_management", true );
        model.put( "right_elasticdata_forms_management", true );

        return model;
    }

    /**
     * The dashboard renders its title as text, its iframe on the Kibana server and its indexing button attribute.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testDashboard( ) throws Exception
    {
        String strHtml = render( TEMPLATE_DASHBOARD, dashboardModel( "https://kibana.example/k" ) );

        assertTrue( strHtml.contains( TITLE_ESCAPED ), strHtml );
        assertTrue( strHtml.contains( "src=\"https://kibana.example/k/s/default/app/kibana#/dashboard/forms_12?embed=true" ), strHtml );
        assertTrue( strHtml.contains( "data-indexid=\"FormsDataSource\"" ), strHtml );
        assertTrue( strHtml.contains( "idForm=12\"" ), strHtml );
    }

    /**
     * A Kibana server URL whose scheme is not a web one is not written in the iframe.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testDashboardServerUrlIsChecked( ) throws Exception
    {
        String strHtml = render( TEMPLATE_DASHBOARD, dashboardModel( "javascript:void(0)//" ) );

        assertFalse( strHtml.contains( "javascript:" ), strHtml );
        assertTrue( strHtml.contains( "src=\"#/s/default/app/kibana" ), strHtml );
    }

    /**
     * The dashboard list renders the titles as text and the dashboard id as a URL parameter.
     *
     * @throws Exception
     *             if the rendering fails
     */
    @Test
    public void testDashboardList( ) throws Exception
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( "dashboards_list", List.of( Map.of( "title", TITLE, "idKibanaDashboard", "a&b" ) ) );
        String strHtml = render( TEMPLATE_DASHBOARD_LIST, model );

        assertTrue( strHtml.contains( TITLE_ESCAPED ), strHtml );
        assertTrue( strHtml.contains( "tab=a%26b\"" ), strHtml );
    }
}
