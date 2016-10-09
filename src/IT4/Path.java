//This is a path for NPCs

package IT4;

/**
 *
 * @author Jim (Admin)
 */

import java.util.ArrayList;

public class Path
{
    private ArrayList<Waypoint> waypoints;
    private int nextWaypoint;

    public Path()
    {
        waypoints = null;
    }

    //The following 2 methods are used for an ally to follow the player
    public void enqueue(Waypoint w)
    {
        if (waypoints != null)
        {
           waypoints.add(w);
        }
    }

    public void dequeue()
    {
        if (!waypoints.isEmpty())
        {
            waypoints.remove(0);
        }
    }

    public void addWaypoints(ArrayList<Waypoint> wpts)
    {
        waypoints = wpts;

        //There must be at least 2 waypoints
        if (waypoints.size() > 1)
        {
            nextWaypoint = 1;
        }
        else
        {
            nextWaypoint = 0;
        }
    }

    public void removeAllWaypoints()
    {
        waypoints.clear();
    }

    public Waypoint getStartingWaypoint()
    {
        return waypoints.get(0);
    }

    public Waypoint getEndingWaypoint()
    {
        return waypoints.get(waypoints.size() - 1);
    }

    public Waypoint getNextWaypoint()
    {
        return waypoints.get(nextWaypoint);
    }

    public void adjust()
    {
        for(int i = nextWaypoint; i < waypoints.size(); i++)
        {
            waypoints.get(i).setWaypointBehavior(WaypointBehavior.STOP);
        }
    }

    public void resetPath()
    {
        nextWaypoint = 0;
    }

    public void refresh()
    {
        if (waypoints.size() > 1)
        {
            nextWaypoint = 1;
        }
        else
        {
            nextWaypoint = 0;
        }
    }

    public Waypoint getCurrentWaypoint()
    {
        if ((nextWaypoint - 1) >= 0)
        {
            return waypoints.get(nextWaypoint - 1);
        }
        else
        {
            return waypoints.get(waypoints.size() - 1);
        }
    }

    public void setNextWaypoint(int index)
    {
        if ((index >= 0) && (index < waypoints.size()))
        {
            nextWaypoint = index;
        }
    }

    public int getNextWaypointIndex()
    {
        return nextWaypoint;
    }

    public int getNumWaypoints()
    {
        return waypoints.size();
    }

    public void reachedWaypoint()
    {
        nextWaypoint++;

        //System.out.println("Next Waypoint: " + nextWaypoint);
        
        //Loop back to the starting Waypoint
        if (nextWaypoint >= waypoints.size())
        {
           nextWaypoint = 0;
        }
    }

    public Path copy()
    {
        Path np = new Path();
        ArrayList<Waypoint> wpts = new ArrayList<Waypoint>();
        for(int i = 0; i < waypoints.size(); i++)
        {
            wpts.add(waypoints.get(i).copy());
        }
        np.addWaypoints(wpts);
        return np;
    }

    public ArrayList<Waypoint> getAllWaypoints()
    {
        return waypoints;
    }

    @Override
    public String toString()
    {
        String ws = "num waypoints: " + waypoints.size() + " nextWaypoint=" + this.nextWaypoint + "\n";
        for(int i = 0; i < waypoints.size(); i++)
        {
            Waypoint w = waypoints.get(i);
            ws += "Waypoint " + i + ": X=" + w.getXPos() + " Y=" + w.getYPos() + " Direction=" + w.getDirection().toString() + " Behavior=" + w.getBehavior().toString() + "\n";
        }
        return ws;
    }

}
