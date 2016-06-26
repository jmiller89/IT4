/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
import GFX.GLRenderThread;
import java.io.File;
import javax.swing.JFileChooser;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class GameFileManager
{
    public static boolean internal;
    public static boolean isLevelset;
    public static String levelPath = "";
    public static String filepath = "";
    public static short levelIndex = 0;
    public static short mapIndex = 0;
    public static boolean isMaze;

    public static short playerHealth;
    public static int maxHealth;
    public static short objectivesCompleted;
    public static short playerTileX;
    public static short playerTileY;
    public static Stance playerStance;
    public static byte playerOxygen;

    public static String[] weaponList = {"", "", ""};
    //public static short[] ammoList = {1, 0, 0, 0, 0, 0, 0, 0};
    public static byte[] itemList = {0, 0, 0, 0, 0, 0};
    public static byte[] bosses = null;
    public static ArrayList<ArrayList<Item>> items = null;
    public static ArrayList<Short> dialogsCompleted = null;
    public static ArrayList<Objective> objectives = null;

    public static long playTime = 0;

    private static final String OBJECTIVES_START = "[objectives]";
    private static final String OBJECTIVES_END = "[/objectives]";
    private static final String DIALOGS_START = "[dialogs]";
    private static final String DIALOGS_END = "[/dialogs]";
    private static final String ITEMS_START = "[items]";
    private static final String ITEMS_END = "[/items]";
    private static final String ALL_ITEMS_START = "[allitems]";
    private static final String ALL_ITEMS_END = "[/allitems]";
    private static final String PLAYTIME_START = "[playtime]";
    private static final String PLAYTIME_END = "[/playtime]";

    public static void saveGame(Game game, Level level, Player player, GLRenderThread gf)
    {
        //Mazes and tutorial cannot be saved
        if ((!isMaze))
        {            
            if (filepath.equalsIgnoreCase(""))
            {
                game.paused = true;
                saveGameAs(game, level, player, gf);
                game.paused = false;
            }
            else
            {
                save(game, level, player);
            }
        }
    }

    private static void saveGameAs(Game game, Level level, Player player, GLRenderThread gf)
    {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new ITSVFileFilter());
        int retval = fc.showDialog(null, "Save Game");

        if (retval == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();

            String fname = file.getPath();

            if (file.getName().toLowerCase().endsWith(".it4save"))
            {
                fname = file.getPath();
            }
            else
            {
                fname += ".it4save";
            }

            filepath = fname;
            System.out.println("SAVE FILE NAME: " + fname);
            save(game, level, player);

        }
    }

    private static void save(Game game, Level level, Player player)
    {
        game.setHUDMessage("Saving game...", HUDMessageType.INFO);
        levelIndex = (short)level.getID();
        mapIndex = (short)game.getRoomIndex();
        playerHealth = (short)player.getCurrentHealth();
        maxHealth = player.maxHealth;
        objectivesCompleted = (short)player.objectives;

        for(int i = 1; i < weaponList.length; i++)
        {
            weaponList[i] = "";
        }

        if (player.stuff.primary != null)
        {
            weaponList[0] = player.stuff.primary.serialize();
        }
        if (player.stuff.sidearm != null)
        {
            weaponList[1] = player.stuff.sidearm.serialize();
        }
        if (player.stuff.explosive != null)
        {
            weaponList[2] = player.stuff.explosive.serialize();
        }

        for(int i = 0; i < itemList.length; i++)
        {
            itemList[i] = 0;
        }

        itemList[0] = (byte)player.getNumHealthKits();
        itemList[1] = (byte)player.getCardKey();

        if (player.hasGasMask())
        itemList[2] = 1;

        if (player.hasNVG())
        itemList[3] = 1;

        if (player.hasBodyArmor())
        itemList[4] = 1;

        if (player.hasMineDetector())
        itemList[5] = 1;

        //ammoList = player.getAmmoList();

        bosses = new byte[level.getNumLevels()];
        items = new ArrayList<ArrayList<Item>>();

        for(int i = 0; i < level.getNumLevels(); i++)
        {
            if (level.getLevelMap(i).getBoss() != null)
            {
                if (level.getLevelMap(i).getBoss().getCurrentHealth() > 0)
                {
                    bosses[i] = 1;
                }
                else
                {
                    bosses[i] = 0;
                }
            }
            else
            {
                bosses[i] = 0;
            }
        }

        for(int i = 0; i < level.getNumLevels(); i++)
        {
            items.add(level.getLevelMap(i).getItemCopies());
        }

        dialogsCompleted = new ArrayList<Short>();

        for(short i = 0; i < level.dialogStore.size(); i++)
        {
            //if ((level.dialogStore.get(i).hasNext() == false) && (level.dialogStore.get(i).isValid()))
            if (!(level.dialogStore.get(i).isValid()))
            {                
                dialogsCompleted.add(i);
            }
        }

        playerTileX = (short)player.getTileX();
        playerTileY = (short)player.getTileY();
        playerStance = player.getStance();
        playerOxygen = (byte)player.getOxygen();
        objectives = game.getAllObjectiveCopies();

        long currTime = System.currentTimeMillis();
        playTime += (currTime - game.startDate);
        game.startDate = currTime;

        writeToFile(game);
    }

    private static void writeToFile(Game game)
    {
        try
        {
            FileWriter fstream = new FileWriter(filepath);
            BufferedWriter out = new BufferedWriter(fstream);

            out.write(Boolean.toString(internal));
            out.newLine();
            out.write(Boolean.toString(isLevelset));
            out.newLine();
            out.write(levelPath);
            out.newLine();
            out.write("" + levelIndex);
            out.newLine();
            out.write("" + mapIndex);
            out.newLine();
            out.write("" + playerHealth);
            out.newLine();
            out.write("" + maxHealth);
            out.newLine();
            out.write("" + objectivesCompleted);
            out.newLine();
            out.write("" + playerTileX);
            out.newLine();
            out.write("" + playerTileY);
            out.newLine();
            out.write(playerStance.toString());
            out.newLine();
            out.write("" + playerOxygen);
            out.newLine();

            for(int i = 0; i < weaponList.length; i++)
            {
                out.write(weaponList[i] + " ");
            }
            out.newLine();

            /*
            for(int i = 0; i < ammoList.length; i++)
            {
                out.write(ammoList[i] + " ");
            }
            out.newLine();
             * 
             */

            for(int i = 0; i < itemList.length; i++)
            {
                out.write(itemList[i] + " ");
            }
            out.newLine();
            
            for(int i = 0; i < bosses.length; i++)
            {
                out.write(bosses[i] + " ");
            }
            out.newLine();

            out.write("[objectives]");
            out.newLine();
            for(int i = 0; i < objectives.size(); i++)
            {
                if ((objectives.get(i).getX() >= 0) && (objectives.get(i).getY() >= 0))
                {
                    out.write(objectives.get(i).name);
                    out.newLine();
                    out.write("" + objectives.get(i).mapIndex);
                    out.newLine();
                }
            }
            out.write("[/objectives]");
            out.newLine();

            out.write("[dialogs]");
            out.newLine();
            for(int i = 0; i < dialogsCompleted.size(); i++)
            {
                out.write(dialogsCompleted.get(i) + " ");
                out.newLine();
            }
            out.write("[/dialogs]");
            out.newLine();

            out.write("[allitems]");
            out.newLine();
            for(int i = 0; i < items.size(); i++)
            {
                out.write("[items]");
                out.newLine();
                for(int j = 0; j < items.get(i).size(); j++)
                {
                    out.write(items.get(i).get(j).getType().toString() + " " + items.get(i).get(j).getTileX() + " " + items.get(i).get(j).getTileY() + " " + items.get(i).get(j).rank);
                    out.newLine();
                }
                out.write("[/items]");
                out.newLine();
            }
            out.write("[/allitems]");
            out.newLine();

            out.write("[playtime]");
            out.newLine();
            out.write("" + playTime);
            out.newLine();
            out.write("[/playtime]");
            out.newLine();

            out.close();
            fstream.close();
            game.setHUDMessage("Game Saved", HUDMessageType.INFO);
        }
        catch(Exception e)
        {
            System.err.println("An error has occured when trying to save the game.");
        }
        
    }

    public static void load()
    {
        Scanner scanner = null;
        String line = "";

        try
        {
            scanner = new Scanner(new BufferedReader(new FileReader(filepath)));
        }
        catch(Exception e)
        {

        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            internal = Boolean.parseBoolean(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            isLevelset = Boolean.parseBoolean(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            levelPath = line;
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            levelIndex = Short.parseShort(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            mapIndex = Short.parseShort(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            playerHealth = Short.parseShort(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            maxHealth = Integer.parseInt(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            objectivesCompleted = Short.parseShort(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            playerTileX = Short.parseShort(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            playerTileY = Short.parseShort(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            if (line.startsWith(Stance.PRONE.toString()))
            {
                playerStance = Stance.PRONE;
            }
            else
            {
                playerStance = Stance.UPRIGHT;
            }
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            playerOxygen = (byte)Integer.parseInt(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            parseWeaponList(line);
        }

        /*
        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            parseAmmoList(line);
        }
         * 
         */

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            parseItemList(line);
        }

        if (scanner.hasNextLine())
        {
            line = scanner.nextLine();
            parseBossList(line);
        }


        while(scanner.hasNextLine())
        {
            line = scanner.nextLine();
            if (line.startsWith(GameFileManager.OBJECTIVES_START))
            {
                objectives = new ArrayList<Objective>();
                line = scanner.nextLine();
                while (!line.startsWith(GameFileManager.OBJECTIVES_END))
                {
                    String objName;
                    int map_index;
                    objName = line;
                    line = scanner.nextLine();
                    map_index = Integer.parseInt(line);
                    line = scanner.nextLine();

                    Objective o = new Objective((short)0, 0, 0, map_index, objName);

                    GameFileManager.objectives.add(o);
                }
            }
            else if (line.startsWith(GameFileManager.DIALOGS_START))
            {
                dialogsCompleted = new ArrayList<Short>();
                line = scanner.nextLine();
                while (!line.startsWith(GameFileManager.DIALOGS_END))
                {
                    line = line.trim();
                    dialogsCompleted.add(Short.parseShort(line));
                    line = scanner.nextLine();
                }
            }
            else if (line.startsWith(GameFileManager.ALL_ITEMS_START))
            {
                int zindex = 0;
                items = new ArrayList<ArrayList<Item>>();
                
                while (!line.startsWith(GameFileManager.ALL_ITEMS_END))
                {
                    items.add(new ArrayList<Item>());
                    line = scanner.nextLine();

                    if (line.startsWith(GameFileManager.ITEMS_START))
                    {
                        line = scanner.nextLine();
                        while (!line.startsWith(GameFileManager.ITEMS_END))
                        {
                            ItemType type = ItemType.MEDKIT;
                            short id = 74;

                            String[] splits = line.split(" ");

                            int x, y, rank=1;
                            String iType = splits[0];

                            x = Integer.parseInt(splits[1]);
                            y = Integer.parseInt(splits[2]);

                            if (splits.length > 3)
                            {
                                rank = Integer.parseInt(splits[3]);
                            }

                            x = x * 40;
                            y = y * 40;

                            boolean isWeapon = false;

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
                            if (iType.equals(ItemType.C4GROUP.toString()))
                            {
                                type = ItemType.C4GROUP;
                                id = 529;
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

                            items.get(zindex).add(myItem);
                            line = scanner.nextLine();
                        }
                        zindex++;
                    }
                }
            }
            else if (line.startsWith(GameFileManager.PLAYTIME_START))
            {
                line = scanner.nextLine();
                playTime = Long.parseLong(line.trim());
                line = scanner.nextLine();
            }
        }

        scanner.close();
        System.out.println("Level loading...");
    }

    private static void parseWeaponList(String s)
    {
        String[] splits = s.split(" ");
        for(int i = 0; i < weaponList.length; i++)
        {
            try
            {
                weaponList[i] = splits[i];
            }
            catch(Exception e)
            {
                System.out.println("Weapon did not exist at the time this game was saved.");
            }
            
        }
    }
    private static void parseItemList(String s)
    {
        String[] splits = s.split(" ");
        for(int i = 0; i < itemList.length; i++)
        {
            try
            {
                itemList[i] = Byte.parseByte(splits[i]);
            }
            catch(Exception e)
            {
                System.out.println("Item did not exist at the time this game was saved.");
            }
        }
    }
    
    /*
    private static void parseAmmoList(String s)
    {
        String[] splits = s.split(" ");
        for(int i = 0; i < ammoList.length; i++)
        {
            try
            {
                ammoList[i] = Short.parseShort(splits[i]);
            }
            catch(Exception e)
            {
                System.out.println("Ammo type did not exist at the time this game was saved.");
            }
        }
    }
     *
     */

    private static void parseBossList(String s)
    {
        String[] splits = s.split(" ");
        bosses = new byte[splits.length];

        for(int i = 0; i < bosses.length; i++)
        {
            bosses[i] = Byte.parseByte(splits[i]);
        }
    }
}
