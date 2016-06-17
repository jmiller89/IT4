/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;

public class LevelLoader
{
    private Game game;

    //Tags
    private static final String ROOM_START = "[room]";
    private static final String ROOM_END = "[/room]";
    private static final String HEADER_START = "[header]";
    private static final String HEADER_END = "[/header]";
    private static final String TM_START = "[tileMap]";
    private static final String TM_END = "[/tileMap]";
    private static final String ITEMS_START = "[items]";
    private static final String ITEMS_END = "[/items]";
    private static final String NPCS_START = "[npcs]";
    private static final String NPCS_END = "[/npcs]";
    private static final String PATH_START = "[path]";
    private static final String PATH_END = "[/path]";
    private static final String WARPS_START = "[warps]";
    private static final String WARPS_END = "[/warps]";
    private static final String DLG_START = "[dialog]";
    private static final String DLG_END = "[/dialog]";
    private static final String BOSS_START = "[boss]";
    private static final String BOSS_END = "[/boss]";
    private static final String DOORS_START = "[doors]";
    private static final String DOORS_END = "[/doors]";
    private static final String CAMERAS_START = "[cameras]";
    private static final String CAMERAS_END = "[/cameras]";
    private static final String SPAWNS_START = "[spawns]";
    private static final String SPAWNS_END = "[/spawns]";
    private static final String OBJECTIVES_START = "[objectives]";
    private static final String OBJECTIVES_END = "[/objectives]";
    private static final String PLAYER_PATH_START = "[playerpath]";
    private static final String PLAYER_PATH_END = "[/playerpath]";

    //subtags
    private static final String PRE_START = "[pre]";
    private static final String PRE_END = "[/pre]";
    private static final String POST_START = "[post]";
    private static final String POST_END = "[/post]";

    private static final String CAMERA_START = "[camera]";
    private static final String CAMERA_END = "[/camera]";

    private static final String ARMORED = "_ARMORED";
    private static final String STRIP_ITEMS = "STRIP_ITEMS";
    private static final String TRANSITION = "TRANSITION";


    private int sizeX = 20;

    private int sizeY = 15;

    //Ctor
    public LevelLoader(Game g)
    {
        game = g;
    }

    public Level getLevel(String filePath, int id, boolean internal)
    {
        int sections = 1;
        Level level = null;
        Scanner scanner = null;
        String line = "";
        short[][] tm = null;
        ArrayList<LevelMap> levmaps = new ArrayList<LevelMap>();
        boolean readStartHeader = false;
        int currentLevelMap = 0;
        ArrayList<NPC> npcs = new ArrayList<NPC>();
        ArrayList<Item> items = new ArrayList<Item>();
        ArrayList<Warp> warps = new ArrayList<Warp>();
        ArrayList<Door> doors = new ArrayList<Door>();
        ArrayList<SecurityCamera> cameras = new ArrayList<SecurityCamera>();
        ArrayList<Spawn> spawns = new ArrayList<Spawn>();
        Dialog dialog = new Dialog(false);
        Boss boss = null;
        Path playerPath = null;

        boolean gas = false;
        boolean dark = false;
        boolean semidark = false;
        boolean midnight = false;
        boolean jam = false;
        boolean rain = false;
        boolean snow = false;
        boolean forceprone = false;

        int song = 0;

        short defaultGroundCover = 0;

        boolean alertStage = false;
        boolean error = false;

        try
        {
            if (internal)
            {
                URL url = this.getClass().getClassLoader().getResource(filePath);

                try
                {
                    scanner = new Scanner(url.openStream());
                }
                catch(Exception e)
                {

                }
            }
            else
            {
                try
                {
                    scanner = new Scanner(new BufferedReader(new FileReader(filePath)));
                }
                catch(Exception e)
                {

                }
            }

            System.out.println("Opened scanner");

            while(scanner.hasNextLine())
            {
                line = scanner.nextLine();

                //System.out.println(line);

                if (line.startsWith(ROOM_START))
                {
                    npcs = new ArrayList<NPC>();
                    items = new ArrayList<Item>();
                    warps = new ArrayList<Warp>();
                    doors = new ArrayList<Door>();
                    cameras = new ArrayList<SecurityCamera>();
                    spawns = new ArrayList<Spawn>();
                    dialog = new Dialog(false);
                    boss = null;
                    playerPath = null;
                    alertStage = false;
                    gas = false;
                    dark = false;
                    semidark = false;
                    midnight = false;
                    jam = false;
                    rain = false;
                    snow = false;
                    forceprone = false;
                }
                else if (line.startsWith(ROOM_END))
                {
                    levmaps.add(new LevelMap(tm, defaultGroundCover));
                    levmaps.get(currentLevelMap).initializeNPCs(npcs);
                    levmaps.get(currentLevelMap).initializeItems(items);
                    levmaps.get(currentLevelMap).initializeWarps(warps);
                    levmaps.get(currentLevelMap).initializeDoors(doors);
                    levmaps.get(currentLevelMap).initializeCameras(cameras);
                    levmaps.get(currentLevelMap).initializeSpawns(spawns);
                    levmaps.get(currentLevelMap).setDialog(dialog);
                    levmaps.get(currentLevelMap).initializeBoss(boss);
                    levmaps.get(currentLevelMap).playerPath = playerPath;
                    
                    if (alertStage)
                    {
                        levmaps.get(currentLevelMap).makeAlertStage();
                    }
                    
                    levmaps.get(currentLevelMap).songIndex = song;
                    //levmaps.get(currentLevelMap)

                    levmaps.get(currentLevelMap).dark = dark;
                    levmaps.get(currentLevelMap).semidark = semidark;
                    levmaps.get(currentLevelMap).gas = gas;
                    levmaps.get(currentLevelMap).midnight = midnight;
                    levmaps.get(currentLevelMap).jam = jam;

                    levmaps.get(currentLevelMap).rain = rain;
                    levmaps.get(currentLevelMap).snow = snow;
                    levmaps.get(currentLevelMap).forceprone = forceprone;

                    level.setLevelMap(levmaps.get(currentLevelMap), currentLevelMap);
                    
                    currentLevelMap++;

                }
                else if (line.startsWith(HEADER_START))
                {
                    System.out.println("Reading a header");

                    if (!readStartHeader)
                    {
                        line = scanner.nextLine();
                        
                        int startX = Integer.parseInt(line.substring(7));
                        line = scanner.nextLine();
                        
                        int startY = Integer.parseInt(line.substring(7));
                        line = scanner.nextLine();

                        sections = Integer.parseInt(line.substring(9));
                        line = scanner.nextLine();

                        boolean strip = false;
                        boolean transition = false;
                        while (!line.startsWith(this.HEADER_END))
                        {
                            if (line.startsWith(STRIP_ITEMS))
                            {
                                strip = true;
                            }
                            else if (line.startsWith(TRANSITION))
                            {
                                transition = true;
                            }

                            line = scanner.nextLine();
                        }

                        level = new Level(sections, id, game);
                        level.setStartX(startX);
                        level.setStartY(startY);

                        level.stripItems = strip;

                        System.out.println("Level Created " + sections + " " + id + " " + startX + " " + startY);

                        readStartHeader = true;
                    }
                    else
                    {
                        line = scanner.nextLine();
                        
                        sizeX = Integer.parseInt(line.substring(6));

                        line = scanner.nextLine();
                        
                        sizeY = Integer.parseInt(line.substring(6));
                        line = scanner.nextLine();
                        
                        defaultGroundCover = Short.parseShort(line.substring(4));
                        line = scanner.nextLine();
                        
                        alertStage = Boolean.parseBoolean(line.substring(6));
                        line = scanner.nextLine();

                        if (line.startsWith("song"))
                        {
                            song = Integer.parseInt(line.substring(5));
                            line = scanner.nextLine();
                        }

                        //Weather effects (dark, semidark, gas, midnight, jam)
                        if (!line.startsWith(HEADER_END))
                        {
                            String[] weatherLines = line.split("\\|");
                            String wline = weatherLines[0];

                            if (wline.startsWith("gas"))
                            {
                                gas = true;
                            }
                            if (wline.startsWith("dark"))
                            {
                                dark = true;
                            }
                            if (wline.startsWith("semidark"))
                            {
                                semidark = true;
                            }
                            if ((wline.startsWith("haze")) || (wline.startsWith("midnight")))
                            {
                                midnight = true;
                            }
                            if (wline.indexOf("jam") >= 0)
                            {
                                jam = true;
                            }

                            //Precipitation (rain and snow)
                            if (weatherLines.length > 1)
                            {
                                String precip = weatherLines[1];
                                if (precip.startsWith("rain"))
                                {
                                    rain = true;
                                    snow = false;
                                }
                                else if (precip.startsWith("snow"))
                                {
                                    snow = true;
                                    rain = false;
                                }
                                else
                                {
                                    rain = false;
                                    snow = false;
                                }
                            }

                            //Check if forceprone
                            if (weatherLines.length > 2)
                            {
                                String forceProneStr = weatherLines[2];
                                if (forceProneStr.startsWith("forceprone"))
                                {
                                    forceprone = true;
                                }
                            }

                        }
                        
                        //dialogStage = Boolean.parseBoolean(line.substring(7));

                    }
                }
                else if (line.startsWith(HEADER_END))
                {
                    System.out.println("Header End");
                }
                else if (line.startsWith(TM_START))
                {
                    System.out.println("Reading Tilemap");
                    tm = new short[sizeY][sizeX];

                    //Load the tiles
                    int x = 0;
                    int y = 0;
                    
                    while (scanner.hasNextInt())
                    {
                        //System.out.println("(" + x + ", " + y + ")");

                        if (y < sizeY)
                        {
                            tm[y][x] = (short)(scanner.nextInt());
                        }
                        
                        x++;

                        if (x >= sizeX)
                        {
                            x = 0;
                            y++;
                        }
                    }
                }
                else if (line.startsWith(TM_END))
                {

                }
                else if (line.startsWith(ITEMS_START))
                {
                    while(!line.startsWith(ITEMS_END))
                    {
                        line = scanner.nextLine();

                        if (!(line.startsWith("#") || line.startsWith(ITEMS_END)))
                        {
                            items.add(parseItem(line));
                        }
                    }
                }
                else if (line.startsWith(ITEMS_END))
                {

                }
                else if (line.startsWith(DOORS_START))
                {
                    while(!line.startsWith(DOORS_END))
                    {
                        line = scanner.nextLine();

                        if (!(line.startsWith("#") || line.startsWith(DOORS_END)))
                        {
                            doors.add(parseDoor(line));
                        }
                    }
                }
                else if (line.startsWith(DOORS_END))
                {

                }
                else if (line.startsWith(NPCS_START))
                {
                    while(!line.startsWith(NPCS_END))
                    {
                        line = scanner.nextLine();

                        if (!(line.startsWith("#") || line.startsWith(NPCS_END)))
                        {
                            npcs.add(parseNPC(line, scanner));
                        }
                    }
                }
                else if (line.startsWith(NPCS_END))
                {

                }
                else if (line.startsWith(PATH_START))
                {

                }
                else if (line.startsWith(PATH_END))
                {

                }
                else if (line.startsWith(PLAYER_PATH_START))
                {
                    System.out.println(line);
                    playerPath = parsePlayerPath(scanner);                    
                }
                else if (line.startsWith(PLAYER_PATH_END))
                {

                }
                else if (line.startsWith(WARPS_START))
                {
                    while(!line.startsWith(WARPS_END))
                    {
                        line = scanner.nextLine();

                        if (!(line.startsWith("#") || line.startsWith(WARPS_END)))
                        {
                            warps.add(parseWarp(line));
                        }
                    }
                }
                else if (line.startsWith(WARPS_END))
                {

                }
                else if (line.startsWith(DLG_START))
                {
                    ArrayList<String> dlgLines = new ArrayList<String>();
                    while(!line.startsWith(DLG_END))
                    {
                        line = scanner.nextLine();

                        if (!(line.startsWith("#") || line.startsWith(DLG_END)))
                        {
                            //dialog.add(line);
                            dlgLines.add(line);
                        }
                    }
                    dialog.addStrings(dlgLines);
                }
                else if (line.startsWith(DLG_END))
                {

                }
                else if (line.startsWith(BOSS_START))
                {
                    line = scanner.nextLine();
                    boss = parseBoss(line);

                    Dialog pre = new Dialog(false);
                    Dialog post = new Dialog(false);

                    line = scanner.nextLine();
                    boss.name = line;
                    if (boss.name.endsWith("_"))
                    {
                        boss.dispName = boss.name.substring(0, boss.name.length() - 1);
                    }
                    else
                    {
                        boss.dispName = boss.name;
                    }

                    line = scanner.nextLine();

                    ArrayList<String> dlgLines = new ArrayList<String>();
                    if (line.startsWith(PRE_START))
                    {
                        line = scanner.nextLine();

                        while (!line.startsWith(PRE_END))
                        {
                            //pre.add(line);
                            dlgLines.add(line);
                            line = scanner.nextLine();
                        }
                    }
                    pre.addStrings(dlgLines);

                    line = scanner.nextLine();

                    dlgLines.clear();
                    if (line.startsWith(POST_START))
                    {
                        line = scanner.nextLine();

                        while (!line.startsWith(POST_END))
                        {
                            //post.add(line);
                            dlgLines.add(line);
                            line = scanner.nextLine();
                        }
                    }
                    post.addStrings(dlgLines);

                    boss.setPreDialog(pre);
                    boss.setPostDialog(post);
                }
                else if (line.startsWith(BOSS_END))
                {

                }
                else if (line.startsWith(CAMERAS_START))
                {
                    while(!line.startsWith(CAMERAS_END))
                    {
                        line = scanner.nextLine();
                        System.out.println(line);

                        if (!(line.startsWith("#") || line.startsWith(CAMERAS_END)))
                        {
                            line = scanner.nextLine();
                            Direction cd = Direction.DOWN;
                            int x = 0;
                            int y = 0;
                            Waypoint start;
                            Waypoint end;
                            int sX = 0;
                            int sY = 0;
                            int eX = 0;
                            int eY = 0;
                            boolean fixed = false;
                            short cid = 197;
                            SecurityCameraType camType = SecurityCameraType.NORMAL;

                            //line = scanner.nextLine();

                            if (line.equals(Direction.UP.toString()))
                            {
                                cd = Direction.UP;
                                cid = 197;
                            }
                            if (line.equals(Direction.DOWN.toString()))
                            {
                                cd = Direction.DOWN;
                                cid = 198;
                            }
                            if (line.equals(Direction.LEFT.toString()))
                            {
                                cd = Direction.LEFT;
                                cid = 199;
                            }
                            if (line.equals(Direction.RIGHT.toString()))
                            {
                                cd = Direction.RIGHT;
                                cid = 200;
                            }

                            line = scanner.nextLine();
                            x = Integer.parseInt(line);
                            line = scanner.nextLine();
                            y = Integer.parseInt(line);

                            line = scanner.nextLine();
                            int index = line.indexOf(' ');
                            sX = Integer.parseInt(line.substring(0, index));
                            sY = Integer.parseInt(line.substring(index + 1));

                            line = scanner.nextLine();
                            index = line.indexOf(' ');
                            eX = Integer.parseInt(line.substring(0, index));
                            eY = Integer.parseInt(line.substring(index + 1));

                            line = scanner.nextLine();
                            fixed = Boolean.parseBoolean(line);

                            start = new Waypoint(sX, sY, cd, WaypointBehavior.CONTINUE);
                            end = new Waypoint(eX, eY, cd, WaypointBehavior.CONTINUE);

                            line = scanner.nextLine();

                            if (line.startsWith(CAMERA_END) == false)
                            {
                                camType = SecurityCameraType.valueOf(line);
                                line = scanner.nextLine();

                                if (camType == SecurityCameraType.GUN)
                                {
                                    cid = 483;
                                }
                                else if (camType == SecurityCameraType.DRONE)
                                {
                                    cid = 487;
                                }
                                else if (camType == SecurityCameraType.GUN_DRONE)
                                {
                                    cid = 491;
                                }

                                if (cd == Direction.DOWN)
                                {
                                    cid += 1;
                                }
                                else if (cd == Direction.LEFT)
                                {
                                    cid += 2;
                                }
                                else if (cd == Direction.RIGHT)
                                {
                                    cid += 3;
                                }

                            }

                            cameras.add(new SecurityCamera(cid, x*40, y*40, cd, start, end, fixed, camType));
                        }
                    }
                }
                else if (line.startsWith(CAMERAS_END))
                {

                }
                else if (line.startsWith(SPAWNS_START))
                {
                    while(!line.startsWith(SPAWNS_END))
                    {
                        line = scanner.nextLine();

                        if (!(line.startsWith("#") || line.startsWith(SPAWNS_END)))
                        {
                            spawns.add(parseSpawn(line));
                        }
                    }
                }
                else if (line.startsWith(SPAWNS_END))
                {

                }
                else if (line.startsWith(OBJECTIVES_START))
                {
                    line = scanner.nextLine();
                    
                    while (!line.startsWith(OBJECTIVES_END))
                    {
                        Objective obj;
                        
                        System.out.println("parsing " + line);
                        int index = line.indexOf(" ");
                        int objectiveid = Integer.parseInt(line.substring(0, index));
                        String l2 = line.substring(index+1);
                        index = l2.indexOf(" ");
                        int mapindex = Integer.parseInt(l2.substring(0, index));
                        String l3 = l2.substring(index+1);
                        index = l3.indexOf(" ");
                        int objectiveX = Integer.parseInt(l3.substring(0, index));
                        int objectiveY = Integer.parseInt(l3.substring(index+1));
                        
                        line = scanner.nextLine();
                        String objectiveDescription = line;

                        obj = new Objective((short)objectiveid, objectiveX, objectiveY, mapindex, objectiveDescription);

                        line = scanner.nextLine();
                        line = scanner.nextLine();

                        Dialog d = new Dialog(false);

                        ArrayList<String> dlgLines = new ArrayList<String>();
                        while (!line.startsWith(this.DLG_END))
                        {
                            //d.add(line);
                            dlgLines.add(line);
                            line = scanner.nextLine();
                        }
                        d.addStrings(dlgLines);

                        obj.dialog = d;

                        level.addObjective(obj);

                        line = scanner.nextLine();
                        
                    }
                }
            }

        }
        catch(Exception e)
        {
            error = true;
            System.err.println("Error: Could not open " + filePath);
            System.exit(1);
        }

        if (!error)
        {
            scanner.close();
        }
        
        if (level != null)
        {
            level.initializeObjectives();
        }

        return level;
    }

    private Warp parseWarp(String w)
    {
        System.out.println(w);
        
        int index = w.indexOf(' ');
        int x = Integer.parseInt(w.substring(0, index));
        w = w.substring(index + 1);
        //System.out.println(w);

        index = w.indexOf(' ');
        int y = Integer.parseInt(w.substring(0, index));
        w = w.substring(index + 1);
        //System.out.println(w);

        index = w.indexOf(' ');
        boolean nextLv = Boolean.parseBoolean(w.substring(0, index));
        w = w.substring(index + 1);
        //System.out.println(w);

        index = w.indexOf(' ');
        int lvIndex = Integer.parseInt(w.substring(0, index));
        w = w.substring(index + 1);
        //System.out.println(w);

        index = w.indexOf(' ');
        int pX = Integer.parseInt(w.substring(0, index));
        w = w.substring(index + 1);
        //System.out.println(w);

        index = w.indexOf(' ');
        int pY = Integer.parseInt(w.substring(0, index));
        w = w.substring(index + 1);
        //System.out.println(w);
        
        int secLv = Integer.parseInt(w.substring(0));

        return new Warp((short)0, x*40, y*40, lvIndex, pX, pY, nextLv, secLv);
    }

    private Item parseItem(String it)
    {
        ItemType type = ItemType.MEDKIT;
        short id = 74;
        System.out.println(it);

        String[] splits = it.split(" ");
        String iType = splits[0];

        int x = Integer.parseInt(splits[1]);
        int y = Integer.parseInt(splits[2]);
        x = x * 40;
        y = y * 40;

        int rank = 1;
        boolean isWeapon = false;

        if (splits.length > 3)
        {
            rank = Integer.parseInt(splits[3]);
        }

        if (iType.equals(ItemType.PRIMARY_AMMO.toString()))
        {
            type = ItemType.PRIMARY_AMMO;
            id = 74;
        }
        if (iType.equals(ItemType.SECONDARY_AMMO.toString()))
        {
            type = ItemType.SECONDARY_AMMO;
            id = 74;
        }
        if (iType.equals(ItemType.PRIMARY_MAG.toString()))
        {
            type = ItemType.PRIMARY_MAG;
            id = 74;
        }
        if (iType.equals(ItemType.SECONDARY_MAG.toString()))
        {
            type = ItemType.SECONDARY_MAG;
            id = 74;
        }
        if (iType.equals(ItemType.ASSAULT_RIFLE.toString()))
        {
            type = ItemType.ASSAULT_RIFLE;
            id = 73;
            isWeapon = true;
        }
        if (iType.equals(ItemType.BODY_ARMOR.toString()))
        {
            type = ItemType.BODY_ARMOR;
            id = 207;
        }
        if (iType.equals(ItemType.BOOSTER_KIT.toString()))
        {
            type = ItemType.BOOSTER_KIT;
            id = 96;
        }
        if (iType.equals(ItemType.CARDKEY_1.toString()))
        {
            type = ItemType.CARDKEY_1;
            id = 75;
        }
        if (iType.equals(ItemType.CARDKEY_2.toString()))
        {
            type = ItemType.CARDKEY_2;
            id = 76;
        }
        if (iType.equals(ItemType.CARDKEY_3.toString()))
        {
            type = ItemType.CARDKEY_3;
            id = 77;
        }
        if (iType.equals(ItemType.CARDKEY_4.toString()))
        {
            type = ItemType.CARDKEY_4;
            id = 201;
        }
        if (iType.equals(ItemType.CARDKEY_5.toString()))
        {
            type = ItemType.CARDKEY_5;
            id = 202;
        }

        if (iType.equals(ItemType.CARDKEY_6.toString()))
        {
            type = ItemType.CARDKEY_6;
            id = 75;
        }
        if (iType.equals(ItemType.CARDKEY_7.toString()))
        {
            type = ItemType.CARDKEY_7;
            id = 76;
        }
        if (iType.equals(ItemType.CARDKEY_8.toString()))
        {
            type = ItemType.CARDKEY_8;
            id = 77;
        }
        if (iType.equals(ItemType.CARDKEY_9.toString()))
        {
            type = ItemType.CARDKEY_9;
            id = 201;
        }
        if (iType.equals(ItemType.CARDKEY_10.toString()))
        {
            type = ItemType.CARDKEY_10;
            id = 202;
        }

        if (iType.equals(ItemType.GASMASK.toString()))
        {
            type = ItemType.GASMASK;
            id = 204;
        }
        if (iType.equals(ItemType.GRENADE.toString()))
        {
            type = ItemType.GRENADE;
            id = 191;
            isWeapon = true;
        }
        if (iType.equals(ItemType.C4.toString()))
        {
            type = ItemType.C4;
            id = 368;
            isWeapon = true;
        }

        if (iType.equals(ItemType.C4GROUP.toString()))
        {
            type = ItemType.C4GROUP;
            id = 529;
        }

        if (iType.equals(ItemType.MEDKIT.toString()))
        {
            type = ItemType.MEDKIT;
            id = 70;
        }
        if (iType.equals(ItemType.NVG.toString()))
        {
            type = ItemType.NVG;
            id = 203;
        }
        if (iType.equals(ItemType.PISTOL.toString()))
        {
            type = ItemType.PISTOL;
            id = 71;
            isWeapon = true;
        }
        if (iType.equals(ItemType.SHOTGUN.toString()))
        {
            type = ItemType.SHOTGUN;
            id = 206;
            isWeapon = true;
        }
        if (iType.equals(ItemType.PRIMARY_SILENCER.toString()))
        {
            type = ItemType.PRIMARY_SILENCER;
            id = 72;
        }
        if (iType.equals(ItemType.SECONDARY_SILENCER.toString()))
        {
            type = ItemType.SECONDARY_SILENCER;
            id = 72;
        }
        if (iType.equals(ItemType.SMG.toString()))
        {
            type = ItemType.SMG;
            id = 205;
            isWeapon = true;
        }
        if (iType.equals(ItemType.TRANQ_PISTOL.toString()))
        {
            type = ItemType.TRANQ_PISTOL;
            id = 250;
            isWeapon = true;
        }
        if (iType.equals("WRISTWATCH"))
        {
            type = ItemType.TRANQ_PISTOL;
            id = 250;
            isWeapon = true;
        }
        if (iType.equals("SMG_SILENCER"))
        {
            type = ItemType.PRIMARY_SILENCER;
            id = 72;
        }
        if (iType.equals("SILENCER"))
        {
            type = ItemType.SECONDARY_SILENCER;
            id = 72;
        }
        if (iType.equals(ItemType.LANDMINE.toString()))
        {
            type = ItemType.LANDMINE;
            id = 293;
        }
        if (iType.equals(ItemType.LASER_HORIZONTAL.toString()))
        {
            type = ItemType.LASER_HORIZONTAL;
            id = 294;
        }
        if (iType.equals(ItemType.LASER_VERTICAL.toString()))
        {
            type = ItemType.LASER_VERTICAL;
            id = 295;
        }
        if (iType.equals(ItemType.MINE_DETECTOR.toString()))
        {
            type = ItemType.MINE_DETECTOR;
            id = 296;
        }

        if (iType.equals("AR_AMMO"))
        {
            type = ItemType.PRIMARY_AMMO;
            id = 74;
        }
        
        if (iType.equals("PISTOL_AMMO"))
        {
            type = ItemType.SECONDARY_AMMO;
            id = 74;
        }

        if (iType.equals("SHOTGUN_AMMO"))
        {
            type = ItemType.PRIMARY_AMMO;
            id = 74;
        }
        if (iType.equals("SMG_AMMO"))
        {
            type = ItemType.PRIMARY_AMMO;
            id = 74;
        }
        if (iType.equals("WW_AMMO"))
        {
            type = ItemType.SECONDARY_AMMO;
            id = 74;
        }

        Item myItem;
        if (isWeapon)
        {
            myItem = Weapon.create(x, y, type, rank);
        }
        else
        {
            myItem = new Item(id, x, y, type, rank);
        }
        //id, x, y, type, rank
        return myItem;
    }

    private Door parseDoor(String d)
    {
        int id = 57;
        int x = 0;
        int y = 0;
        int secLv = 0;
        int objLev = 0;
        boolean dark = false;

        System.out.println(d);

        int index = d.indexOf(' ');
        String r0 = d.substring(0, index);
        
        x = Integer.parseInt(r0);

        id = 57;

        String r1 = d.substring(index+1);
        index = r1.indexOf(' ');
        String r2 = r1.substring(index+1);
        
        y = Integer.parseInt(r1.substring(0, index));

        index = r2.indexOf(' ');

        secLv = Integer.parseInt(r2.substring(0, index));
        String r3 = r2.substring(index+1);
        index = r3.indexOf(" ");
        objLev = Integer.parseInt(r3.substring(0, index));
        dark = Boolean.parseBoolean(r3.substring(index+1));

        x = x * 40;
        y = y * 40;

        if (secLv > 0)
        {
            id = 58;
        }
        else if (secLv < 0)
        {
            id = 378;
            return new CrawlDoor((short)id, x, y);
        }
        
        return new Door((short)id, x, y, secLv, objLev, dark);
    }

    private NPC parseNPC(String n, Scanner s)
    {
        NPC npc = null;
        GuardType NPCType = GuardType.LIGHT;

        boolean friendly = false;
        boolean armored = false;
        String[] splits = n.split(" ");
        if (splits.length > 1)
        {
            if (splits[1].startsWith("FRIEND"))
            {
                friendly = true;
            }
        }
        
        String[] ntsplits = splits[0].split("_ARMORED");
        if (ntsplits.length > 1)
        {
            armored = true;
        }

        String npcTypeString = ntsplits[0];

        try
        {
            NPCType = GuardType.valueOf(npcTypeString);
        }
        catch(Exception e)
        {
            NPCType = GuardType.LIGHT;
        }

        Path p = parsePath(s);

        npc = NPC.create(NPCType, 0, 0, Direction.DOWN, NPCStatus.PATROL, friendly, p);
        npc.bodyArmor = armored;

        return npc;
    }

    private Path parsePlayerPath(Scanner s)
    {
        Path p = new Path();
        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
        //String line = s.nextLine();
        String line = "";

        while(!line.startsWith(PLAYER_PATH_END))
        {
            line = s.nextLine();

            if (!(line.startsWith("#") || line.startsWith(PLAYER_PATH_END)))
            {
                waypoints.add(parseWaypoint(line));
            }
        }

        p.addWaypoints(waypoints);

        if (p.getAllWaypoints().size() < 1)
        {
            p = null;
        }

        System.out.println(line);
        return p;
    }

    private Path parsePath(Scanner s)
    {
        Path p = new Path();
        ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
        String line = s.nextLine();

        while(!line.startsWith(PATH_END))
        {
            line = s.nextLine();

            if (!(line.startsWith("#") || line.startsWith(PATH_END)))
            {
                waypoints.add(parseWaypoint(line));
            }
            
        }

        p.addWaypoints(waypoints);

        return p;
    }

    private Waypoint parseWaypoint(String w)
    {
        int x;
        int y;
        Direction d = Direction.DOWN;
        WaypointBehavior wb = WaypointBehavior.STOP;

        int index = w.indexOf(' ');
        x = Integer.parseInt(w.substring(0, index));
        String r1 = w.substring(index+1);
        index = r1.indexOf(' ');
        y = Integer.parseInt(r1.substring(0, index));
        String r2 = r1.substring(index+1);
        index = r2.indexOf(' ');
        String dir = r2.substring(0, index);
        String wayb = r2.substring(index+1);

        if (dir.equals(Direction.UP.toString()))
        {
            d = Direction.UP;
        }
        if (dir.equals(Direction.DOWN.toString()))
        {
            d = Direction.DOWN;
        }
        if (dir.equals(Direction.LEFT.toString()))
        {
            d = Direction.LEFT;
        }
        if (dir.equals(Direction.RIGHT.toString()))
        {
            d = Direction.RIGHT;
        }

        try
        {
            wb = WaypointBehavior.valueOf(wayb);
        }
        catch(Exception e)
        {
            wb = WaypointBehavior.STOP;
        }

        /*
        if (wayb.equals(WaypointBehavior.CONTINUE.toString()))
        {
            wb = WaypointBehavior.CONTINUE;
        }
        if (wayb.equals(WaypointBehavior.LONG_WAIT_AND_CONTINUE.toString()))
        {
            wb = WaypointBehavior.LONG_WAIT_AND_CONTINUE;
        }
        if (wayb.equals(WaypointBehavior.SLEEP.toString()))
        {
            wb = WaypointBehavior.SLEEP;
        }
        if (wayb.equals(WaypointBehavior.STOP.toString()))
        {
            wb = WaypointBehavior.STOP;
        }
        if (wayb.equals(WaypointBehavior.WAIT_AND_CONTINUE.toString()))
        {
            wb = WaypointBehavior.WAIT_AND_CONTINUE;
        }
        if (wayb.equals(WaypointBehavior.FOLLOW_PLAYER.toString()))
        {
            wb = WaypointBehavior.FOLLOW_PLAYER;
        }
        */

        System.out.println("Waypoint: " + x + ", " + y + ", " + d.toString() + ", " + wb.toString());

        return new Waypoint(x, y, d, wb);
    }

    private Spawn parseSpawn(String s)
    {
        int x = 1;
        int y = 1;
        GuardType gt = GuardType.LIGHT;
        int num = 0;
        boolean armored = false;

        System.out.println(s);

        int index = s.indexOf(' ');
        x = Integer.parseInt(s.substring(0, index));
        String s1 = s.substring(index+1);
        index = s1.indexOf(' ');
        y = Integer.parseInt(s1.substring(0, index));
        String s2 = s1.substring(index+1);
        index = s2.indexOf(' ');

        String gts = s2.substring(0, index);
        String ns = s2.substring(index+1);


        String[] gtssplits = gts.split("_ARMORED");
        if (gtssplits.length > 1)
        {
            armored = true;
        }

        String gtsString = gtssplits[0];

        try
        {
            gt = GuardType.valueOf(gtsString);
        }
        catch(Exception e)
        {
            gt = GuardType.LIGHT;
        }

        num = Integer.parseInt(ns);

        return new Spawn(x, y, gt, num, armored);
    }

    private Boss parseBoss(String s)
    {
        short id = 119;
        int x = 1;
        int y = 1;
        GuardType gt = GuardType.BOSS0;
        int health = 80;
        int damage = 15;
        byte speed = 15;
        int viewDist = 320;
        boolean bodyArmor = false;

        System.out.println(s);

        String[] splits = s.split(" ");

        try
        {
            gt = GuardType.valueOf(splits[0]);
        }
        catch(Exception e)
        {
            gt = GuardType.BOSS0;
        }

        //I was too lazy to map the guardTypes to ids, so we make a dummy NPC and extract the id out of it
        NPC dummy = NPC.create(gt, 0, 0, Direction.UP, NPCStatus.DEAD, false, null);
        id = dummy.getID();

        x = Integer.parseInt(splits[1]);
        y = Integer.parseInt(splits[2]);
        health = Integer.parseInt(splits[3]);
        damage = Integer.parseInt(splits[4]);
        speed = (byte)Integer.parseInt(splits[5]);
        viewDist = Integer.parseInt(splits[6]);
        bodyArmor = Boolean.parseBoolean(splits[7]);

        Warp event = null;

        if (splits.length >= 12)
        {
            //Parse event warp
            int wx = Integer.parseInt(splits[8]);
            int wy = Integer.parseInt(splits[9]);
            int wi = Integer.parseInt(splits[10]);
            boolean wn = Boolean.parseBoolean(splits[11]);

            event = new Warp((short)0, 0, 0, wi, wx, wy, wn, 0);
        }

        return new Boss(id, (x * 40), (y * 40), gt, health, damage, speed, viewDist, bodyArmor, event);
    }
}
