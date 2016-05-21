//This is a container class for all Levels

package IT4;

/**
 *
 * @author Jim (Admin)
 */

import java.awt.Point;
import java.net.URL;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.PriorityQueue;

public class LevelSet
{
    private Level[] levels;
    private int currentLevel;
    private Game game;
    private LevelLoader loader;
    private ArrayList<String> levelPaths;
    private boolean levelset = false;
    private boolean internal = true;

    //Maze vars
    private Random rand;
    private byte[][] maze;
    private static final int mazewidth = 20;
    private static final int mazeheight = 20;
    private int width;
    private int height;
    private ArrayList<Integer> moves;
    private ArrayList<Point> deadEnds;
    private byte[][] obstacleMatrix;
    private Vertex[][] vertices;
    public boolean isMaze = false;

    public LevelSet(Game g, String filePath, boolean lvSet, boolean internalLevels, boolean maze)
    {
        game = g;
        currentLevel = -1;
        loader = new LevelLoader(game);

        levelset = lvSet;
        internal = internalLevels;

        if (!maze)
        {
            initializeLevels(filePath, lvSet, internalLevels);
        }
        else
        {
            isMaze = true;
            initializeMaze();
        }
    }

    public Level getNextLevel()
    {
        currentLevel++;

        try
        {
            return levels[currentLevel];
        }
        catch(Exception e)
        {
            return null;
        }
        
    }

    public Level getLevel(int index)
    {
        Level lv = null;
        
        try
        {
            lv = levels[index];

            //2.03 bugfix: backwards level traversal isn't allowed anyway, so only set currentLevel if index is > than it.
            if (index > currentLevel)
            {
               currentLevel = index;
            }
        }
        catch(Exception e)
        {
            return null;
        }
        return lv;
    }

    public Level reloadLevel()
    {
        //reload the level
        if (!isMaze)
        {
            levels[currentLevel] = initLevel(levelPaths.get(currentLevel), currentLevel, internal);
        }
        else
        {
            initializeMaze();
        }

        try
        {
            return levels[currentLevel];
        }
        catch(Exception e)
        {
            return null;
        }
    }

    private void initializeMaze()
    {
        levels = new Level[1];
        
        rand = new Random(System.nanoTime());
        width = 2 * mazewidth + 1;
        height = 2 * mazeheight + 1;
        maze = new byte[height][width];

        for(int x = 0; x < height; x++)
        {
            for(int y = 0; y < width; y++)
            {
                maze[x][y] = 1;
            }
        }

        moves = new ArrayList<Integer>();
        deadEnds = new ArrayList<Point>();
        obstacleMatrix = new byte[height][width];
        vertices = new Vertex[height][width];
        //Generate vertices
        for(int x = 0; x < height; x++)
        {
            for(int y = 0; y < width; y++)
            {
                vertices[x][y] = new Vertex(x, y);
            }
        }

        generateMaze();
    }

    private void generateMaze()
    {
        int xPos = 1;
        int yPos = 1;

        maze[xPos][yPos] = 0;
        moves.add(yPos + (xPos * width));
        String possibleDirections = "";

        while(moves.size() > 0)
        {
            possibleDirections = "";
            if ((xPos+2 >= 0) && (xPos+2 <= height-1))
            {
                if (maze[xPos + 2][yPos] == 1)
                {
                    possibleDirections += "S";
                }
            }
            if ((xPos-2 >= 0) && (xPos-2 <= height-1))
            {
                if (maze[xPos - 2][yPos] == 1)
                {
                    possibleDirections += "N";
                }
            }
            if ((yPos-2 >= 0) && (yPos-2 <= width-1))
            {
                if (maze[xPos][yPos - 2] == 1)
                {
                    possibleDirections += "W";
                }
            }
            if ((yPos+2 >= 0) && (yPos+2 <= width-1))
            {
                if (maze[xPos][yPos + 2] == 1)
                {
                    possibleDirections += "E";
                }
            }

            if (!possibleDirections.equals(""))
            {
                int move = rand.nextInt(possibleDirections.length());
                switch(possibleDirections.charAt(move))
                {
                    case 'N':
                        maze[xPos - 2][yPos] = 0;
                        maze[xPos - 1][yPos] = 0;
                        xPos-=2;
                        break;

                    case 'S':
                        maze[xPos + 2][yPos] = 0;
                        maze[xPos + 1][yPos] = 0;
                        xPos+=2;
                        break;

                    case 'W':
                        maze[xPos][yPos - 2] = 0;
                        maze[xPos][yPos - 1] = 0;
                        yPos-=2;
                        break;

                    case 'E':
                        maze[xPos][yPos + 2] = 0;
                        maze[xPos][yPos + 1] = 0;
                        yPos+=2;
                        break;
                }
                moves.add((yPos+ (xPos*width)));
            }
            else
            {
                int back = moves.remove(moves.size() - 1);
                xPos = (int) Math.floor((back/width));
                yPos = back%width;
            }
        }
        
        generateObstacleMatrix();
        generateAdjacencies();
        computePaths(vertices[1][1]);
        findDeadEnds();
        populateMaze();

        formatMaze();
    }

    private void generateObstacleMatrix()
    {
        for(int x = 0; x < height; x++)
        {
            for(int y = 0; y < width; y++)
            {
                if (maze[x][y] == 0)
                {
                    obstacleMatrix[x][y] = 1;
                }
                else
                {
                    obstacleMatrix[x][y] = 0;
                }

            }
        }
    }

    private void generateAdjacencies()
    {
        for(int j = 0; j < obstacleMatrix.length; j++)
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
                    if (obstacleMatrix[i-1][j] == 1)
                    {
                        left = new Edge(vertices[i-1][j], obstacleMatrix[i-1][j]);
                        edges.add(left);
                    }
                }

                if (i+1 < obstacleMatrix[0].length)
                {
                    if (obstacleMatrix[i+1][j] == 1)
                    {
                        right = new Edge(vertices[i+1][j], obstacleMatrix[i+1][j]);
                        edges.add(right);
                    }
                }

                if (j-1 > -1)
                {
                    if (obstacleMatrix[i][j-1] == 1)
                    {
                        up = new Edge(vertices[i][j-1], obstacleMatrix[i][j-1]);
                        edges.add(up);
                    }
                }

                if (j+1 < obstacleMatrix.length)
                {
                    if (obstacleMatrix[i][j+1] == 1)
                    {
                        down = new Edge(vertices[i][j+1], obstacleMatrix[i][j+1]);
                        edges.add(down);
                    }
                }

                Edge[] adjs = new Edge[edges.size()];

                for(int z = 0; z < edges.size(); z++)
                {
                    adjs[z] = edges.get(z);
                }

                vertices[i][j].adjacencies = adjs;
            }
        }
    }

    private void computePaths(Vertex source)
    {
        source.minDistance = 0;
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
        vertexQueue.add(source);

        while(!vertexQueue.isEmpty())
        {
            Vertex u = vertexQueue.poll();

            for(Edge e : u.adjacencies)
            {
                Vertex v = e.target;
                int weight = e.weight;
                float distanceThroughU = u.minDistance + weight;
                if (distanceThroughU < v.minDistance)
                {
                    vertexQueue.remove(v);
                    v.minDistance = distanceThroughU;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    private ArrayList<Vertex> getShortestPathTo(Vertex target)
    {
        ArrayList<Vertex> path = new ArrayList<Vertex>();

        for(Vertex vertex = target; vertex != null; vertex = vertex.previous)
        {
            path.add(vertex);
        }

        //Collections.reverse(path);

        return path;
    }

    private void populateMaze()
    {
        ArrayList<ArrayList<Vertex>> dePaths = new ArrayList<ArrayList<Vertex>>();

        for(int i = 0; i < deadEnds.size(); i++)
        {
            dePaths.add(getShortestPathTo(vertices[deadEnds.get(i).x][deadEnds.get(i).y]));
        }

        int li = 0;
        //int nli = 0;
        int ld = 0;
        float md = 0.0f;
        
        for(int i = 0; i < dePaths.size(); i++)
        {
            if (dePaths.get(i).size() > dePaths.get(li).size())
            {
                //nli = li;
                li = i;
            }

            int x = dePaths.get(i).get(0).x;
            int y = dePaths.get(i).get(0).y;

            float dist = (float)((y)/(x));
            if (dist > md)
            {
                md = dist;
                ld = i;
            }

        }

        //maze[dePaths.get(nli).get(0).x][dePaths.get(nli).get(0).y] = 4;
        maze[dePaths.get(ld).get(0).x][dePaths.get(ld).get(0).y] = 4;
        maze[dePaths.get(li).get(0).x][dePaths.get(li).get(0).y] = 3;
        

    }

    private void findDeadEnds()
    {
        for(int x = 1; x < height - 1; x++)
        {
            for(int y = 1; y < width - 1; y++)
            {
                if (maze[x][y] == 0)
                {
                    int walls = 0;

                    if (maze[x-1][y] == 1)
                    {
                        walls++;
                    }
                    if (maze[x+1][y] == 1)
                    {
                        walls++;
                    }
                    if (maze[x][y-1] == 1)
                    {
                        walls++;
                    }
                    if (maze[x][y+1] == 1)
                    {
                        walls++;
                    }

                    if (walls == 3)
                    {
                        //Don't count the starting position
                        if ((x != 1) || (y != 1))
                        {
                            maze[x][y] = 2;
                            deadEnds.add(new Point(x, y));
                        }
                    }

                }
            }
        }

//        System.out.println("Dead Ends:\n" + deadEnds.size());
//        for(int i = 0; i < deadEnds.size(); i++)
//        {
//            System.out.println(deadEnds.get(i).toString());
//        }
    }

    private void formatMaze()
    {
        short[][] tileMap = new short[width][height];

        
        short[] ftIndices = new short[] {44, 55, 56, 78, 79, 80, 81, 82, 84, 97, 98, 118, 143, 144, 154, 256, 257};
        short[] wallIndices = new short[] {48, 51, 52, 53, 54, 107, 157, 160, 208, 209, 252, 253};
        short dgc = ftIndices[rand.nextInt(ftIndices.length)];
        short wall = wallIndices[rand.nextInt(wallIndices.length)];

        //Binary floor will only have binary walls
        if (dgc == 154)
        {
            wall = 155;
        }

        ArrayList<Warp> warps = new ArrayList<Warp>(1);
        ArrayList<Item> items = new ArrayList<Item>();
        ArrayList<Spawn> spawns = new ArrayList<Spawn>();
        ArrayList<SecurityCamera> cameras = new ArrayList<SecurityCamera>(2);

        Objective obj = new Objective((short)0, -1, -1, 0, "Escape the enemy base");

        cameras.add(new SecurityCamera((short)200, 0, 40, Direction.RIGHT, new Waypoint(0, 1, Direction.RIGHT, WaypointBehavior.CONTINUE), new Waypoint(0, 1, Direction.RIGHT, WaypointBehavior.CONTINUE), true, SecurityCameraType.NORMAL));
        cameras.add(new SecurityCamera((short)198, 40, 0, Direction.DOWN, new Waypoint(1, 0, Direction.DOWN, WaypointBehavior.CONTINUE), new Waypoint(1, 0, Direction.DOWN, WaypointBehavior.CONTINUE), true, SecurityCameraType.NORMAL));
        
        for(int x = 0; x < maze[0].length; x++)
        {
            for(int y = 0; y < maze.length; y++)
            {
                if (maze[x][y] == 0)
                {
                    tileMap[y][x] = dgc;
                }
                else if (maze[x][y] == 1)
                {
                    tileMap[y][x] = wall;
                }
                else if (maze[x][y] == 2)
                {
                    //This is a dead end. Item pick ups go here
                    int chance = rand.nextInt(2);
                    if (chance == 0)
                    {
                        items.add(generateItem(x, y));
                        tileMap[y][x] = dgc;
                    }
                    else
                    {
                        chance = rand.nextInt(5);
                        if (chance == 0)
                        {
                            chance = rand.nextInt(7);

                            GuardType gt = GuardType.LIGHT;

                            if (chance < 3)
                            {
                                gt = GuardType.LIGHT;
                            }
                            if (chance < 4)
                            {
                                gt = GuardType.MEDIUM;
                            }
                            if (chance == 5)
                            {
                                gt = GuardType.HEAVY;
                            }
                            if (chance == 6)
                            {
                                chance = rand.nextInt(6);
                                if (chance == 5)
                                {
                                    gt = GuardType.NAZI_HUNTER;
                                }
                                else if ((chance > 2) && (chance != 5))
                                {
                                    gt = GuardType.MUTANT1;
                                }
                                else if ((chance >= 0) && (chance < 3))
                                {
                                    gt = GuardType.MUTANT2;
                                }
                            }
                            
                            spawns.add(new Spawn(x, y, gt, 3, false));
                            tileMap[y][x] = 43;
                        }
                        else
                        {
                            tileMap[y][x] = dgc;
                        }
                    }
                    
                }
                else if (maze[x][y] == 3)
                {
                    tileMap[y][x] = 86;
                    
                    warps.add(new Warp((short)86, 40*x, 40*y, 0, 0, 0, true, 0));

                    GuardType gt = GuardType.LIGHT;

//                    int chance = rand.nextInt(6);
//
//                    if (chance < 3)
//                    {
//                        gt = GuardType.LIGHT;
//                    }
//                    if (chance < 4)
//                    {
//                        gt = GuardType.MEDIUM;
//                    }
//                    if (chance == 5)
//                    {
//                        gt = GuardType.HEAVY;
//                    }

                    spawns.add(new Spawn(x, y, gt, 11, true));
                }
                else if (maze[x][y] == 4)
                {
                    tileMap[y][x] = 43;

                    GuardType gt = GuardType.LIGHT;

//                    int chance = rand.nextInt(6);
//
//                    if (chance < 3)
//                    {
//                        gt = GuardType.LIGHT;
//                    }
//                    if (chance < 4)
//                    {
//                        gt = GuardType.MEDIUM;
//                    }
//                    if (chance == 5)
//                    {
//                        gt = GuardType.HEAVY;
//                    }

                    spawns.add(new Spawn(x, y, gt, 11, true));
                }
            }
        }

        LevelMap lm = new LevelMap(tileMap, dgc);
        Level lv = new Level(1, 0, game);

        lm.initializeWarps(warps);
        lm.initializeCameras(cameras);
        lm.initializeDoors(new ArrayList<Door>());
        lm.initializeItems(items);
        lm.initializeNPCs(new ArrayList<NPC>());
        lm.initializeObjectives(new ArrayList<Objective>());
        lm.initializeSpawns(spawns);
        
        lm.makeAlertStage();

        lv.setLevelMap(lm, 0);
        lv.setStartX(1);
        lv.setStartY(1);
        lv.addObjective(obj);
        lv.initializeObjectives();
        levels[0] = lv;
        
    }

    private Item generateItem(int x, int y)
    {
        Item it = null;
        int chance = rand.nextInt(100);
        int index = 0;
        x = x * 40;
        y = y * 40;

        if (chance < 25)
        {
            index = 0;
        }
        else if (chance < 40)
        {
            index = 1;
        }
        else if (chance < 50)
        {
            index = 2;
        }
        else if (chance < 60)
        {
            index = 3;
        }
        else if (chance < 65)
        {
            index = 4;
        }
        else if (chance < 80)
        {
            index = 5;
        }
        else if (chance < 85)
        {
            index = 6;
        }
        else if (chance < 90)
        {
            index = 9;
        }
        else if (chance < 95)
        {
            index = 7;
        }
        else if (chance < 100)
        {
            index = 8;
        }

        //public Item(int id, int x, int y, ItemType it
        switch(index)
        {
            case 0:
                it = Weapon.create(x, y, ItemType.PISTOL, 2);
                break;
            case 1:
                it = new Item((short)70, x, y, ItemType.MEDKIT, 1);
                break;
            case 2:
                it = Weapon.create(x, y, ItemType.SMG, 2);
                break;
            case 3:
                it = Weapon.create(x, y, ItemType.ASSAULT_RIFLE, 2);
                break;
            case 4:
                it = Weapon.create(x, y, ItemType.TRANQ_PISTOL, 3);
                break;
            case 5:
                it = Weapon.create(x, y, ItemType.SHOTGUN, 2);
                break;
            case 6:
                it = Weapon.create(x, y, ItemType.GRENADE, 2);
                break;
            case 7:
                it = new Item((short)207, x, y, ItemType.BODY_ARMOR, 1);
                break;
            case 8:
                it = new Item((short)96, x, y, ItemType.BOOSTER_KIT, 1);
                break;
            case 9:
                it = Weapon.create(x, y, ItemType.C4, 2);
                break;
        }
        

        return it;
    }
    
    //Set all of the initial Level Data here
    private void initializeLevels(String lvPath, boolean lSet, boolean internalLvs)
    {
        levelPaths = new ArrayList<String>();

        if (lSet)
        {
            Scanner s = null;
            String folderLoc = "";
            
            //Get lvPaths here, save them to an array
            if (internalLvs)
            {
                URL url = this.getClass().getClassLoader().getResource(lvPath);
                folderLoc = "LevelData/";
                try
                {
                    s = new Scanner(url.openStream());
                }
                catch(Exception e)
                {

                }
            }
            else
            {

                try
                {
                    File f = new File(lvPath);
                    folderLoc = f.getParent() + f.separator;
                    
                    s = new Scanner(new BufferedReader(new FileReader(lvPath)));
                    
                    //System.out.println("*** " + folderLoc);

                }
                catch(Exception e)
                {

                }
            }

            int i = 0;
            
            while (s.hasNextLine())
            {
                String line = s.nextLine();
                System.out.println(line);
                
                if (!line.startsWith("#"))
                {
                    levelPaths.add(folderLoc + line);
                    i++;
                }
            }
            
        }
        else
        {
            levelPaths.add(lvPath);
        }

        levels = new Level[levelPaths.size()];

        for (int i = 0; i < levels.length; i++)
        {
            levels[i] = initLevel(levelPaths.get(i), i, internalLvs);
        }


    }

    private Level initLevel(String lvPath, int index, boolean internal)
    {
        return loader.getLevel(lvPath, index, internal);
    }

    public int size()
    {
        return levels.length;
    }

    public int getNextStartX()
    {
        int nx = 0;

        if (currentLevel + 1 < levels.length)
        {
            nx = levels[currentLevel+1].getStartX() * 40;
        }

        return nx;
    }

    public int getNextStartY()
    {
        int ny = 0;

        if (currentLevel + 1 < levels.length)
        {
            ny = levels[currentLevel+1].getStartY() * 40;
        }

        return ny;
    }

}
