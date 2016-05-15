//This is one room in a Level.

package IT4;

/**
 *
 * @author Jim (Admin)
 */

import GFX.GLRenderThread;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class LevelMap
{
    private short[][] levMap;
    private byte[][] obstacleMatrix;
    public Vertex[][] vertices;
    private ArrayList<NPC> guards;
    private short defaultGroundCover;
    private ArrayList<Warp> warps;
    //private ArrayList<Door> doors;
    private HashMap<Point, Door> doors;
    private Door[] doors_ = null;

    private ArrayList<SecurityCamera> cameras;
    private ArrayList<Spawn> spawns;
    private ArrayList<Item> items;
    private ArrayList<Item> itemCopies;
    private ArrayList<Objective> objectives;
    private byte[][] grassWaterDoors;//Also has shadows; values 4+ == shadow
    private boolean alertMode = false;
    private Boss boss = null;
    private Boss bossCopy = null;

    private Dialog dlg;
    public Path playerPath = null;

    public boolean dark = false;
    public boolean semidark = false;
    public boolean gas = false;
    public boolean midnight = false;
    public boolean jam = false;
    public boolean rain = false;
    public boolean snow = false;
    public boolean forceprone = false;

    public int songIndex = 0;

    private static final short[] ignoreShadows = {47, 99, 100, 101, 102, 103, 104, 105, 106,
                                                  110, 111, 113, 114, 115, 116, 117, 145, 146,
                                                  161, 186, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219,
                                                  358, 359, 360, 361, 362, 363, 364, 366, 392, 393, 394, 395, 397, 403};

    private static final short[] appendShadows = {47, 99, 100, 101, 102, 103, 104, 105, 106, 113, 114, 115, 116, 117, 161, 186, 210, 211, 212, 213, 216, 358, 359, 360, 361, 362, 363, 364, 366,
                                                  392, 393, 394, 395};

    public LevelMap(short[][] tilemap, short defaultGC)
    {
        levMap = new short[tilemap.length][tilemap[0].length];
        obstacleMatrix = new byte[tilemap.length][tilemap[0].length];
        vertices = new Vertex[tilemap.length][tilemap[0].length];
        grassWaterDoors = new byte[tilemap.length][tilemap[0].length];
        defaultGroundCover = defaultGC;

        //Shave off anything out of bounds
        
        for(int i = 0; i < levMap[0].length; i++)
        {
            for(int j = 0; j < levMap.length; j++)
            {
                if ((levMap[j][i] < 0) || (levMap[j][i] >= GLRenderThread.NUMSPRITES))
                {
                    levMap[j][i] = defaultGroundCover;
                }
                else
                {
                    levMap[j][i] = tilemap[j][i];
                }
            }
        }

        guards = new ArrayList<NPC>();
        //doors = new ArrayList<Door>();
        doors = new HashMap<Point, Door>();
        warps = new ArrayList<Warp>();
        items = new ArrayList<Item>();
        itemCopies = new ArrayList<Item>();
        cameras = new ArrayList<SecurityCamera>();
        spawns = new ArrayList<Spawn>();
        objectives = new ArrayList<Objective>();

        initializeObstacleMatrix();
        initializeShadows();
        initializeWaterAndTallGrass();

        dlg = new Dialog(false);

    }


    private void setTile(short id, int y, int x)
    {
        if ((y < levMap.length) && (y >= 0) && (x < levMap[0].length) && (x >= 0))
        {
            levMap[y][x] = id;
        }
    }

    public byte[][] getObstacleMatrix()
    {
        return obstacleMatrix;
    }

    public void makeAlertStage()
    {
        alertMode = true;
    }

    public boolean getAlertMode()
    {
        return alertMode;
    }

    public void initializeBoss(Boss b)
    {
        boss = b;

        if (boss != null)
        {
            bossCopy = b.copyBoss();
        }
    }

    public Boss getBoss()
    {
        return boss;
    }

    public void noBoss()
    {
        boss = null;
    }

    public boolean isBossFight()
    {
        return (boss != null);
    }

    private void initializeObstacleMatrix()
    {
        for(int j = 0; j < levMap.length; j++)
        {
            for(int i = 0; i < levMap[0].length; i++)
            {
                //This is the range of "collidables" (walls)
                if (((levMap[j][i] >= 46) && (levMap[j][i] <= 54)) || ((levMap[j][i] >= 99) && (levMap[j][i] <= 107))
                        || ((levMap[j][i] >= 109) && (levMap[j][i] <= 117)) || (levMap[j][i] == 57) || ((levMap[j][i] > 144) && (levMap[j][i] < 147))
                        || ((levMap[j][i] >= 148) && (levMap[j][i] < 154))
                        || ((levMap[j][i] >= 155) && (levMap[j][i] < 192))
                        || ((levMap[j][i] >= 193) && (levMap[j][i] < 220)) || (levMap[j][i] == 252) || (levMap[j][i] == 253)
                        || (levMap[j][i] == 299) || (levMap[j][i] == 300)
                        || ((levMap[j][i] >= 358) && (levMap[j][i] < 365)) || (levMap[j][i] >= 366) && (levMap[j][i] < 369)
                        || ((levMap[j][i] > 370) && (levMap[j][i] < 379))
                        || ((levMap[j][i] > 391) && (levMap[j][i] < 445)))
                {
                    obstacleMatrix[j][i] = 0;
                }
                else
                {
                    obstacleMatrix[j][i] = 1;
                }
                
                vertices[j][i] = new Vertex(i, j);
            }
        }

        for(int j = 0; j < levMap.length; j++)
        {
            for(int i = 0; i < obstacleMatrix[0].length; i++)
            {
                Edge left = null;
                Edge right = null;
                Edge up = null;
                Edge down = null;

                ArrayList<Edge> edges = new ArrayList<Edge>(4);

                if (i-1 > -1)
                {
                    if (obstacleMatrix[j][i-1] == 1)
                    {
                        left = new Edge(vertices[j][i-1], obstacleMatrix[j][i-1]);
                        edges.add(left);
                    }
                }

                if (i+1 < obstacleMatrix[0].length)
                {
                    if (obstacleMatrix[j][i+1] == 1)
                    {
                        right = new Edge(vertices[j][i+1], obstacleMatrix[j][i+1]);
                        edges.add(right);
                    }
                }

                if (j-1 > -1)
                {
                    if (obstacleMatrix[j-1][i] == 1)
                    {
                        up = new Edge(vertices[j-1][i], obstacleMatrix[j-1][i]);
                        edges.add(up);
                    }
                }

                if (j+1 < obstacleMatrix.length)
                {
                    if (obstacleMatrix[j+1][i] == 1)
                    {
                        down = new Edge(vertices[j+1][i], obstacleMatrix[j+1][i]);
                        edges.add(down);
                    }
                }

                Edge[] adjs = new Edge[edges.size()];

                for(int z = 0; z < edges.size(); z++)
                {
                    adjs[z] = edges.get(z);
                }

                vertices[j][i].adjacencies = adjs;
            }
        }

    }

    private boolean ignoreShadow(short t)
    {
        boolean ignore = false;
        for(int i = 0; i < ignoreShadows.length; i++)
        {
            if (t == ignoreShadows[i])
            {
                ignore = true;
                break;
            }
        }
        return ignore;
    }

    private boolean appendShadow(short t)
    {
        boolean append = false;
        for(int i = 0; i < appendShadows.length; i++)
        {
            if (t == appendShadows[i])
            {
                append = true;
                break;
            }
        }
        return append;
    }

    private void initializeShadows()
    {
        for(int j = 0; j < levMap.length; j++)
        {
            for(int i = 0; i < levMap[0].length; i++)
            {
                //init to 0
                grassWaterDoors[j][i] = 0;

                boolean sf = false;
                if (i-1 >= 0)
                {
                    if (!ignoreShadow(levMap[j][i-1]))
                    {
                        if (((obstacleMatrix[j][i] == 1) || (appendShadow(levMap[j][i]))) && (obstacleMatrix[j][i-1] == 0))
                        {
                            grassWaterDoors[j][i] = 5;
                            sf = true;
                        }
                    }
                }

                if (j-1 >= 0)
                {
                    if (!ignoreShadow(levMap[j-1][i]))
                    {
                        if (((obstacleMatrix[j][i] == 1) || (appendShadow(levMap[j][i]))) && (obstacleMatrix[j-1][i] == 0))
                        {
                            if (!sf)
                            {
                                grassWaterDoors[j][i] = 4;
                            }
                            else
                            {
                                grassWaterDoors[j][i] = 6;
                            }
                        }
                    }
                }

                if ((j-1 >=0) && (i-1 >= 0))
                {
                    if (!ignoreShadow(levMap[j-1][i-1]))
                    {
                        if (((obstacleMatrix[j][i] == 1) || (appendShadow(levMap[j][i]))) && ((obstacleMatrix[j-1][i] == 1) || (appendShadow(levMap[j-1][i]))) && ((obstacleMatrix[j][i-1] == 1) || (appendShadow(levMap[j][i-1]))) && (obstacleMatrix[j-1][i-1] == 0))
                        {
                            grassWaterDoors[j][i] = 7;
                        }
                    }
                }

                if ((levMap[j][i] == 445) || (levMap[j][i] == 446) || (levMap[j][i] == 447) || (levMap[j][i] == 448))
                {
                    grassWaterDoors[j][i] = 0;
                }

            }
        }
    }

    private void initializeWaterAndTallGrass()
    {
        //water = new ArrayList<Water>();
        //tallGrass = new ArrayList<TallGrass>();

        for(int j = 0; j < levMap.length; j++)
        {
            for(int i = 0; i < levMap[0].length; i++)
            {
                //This is the range of water
                if ((levMap[j][i] == 43) || (levMap[j][i] == 83) || (levMap[j][i] == 254))
                {
                    grassWaterDoors[j][i] = 2;
                }
                else if (levMap[j][i] == 45) //This is tall grass
                {
                    if (grassWaterDoors[j][i] > 3)
                    {
                        grassWaterDoors[j][i] += 4;
                    }                   
                    else
                    {
                        grassWaterDoors[j][i] = 1;
                    }
                }                
            }
        }

    }

    public void initializeNPCs(ArrayList<NPC> npcs)
    {
        guards = npcs;
    }

    public void initializeWarps(ArrayList<Warp> w)
    {
        warps = w;
    }

    public void initializeSpawns(ArrayList<Spawn> s)
    {
        spawns = s;
    }

    public void initializeCameras(ArrayList<SecurityCamera> c)
    {
        cameras = c;
    }

    public void initializeDoors(ArrayList<Door> d)
    {
        doors = new HashMap<Point, Door>(d.size());
        doors_ = d.toArray(new Door[d.size()]);
        d.clear();

        //Set this so RenderThread knows not to draw defaultGC over the doors
        for(int i = 0; i < doors_.length; i++)
        {
            Door q = doors_[i];
            setTile(q.getID(), q.getTileY(), q.getTileX());

            int x,y;
            x = q.getTileX();
            y = q.getTileY();
            Point p = new Point(x,y);
            doors.put(p, q);
            
            //coords will point to key in hashmap
            grassWaterDoors[y][x] = 3;
        }
    }

    public void initializeItems(ArrayList<Item> it)
    {
        items = it;

        this.saveItems();
    }

    public void initializeObjectives(ArrayList<Objective> ob)
    {
        objectives = ob;
    }

    public int getTile(int x, int y)
    {
        return levMap[y][x];
    }

    public short getDefaultGroundCover()
    {
        return defaultGroundCover;
    }

    public ArrayList<NPC> getNPCs()
    {
        ArrayList<NPC> npcs = new ArrayList<NPC>();

        for(int i = 0; i < guards.size(); i++)
        {
            NPC copy = guards.get(i).copy();

            if (guards.get(i).bodyArmor)
            {
                copy.bodyArmor = true;
            }

            npcs.add(copy);
        }

        return npcs;
    }

    public ArrayList<Warp> getWarps()
    {
        return warps;
    }

    public HashMap<Point, Door> getDoorsHashMap()
    {
        return doors;
    }

    public Door[] getDoors()
    {
        return doors_;
    }

    public ArrayList<SecurityCamera> getCameras()
    {
        ArrayList<SecurityCamera> tCameras = new ArrayList<SecurityCamera>();

        for(int i = 0; i < cameras.size(); i++)
        {
            tCameras.add(cameras.get(i).copy());
        }

        return tCameras;
    }

    public ArrayList<Spawn> getSpawns()
    {
        return spawns;
    }

    public ArrayList<Item> getItems()
    {
        return items;
    }

    public ArrayList<Item> getItemCopies()
    {
        return itemCopies;
    }

    public void saveItems()
    {
        itemCopies.clear();
        for(int i = 0; i < items.size(); i++)
        {
            itemCopies.add(items.get(i).copy());
        }
    }

    public void respawnItems()
    {
        items.clear();
        for(int i = 0; i < itemCopies.size(); i++)
        {
            items.add(itemCopies.get(i).copy());
        }
    }

    public void respawnBoss()
    {
        if (boss != null)
        {
            boss = bossCopy.copyBoss();
        }
    }

    public ArrayList<Objective> getObjectives()
    {
        return objectives;
    }

    public byte[][] getGrassWaterDoors()
    {
        return grassWaterDoors;
    }

    public void setDialog(Dialog d)
    {
        dlg = d;
    }

    public Dialog getDialog()
    {
        return dlg;
    }

    public short[][] getTileMap()
    {
        return levMap;
    }

    public void handleBossEvent(int bossX, int bossY, Direction bossDirection, GuardType bossType)
    {
        for(int i = 0; i < guards.size(); i++)
        {
            NPC guard = guards.get(i);
            if (guard.getType() == bossType)
            {
                Waypoint target = guard.getPath().getStartingWaypoint().copy();
                Path p = new Path();
                ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
                waypoints.add(new Waypoint(bossX, bossY, bossDirection, WaypointBehavior.EXIT));
                waypoints.add(target);
                p.addWaypoints(waypoints);
                guard.setPath(p);
            }
        }
    }
}
