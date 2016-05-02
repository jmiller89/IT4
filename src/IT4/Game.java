/*
 * Intruder's Thunder 4: The Endling's Artifice
 * Programmed By: Jim Miller
 * (C) 2011-2016
 */

package IT4;

/**
 *
 * @author Jim (Admin)
 */

import GFX.GLRenderThread;
import GFX.ResourceFactory;
import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.lwjgl.opengl.Display;
import java.util.ArrayDeque;
import java.util.concurrent.TimeUnit;

public final class Game
{
    private Player player;
    private Player playerCopy;
    private ArrayList<NPC> NPCs;
    private Thread NPCThread;

    private ArrayList<SecurityCamera> cameras;
    private ArrayList<Spawn> spawns;

    private Boss boss;
    private boolean fightingBoss;

    private byte[][] obstacleMatrix;

    private ArrayList<Bullet> bullets;
    private Thread bulletThread;

    private GLRenderThread glrt;
    private LevelSet levels;
    private Level currentLevel;
    private LevelMap currentLevelMap;
    private int playerLevMapX = 0;

    private char playerAnimationIterations = 1;

    public static final byte RUNSPEED = 2; //3
    public static final byte WALKSPEED = 1;

    public static final int knifedamage = 16;

    private boolean firingWeapon = false;
    private boolean protectPlayer = false;
    private boolean playerSpotted = false;

    private boolean playerMoving = false;
    private boolean playerAttacking = false;

    public boolean movingUp = false;
    public boolean movingDown = false;
    public boolean movingLeft = false;
    public boolean movingRight = false;

    public boolean paused = false;

    //public int roundsFired = 0;
    
    public short enemiesKilled = 0;
    public short timesWounded = 0;
    public short alertsTriggered = 0;
    public short medkitsUsed = 0;
    public short bossesDefeated = 0;
    public short deaths = 0;
    public long startDate = System.currentTimeMillis();

    private boolean restarting = false;
    public boolean running = true;

    public boolean loading = false;

    public boolean semiDarkness = false;
    public boolean darkness = false;
    public boolean nightVision = false;
    public boolean gas = false;
    public boolean midnight = false;
    public boolean rain = false;
    public boolean snow = false;
    public boolean jam = false;
    public boolean forceprone = false;
    public boolean mineDetector = false;
    public boolean playerOnPath = false;

    private static final FloatVec upVector = new FloatVec(0, -0.5f);
    private static final FloatVec downVector = new FloatVec(0, 0.5f);
    private static final FloatVec leftVector = new FloatVec(-0.5f, 0);
    private static final FloatVec rightVector = new FloatVec(0.5f, 0);

    private static final Point _upVector = new Point(0, -1);
    private static final Point _downVector = new Point(0, 1);
    private static final Point _leftVector = new Point(-1, 0);
    private static final Point _rightVector = new Point(1, 0);
    private static final Point _zeroVector = new Point(0, 0);

    private static final FloatVec upVectorRun = new FloatVec(0, -1.0f * RUNSPEED);
    private static final FloatVec downVectorRun = new FloatVec(0, 1.0f * RUNSPEED);
    private static final FloatVec leftVectorRun = new FloatVec(-1.0f * RUNSPEED, 0);
    private static final FloatVec rightVectorRun = new FloatVec(1.0f * RUNSPEED, 0);
    
    private Point doorXY = new Point(0, 0);
    private Point visionXY = new Point(0, 0);
    public static int SLEEPTIME = 10;
    public static boolean FULLSCREEN = true;

    public int lastPlayerTileX = 0;
    public int lastPlayerTileY = 0;

    public final Object followerLock = new Object();

    public boolean drawingBullets = false;
    public boolean inDialog = false;
    private Dialog itd = null;    

    private ArrayList<Explosion> explosions;
    private boolean dialogEnabled = true;

    public short SPAWN_TIME = 270;

    public String message;
    public HUDMessageType mtype = HUDMessageType.INFO;
    public long lastMessageDate = 0;

    public Point lurchVector = new Point(0, 0);
    public boolean lurch = false;

    public ArrayDeque<Explosion> C4Queue;
    
    @SuppressWarnings("LeakingThisInConstructor")
    public Game(String levelPath, boolean levelSet, boolean internalLevels, boolean maze, boolean loadingSaveGame, MainMenuFrame mmf)
    {
        mmf.dispose();
        GameFileManager.internal = internalLevels;
        GameFileManager.isLevelset = levelSet;
        GameFileManager.levelPath = levelPath;
        GameFileManager.isMaze = maze;        

        //test = new TestLevel(this);
        //Must specify how many levels to include here
        levels = new LevelSet(this, levelPath, levelSet, internalLevels, maze);
        
        if (maze)
        {
            SPAWN_TIME = 540;
        }

        boolean error = false;

        if (!loadingSaveGame)
        {
            currentLevel = levels.getNextLevel();

            if (currentLevel != null)
            {
                currentLevelMap = currentLevel.getLevelMap(playerLevMapX);
            }
            else
            {
                running = false;
                error = true;
                showErrorMessage("Error: The level(s) could not be opened.");
            }
        }
        else
        {
            //v2.02 Bugfix: Can now save/load from single external levels
            //Check to see if it is a single external level
            if ((!levelSet) && (!internalLevels))
            {
                currentLevel = levels.getLevel(0);
            }
            else
            {
                currentLevel = levels.getLevel(GameFileManager.levelIndex);
            }

            if (currentLevel != null)
            {
                currentLevelMap = currentLevel.getLevelMap(GameFileManager.mapIndex);
            }
            else
            {
                running = false;
                error = true;
                showErrorMessage("Error: The level(s) could not be opened.");
            }
        }

        if (!error)
        {
            darkness = currentLevelMap.dark;
            semiDarkness = currentLevelMap.semidark;
            rain = currentLevelMap.rain;
            snow = currentLevelMap.snow;
            gas = currentLevelMap.gas;
            midnight = currentLevelMap.midnight;
            jam = currentLevelMap.jam;
            forceprone = currentLevelMap.forceprone;

            short playerid = 1;
            if (forceprone)
            {
                playerid = 88;
            }

            if (!loadingSaveGame)
            {
                player = new Player(playerid, currentLevel.getStartX() * 40, currentLevel.getStartY() * 40, Direction.UP);
            }
            else
            {
                player = new Player(playerid, GameFileManager.playerTileX * 40, GameFileManager.playerTileY * 40, Direction.UP);

                player.setMaxHealth(GameFileManager.maxHealth);
                player.setHealth(GameFileManager.playerHealth);

                player.givePrimary(Weapon.load(GameFileManager.weaponList[0]));
                player.giveSecondary(Weapon.load(GameFileManager.weaponList[1]));
                player.giveExplosive(Weapon.load(GameFileManager.weaponList[2]));

                player.setItems(GameFileManager.itemList);

                player.objectives = GameFileManager.objectivesCompleted;

                if (GameFileManager.playerStance == Stance.PRONE)
                {
                    player.changeStance(forceprone);
                }

                player.setOxygen(GameFileManager.playerOxygen);
            }

            if (forceprone)
            {
                player.changeStance(forceprone);
            }

            //synchronized (followerLock)
            //{
                this.lastPlayerTileX = player.getTileX();
                this.lastPlayerTileY = player.getTileY();
            //}

            playerCopy = new Player(player);

            explosions = new ArrayList<Explosion>();

            NPCs = currentLevelMap.getNPCs();
            NPCThread = new Thread(new GuardThread(this));

            cameras = currentLevelMap.getCameras();
            spawns = currentLevelMap.getSpawns();

            //walls = currentLevelMap.getCollidables();
            obstacleMatrix = currentLevelMap.getObstacleMatrix();

            bullets = new ArrayList<Bullet>();
            bulletThread = new Thread(new BulletThread(bullets, this));

            C4Queue = new ArrayDeque<Explosion>();

            //gameThread = new Thread(new GameThread(this));

            //gf = new GameFrame(this);
            //gf.setVisible(true);
            //gf.initLog();

            startDate = System.currentTimeMillis();
            //soundPlayer = new SoundPlayer(0);

            if (loadingSaveGame)
            {
                currentLevel.load();
                playerLevMapX = GameFileManager.mapIndex;                
            }
            
            //SFX_OLD.stopMusic();
            SFX.playMusic(currentLevelMap.songIndex);

            if (currentLevelMap.getDialog().isValid())
            {
                displayDialog(currentLevelMap.getDialog(), "Dialog", false);
            }

            if (currentLevelMap.playerPath != null)
            {
                if (!playerOnPath)
                {
                    if ((player.getTileX() == currentLevelMap.playerPath.getStartingWaypoint().getXPos()) && (player.getTileY() == currentLevelMap.playerPath.getStartingWaypoint().getYPos()))
                    {
                        playerOnPath = true;
                    }
                }
            }

            if (currentLevelMap.isBossFight())
            {
                boss = currentLevelMap.getBoss();

                if (boss.getCurrentHealth() > 0)
                {
                    //fightingBoss = true;
                    setBossFight();
                    //bossThread.start();
                }
                else
                {
                    fightingBoss = false;
                }
            }
            else
            {
                fightingBoss = false;
            }

            if (currentLevelMap.getAlertMode() == true)
            {
                playerSpotted = true;
                startAlertMusic();
            }
            else
            {
                playerSpotted = false;
            }

            glrt = new GLRenderThread(this, ResourceFactory.OPENGL_LWJGL);
        }
    }

    public void startNPCThread()
    {
        NPCThread.start();
    }

    public void startBulletThread()
    {
        bulletThread.start();
        //bulletThread.setPriority(Thread.MAX_PRIORITY);
    }

    //May be deprecated
    public void stopBulletThread()
    {
        bulletThread.interrupt();
    }

    public short[][] getTileMap()
    {
        return currentLevelMap.getTileMap();
    }

    public short getDefaultGroundCover()
    {
        return currentLevelMap.getDefaultGroundCover();
    }

    //public void updateHUD()
    //{
    //    gf.updateHUD();
    //}

    private boolean testWarp(int lvX, int pX, int pY)
    {
        if ((lvX >= 0) && (lvX < currentLevel.getNumLevels()))
        {
            player.setX(pX * 40);
            player.setY(pY * 40);

            warpTo(false, lvX);
            return true;
        }
        else
        {
            return false;
        }
    }

    public void openConsole()
    {
        boolean success = false;
        paused = true;

        movingUp = false;
        movingDown = false;
        movingLeft = false;
        movingRight = false;

        String cmds = JOptionPane.showInputDialog(null, "Enter a command", "IT3 Console", JOptionPane.WARNING_MESSAGE);

        if (cmds != null)
        {
            if (cmds.equalsIgnoreCase("giveall"))
            {
                player.givePrimary(Weapon.create(ItemType.ASSAULT_RIFLE, 3));
                player.giveSecondary(Weapon.create(ItemType.PISTOL, 3));
                player.giveExplosive(Weapon.create(ItemType.GRENADE, 3));
                player.givePrimaryAmmo();
                player.giveSecondaryAmmo();
                player.giveExplosiveAmmo();
                player.givePrimarySilencer();
                player.giveSecondarySilencer();
                player.giveGasMask();
                player.giveNVG();
                player.giveBodyArmor();
                player.giveMineDetector();
                player.giveCardKey(ItemType.CARDKEY_5);
                player.giveHealthKit();
                player.giveHealthKit();
                player.objectives = 9999; //Will anyone ever make a level with 10000 objectives? No.
            }
            if (cmds.equalsIgnoreCase("boost"))
            {
                player.giveBoosterKit();
            }
            if (cmds.equalsIgnoreCase("tm"))
            {
                SFX.toggleMusic();
                if (SFX.musicOn)
                {
                    SFX.playMusic(currentLevelMap.songIndex);
                }
                else
                {
                    SFX.stopMusic();
                }
            }
            if (cmds.equalsIgnoreCase("help"))
            {
                String msg = "Command List:\ngiveall\nboost\ntm\nhelp\njump [X] [Y]\nwarp [ROOM] [X] [Y]\nclear\ngas\ndark\nsdark\nhaze\njam\ncs\nrain\nsnow\n [SONG #]\nheal\ntd";
                System.out.println(msg);
                //gf.displayMessage(msg);
            }
            if (cmds.startsWith("jump "))
            {
                try
                {
                    String params = cmds.substring(5);
                    System.out.println(params);
                    int index = params.indexOf(" ");
                    int x = Integer.parseInt(params.substring(0, index));
                    int y = Integer.parseInt(params.substring(index + 1));
                    System.out.println("X=" + x + " Y=" + y);
                    player.setX(40*x);
                    player.setY(40*y);
                    playerOnPath = false;
                    //synchronized (followerLock)
                    //{
                    //    this.lastPlayerTileX = player.getTileX();
                    //    this.lastPlayerTileY = player.getTileY();
                    //}
                }
                catch(Exception e)
                {
                    System.out.println("Invalid jump");
                }
            }
            if (cmds.startsWith("warp "))
            {
                try
                {
                    String params = cmds.substring(5);
                    System.out.println(params);
                    int index = params.indexOf(" ");
                    int r = Integer.parseInt(params.substring(0, index));
                    params = params.substring(index + 1);
                    index = params.indexOf(" ");
                    int x = Integer.parseInt(params.substring(0, index));
                    int y = Integer.parseInt(params.substring(index + 1));
                    System.out.println("R=" + r + " X=" + x + " Y=" + y);
                    success = testWarp(r, x, y);
                    if (!success)
                    {
                        System.out.println("Invalid warp! Make sure your room argument is valid (greater than 0, less than # of rooms in level)");
                    }
                }
                catch(Exception e)
                {
                    System.out.println("Invalid warp");
                }
            }
            if (cmds.equalsIgnoreCase("clear"))
            {
                this.gas = false;
                this.darkness = false;
                this.semiDarkness = false;
                this.midnight = false;
                this.jam = false;
                this.rain = false;
                this.snow = false;
            }
            if (cmds.equalsIgnoreCase("gas"))
            {
                this.gas = true;
                this.darkness = false;
                this.semiDarkness = false;
                this.midnight = false;
            }
            if (cmds.equalsIgnoreCase("dark"))
            {
                this.gas = false;
                this.darkness = true;
                this.semiDarkness = false;
                this.midnight = false;
            }
            if (cmds.equalsIgnoreCase("sdark"))
            {
                this.gas = false;
                this.darkness = false;
                this.semiDarkness = true;
                this.midnight = false;
            }
            if (cmds.equalsIgnoreCase("night"))
            {
                this.gas = false;
                this.darkness = false;
                this.semiDarkness = false;
                this.midnight = true;
            }
            if (cmds.equals("jam"))
            {
                this.jam = true;
            }
            if (cmds.equalsIgnoreCase("rain"))
            {
                this.snow = false;
                this.rain = true;
            }
            if (cmds.equalsIgnoreCase("snow"))
            {
                this.rain = false;
                this.snow = true;
            }
            if (cmds.startsWith("cs "))
            {
                try
                {
                    String[] s = cmds.split(" ");
                    int index = Integer.parseInt(s[1]);
                    
                    if ((index >= 0) && (index < SFX.getNumSongs()))
                    {
                        //SFX_OLD.stopMusic();
                        SFX.playMusic(index);
                    }
                    else
                    {
                        System.out.println("Could not change song");
                    }

                }
                catch(Exception e)
                {
                    System.out.println("Could not change song");
                }
            }
            if (cmds.equalsIgnoreCase("heal"))
            {
                player.replenish();
            }
            if (cmds.equalsIgnoreCase("td"))
            {
                if (dialogEnabled)
                {
                    dialogEnabled = false;
                    System.out.println("Dialog is OFF");
                }
                else
                {
                    dialogEnabled = true;
                    System.out.println("Dialog is ON");
                }
            }
            if (cmds.equalsIgnoreCase("l"))
            {
                goToNextLevel();
            }
        }

        
        paused = false;
        
    }

    public Door[] getDoors()
    {
        return currentLevelMap.getDoors();
    }

    public void checkDoors(ITCharacter character, int vel)
    {
        doorXY.x = character.doorXY.x;
        doorXY.y = character.doorXY.y;

        doorXY.x = character.getTileX();
        doorXY.y = character.getTileY();

        byte[][] gwd = currentLevelMap.getGrassWaterDoors();

        if (gwd != null)
        {
            try
            {
                if (((doorXY.x >= 0) && (doorXY.x < gwd[0].length)) && ((doorXY.y >= 0) && (doorXY.y < gwd.length)))
                {
                    if (gwd[doorXY.y][doorXY.x] == 3)
                    {
                        checkDoors_(character, doorXY, vel);
                    }

                    if (character.getDirection() == Direction.UP)
                    {
                        if (doorXY.y - 1 >= 0)
                        {
                            if (gwd[doorXY.y-1][doorXY.x] == 3)
                            {
                                doorXY.y = doorXY.y - 1;
                                checkDoors_(character, doorXY, vel);
                            }
                        }
                    }
                    else if (character.getDirection() == Direction.DOWN)
                    {
                        if (doorXY.y + 1 < gwd.length)
                        {
                            if (gwd[doorXY.y+1][doorXY.x] == 3)
                            {
                                doorXY.y = doorXY.y + 1;
                                checkDoors_(character, doorXY, vel);
                            }
                        }
                    }
                    else if (character.getDirection() == Direction.LEFT)
                    {
                        if (doorXY.x - 1 >= 0)
                        {
                            if (gwd[doorXY.y][doorXY.x-1] == 3)
                            {
                                doorXY.x = doorXY.x - 1;
                                checkDoors_(character, doorXY, vel);
                            }
                        }
                    }
                    else if (character.getDirection() == Direction.RIGHT)
                    {
                        if (doorXY.x + 1 < gwd[0].length)
                        {
                            if (gwd[doorXY.y][doorXY.x+1] == 3)
                            {
                                doorXY.x = doorXY.x + 1;
                                checkDoors_(character, doorXY, vel);
                            }
                        }
                    }
                }
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                System.out.println("Something went wrong w/ checking doors, ignoring");
            }
        }
    }

    private void checkCrawlDoor_(ITCharacter c, Point p, int vel, Door d)
    {
        //System.out.println("Crawl door");
        if (c.getDirection() == Direction.UP)
        {
            if ((c.getX() + 20 >= d.getX()) && (c.getX() + 20 <= d.getX() + 40))
            {
                if ((c.getY() + 19 <= d.getY() + 40) && (c.getY() + 19 >= d.getY()))
                {
                    if (c.stance != Stance.PRONE)
                    {
                        c.setY(c.getY() + vel);
                    }
                }
            }
        }
        else if (c.getDirection() == Direction.DOWN)
        {
            if ((c.getX() + 20 >= d.getX()) && (c.getX() + 20 <= d.getX() + 40))
            {
                if ((c.getY() + 38 >= d.getY()) && (c.getY() + 38 <= d.getY() + 40))
                {
                    if (c.stance != Stance.PRONE)
                    {
                        c.setY(c.getY() - vel);
                    }
                }
            }
        }
        else if (c.getDirection() == Direction.LEFT)
        {
            if ((c.getY() + 20 >= d.getY()) && (c.getY() + 20 <= d.getY() + 40))
            {
                if ((c.getX() <= d.getX() + 28) && (c.getX() + 28 >= d.getX()))
                {
                    if (c.stance != Stance.PRONE)
                    {
                        c.setX(c.getX() + vel);
                    }
                }
            }
        }
        else if (c.getDirection() == Direction.RIGHT)
        {
            if ((c.getY() + 20 >= d.getY()) && (c.getY() + 20 <= d.getY() + 40))
            {
                if ((c.getX() + 28 >= d.getX()) && (c.getX() <= d.getX() + 28))
                {
                    if (c.stance != Stance.PRONE)
                    {
                        c.setX(c.getX() - vel);
                    }
                }
            }
        }
    }

    private void checkDoors_(ITCharacter c, Point p, int vel)
    {
        Door d = currentLevelMap.getDoorsHashMap().get(p);

        if (c.lastDoor != null)
        {
            c.lastDoor.closing();
            //c.lastDoor.closing = true;
            //c.lastDoor.opening = false;
        }

        c.lastDoor = d;
        boolean isNPC = c.isNPC();

        if (d == null)
        {
            return;
        }

        if (d.crawl)
        {
            checkCrawlDoor_(c, p, vel, d);
            return;
        }

        boolean locked = false;

        if (c.getDirection() == Direction.UP)
        {
            if ((c.getX() + 20 >= d.getX()) && (c.getX() + 20 <= d.getX() + 40))
            {
                if ((c.getY() + 19 <= d.getY() + 40) && (c.getY() + 19 >= d.getY()))
                {
                    if (!isNPC)
                    {
                        if (d.getSecuritylevel() > player.getCardKey())
                        {
                            cardKeyDenied(d.getSecuritylevel());
                            locked = true;
                        }
                        else if (d.getObjectiveLevel() > player.objectives)
                        {
                            doorLocked();
                            locked = true;
                        }
                    }

                    if ((!fightingBoss) && (!locked))
                    {
                        if (!d.isOpening())
                        {
                            d.opening();
                            //d.closing = false;
                            //d.opening = true;
                        }


                        if (!d.isOpen())
                        {
                            c.setY(c.getY() + vel);
                        }
                    }
                    else
                    {
                        if (!isNPC)
                        {
                            if (!locked)
                            doorLocked();
                        }
                        
                        c.setY(c.getY() + vel);
                    }
                }
            }
        }
        else if (c.getDirection() == Direction.DOWN)
        {
            if ((c.getX() + 20 >= d.getX()) && (c.getX() + 20 <= d.getX() + 40))
            {
                if ((c.getY() + 38 >= d.getY()) && (c.getY() + 38 <= d.getY() + 40))
                {
                    if (!isNPC)
                    {
                        if (d.getSecuritylevel() > player.getCardKey())
                        {
                            cardKeyDenied(d.getSecuritylevel());
                            locked = true;
                        }
                        else if (d.getObjectiveLevel() > player.objectives)
                        {
                            doorLocked();
                            locked = true;
                        }
                    }

                    if ((!fightingBoss) && (!locked))
                    {
                        if (!d.isOpening())
                        {
                            d.opening();
                            //d.closing = false;
                            //d.opening = true;
                        }

                        if (!d.isOpen())
                        {
                            c.setY(c.getY() - vel);
                        }
                    }
                    else
                    {
                        if (!isNPC)
                        {
                            if (!locked)
                            doorLocked();
                        }
                        
                        c.setY(c.getY() - vel);
                    }
                }
            }
        }
        else if (c.getDirection() == Direction.LEFT)
        {
            if ((c.getY() + 20 >= d.getY()) && (c.getY() + 20 <= d.getY() + 40))
            {
                if ((c.getX() <= d.getX() + 28) && (c.getX() + 28 >= d.getX()))
                {
                    if (!isNPC)
                    {
                        if (d.getSecuritylevel() > player.getCardKey())
                        {
                            cardKeyDenied(d.getSecuritylevel());
                            locked = true;
                        }
                        else if (d.getObjectiveLevel() > player.objectives)
                        {
                            doorLocked();
                            locked = true;
                        }
                    }

                    if ((!fightingBoss) && (!locked))
                    {   
                        if (!d.isOpening())
                        {
                            d.opening();
                            //d.closing = false;
                            //d.opening = true;
                        }

                        if (!d.isOpen())
                        {
                            c.setX(c.getX() + vel);
                        }
                    }
                    else
                    {
                        if (!isNPC)
                        {
                            if (!locked)
                            doorLocked();
                        }
                        
                        c.setX(c.getX() + vel);
                    }
                }
            }
        }
        else if (c.getDirection() == Direction.RIGHT)
        {
            if ((c.getY() + 20 >= d.getY()) && (c.getY() + 20 <= d.getY() + 40))
            {
                if ((c.getX() + 28 >= d.getX()) && (c.getX() <= d.getX() + 28))
                {
                    if (!isNPC)
                    {
                        if (d.getSecuritylevel() > player.getCardKey())
                        {
                            cardKeyDenied(d.getSecuritylevel());
                            locked = true;
                        }
                        else if (d.getObjectiveLevel() > player.objectives)
                        {
                            doorLocked();
                            locked = true;
                        }
                    }

                    if ((!fightingBoss) && (!locked))
                    {
                        if (!d.isOpening())
                        {
                            d.opening();
                            //d.closing = false;
                            //d.opening = true;
                        }

                        if (!d.isOpen())
                        {
                            c.setX(c.getX() - vel);
                        }
                    }
                    else
                    {
                        if (!isNPC)
                        {
                            if (!locked)
                            doorLocked();
                        }
                        
                        c.setX(c.getX() - vel);
                    }
                }
            }
        }

        if (locked)
        {
            if (!d.isClosed())
            {
                d.closing();
                //d.closing = true;
                //d.opening = false;
            }
        }
    }

    //Checks to see if there is a warp one step ahead. If so, then lurch.
    private boolean checkFutureWarps(Direction dir)
    {
        int px = player.getX();
        int py = player.getY();
        int ptx = player.getTileX();
        int pty = player.getTileY();

        if (dir == Direction.UP)
        {
            py--;
            pty = (py + 19) / 40;
        }
        else if (dir == Direction.DOWN)
        {
            py++;
            pty = (py + 19) / 40;
        }
        else if (dir == Direction.LEFT)
        {
            px--;
            ptx = (px + 19) / 40;
        }
        else if (dir == Direction.RIGHT)
        {
            px++;
            ptx = (px + 19) / 40;
        }

        for(int i = 0; i < currentLevelMap.getWarps().size(); i++)
        {

            if ((ptx == currentLevelMap.getWarps().get(i).getTileX()) && (pty == currentLevelMap.getWarps().get(i).getTileY()))
            {
                if (!currentLevelMap.getWarps().get(i).isPlayerInWarp)
                {
                    return true;
                }
            }
        }

        return false;
    }

    private void checkWarps()
    {
        for(int i = 0; i < currentLevelMap.getWarps().size(); i++)
        {
            if ((player.getTileX() == currentLevelMap.getWarps().get(i).getTileX()) && (player.getTileY() == currentLevelMap.getWarps().get(i).getTileY()))
            {
                if (!currentLevelMap.getWarps().get(i).isPlayerInWarp)
                {
                    currentLevelMap.getWarps().get(i).isPlayerInWarp = true;

                    int warpx, warpy;
                    warpx = currentLevelMap.getWarps().get(i).getPlayerWarpX();
                    warpy = currentLevelMap.getWarps().get(i).getPlayerWarpY();

                    if ((warpx >= 0) && (warpy >= 0))
                    {
                        player.setX(warpx);
                        player.setY(warpy);
                    }

                    synchronized (followerLock)
                    {
                        lastPlayerTileX = player.getTileX();
                        lastPlayerTileY = player.getTileY();
                    }
                    
                    warpTo(currentLevelMap.getWarps().get(i).isNextLevelWarp(),
                            currentLevelMap.getWarps().get(i).getLevelX());
                    
                }

                break;
            }
            else
            {
                currentLevelMap.getWarps().get(i).isPlayerInWarp = false;
            }
        }
        
    }

    private void warpTo(boolean isLvlWarp, int x)
    {
        int lastSong = currentLevelMap.songIndex;
        
        loading = true;

        //resetNPCIDs();

        playerSpotted = false;
        playerOnPath = false;

        try
        {
            for(int i = 0; i < bullets.size(); i++)
            {
                //bullets.remove(i);
                bullets.get(i).setStatus(false);
            }
        }
        catch(Exception e)
        {

        }

        try
        {
            for(int i = 0; i < NPCs.size(); i++)
            {
                NPCs.get(i).setStatus(NPCStatus.DEAD);
            }
        }
        catch(Exception e)
        {

        }

        try
        {
            for(int i = 0; i < spawns.size(); i++)
            {
                spawns.get(i).reset();
            }
        }
        catch(Exception e)
        {
            
        }

        C4Queue.clear();
        //gf.stopRenderThread();

        if (isLvlWarp == true)
        {
            System.out.println("Changing level");
            currentLevel = levels.getNextLevel();
            //resetCardKey();
            player.objectives = 0;

            playerLevMapX = x;
            player.replenish();

            if (currentLevel != null)
            {
                if (currentLevel.stripItems)
                {
                    System.out.println("Stripping all Weapons and Items...");
                    player.stripItems();
                    resetCardKey();
                    nightVision = false;
                }
            }

            playerCopy = new Player(player);

            //gf.clearLog();

            //End of game
            if (currentLevel == null)
            {
                System.out.println("End of game reached");
                setHUDMessage("Mission Accomplished!", HUDMessageType.RESULT);
                running = false;                
                returnToMainMenu(displayStats("THE END"));
            }
            else
            {                
                //soundPlayer.stopSong();
                //soundPlayer = new SoundPlayer(currentLevel.getID());
                //SFX_OLD.stopMusic();
                SFX.playMusic(currentLevelMap.songIndex);
                
                player.setX(currentLevel.getStartX() * 40);
                player.setY(currentLevel.getStartY() * 40);

                //synchronized (followerLock)
                //{
                //    lastPlayerTileX = currentLevel.getStartX();
                //    lastPlayerTileY = currentLevel.getStartY();
                //}
            }
        }

        if (currentLevel != null)
        {
            currentLevel.saveState();
            playerCopy = new Player(player);

            //Close all doors before warp
            Door[] doors = currentLevelMap.getDoors();
            for(int i = 0; i < doors.length; i++)
            {
                doors[i].forceClose();
            }

            currentLevelMap = currentLevel.getLevelMap(x);
            playerLevMapX = x;

            if ((currentLevelMap.songIndex != lastSong) | (SFX.alertMode))
            {                
                //SFX.stopMusic();
                SFX.playMusic(currentLevelMap.songIndex);
            }            

            int ptx = player.getTileX();
            int pty = player.getTileY();
            byte[][] om = currentLevelMap.getObstacleMatrix();

            Direction d = player.getDirection();

            if (d == Direction.UP)
            {
                if (pty + 1 < om.length)
                {
                    if (om[pty+1][ptx] == 1)
                    {
                        synchronized (followerLock)
                        {
                            lastPlayerTileX = ptx;
                            lastPlayerTileY = pty + 1;
                        }
                    }
                }
            }
            else if (d == Direction.DOWN)
            {
                if (pty - 1 >= 0)
                {
                    if (om[pty-1][ptx] == 1)
                    {
                        synchronized (followerLock)
                        {
                            lastPlayerTileX = ptx;
                            lastPlayerTileY = pty - 1;
                        }
                    }
                }
            }
            else if (d == Direction.LEFT)
            {
                if (ptx - 1 >= 0)
                {
                    if (om[pty][ptx-1] == 1)
                    {
                        synchronized (followerLock)
                        {
                            lastPlayerTileX = ptx - 1;
                            lastPlayerTileY = pty;
                        }
                    }
                }
            }
            else if (d == Direction.RIGHT)
            {
                if (ptx + 1 < om[0].length)
                {
                    if (om[pty][ptx+1] == 1)
                    {
                        synchronized (followerLock)
                        {
                            lastPlayerTileX = ptx + 1;
                            lastPlayerTileY = pty;
                        }
                    }
                }
            }

            darkness = currentLevelMap.dark;
            semiDarkness = currentLevelMap.semidark;
            rain = currentLevelMap.rain;
            snow = currentLevelMap.snow;
            gas = currentLevelMap.gas;
            midnight = currentLevelMap.midnight;
            jam = currentLevelMap.jam;
            forceprone = currentLevelMap.forceprone;

            if (forceprone)
            {
                player.changeStance(forceprone);
            }

            NPCs = currentLevelMap.getNPCs();

            cameras = currentLevelMap.getCameras();
            spawns = currentLevelMap.getSpawns();

            //walls = currentLevelMap.getCollidables();
            obstacleMatrix = currentLevelMap.getObstacleMatrix();

            //gf.setHUDObstacleMatrix();

            if (currentLevelMap.isBossFight())
            {
                boss = currentLevelMap.getBoss();
                
                if (boss.getCurrentHealth() > 0)
                {
                    //fightingBoss = true;
                    setBossFight();
                    //bossThread.start();
                }
                else
                {
                    fightingBoss = false;
                }
            }
            else
            {
                fightingBoss = false;
            }

            if (currentLevelMap.playerPath != null)
            {
                if (!playerOnPath)
                {
                    int xpos,ypos;
                    xpos = currentLevelMap.playerPath.getStartingWaypoint().getXPos();
                    ypos = currentLevelMap.playerPath.getStartingWaypoint().getYPos();
                    if ((player.getTileX() == xpos) && (player.getTileY() == ypos))
                    {
                        playerOnPath = true;
                    }
                    else
                    {
                        if ((xpos < 0) || (ypos < 0))
                        {
                            playerOnPath = true;
                        }
                    }
                }
            }

            this.updateNPCStatus();

            if (currentLevelMap.getAlertMode() == true)
            {
                playerSpotted = true;
                startAlertMusic();
            }
            else
            {
                playerSpotted = false;
            }

            if (currentLevelMap.getDialog().isValid())
            {
                displayDialog(currentLevelMap.getDialog(), "Dialog", false);
            }
        }

        loading = false;
    }

    public void returnToMainMenu(Dialog d)
    {                
        //SFX_OLD.stopMusic();

        running = false;

        try
        {            
            Display.destroy();
        }
        catch (Exception e)
        {
            System.err.println("Error in destroying display");            
            System.exit(-1);
        }

        try
        {
            ResourceFactory.get().destroy();
        }
        catch(Exception e)
        {
            System.err.println("Error in destroying ResourceFactory");
        }

        GameFileManager.filepath = "";

        MainMenuFrame mmf = null;

        if (d != null)
        {
            mmf = new MainMenuFrame(d);
        }
        else
        {
            mmf = new MainMenuFrame();
        }
    }

    private void resetCardKey()
    {
        player.resetCardKey();
    }

    private void cardKeyDenied(int req)
    {
        setHUDMessage("Requires card " + req, HUDMessageType.ACCESS_DENIED);
    }

    private void doorLocked()
    {
        setHUDMessage("Door is locked", HUDMessageType.ACCESS_DENIED);
    }

    private void checkObjectives()
    {
        if (currentLevel.getAllObjectives().size() > 0)
        {
            for(int i = 0; i < currentLevel.getObjectives(playerLevMapX).size(); i++)
            {
                Objective obj = currentLevel.getObjectives(playerLevMapX).get(i);

                if ((player.getTileX() == obj.getTileX()) &&
                        (player.getTileY() == obj.getTileY()))
                {
                    objectiveAccomplished(obj);
                    break;
                }
            }
        }
    }

    private void objectiveAccomplished(Objective obj)
    {
        player.objectives++;
        displayDialog(obj.dialog, "Objective Complete", false);
        currentLevel.removeObjective(obj, playerLevMapX);
    }

    private void checkItems()
    {
        //Only pickup item if player requests to do so
        for(int i = 0; i < currentLevelMap.getItems().size(); i++)
        {
            if ((player.getTileX() == currentLevelMap.getItems().get(i).getTileX()) && (player.getTileY() == currentLevelMap.getItems().get(i).getTileY()))
            {
                if (!currentLevelMap.getItems().get(i).touched)
                {
                    currentLevelMap.getItems().get(i).touched = true;
                    useItem(currentLevelMap.getItems().get(i));
                }
                break;
            }
            else
            {
                currentLevelMap.getItems().get(i).touched = false;
            }
        }
    }

    public void confirmItem(Item it)
    {
        if (it != null)
        {
            if (it.isWeapon)
            {
                Weapon oldWeapon = null;
                if ((it.getType() == ItemType.PISTOL) || (it.getType() == ItemType.TRANQ_PISTOL))
                {
                    oldWeapon = player.giveSecondary((Weapon)it);
                    it.flagForRemoval();
                    setHUDMessage(it.toString(), HUDMessageType.FOUND);
                    player.changeWeapon(2);
                }
                else if ((it.getType() == ItemType.SMG) || (it.getType() == ItemType.ASSAULT_RIFLE) || (it.getType() == ItemType.SHOTGUN))
                {
                    oldWeapon = player.givePrimary((Weapon)it);
                    it.flagForRemoval();
                    setHUDMessage(it.toString(), HUDMessageType.FOUND);
                    player.changeWeapon(1);
                }
                else if ((it.getType() == ItemType.GRENADE) || (it.getType() == ItemType.C4))
                {
                    oldWeapon = player.giveExplosive((Weapon)it);
                    it.flagForRemoval();
                    setHUDMessage(it.toString(), HUDMessageType.FOUND);
                    player.changeWeapon(3);
                }

                if (oldWeapon != null)
                {
                    oldWeapon.setX(player.getTileX() * 40);
                    oldWeapon.setY(player.getTileY() * 40);
                    oldWeapon.touched = true;
                    currentLevelMap.getItems().add(oldWeapon);
                }

                //Clean up items
                for(int i = 0; i < currentLevelMap.getItems().size(); i++)
                {
                    if (currentLevelMap.getItems().get(i).shouldRemove() == true)
                    {
                        currentLevelMap.getItems().remove(i);
                    }
                }
            }
        }
    }

    private void foundItem(Item it)
    {
        if (it != null)
        {
            String title = "Item";
            
            if (it.isWeapon)
            {
                title = "Weapon";
            }

            Dialog d = new Dialog(it, new ITEvent() {
                public <T> void function(T arg)
                {
                    Item it = (Item)arg;
                    confirmItem(it);
                }
            });
            this.displayDialog(d, "Confirm " + title + " Pickup", true);
        }
    }

    private void useItem(Item it)
    {
        if (it != null)
        {
            if (it.isWeapon)
            {                
                if ((it.getType() == ItemType.PISTOL) || (it.getType() == ItemType.TRANQ_PISTOL))
                {
                    if (player.stuff.sidearm == null)
                    {   
                        foundItem(it);
                    }
                    else
                    {
                        if (player.stuff.sidearm.getType() == it.getType())
                        {
                            if (player.stuff.sidearm.rank >= it.rank)
                            {
                                if (player.giveSecondaryMag((Weapon)it))
                                {
                                    setHUDMessage("Secondary ammo x " + ((Weapon)it).ammo, HUDMessageType.FOUND);
                                }
                                it.flagForRemoval();
                            }
                            else
                            {
                                foundItem(it);
                            }
                        }
                        else
                        {
                            foundItem(it);
                        }
                    }

                    if (player.stuff.selectedWeapon != null)
                    {
                        if ((player.stuff.selectedWeapon.type == ItemType.PISTOL) || (player.stuff.selectedWeapon.type == ItemType.TRANQ_PISTOL))
                        {
                            player.changeWeapon(2);
                        }
                    }
                    else
                    {
                        player.changeWeapon(2);
                    }

                }
                else if ((it.getType() == ItemType.SMG) || (it.getType() == ItemType.ASSAULT_RIFLE) || (it.getType() == ItemType.SHOTGUN))
                {
                    if (player.stuff.primary == null)
                    {
                        foundItem(it);
                    }
                    else
                    {
                        if (player.stuff.primary.getType() == it.getType())
                        {
                            if (player.stuff.primary.rank >= it.rank)
                            {
                                if (player.givePrimaryMag((Weapon)it))
                                {
                                    setHUDMessage("Primary ammo x " + ((Weapon)it).ammo, HUDMessageType.FOUND);
                                }
                                it.flagForRemoval();
                            }
                            else
                            {
                                foundItem(it);
                            }
                        }
                        else
                        {
                            foundItem(it);
                        }
                    }

                    if (player.stuff.selectedWeapon != null)
                    {
                        if ((player.stuff.selectedWeapon.type == ItemType.ASSAULT_RIFLE) || (player.stuff.selectedWeapon.type == ItemType.SMG) || (player.stuff.selectedWeapon.type == ItemType.SHOTGUN))
                        {
                            player.changeWeapon(1);
                        }
                    }
                    else
                    {
                        player.changeWeapon(1);
                    }
                }
                else
                {
                    if (player.stuff.explosive == null)
                    {
                        foundItem(it);
                    }
                    else
                    {
                        if (player.stuff.explosive.getType() == it.getType())
                        {
                            if (player.stuff.explosive.rank >= it.rank)
                            {
                                if (player.giveExplosiveAmmo((Weapon)it))
                                {
                                    setHUDMessage(it.toString(), HUDMessageType.FOUND);
                                }
                                it.flagForRemoval();
                            }
                            else
                            {
                                foundItem(it);
                            }
                        }
                        else
                        {
                            foundItem(it);
                        }
                    }

                    if (player.stuff.selectedWeapon != null)
                    {
                        if ((player.stuff.selectedWeapon.type == ItemType.GRENADE) || (player.stuff.selectedWeapon.type == ItemType.C4))
                        {
                            player.changeWeapon(3);
                        }
                    }
                    else
                    {
                        player.changeWeapon(3);
                    }
                }
            }
            else if (it.getType() == ItemType.SECONDARY_SILENCER)
            {
                if (player.stuff.sidearm != null)
                {
                    player.giveSecondarySilencer();
                    it.flagForRemoval();
                    setHUDMessage("Suppressor - Secondary", HUDMessageType.FOUND);
                }
                else
                {
                    setHUDMessage("Find Sidearm First", HUDMessageType.NO_USE);
                }
            }
            else if (it.getType() == ItemType.PRIMARY_SILENCER)
            {
                if (player.stuff.primary != null)
                {
                    if (player.stuff.primary.suppressorDurability > 0)
                    {
                        player.givePrimarySilencer();
                        it.flagForRemoval();
                        setHUDMessage("Suppressor - Primary", HUDMessageType.FOUND);
                    }
                    else
                    {
                        setHUDMessage("Weapon cannot be suppressed", HUDMessageType.NO_USE);
                    }
                }
                else
                {
                    setHUDMessage("Find primary weapon first", HUDMessageType.NO_USE);
                }
            }
            else if (it.getType() == ItemType.MEDKIT)
            {
                if (player.giveHealthKit() == true)
                {
                    it.flagForRemoval();
                    setHUDMessage("Medkit", HUDMessageType.FOUND);
                }
                else
                {
                    setHUDMessage("Medkits full", HUDMessageType.NO_USE);
                }
            }
            else if (it.getType() == ItemType.BOOSTER_KIT)
            {
                player.giveBoosterKit();
                it.flagForRemoval();
                setHUDMessage("Booster Kit", HUDMessageType.FOUND);
                
            }
            else if (it.getType() == ItemType.PRIMARY_AMMO)
            {
                if (player.givePrimaryAmmo() == true)
                {
                    it.flagForRemoval();
                    setHUDMessage(it.toString(player), HUDMessageType.FOUND);
                }
                else
                {
                    setHUDMessage("Ammo is full", HUDMessageType.NO_USE);
                }
            }
            else if (it.getType() == ItemType.SECONDARY_AMMO)
            {
                if (player.giveSecondaryAmmo() == true)
                {
                    it.flagForRemoval();
                    setHUDMessage(it.toString(player), HUDMessageType.FOUND);
                }
                else
                {
                    setHUDMessage("Ammo is full", HUDMessageType.NO_USE);
                }
            }
            else if (it.getType() == ItemType.PRIMARY_MAG)
            {
                if (player.givePrimaryMag() == true)
                {
                    it.flagForRemoval();
                    setHUDMessage(it.toString(player), HUDMessageType.FOUND);
                }
                else
                {
                    setHUDMessage("Ammo is full", HUDMessageType.NO_USE);
                }
            }
            else if (it.getType() == ItemType.SECONDARY_MAG)
            {
                if (player.giveSecondaryMag() == true)
                {
                    it.flagForRemoval();
                    setHUDMessage(it.toString(player), HUDMessageType.FOUND);
                }
                else
                {
                    setHUDMessage("Ammo is full", HUDMessageType.NO_USE);
                }
            }
            else if (it.getType() == ItemType.NVG)
            {
                player.giveNVG();
                it.flagForRemoval();
                setHUDMessage("Night Vision Goggles", HUDMessageType.FOUND);
            }
            else if (it.getType() == ItemType.GASMASK)
            {
                player.giveGasMask();
                it.flagForRemoval();
                setHUDMessage("Gas Mask", HUDMessageType.FOUND);
            }
            else if (it.getType() == ItemType.BODY_ARMOR)
            {
                player.giveBodyArmor();
                it.flagForRemoval();
                setHUDMessage("Body Armor", HUDMessageType.FOUND);
            }
            else if ((it.getType() == ItemType.CARDKEY_1) ||
                    (it.getType() == ItemType.CARDKEY_2) ||
                    (it.getType() == ItemType.CARDKEY_3) ||
                    (it.getType() == ItemType.CARDKEY_4) ||
                    (it.getType() == ItemType.CARDKEY_5))
            {
                player.giveCardKey(it.getType());
                it.flagForRemoval();
                setHUDMessage("Card Key Level " + player.getCardKey(), HUDMessageType.FOUND);
            }
            else if (it.getType() == ItemType.LANDMINE)
            {
                explosion(it.getX(), it.getY(), false, it.rank);
                it.flagForRemoval();
                setHUDMessage("Stepped on a landmine", HUDMessageType.DANGER);
            }
            else if ((it.getType() == ItemType.LASER_HORIZONTAL) || (it.getType() == ItemType.LASER_VERTICAL))
            {
                setPlayerSpotted();
                gas = true;
                jam = true;
                setHUDMessage("Poison gas!", HUDMessageType.DANGER);
            }
            else if (it.getType() == ItemType.MINE_DETECTOR)
            {
                player.giveMineDetector();
                it.flagForRemoval();
                setHUDMessage("Mine Detector", HUDMessageType.FOUND);
            }

            //Clean up items
            for(int i = 0; i < currentLevelMap.getItems().size(); i++)
            {
                if (currentLevelMap.getItems().get(i).shouldRemove() == true)
                {
                    currentLevelMap.getItems().remove(i);
                }
            }
        }
    }

    public void checkWaterAndGrass(ITCharacter character)
    {
        if (((character.getTileY() >= 0) && (character.getTileY() < currentLevelMap.getGrassWaterDoors().length))
                && ((character.getTileX() >= 0) && (character.getTileX() < currentLevelMap.getGrassWaterDoors()[0].length)))
        {
            if ((currentLevelMap.getGrassWaterDoors()[character.getTileY()][character.getTileX()] == 1) | ((currentLevelMap.getGrassWaterDoors()[character.getTileY()][character.getTileX()] > 7)))
            {
                character.isInTallGrass = true;
            }
            else
            {
                character.isInTallGrass = false;
            }
            
            if (currentLevelMap.getGrassWaterDoors()[character.getTileY()][character.getTileX()] == 2)
            {
                character.isInWater = true;
            }
            else
            {
                character.isInWater = false;
            }
        }
        else
        {
            character.isInWater = false;
            character.isInTallGrass = false;
        }

    }

    

    private void resetNPCIDs()
    {
        for(int i = 0; i < NPCs.size(); i++)
        {
            if (NPCs.get(i).getID() == 23)
            {
                NPCs.get(i).setID(19);
            }
            else if (NPCs.get(i).getID() == 24)
            {
                NPCs.get(i).setID(20);
            }
            else if (NPCs.get(i).getID() == 25)
            {
                NPCs.get(i).setID(21);
            }
            else if (NPCs.get(i).getID() == 26)
            {
                NPCs.get(i).setID(22);
            }
            else if (NPCs.get(i).getID() == 31)
            {
                NPCs.get(i).setID(27);
            }
            else if (NPCs.get(i).getID() == 32)
            {
                NPCs.get(i).setID(28);
            }
            else if (NPCs.get(i).getID() == 33)
            {
                NPCs.get(i).setID(29);
            }
            else if (NPCs.get(i).getID() == 34)
            {
                NPCs.get(i).setID(30);
            }
            else if (NPCs.get(i).getID() == 39)
            {
                NPCs.get(i).setID(35);
            }
            else if (NPCs.get(i).getID() == 40)
            {
                NPCs.get(i).setID(36);
            }
            else if (NPCs.get(i).getID() == 41)
            {
                NPCs.get(i).setID(37);
            }
            else if (NPCs.get(i).getID() == 42)
            {
                NPCs.get(i).setID(38);
            }

        }
    }

    //Used for hit detection (walls)
    public boolean canAdvance(ITObject obj, Direction dir, int distanceDelta)
    {
        boolean clearToMove = true;
        int dx = 0;
        int dy = 16;
        int dy2 = 0;

        if (dir == Direction.UP)
        {
            dy -= distanceDelta;
            dy2 -= distanceDelta;
        }
        else if (dir == Direction.DOWN)
        {
            //dy = 16;
            dy += distanceDelta;
            dy2 += distanceDelta;
        }
        else if (dir == Direction.LEFT)
        {
            //dy = 16;
            dx -= distanceDelta;
        }
        else if (dir == Direction.RIGHT)
        {
            //dy = 16;
            dx += distanceDelta;
        }

        try
        {
            if (obstacleMatrix[calculateTileLoc(obj.getY() + dy)][calculateTileLoc(obj.getX() + dx)] == 0)
            {
                clearToMove = false;
            }
            else if(obstacleMatrix[calculateTileLoc(obj.getY() + dy2)][calculateTileLoc(obj.getX() + dx)] == 0)
            {
                clearToMove = false;
            }
        }
        catch(Exception e)
        {
            clearToMove = false;
        }

        return clearToMove;
    }

    public boolean canBulletAdvance(Bullet b)
    {
        boolean clearToMove = true;
        int newTileX = calculateTileLoc(((int) ((b.getX() + b.getDeltaX()) + 0.5f)));
        int newTileY = calculateTileLoc(((int) ((b.getY() + b.getDeltaY()) + 0.5f)));

        if (((newTileX >= 0) && (newTileX < obstacleMatrix[0].length)) && ((newTileY >= 0) && (newTileY < obstacleMatrix.length)))
        {
            if (obstacleMatrix[newTileY][newTileX] == 0)
            {
                clearToMove = false;
            }
        }
        else
        {
            clearToMove = false;
        }

        return clearToMove;
    }

    public boolean isPlayerVisible(int npcX, int npcY, Direction d, int viewDistance)
    {
        boolean spotted = false;
        Point dir = getDirectionVector(d);
        float distance = 0.0f;

        int x = (player.getX() + 20) - npcX;
        int y = (player.getY() + 20) - npcY;

        float normal = (float)Math.sqrt((double)((x * x) + (y * y)));

        float nX = x/normal;
        float nY = y/normal;

        float dotProduct = ((dir.x * nX) + (dir.y * nY));

        float angle = (float)Math.acos(dotProduct);

        if ((angle * 100) <= 35) //Player is in FOV
        {
            int dx = player.getX() - npcX;
            int dy = player.getY() - npcY;

            distance = (float)Math.sqrt((double)((dx * dx) + (dy * dy)));

            if (((int)distance) <= viewDistance) //Player is theoretically visible
            {
                float vX = dx/distance;
                float vY = dy/distance;
                
                if (!isPlayerConcealed(npcX, npcY, vX, vY, (int)distance)) //Player is absolutely visible
                {
                    spotted = true;
                }
            }
        }


        return spotted;
    }

    private boolean isPlayerConcealed(int x, int y, float vX, float vY, int distance)
    {
        byte[][] gwd = currentLevelMap.getGrassWaterDoors();

        boolean concealed = false;

        float checkX = x;
        float checkY = y;
        int traveled = 0;

        while ((traveled <= distance) && (concealed == false))
        {
            checkX += vX;
            checkY += vY;

            int zX = calculateTileLoc((int)(checkX + 0.5f));
            int zY = calculateTileLoc((int)(checkY + 0.5f));

            if (((zY >= 0) && (zY < obstacleMatrix.length)) && ((zX >= 0) && (zX < obstacleMatrix[0].length)))
            {
                if (obstacleMatrix[zY][zX] == 0)
                {
                    if (((zX ^ x) | (zY ^ y)) != 0)
                    {
                        concealed = true;
                    }
                }
                else if (gwd[zY][zX] == 3)
                {
                    visionXY.x = zX;
                    visionXY.y = zY;

                    if (!currentLevelMap.getDoorsHashMap().get(visionXY).isOpen())
                    {
                        concealed = true;
                    }
                }
            }

            float dx = checkX - x;
            float dy = checkY - y;

            traveled = (int)Math.sqrt((double)((dx * dx) + (dy * dy)));

        }

        return concealed;
    }

    private Point getDirectionVector(Direction d)
    {
        if (d == Direction.UP)
        {
            return _upVector;
        }
        else if (d == Direction.DOWN)
        {
            return _downVector;
        }
        else if (d == Direction.LEFT)
        {
            return _leftVector;
        }
        else if (d == Direction.RIGHT)
        {
            return _rightVector;
        }
        else //Default to zero vector
        {
            return _zeroVector;
        }
    }

    private Direction getDirectionFromVector(float vX, float vY)
    {
        float aX = Math.abs(vX);
        float aY = Math.abs(vY);
        float max = Math.max(aX, aY);

        if (aX == max)
        {
            if (vX > 0.0f)
            {
                return Direction.RIGHT;
            }
            if (vX < 0.0f)
            {
                return Direction.LEFT;
            }
        }
        else
        {
            if (vY > 0.0f)
            {
                return Direction.DOWN;
            }
            if (vY < 0.0f)
            {
                return Direction.UP;
            }
        }

        return Direction.DOWN;
    }


    private int calculateTileLoc(int x)
    {
        return ((x + 19) / 40);
    }

    //Detect Bullet collision with NPC
    public boolean bulletCollisionNPC(Bullet b)
    {
        boolean collision = false;

        if (fightingBoss == false)
        {
            for(int i = 0; i < NPCs.size(); i++)
            {
                if ((b.getTileX() == NPCs.get(i).getTileX()) && (b.getTileY() == NPCs.get(i).getTileY()))
                {
                    collision = true;
                    int health = NPCs.get(i).receiveDamage(b.getDamage());

                    if (health > 0)
                    {
                        if (b.getTranquilizer() == true)
                        {
                            NPCs.get(i).tranqTimeMillis = (b.staminaDamage * 1000);
                            NPCs.get(i).setStatus(NPCStatus.TRANQUILIZED_SLEEP);
                        }
                        else
                        {
                            if (NPCs.get(i).getStatus() != NPCStatus.TRANQUILIZED_SLEEP)
                            {
                                if (!NPCs.get(i).ally)
                                {
                                   NPCs.get(i).suspect(getDirectionFromVector(b.getDeltaX(), b.getDeltaY()));
                                }
                            }
                        }
                    }

                    updateNPCStatus();
                    break;
                    
                }
                
            }
        }
        else if (fightingBoss)
        {
            
            if ((b.getTileX() == boss.getTileX()) && (b.getTileY() == boss.getTileY()))
            {
                collision = true;

                int bdamage = b.getDamage();

                //Allow tranq weapons to do actual damage to bosses
                if (b.staminaDamage > 0)
                {
                    bdamage = b.staminaDamage;
                }

                //Account for close-range shotgun hits
                if (b.buckshot)
                {
                    if (b.distTraversed <= 160)
                    {
                        bdamage *= 2;
                        bdamage += 5;
                    }
                }

                //Close range bonus
                if ((b.distTraversed <= 80) && (bdamage > 0))
                {
                    bdamage += 10;
                }

                boss.receiveDamage(bdamage);

                boss.getPath().adjust();
            }
            
        }

        if (collision)
        {
            SFX.playSound(SFX.HIT);
        }

        return collision;
    }

    public void enemyKilled()
    {
        enemiesKilled++;
    }

    public boolean bulletCollisionPlayer(Bullet b)
    {
        boolean collision = false;

        if ((isPlayerProtected() == false) && (!paused))
        {
            if ((b.getTileX() == player.getTileX()) && (b.getTileY() == player.getTileY()))
            {
                collision = true;
                player.receiveDamage(b.getDamage());
                SFX.playSound(SFX.HIT);
            }
        }

        if ((collision) && (b.getDamage() > 0))
        {
            timesWounded++;
            protectPlayer();
        }

        return collision;
    }

    //Detect Knife collision with NPC
    private void knifeCollisionNPC()
    {
        if (fightingBoss == false)
        {
            for(int i = 0; i < NPCs.size(); i++)
            {
                if (player.getDirection() == Direction.UP)
                {
                    if ((player.getX() + 20 >= NPCs.get(i).getX()) && (player.getX() + 20 <= NPCs.get(i).getX() + 40))
                    {
                        if ((player.getY() + 20 - 1 <= NPCs.get(i).getY() + 40) &&
                                (player.getY() + 20 - 1 >= NPCs.get(i).getY()))
                        {
                            //collision = true;
                            NPCs.get(i).receiveDamage(knifedamage);

                            if ((NPCs.get(i).getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
                            {
                                if (!NPCs.get(i).ally)
                                {
                                   NPCs.get(i).suspect(player.getDirection());
                                }
                            }

                            updateNPCStatus();
                            break;
                        }
                    }
                }
                else if (player.getDirection() == Direction.DOWN)
                {
                    if ((player.getX() + 20 >= NPCs.get(i).getX()) && (player.getX() + 20 <= NPCs.get(i).getX() + 40))
                    {
                        if ((player.getY() + 37 + 1 >= NPCs.get(i).getY()) &&
                                ((player.getY() + 37 + 1 <= NPCs.get(i).getY() + 40)))
                        {
                            //collision = true;
                            NPCs.get(i).receiveDamage(knifedamage);

                            if ((NPCs.get(i).getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
                            {
                                if (!NPCs.get(i).ally)
                                {
                                   NPCs.get(i).suspect(player.getDirection());
                                }
                            }

                            updateNPCStatus();
                            break;
                        }
                    }
                }
                else if (player.getDirection() == Direction.LEFT)
                {
                    if ((player.getY() + 20 >= NPCs.get(i).getY()) && (player.getY() + 20 <= NPCs.get(i).getY() + 40))
                    {
                        if ((player.getX() - 1 <= NPCs.get(i).getX() + 40) &&
                                ((player.getX() + 0 - 1 >= NPCs.get(i).getX())))
                        {
                            //collision = true;
                            NPCs.get(i).receiveDamage(knifedamage);

                            if ((NPCs.get(i).getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
                            {
                                if (!NPCs.get(i).ally)
                                {
                                   NPCs.get(i).suspect(player.getDirection());
                                }
                            }

                            updateNPCStatus();
                            break;
                        }
                    }
                }
                else if (player.getDirection() == Direction.RIGHT)
                {
                    if ((player.getY() + 20 >= NPCs.get(i).getY()) && (player.getY() + 20 <= NPCs.get(i).getY() + 40))
                    {
                        if ((player.getX() + 37 + 1 >= NPCs.get(i).getX()) &&
                                ((player.getX() + 37 + 1 <= NPCs.get(i).getX() + 40)))
                        {
                            //collision = true;
                            NPCs.get(i).receiveDamage(knifedamage);

                            if ((NPCs.get(i).getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
                            {
                                if (!NPCs.get(i).ally)
                                {
                                   NPCs.get(i).suspect(player.getDirection());
                                }
                            }

                            updateNPCStatus();
                            break;
                        }
                    }
                }
            }
        }
        else if (fightingBoss == true)
        {
            if (player.getDirection() == Direction.UP)
            {
                if ((player.getX() + 20 >= boss.getX()) && (player.getX() + 20 <= boss.getX() + 40))
                {
                    if ((player.getY() + 20 - 1 <= boss.getY() + 40) &&
                            (player.getY() + 20 - 1 >= boss.getY()))
                    {
                        //collision = true;
                        boss.receiveDamage(knifedamage);

                    }
                }
            }
            else if (player.getDirection() == Direction.DOWN)
            {
                if ((player.getX() + 20 >= boss.getX()) && (player.getX() + 20 <= boss.getX() + 40))
                {
                    if ((player.getY() + 37 + 1 >= boss.getY()) &&
                            ((player.getY() + 37 + 1 <= boss.getY() + 40)))
                    {
                        //collision = true;
                        boss.receiveDamage(knifedamage);

                    }
                }
            }
            else if (player.getDirection() == Direction.LEFT)
            {
                if ((player.getY() + 20 >= boss.getY()) && (player.getY() + 20 <= boss.getY() + 40))
                {
                    if ((player.getX() - 1 <= boss.getX() + 40) &&
                            ((player.getX() + 0 - 1 >= boss.getX())))
                    {
                        //collision = true;
                        boss.receiveDamage(knifedamage);
                    }
                }
            }
            else if (player.getDirection() == Direction.RIGHT)
            {
                if ((player.getY() + 20 >= boss.getY()) && (player.getY() + 20 <= boss.getY() + 40))
                {
                    if ((player.getX() + 37 + 1 >= boss.getX()) &&
                            ((player.getX() + 37 + 1 <= boss.getX() + 40)))
                    {
                        //collision = true;
                        boss.receiveDamage(knifedamage);

                    }
                }
            }
        }        
    }

    private void updateNPCStatus()
    {
        for(int i = 0; i < NPCs.size(); i++)
        {
            if (NPCs.get(i).getCurrentHealth() <= 0)
            {
                NPCs.get(i).setStatus(NPCStatus.DEAD);
                //NPCs.remove(i);
            }
        }
    }

    public void checkPlayerStatus()
    {
        if (player.getCurrentHealth() <= 0)
        {
            this.movingUp = false;
            this.movingDown = false;
            this.movingLeft = false;
            this.movingRight = false;

            deaths++;
            setHUDMessage("Mission Failed", HUDMessageType.RESULT);
            displayStats("You have been killed in action.\nGame Over!\n");

            if (!restarting)
            {
                //restartLevel();
                respawn();
            }
        }
    }

    private Dialog displayStats(String title)
    {
        long timeElapsed = (System.currentTimeMillis() - startDate) + GameFileManager.playTime;

        String etStr = String.format("%d:%02d:%02d",
        TimeUnit.MILLISECONDS.toHours(timeElapsed),
        TimeUnit.MILLISECONDS.toMinutes(timeElapsed) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeElapsed)),        
        TimeUnit.MILLISECONDS.toSeconds(timeElapsed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeElapsed)));

        Dialog d = new Dialog(true);

        if (title.startsWith("THE END"))
        {
            d.add("You have reached the end of the game.");
            d.add("I hope you enjoyed it!");
            d.add("-Jim Miller\n");
        }

        d.add("Play Time: " + etStr);
        d.add("Enemies Killed: " + enemiesKilled);
        d.add("Deaths: " + deaths);
        d.add("Times Wounded: " + timesWounded);
        d.add("Alerts Triggered: " + alertsTriggered);
        d.add("Medkits Used: " + medkitsUsed);
        d.add("Bosses Defeated: " + bossesDefeated);        

        d.title = title;
        
        //this.displayDialog(d, title);
        
        return d;
    }

    private void respawn()
    {
        SLEEPTIME = 10;
        //SFX_OLD.stopMusic();
        SFX.playMusic(currentLevelMap.songIndex);

        //Turn off stat modifiers
        player.bodyArmor = false;
        player.gasMask = false;
        nightVision = false;

        player = new Player(playerCopy);
        playerCopy = new Player(player);

        equipItem();

        movingUp = false;
        movingDown = false;
        movingLeft = false;
        movingRight = false;

        firingWeapon = false;

        resetNPCIDs();

        playerSpotted = false;        

        for(int i = 0; i < bullets.size(); i++)
        {
            //bullets.remove(i);
            bullets.get(i).setStatus(false);
        }

        for(int i = 0; i < NPCs.size(); i++)
        {
            NPCs.get(i).setStatus(NPCStatus.DEAD);
        }

        for(int i = 0; i < spawns.size(); i++)
        {
            spawns.get(i).reset();
        }

        C4Queue.clear();

        //Close all doors
        Door[] doors = currentLevelMap.getDoors();
        for(int i = 0; i < doors.length; i++)
        {
            doors[i].forceClose();
        }

        currentLevel.respawnState();
        this.warpTo(false, playerLevMapX);
    }

    public boolean isPlayerAlive()
    {
        return (player.getCurrentHealth() > 0);
    }

    public boolean isBossAlive()
    {
        return (boss.getCurrentHealth() > 0);
    }

    public boolean isPlayerAttacking()
    {
        return playerAttacking;
    }

    public boolean isPlayerRecentlyWounded()
    {
        return player.isRecentlyWounded;
    }

    public void setPlayerNoLongerWounded()
    {
        player.isRecentlyWounded = false;
    }

    public void setPlayerAttacking(boolean a)
    {
        playerAttacking = a;
    }

    public boolean isPlayerMoving()
    {
        return playerMoving;
    }

    public void setPlayerMoving(boolean m)
    {
        playerMoving = m;
    }

    public void firingWeapon()
    {
        firingWeapon = true;
    }

    public void notFiringWeapon()
    {
        firingWeapon = false;
    }

    public boolean getFiringWeapon()
    {
        return firingWeapon;
    }

    public boolean isPlayerProtected()
    {
        return protectPlayer;
    }

    public void protectPlayer()
    {
        protectPlayer = true;
    }

    public void stopProtectingPlayer()
    {
        protectPlayer = false;
    }

    public boolean isPlayerSpotted()
    {
        return playerSpotted;
    }

    public void setPlayerSpotted()
    {
        if (!playerSpotted)
        {
            //Scripted alerts don't count...
            if (!currentLevelMap.getAlertMode())
            {
                startAlertMusic();
                alertsTriggered++;
                setHUDMessage("You have been spotted!", HUDMessageType.DANGER);
            }
        }
        
        playerSpotted = true;
    }

    public void setPlayerOutOfDetection()
    {
        playerSpotted = false;
    }

    public void drownPlayer()
    {
        player.receiveDamage(1);
    }

    public void setHUDMessage(String msg, HUDMessageType t)
    {
        //gf.setHUDMessage(msg, t);
        message = msg;
        mtype = t;
        lastMessageDate = System.currentTimeMillis();
    }

    public void resetHUDMessage()
    {
        message = null;
    }

    
    //For player path movement
    public void moveUp(float speed)
    {
        playerMoving = true;        

        if (player.getDirection() != Direction.UP)
        {
            player.changeDirection(Direction.UP);
            player.setID(1);
        }

        if (playerAnimationIterations <= 0)
        {
            if (player.getID() == 1)
            {
                player.setID(5);
            }
            else
            {
                player.setID(1);
            }

            playerAnimationIterations = 16; //6
        }
        else
        {
            playerAnimationIterations--;
        }

        if (canAdvance(player, player.getDirection(), 1) == true)
        {
            //player.setY(player.getY() - RUNSPEED);
            player.move(0, -1 * speed);
        }

        
        player.doorXY.x = player.getTileX();
        player.doorXY.y = player.getTileY();

        checkDoors(player, 1);

        checkObjectives();        

        checkItems();

        checkWaterAndGrass(player);

        player.checkDoorStatus();

        if (checkFutureWarps(Direction.UP))
        {
            lurchVector.x = 0;
            lurchVector.y = -1;
            playerOnPath = false;
            lurch = true;
        }
        
    }

    public void moveDown(float speed)
    {
        playerMoving = true;

        if (player.getDirection() != Direction.DOWN)
        {
            player.changeDirection(Direction.DOWN);
            player.setID(2);
        }

        if (playerAnimationIterations <= 0)
        {
            if (player.getID() == 2)
            {
                player.setID(6);
            }
            else
            {
                player.setID(2);
            }

            playerAnimationIterations = 16;//6
        }
        else
        {
            playerAnimationIterations--;
        }


        if (canAdvance(player, player.getDirection(), 1) == true)
        {
            //player.setY(player.getY() + RUNSPEED);
            player.move(0, 1 * speed);
        }


        player.doorXY.x = player.getTileX();
        player.doorXY.y = player.getTileY();
        checkDoors(player, 1);
        checkObjectives();
        checkItems();
        checkWaterAndGrass(player);

        player.checkDoorStatus();

        if (checkFutureWarps(Direction.DOWN))
        {
            lurchVector.x = 0;
            lurchVector.y = 1;
            playerOnPath = false;
            lurch = true;
        }
    }

    public void moveLeft(float speed)
    {
        playerMoving = true;

        if (player.getDirection() != Direction.LEFT)
        {
            player.changeDirection(Direction.LEFT);
            player.setID(3);
        }

        if (playerAnimationIterations <= 0)
        {
            if (player.getID() == 3)
            {
                player.setID(7);
            }
            else
            {
                player.setID(3);
            }

            playerAnimationIterations = 16;//6
        }
        else
        {
            playerAnimationIterations--;
        }

        if (canAdvance(player, player.getDirection(), 1) == true)
        {
            //player.setX(player.getX() - RUNSPEED);
            player.move(-1 * speed, 0);
        }

        player.doorXY.x = player.getTileX();
        player.doorXY.y = player.getTileY();
        checkDoors(player, 1);
        checkObjectives();
        checkItems();
        checkWaterAndGrass(player);

        player.checkDoorStatus();

        if (checkFutureWarps(Direction.LEFT))
        {
            lurchVector.x = -1;
            lurchVector.y = 0;
            playerOnPath = false;
            lurch = true;
        }
    }

    public void moveRight(float speed)
    {
        playerMoving = true;

        if (player.getDirection() != Direction.RIGHT)
        {
            player.changeDirection(Direction.RIGHT);
            player.setID(4);
        }

        if (playerAnimationIterations <= 0)
        {
            if (player.getID() == 4)
            {
                player.setID(8);
            }
            else
            {
                player.setID(4);                
            }

            playerAnimationIterations = 16;//6
        }
        else
        {
            playerAnimationIterations--;
        }


        if (canAdvance(player, player.getDirection(), 1) == true)
        {
            //player.setX(player.getX() + RUNSPEED);
            player.move(1 * speed, 0);
        }


        player.doorXY.x = player.getTileX();
        player.doorXY.y = player.getTileY();
        checkDoors(player, 1);
        checkObjectives();
        checkItems();
        checkWaterAndGrass(player);

        player.checkDoorStatus();

        if (checkFutureWarps(Direction.RIGHT))
        {
            lurchVector.x = 1;
            lurchVector.y = 0;
            playerOnPath = false;
            lurch = true;
        }
    }

    //End path movement functions

    public void moveUp()
    {
        playerMoving = true;

        if (player.getDirection() != Direction.UP)
        {
            player.changeDirection(Direction.UP);
            
            if (player.getStance() == Stance.UPRIGHT)
            {
                player.setID(1);
            }
            else
            {
                player.setID(88);
            }
        }

        if (playerAnimationIterations <= 0)
        {
            if (player.getID() == 1)
            {
                player.setID(5);
            }
            else if (player.getID() == 88)
            {
                player.setID(92);
            }
            else
            {
                if (player.getStance() == Stance.UPRIGHT)
                {
                    player.setID(1);
                }
                else
                {
                    player.setID(88);
                }
            }

            if (player.getStance() == Stance.UPRIGHT)
            {
                playerAnimationIterations = 6; //3
            }
            else
            {
                playerAnimationIterations = 12;
            }
            
        }
        else
        {
            playerAnimationIterations--;
        }

        if (player.getStance() == Stance.UPRIGHT)
        {
            if (canAdvance(player, player.getDirection(), RUNSPEED) == true)
            {
                //player.setY(player.getY() - RUNSPEED);
                player.move(upVectorRun);
            }
        }
        else
        {
            if (canAdvance(player, player.getDirection(), WALKSPEED) == true)
            {
                //player.setY(player.getY() - WALKSPEED);
                player.move(upVector);
            }
        }

        player.doorXY.x = player.getTileX();
        player.doorXY.y = player.getTileY();
        checkDoors(player, RUNSPEED);
        checkObjectives();
        checkWarps();
        checkItems();
        //checkTallGrass();
        checkWaterAndGrass(player);
        //checkWater(player);

        if (currentLevelMap.playerPath != null)
        {
            if (!playerOnPath)
            {
                int xpos, ypos;
                Waypoint swp = currentLevelMap.playerPath.getStartingWaypoint();
                xpos = swp.getXPos();
                ypos = swp.getYPos();
                if ((player.getTileX() == xpos) && (player.getTileY() == ypos))
                {
                    playerOnPath = true;
                }
            }
        }

        player.checkDoorStatus();
    }

    public void moveDown()
    {
        playerMoving = true;

        if (player.getDirection() != Direction.DOWN)
        {
            player.changeDirection(Direction.DOWN);

            if (player.getStance() == Stance.UPRIGHT)
            {
                player.setID(2);
            }
            else
            {
                player.setID(89);
            }

        }

        if (playerAnimationIterations <= 0)
        {
            if (player.getID() == 2)
            {
                player.setID(6);
            }
            else if (player.getID() == 89)
            {
                player.setID(93);
            }
            else
            {
                if (player.getStance() == Stance.UPRIGHT)
                {
                    player.setID(2);
                }
                else
                {
                    player.setID(89);
                }
            }

            if (player.getStance() == Stance.UPRIGHT)
            {
                playerAnimationIterations = 6; //3
            }
            else
            {
                playerAnimationIterations = 12;
            }
        }
        else
        {
            playerAnimationIterations--;
        }

        if (player.getStance() == Stance.UPRIGHT)
        {
            if (canAdvance(player, player.getDirection(), RUNSPEED) == true)
            {
                //player.setY(player.getY() + RUNSPEED);
                player.move(downVectorRun);
            }
        }
        else
        {
            if (canAdvance(player, player.getDirection(), WALKSPEED) == true)
            {
                //player.setY(player.getY() + WALKSPEED);
                player.move(downVector);
            }
        }

        player.doorXY.x = player.getTileX();
        player.doorXY.y = player.getTileY();
        checkDoors(player, RUNSPEED);
        checkObjectives();
        checkWarps();
        checkItems();
        //checkTallGrass();
        checkWaterAndGrass(player);

        if (currentLevelMap.playerPath != null)
        {
            if (!playerOnPath)
            {
                int xpos, ypos;
                Waypoint swp = currentLevelMap.playerPath.getStartingWaypoint();
                xpos = swp.getXPos();
                ypos = swp.getYPos();
                if ((player.getTileX() == xpos) && (player.getTileY() == ypos))
                {
                    playerOnPath = true;
                }
            }
        }

        player.checkDoorStatus();
    }

    public void moveLeft()
    {
        playerMoving = true;

        if (player.getDirection() != Direction.LEFT)
        {
            player.changeDirection(Direction.LEFT);

            if (player.getStance() == Stance.UPRIGHT)
            {
                player.setID(3);
            }
            else
            {
                player.setID(90);
            }
            
        }

        if (playerAnimationIterations <= 0)
        {
            if (player.getID() == 3)
            {
                player.setID(7);
            }
            else if (player.getID() == 90)
            {
                player.setID(94);
            }
            else
            {
                if (player.getStance() == Stance.UPRIGHT)
                {
                    player.setID(3);
                }
                else
                {
                    player.setID(90);
                }
            }

            if (player.getStance() == Stance.UPRIGHT)
            {
                playerAnimationIterations = 6; //3
            }
            else
            {
                playerAnimationIterations = 12;
            }
        }
        else
        {
            playerAnimationIterations--;
        }
        
        if (player.getStance() == Stance.UPRIGHT)
        {
            if (canAdvance(player, player.getDirection(), RUNSPEED) == true)
            {
                //player.setX(player.getX() - RUNSPEED);
                player.move(leftVectorRun);
            }
        }
        else
        {
            if (canAdvance(player, player.getDirection(), WALKSPEED) == true)
            {
                //player.setX(player.getX() - WALKSPEED);
                player.move(leftVector);
            }
        }

        player.doorXY.x = player.getTileX();
        player.doorXY.y = player.getTileY();
        checkDoors(player, RUNSPEED);
        checkObjectives();
        checkWarps();
        checkItems();
        //checkTallGrass();
        checkWaterAndGrass(player);

        if (currentLevelMap.playerPath != null)
        {
            if (!playerOnPath)
            {
                int xpos, ypos;
                Waypoint swp = currentLevelMap.playerPath.getStartingWaypoint();
                xpos = swp.getXPos();
                ypos = swp.getYPos();
                if ((player.getTileX() == xpos) && (player.getTileY() == ypos))
                {
                    playerOnPath = true;
                }
            }
        }

        player.checkDoorStatus();
    }

    public void moveRight()
    {
        playerMoving = true;
        
        if (player.getDirection() != Direction.RIGHT)
        {
            player.changeDirection(Direction.RIGHT);

            if (player.getStance() == Stance.UPRIGHT)
            {
                player.setID(4);
            }
            else
            {
                player.setID(91);
            }
        }

        if (playerAnimationIterations <= 0)
        {
            if (player.getID() == 4)
            {
                player.setID(8);
            }
            else if (player.getID() == 91)
            {
                player.setID(95);
            }
            else
            {
                if (player.getStance() == Stance.UPRIGHT)
                {
                    player.setID(4);
                }
                else
                {
                    player.setID(91);
                }
            }

            if (player.getStance() == Stance.UPRIGHT)
            {
                playerAnimationIterations = 6; //3
            }
            else
            {
                playerAnimationIterations = 12;
            }
        }
        else
        {
            playerAnimationIterations--;
        }

        if (player.getStance() == Stance.UPRIGHT)
        {
            if (canAdvance(player, player.getDirection(), RUNSPEED) == true)
            {
                //player.setX(player.getX() + RUNSPEED);
                player.move(rightVectorRun);
            }
        }
        else
        {
            if (canAdvance(player, player.getDirection(), WALKSPEED) == true)
            {
                //player.setX(player.getX() + WALKSPEED);
                player.move(rightVector);
            }
        }

        player.doorXY.x = player.getTileX();
        player.doorXY.y = player.getTileY();
        checkDoors(player, RUNSPEED);
        checkObjectives();
        checkWarps();
        checkItems();
        //checkTallGrass();
        checkWaterAndGrass(player);

        if (currentLevelMap.playerPath != null)
        {
            if (!playerOnPath)
            {
                int xpos, ypos;
                Waypoint swp = currentLevelMap.playerPath.getStartingWaypoint();
                xpos = swp.getXPos();
                ypos = swp.getYPos();
                if ((player.getTileX() == xpos) && (player.getTileY() == ypos))
                {
                    playerOnPath = true;
                }
            }
        }

        player.checkDoorStatus();
    }

    public void resetPlayerAnimation()
    {
        if (player.getStance() == Stance.UPRIGHT)
        {
            if (player.getDirection() == Direction.UP)
            {
                player.setID(1);
            }
            else if (player.getDirection() == Direction.DOWN)
            {
                player.setID(2);
            }
            else if (player.getDirection() == Direction.LEFT)
            {
                player.setID(3);
            }
            else if (player.getDirection() == Direction.RIGHT)
            {
                player.setID(4);
            }
        }
        else
        {
            if (player.getDirection() == Direction.UP)
            {
                player.setID(88);
            }
            else if (player.getDirection() == Direction.DOWN)
            {
                player.setID(89);
            }
            else if (player.getDirection() == Direction.LEFT)
            {
                player.setID(90);
            }
            else if (player.getDirection() == Direction.RIGHT)
            {
                player.setID(91);
            }
        }

        //playerAnimationIterations = 3;
    }

    public void changeStance()
    {
        player.changeStance(forceprone);

        resetPlayerAnimation();

    }

//    public void playerHeal()
//    {
//        if (player.heal())
//        {
//            medkitsUsed++;
//        }
//    }

    public int getNumMedkits()
    {
        return player.getNumHealthKits();
    }

    public int getMaxNumMedkits()
    {
        return player.getMaxNumHealthKits();
    }

    public int getCardKeyLevel()
    {
        return player.getCardKey();
    }

    //The player fires a bullet
    public void playerAttack(int shot)
    {
        if (player.getAmmo() > 0)
        {
            playerAttacking = true;
            //System.out.println("Player attacks! Bullets in Queue: " + bullets.size());

            int tX = -1;
            int tY = -1;

            if (player.getDirection() == Direction.UP)
            {
                tX = player.getX();
                tY = player.getY() + (9 * 40);
            }
            else if (player.getDirection() == Direction.DOWN)
            {
                tX = player.getX();
                tY = player.getY() - (9 * 40);
            }
            else if (player.getDirection() == Direction.LEFT)
            {
                tY = player.getY();
                tX = player.getX() + (9 * 40);
            }
            else if (player.getDirection() == Direction.RIGHT)
            {
                tY = player.getY();
                tX = player.getX() - (9 * 40);
            }

            short bulletSprite;
            
            if (player.isGrenadeEquipped())
            {
                bulletSprite = 191;
            }
            else if (player.isC4Equipped())
            {
                bulletSprite = 368;
            }
            else
            {
                bulletSprite = 181;
            }

            boolean isSuppressed = player.getWeapon().isSuppressed();
            int rank = player.getWeapon().rank;

            if (player.isC4Equipped())
            {
                C4Queue.add(new Explosion(player.getX(), player.getY(), rank));
                player.subtractAmmo();
            }
            else if (!player.isShotgunEquipped())
            {
                Bullet b = player.attack(bulletSprite, tX, tY, shot, rank);
                if (b != null)
                {
                    bullets.add(b);
                }
                else
                {
                    //Melee (Knife)
                    knifeCollisionNPC();
                }
            }
            else
            {
                Bullet b1 = player.attack(bulletSprite, tX, tY, 10, 1);
                Bullet b2 = player.attack(bulletSprite, tX, tY, 70, 1);
                Bullet b3 = player.attack(bulletSprite, tX, tY, 90, 1);
                Bullet b4 = player.attack(bulletSprite, tX, tY, -70, 1);
                Bullet b5 = player.attack(bulletSprite, tX, tY, -90, 1);

                b1.buckshot = true;
                b2.buckshot = true;
                b3.buckshot = true;
                b4.buckshot = true;
                b5.buckshot = true;

                bullets.add(b1);
                bullets.add(b2);
                bullets.add(b3);
                bullets.add(b4);
                bullets.add(b5);

                player.subtractAmmo();

            }

            if (!isSuppressed)
            {
                SFX.playSound(SFX.RIFLE_GUNSHOT);
            }
            else
            {
                if ((player.getWeapon().getType() == ItemType.PISTOL) ||
                        (player.getWeapon().getType() == ItemType.TRANQ_PISTOL) || (player.getWeapon().getType() == ItemType.SMG)
                        || (player.getWeapon().getType() == ItemType.ASSAULT_RIFLE))
                {
                    SFX.playSound(SFX.SILENCED_GUNSHOT);
                }
            }

            if ((isSuppressed == false) && (playerSpotted == false))
            {
                if (audibleAttack())
                {
                    //Scripted Alert Modes do not count
                    if (!currentLevelMap.getAlertMode())
                    {
                        startAlertMusic();
                        alertsTriggered++;
                    }

                    playerSpotted = true;
                    setHUDMessage("You have been spotted!", HUDMessageType.DANGER);
                }

            }

        }

        if (player.getDirection() == Direction.UP)
        {
            player.setID(9);
        }
        else if (player.getDirection() == Direction.DOWN)
        {
            if (player.getWeapon().getType() == ItemType.KNIFE)
            {
                player.setID(10);
            }
            else if ((player.getWeapon().getType() == ItemType.PISTOL) || (player.getWeapon().getType() == ItemType.TRANQ_PISTOL))
            {
                player.setID(13);
            }
            else if (player.getWeapon().getType() == ItemType.ASSAULT_RIFLE)
            {
                player.setID(16);
            }
            else if (player.getWeapon().getType() == ItemType.SMG)
            {
                player.setID(188);
            }
            else if (player.getWeapon().getType() == ItemType.SHOTGUN)
            {
                player.setID(194);
            }
        }
        else if (player.getDirection() == Direction.LEFT)
        {
            if (player.getWeapon().getType() == ItemType.KNIFE)
            {
                player.setID(11);
            }
            else if ((player.getWeapon().getType() == ItemType.PISTOL) || (player.getWeapon().getType() == ItemType.TRANQ_PISTOL))
            {
                player.setID(14);
            }
            else if (player.getWeapon().getType() == ItemType.ASSAULT_RIFLE)
            {
                player.setID(17);
            }
            else if (player.getWeapon().getType() == ItemType.SMG)
            {
                player.setID(189);
            }
            else if (player.getWeapon().getType() == ItemType.SHOTGUN)
            {
                player.setID(195);
            }
        }
        else if (player.getDirection() == Direction.RIGHT)
        {
            if (player.getWeapon().getType() == ItemType.KNIFE)
            {
                player.setID(12);
            }
            else if ((player.getWeapon().getType() == ItemType.PISTOL) || (player.getWeapon().getType() == ItemType.TRANQ_PISTOL))
            {
                player.setID(15);
            }
            else if (player.getWeapon().getType() == ItemType.ASSAULT_RIFLE)
            {
                player.setID(18);
            }
            else if (player.getWeapon().getType() == ItemType.SMG)
            {
                player.setID(190);
            }
            else if (player.getWeapon().getType() == ItemType.SHOTGUN)
            {
                player.setID(196);
            }
        }

        playerAnimationIterations = 0;
    }

    public void meleeAttack()
    {
        playerAttacking = true;
        //System.out.println("Player attacks! Bullets in Queue: " + bullets.size());

        Explosion c4 = C4Queue.poll();
        if (c4 != null)
        {
            explosions.add(c4);
            explosionCollision(c4.x, c4.y, false, c4.rank);
            SFX.playSound(SFX.EXPLOSION);
        }

        //Melee (Knife)
        knifeCollisionNPC();

        if (player.getDirection() == Direction.UP)
        {
            player.setID(9);
        }
        else if (player.getDirection() == Direction.DOWN)
        {
            player.setID(10);
        }
        else if (player.getDirection() == Direction.LEFT)
        {
            player.setID(11);
        }
        else if (player.getDirection() == Direction.RIGHT)
        {
            player.setID(12);
        }

        playerAnimationIterations = 0;
    }

    private boolean audibleAttack()
    {
        boolean audible = false;

        for(int i = 0; i < NPCs.size(); i++)
        {
            if (!NPCs.get(i).isFriendly())
            {
                audible = true;
                break;
            }
        }

        return audible;
    }

    //An NPC fires a bullet
    public void NPCAttack(Bullet b)
    {
        if (b != null)
        {
            SFX.playSound(SFX.RIFLE_GUNSHOT);
            bullets.add(b);
        }
    }

    public boolean NPCMelee(NPC n)
    {
        if ((n.getTileX() == player.getTileX()) && (n.getTileY() == player.getTileY()))
        {
            int damage = n.getWeaponDamage() / 2;

            damage = (damage > 0) ? damage : 15;

            player.receiveDamage(damage);
            timesWounded++;
            protectPlayer();
            //checkPlayerStatus();

            return true;
        }

        return false;
    }

    public boolean isAREquipped()
    {
        return player.isAREquipped();
    }

    public boolean isSMGEquipped()
    {
        return player.isSMGEquipped();
    }

    public boolean isShotgunEquipped()
    {
        return player.isShotgunEquipped();
    }

    public boolean isGrenadeEquipped()
    {
        return player.isGrenadeEquipped();
    }

    public boolean isPistolEquipped()
    {
        return player.isPistolEquipped();
    }

    public boolean isMedkitEquipped()
    {
        return player.isMedkitEquipped();
    }

    /*
    public boolean isPistolSilenced()
    {
        return player.isPistolSilenced();
    }
     * 
     */

    public boolean isTranqWatchEquipped()
    {
        return player.isTranqEquipped();
    }

    /*
    public boolean isKnifeEquipped()
    {
        return player.isKnifeEquipped();
    }
     * 
     */

    public boolean isGasMaskEquipped()
    {
        return (player.getItem().getType() == ItemType.GASMASK);
    }

    public String getSelectedWeaponStr()
    {
        String wstr = "No Weapon";
        if (player.stuff.selectedWeapon != null)
        {
            wstr = player.stuff.selectedWeapon.toString();
        }
        return wstr;
    }

    public String getSelectedItemStr()
    {
        return player.getItem().toString(player);
    }

    public int getAmmo()
    {
        return player.getAmmo();
    }

    public int getMaxAmmo()
    {
        if (player.getWeapon() != null)
        {
            return player.getWeapon().getMaxAmmo();
        }
        return 0;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Path getPlayerPath()
    {
        return currentLevelMap.playerPath;
    }

    public int getPlayerHealth()
    {
        return player.getCurrentHealth();
    }

    public int getPlayerMaxHealth()
    {
        return player.getMaxHealth();
    }

    public int getPlayerX()
    {
        return player.getX();
    }

    public int getPlayerY()
    {
        return player.getY();
    }

    public int getPlayerTileX()
    {
        return player.getTileX();
    }

    public int getPlayerTileY()
    {
        return player.getTileY();
    }

    public Direction getPlayerDirection()
    {
        return player.getDirection();
    }

    public Stance getPlayerStance()
    {
        return player.getStance();
    }

    public short getPlayerSprite()
    {
        return player.getID();
    }

    public int getPlayerOxygen()
    {
        return player.getOxygen();
    }

    public void setPlayerOxygen(int o2)
    {
        player.setOxygen(o2);
    }

    public boolean isPlayerInWater()
    {
        return player.isInWater;
    }

    public boolean isPlayerInTallGrass()
    {
        return player.isInTallGrass;
    }

    public byte[][] getShadows()
    {
        return currentLevelMap.getGrassWaterDoors();
    }

    public boolean isPlayerInWaterOrTallGrass()
    {
        return (player.isInWater | player.isInTallGrass);
    }

    public ArrayList<NPC> getNPCs()
    {
        return NPCs;
    }

    public ArrayList<SecurityCamera> getCameras()
    {
        return cameras;
    }

    public ArrayList<Spawn> getSpawns()
    {
        return spawns;
    }

    private void startBossMusic()
    {
        //soundPlayer.stopSong();
        //soundPlayer = new SoundPlayer(20);
        //SFX_OLD.stopMusic();
        SFX.playMusic(SFX.BOSS_MUSIC);
    }

    private void startAlertMusic()
    {
        if (!SFX.alertDefault)
        {
            //SFX_OLD.stopMusic();
            SFX.playSound(SFX.SIREN);
            SFX.playMusic(SFX.ALERT_MUSIC);
        }
    }

    public void bossDefeated()
    {
        if (this.isPlayerAlive())
        {
            this.protectPlayer();
            //SFX_OLD.stopMusic();
            SFX.playMusic(currentLevelMap.songIndex);

            player.objectives++;
            currentLevel.removeObjective(new Objective((short)0, -1, -1, playerLevMapX, "Kill " + boss.name), playerLevMapX);

            displayDialog(boss.getPostDialog(), "Boss defeated", false);

            //Remove all travelling bullets
            for(int i = 0; i < bullets.size(); i++)
            {
                try
                {
                    bullets.get(i).nullify();
                }
                catch (Exception e)
                {
                    //Do nothing
                }
            }

            fightingBoss = false;
            bossesDefeated++;
            this.stopProtectingPlayer();
        }
    }

    public Boss getBoss()
    {
        return boss;
    }

    public boolean fightBoss()
    {
        return fightingBoss;
    }

    public void setBossFight()
    {
        fightingBoss = true;
        startBossMusic();
    }

    public ArrayList<Item> getItems()
    {
        return currentLevelMap.getItems();
    }

    public void addBullet(Bullet b)
    {
        bullets.add(b);
    }

    public ArrayList<Bullet> getBullets()
    {
        return bullets;
    }
    
    public void goToNextLevel()
    {
        if (currentLevel.getID() < levels.size() - 1)
        {
            playerLevMapX = 0;

            player.setX(levels.getNextStartX());
            player.setY(levels.getNextStartY());

            warpTo(true, playerLevMapX);
        }
    }

    public byte[][] getObstacleMatrix()
    {
        return obstacleMatrix;
    }

    public void displayDialog(Dialog d, String title, boolean applyTitle)
    {
        if (dialogEnabled)
        {
            if (d.isValid())
            {
                if (applyTitle)
                {
                    for(int i = 0; i < d.dialog.size(); i++)
                    {
                        d.dialog.get(i).speaker = title;
                    }
                }

                itd = d;
                d.title = title;
                inDialog = true;
                paused = true;
                //JOptionPane.showMessageDialog(null, d.toString(), title, JOptionPane.PLAIN_MESSAGE);
                //d.fastForward();

                this.firingWeapon = false;
            }
        }
    }

    public void closeDialog()
    {
        inDialog = false;
        paused = false;
        itd.fastForward();
    }

    public Dialog getDialog()
    {
        return itd;
    }

    public void changeItem(int change)
    {
        player.changeItem(change);

        equipItem();
    }

    /*
    public void changeWeapon(int change)
    {
        player.changeWeapon(change);
    }
     * 
     */
    public void changeWeaponOrItem(int code)
    {
        if (code > 3)
        {
            changeItem(1);
        }
        else
        {
            player.changeWeapon(code);
        }
    }

    public void useMedkit()
    {
        if (player.heal())
        {
            medkitsUsed++;
            setHUDMessage("Recovered 50 health", HUDMessageType.USED_MEDKIT);
        }
        else
        {
            if (player.getNumHealthKits() == 0)
            {
                setHUDMessage("Out of Medkits", HUDMessageType.NO_USE);
            }
            else
            {
                setHUDMessage("Health is Maxed Out", HUDMessageType.NO_USE);
            }
        }
    }

    private void equipItem()
    {
        if (player.getItem().getType() == ItemType.BODY_ARMOR)
        {
            equipBodyArmor();
        }
        if (player.getItem().getType() == ItemType.GASMASK)
        {
            equipGasMask();
        }
        if (player.getItem().getType() == ItemType.NVG)
        {
            equipNVG();
        }
        if (player.getItem().getType() == ItemType.MEDKIT)
        {
            equipMedkit();
        }
        if ((player.getItem().getType() == ItemType.CARDKEY_1) || ((player.getItem().getType() == ItemType.CARDKEY_2))
           || (player.getItem().getType() == ItemType.CARDKEY_3) || (player.getItem().getType() == ItemType.CARDKEY_4)
           || (player.getItem().getType() == ItemType.CARDKEY_5))
        {
            equipCardKey();
        }
        if (player.getItem().getType() == ItemType.MINE_DETECTOR)
        {
            equipMineDetector();
        }
    }

    private void equipBodyArmor()
    {
        player.bodyArmor = true;
        this.nightVision = false;
        player.gasMask = false;
        mineDetector = false;
    }

    private void equipGasMask()
    {
        player.bodyArmor = false;
        this.nightVision = false;
        player.gasMask = true;
        mineDetector = false;
    }

    private void equipNVG()
    {
        player.bodyArmor = false;
        this.nightVision = true;
        player.gasMask = false;
        mineDetector = false;
    }

    private void equipMedkit()
    {
        player.bodyArmor = false;
        this.nightVision = false;
        player.gasMask = false;
        mineDetector = false;
    }

    private void equipCardKey()
    {
        player.bodyArmor = false;
        this.nightVision = false;
        player.gasMask = false;
        mineDetector = false;
    }

    private void equipMineDetector()
    {
        mineDetector = true;
        player.bodyArmor = false;
        this.nightVision = false;
        player.gasMask = false;
    }

    public void explosion(int x, int y, boolean fromBoss, int rank)
    {
        explosions.add(new Explosion(x, y, rank));
        explosionCollision(x, y, fromBoss, rank);
        SFX.playSound(SFX.EXPLOSION);
    }

    public ArrayList<Explosion> getExplosions()
    {
        return explosions;
    }

    private void explosionCollision(int x, int y, boolean fromBoss, int rank)
    {
        int dx;
        int dy;
        double distance;

        if (!fromBoss)
        {
            for (int i = 0; i < NPCs.size(); i++)
            {
                NPC xnpc = NPCs.get(i);

                dx = xnpc.getX() - x;
                dy = xnpc.getY() - y;

                dx *= dx;
                dy *= dy;

                distance = Math.sqrt(dx + dy);

                if (distance <= 50.0)
                {
                    if (xnpc.juggernaut)
                    {
                        int xdmg = 150 + (rank * 150) + (rank * 10);
                        xnpc.receiveDamage(xdmg);
                    }
                    else
                    {
                        xnpc.receiveDamage(80 + (15 * rank));
                    }
                }

                if (audibleAttack())
                {
                    setPlayerSpotted();
                }
            }

            if (fightingBoss)
            {
                dx = boss.getX() - x;
                dy = boss.getY() - y;

                dx *= dx;
                dy *= dy;

                distance = Math.sqrt(dx + dy);

                if (distance <= 20.0)
                {
                    boss.receiveDamage(45 + (10 * rank));
                }
                else if (distance <= 50.0)
                {
                    boss.receiveDamage(25 + (10 * rank));
                }
            }
        }
        
        //Check to see if the player is in the blast radius
        dx = player.getX() - x;
        dy = player.getY() - y;

        dx *= dx;
        dy *= dy;

        distance = Math.sqrt(dx + dy);

        if (distance <= 20.0)
        {
            if (fromBoss)
            {
                if ((isPlayerProtected() == false) && (!paused))
                {
                    player.receiveDamage(75);
                    timesWounded++;
                    protectPlayer();
                }
            }
            else
            {
                player.receiveDamage(80 + (10 * rank));
                timesWounded++;
                protectPlayer();
            }
            
        }
        else if (distance <= 50.0)
        {
            if (fromBoss)
            {
                if ((isPlayerProtected() == false) && (!paused))
                {
                    player.receiveDamage(38);
                    timesWounded++;
                    protectPlayer();
                }
            }
            else
            {
                player.receiveDamage(60 + (10 * rank));
                timesWounded++;
                protectPlayer();
            }
            
        }

    }

    public ArrayList<Objective> getAllObjectives()
    {
        return currentLevel.getAllObjectives();
    }

    public ArrayList<Objective> getAllObjectiveCopies()
    {
        return currentLevel.getAllObjectiveCopies();
    }

    public ArrayList<Objective> getLocalObjectives()
    {
        if (currentLevel.getObjectives(playerLevMapX) != null)
        {
            return currentLevel.getObjectives(playerLevMapX);
        }
        else
        {
            return null;
        }
    }

    public void printObjectives()
    {
        Dialog d = new Dialog(true);

        d.title = "Objectives";

        System.out.println("--Objectives--");

        int actual = 1;

        for(int i = 0; i < currentLevel.getAllObjectives().size(); i++)
        {
            System.out.println("" + (i+1) + ": " + currentLevel.getAllObjectives().get(i).name);
            if (!currentLevel.getAllObjectives().get(i).name.endsWith("_"))
            {
                d.add("" + actual + ": " + currentLevel.getAllObjectives().get(i).name);
                actual++;
            }
        }

        if (currentLevel.getAllObjectives().isEmpty())
        {
            System.out.println("Completed");
            d.add("None remaining");
        }        

        System.out.println("\n");

        this.displayDialog(d, "Mission Objectives", true);

    }

    public int getRoomIndex()
    {
        return playerLevMapX;
    }

    public void promptExitToMainMenu()
    {
        paused = true;
        
        //if (gf.displayPrompt("Are you sure you want to exit the game and return to the main menu?"))
        {
            running = false;
            returnToMainMenu(null);
        }

        paused = false;
    }    

    public void clearMovementStack()
    {
        if (glrt != null)
        {
            glrt.clearMovementStack();
        }
    }

    public void render()
    {
        try
        {
            glrt.frameRendering();
        }
        catch(Exception e)
        {
            
        }
    }

    public void save()
    {
        System.out.println("Saving game...");
        GameFileManager.saveGame(this, currentLevel, playerCopy, glrt);
    }

    public void saveAs()
    {
        GameFileManager.filepath = "";
        GameFileManager.saveGame(this, currentLevel, playerCopy, glrt);
    }

    private void showErrorMessage(String msg)
    {
        JOptionPane.showMessageDialog(null, msg, "The Endling's Artifice", JOptionPane.ERROR_MESSAGE);
        returnToMainMenu(null);
    }

    public Vertex[][] getVertices()
    {
        return currentLevelMap.vertices;
    }

    public void regenerateHealth()
    {
        int ch = player.getCurrentHealth();
        int mh = ((player.getMaxHealth() * 4) / 5);
        if (ch < mh)
        {
            player.setHealth(ch+1);
        }
    }

    public void actionTaken()
    {
        //TODO: Add more support
        for(int i = 0; i < currentLevelMap.getItems().size(); i++)
        {
            currentLevelMap.getItems().get(i).touched = false;
        }
        checkItems();
    }
}
