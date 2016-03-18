/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
import java.util.ArrayList;

public class Spawn
{
    private int tileX;
    private int tileY;
    private int realX;
    private int realY;
    private GuardType guardType;
    private int limit;
    private int numSpawned = 0;
    public boolean remove = false;
    private boolean armored = false;

    public Spawn(int x, int y, GuardType gt, int lim, boolean armor)
    {
        tileX = x;
        tileY = y;

        realX = tileX * 40;
        realY = tileY * 40;

        guardType = gt;
        limit = lim;
        armored = armor;
    }

    public Spawn copy()
    {
        return new Spawn(this.tileX, this.tileY, this.guardType, this.limit, this.armored);
    }

    public void reset()
    {
        numSpawned = 0;
    }

    public NPC spawnNPC()
    {
        Path p = new Path();
        ArrayList<Waypoint> way = new ArrayList<Waypoint>();
        way.add(new Waypoint(tileX, tileY, Direction.DOWN, WaypointBehavior.CONTINUE));
        p.addWaypoints(way);

        NPC toSpawn = null;

        if (numSpawned < limit)
        {
            toSpawn = NPC.create(guardType, realX, realY, Direction.DOWN, NPCStatus.ALERT, false, p);

            toSpawn.bodyArmor = armored;
            toSpawn.spawned = true;
            numSpawned++;
        }
        
        return toSpawn;
    }
}
