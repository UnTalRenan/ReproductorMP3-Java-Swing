

package mp3seth;


import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
//https://www.youtube.com/user/Renan6x3
public class AbsoluteConstraints
    implements Serializable
{

    public AbsoluteConstraints(Point pos)
    {
        this(pos.x, pos.y);
    }

    public AbsoluteConstraints(int x, int y)
    {
        width = -1;
        height = -1;
        this.x = x;
        this.y = y;
    }

    public AbsoluteConstraints(Point pos, Dimension size)
    {
        width = -1;
        height = -1;
        x = pos.x;
        y = pos.y;
        if(size != null)
        {
            width = size.width;
            height = size.height;
        }
    }

    public AbsoluteConstraints(int x, int y, int width, int height)
    {
        this.width = -1;
        this.height = -1;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public String toString()
    {
        return (new StringBuilder()).append(super.toString()).append(" [x=").append(x).append(", y=").append(y).append(", width=").append(width).append(", height=").append(height).append("]").toString();
    }

    static final long serialVersionUID = 0x490476a535ef832eL;
    public int x;
    public int y;
    public int width;
    public int height;
}
