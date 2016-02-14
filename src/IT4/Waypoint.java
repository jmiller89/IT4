//This is a Waypoint for an NPC's Path

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class Waypoint
{
    private int xPos;
    private int yPos;
    private Direction dir;
    private WaypointBehavior behavior;

    public Waypoint(int x, int y, Direction d, WaypointBehavior b)
    {
        xPos = x;
        yPos = y;
        dir = d;
        behavior = b;
    }

    public int getXPos()
    {
        return xPos;
    }

    public int getYPos()
    {
        return yPos;
    }

    public Direction getDirection()
    {
        return dir;
    }

    public WaypointBehavior getBehavior()
    {
        return behavior;
    }

    public void setXPos(int x)
    {
        xPos = x;
    }

    public void setYPos(int y)
    {
        yPos = y;
    }

    public void setDirection(Direction d)
    {
        dir = d;
    }

    public void setWaypointBehavior(WaypointBehavior b)
    {
        behavior = b;
    }

    public Waypoint copy()
    {
        return new Waypoint(this.xPos, this.yPos, this.dir, this.behavior);
    }

    public Waypoint copyAndReverse()
    {
        Waypoint copied = this.copy();

        if (copied.getDirection() == Direction.UP)
        {
            copied.setDirection(Direction.RIGHT);
        }
        else if (copied.getDirection() == Direction.DOWN)
        {
            copied.setDirection(Direction.LEFT);
        }
        else if(copied.getDirection() == Direction.LEFT)
        {
            copied.setDirection(Direction.UP);
        }
        else if (copied.getDirection() == Direction.RIGHT)
        {
            copied.setDirection(Direction.DOWN);
        }

        return copied;
    }
}
