/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fr.paris.lutece.plugins.kibana.service;

import fr.paris.lutece.test.LuteceTestCase;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import org.junit.Test;

/**
 *
 * @author evrardmax
 */
public class DashboardServiceTest extends LuteceTestCase {
 

    /**
     * Test of getListDashboard method, of class DashboardService.
     */
    @Test
    public void testGetListDashboard() {
        System.out.println("getListDashboard");
        String strJSON = getTestJson(  );
        DashboardService instance = new DashboardService();
        List<String> expResult = null;
        List<String> result = instance.getListDashboard(strJSON);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    private String getTestJson(  )
    {
        try
        {
            URL url = Thread.currentThread(  ).getContextClassLoader(  ).getResource( "search_dashboard.json" );
            File file = new File( url.getPath(  ) );

            return readFile( file );
        }
        catch ( IOException ex )
        {
            throw new RuntimeException( "Unable to load test file : " + ex.getMessage(  ) );
        }
    }
    

    
    private static String readFile( File file ) throws IOException
    {
        BufferedReader reader = new BufferedReader( new FileReader( file ) );
        String strLine;
        StringBuilder sbString = new StringBuilder(  );

        while ( ( strLine = reader.readLine(  ) ) != null )
        {
            sbString.append( strLine );
        }

        return sbString.toString(  );
    }

}
