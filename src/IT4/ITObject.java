//This is any "Object" that can be drawn on the map

package IT4;

import java.awt.Point;

/**
 *
 * @author Jim (Admin)
 */
public class ITObject
{
    protected short ID; //Used for Sprite Mapping
    protected int locX;
    protected int locY;
    public int lastX;
    public int lastY;
    public float fX;
    public float fY;

    public ITObject(short identification, int x, int y)
    {
        if (identification >= 0)
        {
            ID = identification;
        }
        else
        {
            ID = 0;
        }
        
        locX = x;
        locY = y;

        lastX = locX;
        lastY = locY;

        fX = (float)x;
        fY = (float)y;
    }

    public short getID()
    {
        return ID;
    }

    public int getX()
    {
        return locX;
    }

    public int getY()
    {
        return locY;
    }

    public int getTileX()
    {
        return ((locX + 19) / 40);
    }

    public int getTileY()
    {
        return ((locY + 19) / 40);
    }

    public void setID(int identification)
    {
        if (identification >= 0)
        {
            ID = (short)identification;
        }
    }

    public void setX(int x)
    {
        locX = x;
        fX = (float)locX;
    }

    public void setY(int y)
    {
        locY = y;
        fY = (float)locY;
    }

    /*
    public void move(Point vec)
    {
        locX+=vec.x;
        locY+=vec.y;
        fX = (float)locX;
        fY = (float)locY;
    }
     * 
     */

    public void move(FloatVec vec, float delta)
    {
        fX += (vec.x * delta);
        fY += (vec.y * delta);

        lastX = locX;
        lastY = locY;

        locX = (int)(fX + 0.5f);
        locY = (int)(fY + 0.5f);
    }

    public void move(float dX, float dY, float delta)
    {
        fX += (dX * delta);
        fY += (dY * delta);

        lastX = locX;
        lastY = locY;

        locX = (int)(fX + 0.5f);
        locY = (int)(fY + 0.5f);
    }

}
