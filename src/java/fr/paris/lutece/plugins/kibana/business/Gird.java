package fr.paris.lutece.plugins.kibana.business;

public class Gird
{
    private int X;
    private int Y;
    private int W;
    private int H;

    public Gird( )
    {
        this( 0, 0, 0, 0 );
    }

    public Gird( int X, int Y, int W, int H )
    {
        this.X = X;
        this.Y = Y;
        this.W = W;
        this.H = H;
    }

    public int getX( )
    {
        return X;
    }

    public int getY( )
    {
        return Y;
    }

    public int getW( )
    {
        return W;
    }

    public int getH( )
    {
        return H;
    }

    public void setX( int X )
    {
        this.X = X;
    }

    public void setY( int Y )
    {
        this.Y = Y;
    }

    public void setW( int W )
    {
        this.W = W;
    }

    public void setH( int H )
    {
        this.H = H;
    }
}
