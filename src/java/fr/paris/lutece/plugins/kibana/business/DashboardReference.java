package fr.paris.lutece.plugins.kibana.business;

import java.util.UUID;

public class DashboardReference
{
    String _id;
    String _name;
    String _type;

    public DashboardReference( )
    {
        _id = UUID.randomUUID( ).toString( );
        _type = "visualization";
        _name = "panel_" + _id;
    }

    public DashboardReference( String strType )
    {
        _id = UUID.randomUUID( ).toString( );
        _type = strType;
        _name = "panel_" + _id;
    }

    public String getId( )
    {
        return _id;
    }

    public void setId( String id )
    {
        _id = id;
    }

    public String getName( )
    {
        return _name;
    }

    public void setName( String name )
    {
        _name = name;
    }

    public String getType( )
    {
        return _type;
    }

    public void setType( String type )
    {
        _type = type;
    }
}
