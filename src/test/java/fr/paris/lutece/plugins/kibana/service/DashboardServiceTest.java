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

import fr.paris.lutece.plugins.kibana.business.Dashboard;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.Utils;

import org.junit.Test;

import java.io.IOException;

import java.util.List;

/**
 *
 * @author evrardmax
 */
public class DashboardServiceTest extends LuteceTestCase
{
    /**
     * Test of getListDashboard method, of class DashboardService.
     */
    @Test
    public void testGetListDashboard( ) throws IOException
    {
        System.out.println( "getListDashboard" );

        String strJSON = Utils.getFileContent( "search_dashboard.json" );
        System.out.println( strJSON );

        List<Dashboard> list = DashboardService.getInstance( ).getListDashboard( strJSON );
        assertTrue( list.size( ) == 2 );
        assertEquals( list.get( 0 ).getIdKibanaDashboard( ), "87a95c20-f257-11e8-b738-7fcdd48fbabe" );
        assertEquals( list.get( 0 ).getTitle( ), "a # b" );
        assertEquals( list.get( 1 ).getIdKibanaDashboard( ), "9fd58fd0-f257-11e8-b738-7fcdd48fbabe" );
        assertEquals( list.get( 1 ).getTitle( ), "not so easy" );
    }
}
