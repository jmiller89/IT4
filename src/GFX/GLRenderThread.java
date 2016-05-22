/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GFX;
import IT4.Boss;
import IT4.Bullet;
import IT4.Dialog;
import IT4.DialogSection; //This is actually used, ignore the warning
import IT4.Direction;
import IT4.Door;
import IT4.Explosion;
import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;

import IT4.Game;
import IT4.Intruder;
import IT4.Item;
import IT4.ItemType;
import IT4.NPC;
import IT4.NPCStatus;
import IT4.Objective;
import IT4.SecurityCamera;
import IT4.Stance;
import java.util.Iterator;
import java.util.Stack;
import org.lwjgl.opengl.Display;

/**
 *
 * @author Jim
 */
public class GLRenderThread extends Canvas implements GameWindowCallback
{
    
    //Used to prevent guns from firing full auto
    private boolean triggerPulled;
    private boolean stanceToggled;
    private boolean itemUsed;
    private boolean actionTaken;
    private boolean primaryWeaponSwitched;
    private boolean secondaryWeaponSwitched;
    private boolean explosiveWeaponSwitched;
    //private boolean weaponSwitched2;
    private boolean itemSwitched;
    //private boolean itemSwitched2;
    private boolean pauseToggled;
    private boolean meleePerformed;
    private boolean saveRequested;
    private boolean showObjs;
    private boolean dispConsole;
    private boolean showRadarPressed;
    //private boolean showRadar;

    private boolean closing = false;

    //To communicate with game data
    private Game game;

    //private Sprite playerSprite;
    private short waterTrail;
    private short bloodSprite;
    private short explosionSprite;

    private short radarSolid = 183;
    private short playerSprite = 184;
    private short NPCSprite = 185;

    private short white = 297;
    //private short borderbox = 298;

    private short vconeup = 304;
    private short vconedown = 305;
    private short vconeleft = 306;
    private short vconeright = 307;

    private short c4 = 368;

    private short rain = 376;
    private short snow = 375;

    private short[][] tileMap;
    private byte[][] obstacleMatrix;
    private byte[][] shadows;
    private short defaultGroundCover;
    private short black;
    private short securityDoor;

    private static final short[] shadowmap = {0, 0, 0, 0, 301, 302, 1, 303, 301, 302, 1, 303};

    private static short COLSIZE = 25;

    private Stack<Direction> moveStack;

    private ArrayList<Explosion> explosions = new ArrayList<Explosion>(1);
    private ArrayList<NPC> NPCs = new ArrayList<NPC>(1);
    private Door[] doors = null;
    private ArrayList<SecurityCamera> cameras = new ArrayList<SecurityCamera>(1);
    private ArrayList<Objective> localObjectives = new ArrayList<Objective>(1);
    private ArrayList<Item> items = new ArrayList<Item>(1);
    private ArrayList<Bullet> bullets = new ArrayList<Bullet>(1);
    private Iterator<Explosion> C4Iter;
    private Boss boss = null;

    /** The window that is being used to render the game */
    private GameWindow window;
    /** True if the fire key has been released */

    /** The normal title of the window */
    private String windowTitle = "The Endling's Artifice";

    private static final String SPRITESHEET = "Sprites/spritesheet.png";
    private static final String TEXT = "Sprites/IT3Text.png"; //was .gif
    private static final String OVERLAY = "Sprites/overlay.png";
    private LWJGLSprite spritesheet;
    public static final int NUMSPRITES = 510;

    private LWJGLSprite text;

    private LWJGLSprite overlay;

    private IT3String[] strings = new IT3String[64];
    private byte stringsIndex = 0;

    private static float width = 0.0f;
    private static float height = 0.0f;

    private short dialogPage = 0;
    private String nextArrow;

    public ParticleSystem weatherParticleSystem;

    /**
     * Construct our render thread and set it running.
     *
     * @param renderingType The type of rendering to use (should be one of the constants from ResourceFactory)
     */
    public GLRenderThread(Game g, int renderingType)
    {
        game = g;

        waterTrail = 87;
        bloodSprite = 108;
        
        tileMap = game.getTileMap();

        defaultGroundCover = game.getDefaultGroundCover();
        black = 0;
        securityDoor = 59;
        explosionSprite = 193;

        triggerPulled = false;
        stanceToggled = false;
        itemUsed = false;
        primaryWeaponSwitched = false;
        secondaryWeaponSwitched = false;
        explosiveWeaponSwitched = false;
        itemSwitched = false;
        actionTaken = false;
        //weaponSwitched2 = false;
        //itemSwitched2 = false;
        pauseToggled = false;
        meleePerformed = false;
        saveRequested = false;
        showObjs = false;
        dispConsole = false;
        showRadarPressed = false;
        //showRadar = true;

        setVisible(true);

        game.startNPCThread();
        game.startBulletThread();
        //game.startGameThread(); //Game thread has been moved

        moveStack = new Stack<Direction>();



        // create a window based on a chosen rendering method
        ResourceFactory.get().setRenderingType(renderingType);
        window = ResourceFactory.get().getGameWindow(game);

        window.setResolution(800, 600);//was 1024, 768
        window.setGameWindowCallback(this);
        window.setTitle(windowTitle);

        for(int i = 0; i < strings.length; i++)
        {
            strings[i] = new IT3String();
        }

        nextArrow = String.valueOf((char)187);
        nextArrow += nextArrow;
        nextArrow += String.valueOf((char)187);

        weatherParticleSystem = new ParticleSystem(256);

        window.startRendering();
    }

    public void setSpriteMap()
    {
        tileMap = game.getTileMap();
    }

    public void setDefaultGroundCover()
    {
        defaultGroundCover = game.getDefaultGroundCover();
    }

    //The following four methods are used to cull out of view tiles
    //Was 12 for X, 9 for Y
    /*
    private int getMinX(int x, int cx)
    {
        if (cx > 400)
        {
            if ((x - 19) < 0)
            {
                return 0;
            }
            else
            {
                return (x - 19);
            }
        }
        else
        {
            if ((x - 12) < 0)
                return 0;
            return (x - 12);
        }
    }
     *
     */

    private int getMinXY(int y, int cy)
    {
        if (cy > 280)
        {
            if ((y - 15) < 0)
            {
                return 0;
            }
            else
            {
                return (y - 15);
            }
        }
        else
        {
            if ((y - 9) < 0)
                return 0;
            return (y - 9);
        }
    }

    /*
    private int getMaxX(int x, int max, int cx)
    {
        if (cx < 400)
        {
            if ((x + 20) > max)
            {
                return max;
            }
            else
            {
                return (x + 20);
            }
        }
        else
        {
            if ((x + 12) > max)
                return max;
            return (x + 12);
        }
    }
     * 
     */

    private int getMaxXY(int y, int max, int cy)
    {
        if (cy < 280)
        {
            if ((y + 16) > max)
            {
                return max;
            }
            else
            {
                return (y + 16);
            }
        }
        else
        {
            if ((y + 9) > max)
                return max;
            return (y + 9);
        }
    }

    //The following four methods are used to cull out of view tiles for radar
    private int getRMinX(int x)
    {
        //was 23
        if ((x - 13) < 0)
        {
            return 0;
        }
        else
        {
            return (x - 13);
        }
    }

    private int getRMinY(int y)
    {
        //Was 16
        if ((y - 10) < 0)
        {
            return 0;
        }
        else
        {
            return (y - 10);
        }
    }

    private int getRMaxX(int x, int max)
    {
        //Was 21
        if ((x + 13) > max)
        {
            return max;
        }
        else
        {
            return (x + 13);
        }
    }

    private int getRMaxY(int y, int max)
    {
        //Was 16
        if ((y + 10) > max)
        {
            return max;
        }
        else
        {
            return (y + 10);
        }
    }

    public static float getTX(float index, float wdt)
    {
        return (index / COLSIZE) * wdt;
    }

    public void initialize()
    {
        spritesheet = (LWJGLSprite)ResourceFactory.get().getSprite(SPRITESHEET);
        System.out.println("Width = " + spritesheet.texture.getWidth());
        System.out.println("Height = " + spritesheet.texture.getHeight());
        width = spritesheet.texture.getWidth();
        height = spritesheet.texture.getHeight();

        text = (LWJGLSprite)ResourceFactory.get().getSprite(TEXT);
        overlay = (LWJGLSprite)ResourceFactory.get().getSprite(OVERLAY);
    }

    public void clearMovementStack()
    {
        moveStack.clear();
    }

    private void resumeMovement()
    {
        if (!moveStack.isEmpty())
        {
            Direction d = moveStack.pop();
            
            if (d == Direction.UP)
            {
                game.movingUp = true;
                game.movingDown = false;
                game.movingLeft = false;
                game.movingRight = false;
                moveStack.push(Direction.UP);
            }
            else if (d == Direction.DOWN)
            {
                game.movingDown = true;
                game.movingUp = false;
                game.movingLeft = false;
                game.movingRight = false;
                moveStack.push(Direction.DOWN);
            }
            else if (d == Direction.LEFT)
            {
                game.movingLeft = true;
                game.movingDown = false;
                game.movingUp = false;
                game.movingRight = false;
                moveStack.push(Direction.LEFT);
            }
            else if (d == Direction.RIGHT)
            {
                game.movingRight = true;
                game.movingDown = false;
                game.movingLeft = false;
                game.movingUp = false;
                moveStack.push(Direction.RIGHT);
            }
        }
    }

    private void addString(String s, int x, int y, int size, float r, float g, float b, float a, boolean forward)
    {
        if (stringsIndex < strings.length)
        {
            if (s.length() > 0)
            {
                strings[stringsIndex].text = s;
                strings[stringsIndex].x = x;
                strings[stringsIndex].y = y;
                strings[stringsIndex].textSize = size;
                strings[stringsIndex].r = r;
                strings[stringsIndex].g = g;
                strings[stringsIndex].b = b;
                strings[stringsIndex].a = a;
                strings[stringsIndex].visible = true;
                strings[stringsIndex].forward = forward;
                stringsIndex++;
            }
        }
    }

    private void addString(String s, int x, int y)
    {
        if (stringsIndex < strings.length)
        {
            if (s.length() > 0)
            {
                strings[stringsIndex].text = s;
                strings[stringsIndex].x = x;
                strings[stringsIndex].y = y;
                strings[stringsIndex].textSize = 10;
                strings[stringsIndex].r = 1.0f;
                strings[stringsIndex].g = 1.0f;
                strings[stringsIndex].b = 1.0f;
                strings[stringsIndex].a = 1.0f;
                strings[stringsIndex].visible = true;
                strings[stringsIndex].forward = true;
                stringsIndex++;
            }
        }
    }

    private void resetStrings()
    {
        for(int i = 0; i < stringsIndex; i++)
        {
            strings[i].reset();
        }

        stringsIndex = 0;
    }

    private void drawDialogBackground(int numRows, float startPos)
    {
        // store the current model matrix
        GL11.glPushMatrix();

        // bind to the appropriate texture for this sprite
        //spritesheet.texture.bind();

        // translate to the right location and prepare to draw
        GL11.glTranslatef(0, 0, 0);
        GL11.glColor3f(0.0f,0.0f,0.0f);

        //Transparancy:
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_DST_ALPHA);

        // draw a quad textured to match the sprite
        GL11.glBegin(GL11.GL_QUADS);
        {
            //float tx,tx2,ty,ty2;
            float ax,bx;
            ax = 0.0f; //was * 10
            bx = 600.0f; //was * 10

            float ymax = (numRows + 2) * 16.0f;
            if (startPos > 0)
            {
                ymax = 600.0f;
            }

            //int row,col;
            //row = white / COLSIZE;
            //col = white - (row * COLSIZE);

            //tx = getTX(col, height);
            //tx2 = getTX(col + 1, height);
            //ty = getTX(row, width);
            //ty2 = getTX(row + 1, width);

            //Draw tile
            //GL11.glTexCoord2f(tx, ty);
            GL11.glVertex2f(ax, startPos);
            //GL11.glTexCoord2f(tx, ty2);
            GL11.glVertex2f(ax, ymax);
            //GL11.glTexCoord2f(tx2, ty2);
            GL11.glVertex2f(bx,ymax);
            //GL11.glTexCoord2f(tx2, ty);
            GL11.glVertex2f(bx,startPos);
        }
        GL11.glEnd();

        // restore the model view matrix to prevent contamination
        GL11.glPopMatrix();
    }

    /**
     * Notification that a frame is being rendered. Responsible for
     * running game logic and rendering the scene.
     */
    public void frameRendering()
    {
        try
        {
            tileMap = game.getTileMap();
            defaultGroundCover = game.getDefaultGroundCover();
            NPCs = game.getNPCs();
            boss = game.getBoss();
            doors = game.getDoors();
            cameras = game.getCameras();
            items = game.getItems();
            localObjectives = game.getLocalObjectives();
            explosions = game.getExplosions();
            bullets = game.getBullets();
            C4Iter = game.C4Queue.iterator();
            obstacleMatrix = game.getObstacleMatrix();
            shadows = game.getShadows();
        }
        catch(Exception e)
        {
            System.out.println("Can't render null stuff");
            if (!game.running)
            {                                
                if (!closing)
                {
                    closing = true;
                    Display.destroy();
                    windowClosed();
                }
            }
            return;
        }

        resetStrings();

        int PlayerX = game.getPlayerX();
        int PlayerY = game.getPlayerY();
        int PlayerTileX = game.getPlayerTileX();
        int PlayerTileY = game.getPlayerTileY();
        int centerX = 7 * 40;
        int centerY = 7 * 40;

        int maxX = tileMap[0].length * 40;
        int maxY = tileMap.length * 40;

        float r_ = 1.0f;
        float g_ = 1.0f;
        float b_ = 1.0f;


        if ((PlayerX - 280) < 0)
        {
            centerX += (PlayerX - 280);
        }
        else if ((PlayerX + 320) > maxX)
        {
            if (maxX >= 600)
            {
                centerX += (320 - (maxX - PlayerX));
            }
        }

        if ((PlayerY - 280) < 0)
        {
            centerY += (PlayerY - 280);
        }
        else if ((PlayerY + 320) > maxY)
        {
            if (maxY >= 600)
            {
                centerY += (320 - (maxY - PlayerY));
            }
        }

        // store the current model matrix
        GL11.glPushMatrix();
        GL11.glColor3f(1,1,1);

        // bind to the appropriate texture for this sprite
        spritesheet.texture.bind();

        // translate to the right location and prepare to draw
        GL11.glTranslatef(0, 0, 0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);        

        // draw a quad textured to match the sprites
        GL11.glBegin(GL11.GL_QUADS);
        {
            if (game.darkness)
            {
                if (!game.nightVision)
                {
                    GL11.glColor3f(0, 0, 0);
                    r_ = 0;
                    g_ = 0;
                    b_ = 0;
                }
            }

            if (game.nightVision)
            {
                GL11.glColor3f(0, 1.0f, 0);//Red makes a good thermal effect, blue is creepy
                r_ = 0;
                g_ = 1.0f;
                b_ = 0;
            }


            //Texture coordinates
            float tx,tx2,ty,ty2;

            /*
             *  int row,col;
                row = buf[i] >> 4;
                col = buf[i] - (row << 4);

                tx = getTX(col, 16, height);
                tx2 = getTX(col + 1, 16, height);
                ty = getTX(row, 16, width);
                ty2 = getTX(row + 1, 16, width);

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(bx, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(bx, y+16);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(ax,y+16);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(ax,y);
             */
            //Draw doors
            if (doors != null)
            {
                //Save off the red, green, and blue values               
                for(int i = 0; i < doors.length; i++)
                {
                    int x,y,mx,my;
                    x = ((doors[i].getX()) - PlayerX + (centerX));
                    y = ((doors[i].getY()) - PlayerY + (centerY));
                    mx = (((doors[i].getX() + 40)) - PlayerX + (centerX));
                    my = (((doors[i].getY() + 40)) - PlayerY + (centerY));
                    
                    if (doors[i].isDark())
                    {
                        int row,col;
                        row = black / COLSIZE;
                        col = black - (row * COLSIZE);                        

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);
                    }
                    else
                    {
                        int row,col;
                        row = defaultGroundCover / COLSIZE;
                        col = defaultGroundCover - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);                        
                    }


                    //Draw tile
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);

                    //Draw shadow
                    int row, col;                    
                    GL11.glColor4f(0, 0, 0, 0.4f);
                    row = 302 / COLSIZE;
                    col = 302 - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);


                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);

                    //Reset the color
                    //GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glColor4f(r_, g_, b_, 1.0f);
                    
                    
                    row = doors[i].getID() / COLSIZE;
                    col = doors[i].getID() - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);


                    x = ((doors[i].getX()) - PlayerX + (centerX)) + doors[i].getOpenPos();
                    mx = (((doors[i].getX() + 40)) - PlayerX + (centerX)) + doors[i].getOpenPos();

                    //Draw door
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);

                    if (game.getDoors()[i].getSecuritylevel() > 0)
                    {
                        x = ((doors[i].getX()) - PlayerX + (centerX));
                        mx = (((doors[i].getX() + 40)) - PlayerX + (centerX));

                        row = securityDoor / COLSIZE;
                        col = securityDoor - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);

                        
                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);
                    }


                }
            }

            //Draw the background
            for(int j = getMinXY(PlayerTileY, centerY); j < getMaxXY(PlayerTileY, tileMap.length, centerY); j++)
            {
                for(int i = getMinXY(PlayerTileX, centerX); i < getMaxXY(PlayerTileX, tileMap[0].length, centerX); i++)
                {

                    if ((tileMap[j][i] != 57) && (tileMap[j][i] != 58)) //Don't draw Doors here
                    {
                        int x,y,mx,my;
                        x = ((i*40) - PlayerX + (centerX));
                        y = ((j*40) - PlayerY + (centerY));
                        mx = (((i + 1) *40) - PlayerX + (centerX));
                        my = (((j + 1) *40) - PlayerY + (centerY));

                        int row,col;
                        row = defaultGroundCover / COLSIZE;
                        col = defaultGroundCover - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);


                        //Draw tiles
                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);

                        if ((tileMap[j][i] != defaultGroundCover) && (tileMap[j][i] != 45))
                        {

                            row = tileMap[j][i] / COLSIZE;
                            col = tileMap[j][i] - (row * COLSIZE);

                            tx = getTX(col, height);
                            tx2 = getTX(col + 1, height);
                            ty = getTX(row, width);
                            ty2 = getTX(row + 1, width);


                            GL11.glTexCoord2f(tx, ty);
                            GL11.glVertex2f(x, y);
                            GL11.glTexCoord2f(tx, ty2);
                            GL11.glVertex2f(x, my);
                            GL11.glTexCoord2f(tx2, ty2);
                            GL11.glVertex2f(mx,my);
                            GL11.glTexCoord2f(tx2, ty);
                            GL11.glVertex2f(mx,y);
                        }

                        if (shadows[j][i] > 3)
                        {
                            //if (!game.darkness && !game.nightVision)
                            //{
                                short st = shadowmap[shadows[j][i]];
                                GL11.glColor4f(0, 0, 0, 0.4f);
                                if (st > 0)
                                {
                                    if (st != 1)
                                    {
                                        row = st / COLSIZE;
                                        col = st - (row * COLSIZE);

                                        tx = getTX(col, height);
                                        tx2 = getTX(col + 1, height);
                                        ty = getTX(row, width);
                                        ty2 = getTX(row + 1, width);


                                        GL11.glTexCoord2f(tx, ty);
                                        GL11.glVertex2f(x, y);
                                        GL11.glTexCoord2f(tx, ty2);
                                        GL11.glVertex2f(x, my);
                                        GL11.glTexCoord2f(tx2, ty2);
                                        GL11.glVertex2f(mx,my);
                                        GL11.glTexCoord2f(tx2, ty);
                                        GL11.glVertex2f(mx,y);
                                    }
                                    else
                                    {
                                        row = (shadowmap[shadows[j][i] - 1]) / COLSIZE;
                                        col = (shadowmap[shadows[j][i] - 1]) - (row * COLSIZE);

                                        tx = getTX(col, height);
                                        tx2 = getTX(col + 1, height);
                                        ty = getTX(row, width);
                                        ty2 = getTX(row + 1, width);


                                        GL11.glTexCoord2f(tx, ty);
                                        GL11.glVertex2f(x, y);
                                        GL11.glTexCoord2f(tx, ty2);
                                        GL11.glVertex2f(x, my);
                                        GL11.glTexCoord2f(tx2, ty2);
                                        GL11.glVertex2f(mx,my);
                                        GL11.glTexCoord2f(tx2, ty);
                                        GL11.glVertex2f(mx,y);

                                        row = (shadowmap[shadows[j][i] - 2]) / COLSIZE;
                                        col = (shadowmap[shadows[j][i] - 2]) - (row * COLSIZE);

                                        tx = getTX(col, height);
                                        tx2 = getTX(col + 1, height);
                                        ty = getTX(row, width);
                                        ty2 = getTX(row + 1, width);


                                        GL11.glTexCoord2f(tx, ty);
                                        GL11.glVertex2f(x+10, y);
                                        GL11.glTexCoord2f(tx, ty2);
                                        GL11.glVertex2f(x+10, my);
                                        GL11.glTexCoord2f(tx2, ty2);
                                        GL11.glVertex2f(mx,my);
                                        GL11.glTexCoord2f(tx2, ty);
                                        GL11.glVertex2f(mx,y);                                        
                                    }
                                }
                                //GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                                GL11.glColor4f(r_, g_, b_, 1.0f);
                            //}
                        }
                    }
                }
            }


            //Draw the items
            for(int i = 0; i < items.size(); i++)
            {
                if (items.get(i) != null)
                {
                    int row,col;
                    row = items.get(i).getID() / COLSIZE;
                    col = items.get(i).getID() - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);


                    int ix, iy;
                    ix = items.get(i).getX();
                    iy = items.get(i).getY();

                    int x,y,mx,my;
                    x = ((ix) - PlayerX + (centerX));
                    y = ((iy) - PlayerY + (centerY));
                    mx = x + 40;
                    my = y + 40;

                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);

                    if (x < 561)
                    {
                        addString(items.get(i).toString(null), x, y+30);
                    }
                    //g.drawString(items.get(i).toString(), (items.get(i).getX() - PlayerX + (centerX)), (items.get(i).getY() - PlayerY + (centerY)) + 45);
                }
            }

            //Draw tall grass on top of items and stuff
            for(int j = getMinXY(PlayerTileY, centerY); j < getMaxXY(PlayerTileY, tileMap.length, centerY); j++)
            {
                for(int i = getMinXY(PlayerTileX, centerX); i < getMaxXY(PlayerTileX, tileMap[0].length, centerX); i++)
                {
                    if (tileMap[j][i] == 45)
                    {
                        int x,y,mx,my;
                        x = ((i*40) - PlayerX + (centerX));
                        y = ((j*40) - PlayerY + (centerY));
                        mx = x + 40;
                        my = y + 40;

                        int row,col;
                        row = 45 / COLSIZE;
                        col = 45 - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);


                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);
                    }
                }
            }

            //Draw the LocalObjectives
            if (localObjectives != null)
            {
                for(int i = 0; i < localObjectives.size(); i++)
                {
                    if (localObjectives.get(i) != null)
                    {
                        //localObjectives.get(i).getSprite().draw(g, (localObjectives.get(i).getX() - PlayerX + (centerX)), (localObjectives.get(i).getY() - PlayerY + (centerY)));
                        int row,col;
                        row = localObjectives.get(i).getID() / COLSIZE;
                        col = localObjectives.get(i).getID() - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);


                        int ix, iy;
                        ix = localObjectives.get(i).getX();
                        iy = localObjectives.get(i).getY();

                        int x,y,mx,my;
                        x = ((ix) - PlayerX + (centerX));
                        y = ((iy) - PlayerY + (centerY));
                        mx = x + 40;
                        my = y + 40;

                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);
                        if (x < 561)
                        {
                            addString(localObjectives.get(i).toString(), x - 10, y + 30);
                        }
                        //g.drawString(localObjectives.get(i).toString(), (localObjectives.get(i).getX() - PlayerX + (centerX)) - 12, (localObjectives.get(i).getY() - PlayerY + (centerY)) + 45);
                    }
                }
            }

            while (C4Iter.hasNext())
            {
                Explosion xC4 = C4Iter.next();
                int row,col;
                row = c4 / COLSIZE;
                col = c4 - (row * COLSIZE);

                tx = getTX(col, height);
                tx2 = getTX(col + 1, height);
                ty = getTX(row, width);
                ty2 = getTX(row + 1, width);


                int ix, iy;
                ix = xC4.x;
                iy = xC4.y;

                int x,y,mx,my;
                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);
            }

            //Draw the NPCs
            for (int i = 0; i < NPCs.size(); i++)
            {
                if (NPCs.get(i) != null)
                {
                    //NPCs.get(i).getSprite().draw(g, (NPCs.get(i).getX() - PlayerX + (centerX)), (NPCs.get(i).getY() - PlayerY + (centerY)));
                    int nx = NPCs.get(i).getX();
                    int ny = NPCs.get(i).getY();
                    int nid = NPCs.get(i).getID();
                    
                    int x,y,mx,my;
                    x = (nx - PlayerX + (centerX));
                    y = (ny - PlayerY + (centerY));
                    mx = x + 40;
                    my = y + 40;

                    int row,col;
                    row = nid / COLSIZE;
                    col = nid - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);


                    //Draw tile
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);

                    if (i < game.getNPCs().size())
                    {
                        if (game.getNPCs().get(i).isRecentlyWounded == true)
                        {
                            row = bloodSprite / COLSIZE;
                            col = bloodSprite - (row * COLSIZE);

                            tx = getTX(col, height);
                            tx2 = getTX(col + 1, height);
                            ty = getTX(row, width);
                            ty2 = getTX(row + 1, width);


                            //Draw tile
                            GL11.glTexCoord2f(tx, ty);
                            GL11.glVertex2f(x, y);
                            GL11.glTexCoord2f(tx, ty2);
                            GL11.glVertex2f(x, my);
                            GL11.glTexCoord2f(tx2, ty2);
                            GL11.glVertex2f(mx,my);
                            GL11.glTexCoord2f(tx2, ty);
                            GL11.glVertex2f(mx,y);
                        }
                    }

                    if ((NPCs.get(i).getStatus() == NPCStatus.SLEEP) || (NPCs.get(i).getStatus() == NPCStatus.TRANQUILIZED_SLEEP))
                    {
                        //g.setColor(Color.darkGray);
                        if (x < 580)
                        {
                            addString("Z", x+15, y-13);
                        }
                        //g.drawString("Z", (NPCs.get(i).getX() - PlayerX + (centerX)) + 16, (NPCs.get(i).getY() - PlayerY + (centerY)));
                        //g.drawString("Z", (NPCs.get(i).getX() - PlayerX + (centerX)) + 17, (NPCs.get(i).getY() - PlayerY + (centerY)));
                        //g.drawString("Z", (NPCs.get(i).getX() - PlayerX + (centerX)) + 16, (NPCs.get(i).getY() - PlayerY + (centerY)) + 1);
                    }

                    if ((NPCs.get(i).isInWater) | (NPCs.get(i).isInTallGrass))
                    {
                        row = waterTrail / COLSIZE;
                        col = waterTrail - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);


                        //Draw tile
                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);
                    }
                }
            }

            //Draw the boss
            if (game.fightBoss())
            {
                if (boss != null)
                {
                    int nx = boss.getX();
                    int ny = boss.getY();
                    int nid = boss.getID();

                    int x,y,mx,my;
                    x = (nx - PlayerX + (centerX));
                    y = (ny - PlayerY + (centerY));
                    mx = x + 40;
                    my = y + 40;

                    int row,col;
                    row = nid / COLSIZE;
                    col = nid - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);


                    //Draw tile
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);

                    if (boss.isRecentlyWounded)
                    {
                        row = bloodSprite / COLSIZE;
                        col = bloodSprite - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);


                        //Draw tile
                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);
                    }

                    if ((boss.isInWater) | (boss.isInTallGrass))
                    {
                        row = waterTrail / COLSIZE;
                        col = waterTrail - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);


                        //Draw tile
                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);
                    }                    
                }
            }

            //Draw the Player
            if (game.isPlayerAlive())
            {
                if ((!game.isPlayerInWaterOrTallGrass()) || (game.getPlayerStance() != Stance.PRONE))
                {
                    //game.getPlayerSprite().draw(g, centerX, centerY);
                    short ps = game.getPlayerSprite();

                    int row,col;
                    row = ps / COLSIZE;
                    col = ps - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);


                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(centerX, centerY);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(centerX, centerY + 40);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(centerX + 40, centerY + 40);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(centerX + 40, centerY);
                }
            }
            else
            {
                if (!moveStack.empty())
                {
                    moveStack.clear();
                }
            }

            //Draw a water trail over the player if applicable
            if ((game.isPlayerInWaterOrTallGrass()))
            {
                int row,col;
                row = waterTrail / COLSIZE;
                col = waterTrail - (row * COLSIZE);

                tx = getTX(col, height);
                tx2 = getTX(col + 1, height);
                ty = getTX(row, width);
                ty2 = getTX(row + 1, width);


                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(centerX, centerY);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(centerX, centerY + 40);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(centerX + 40, centerY + 40);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(centerX + 40, centerY);
            }

            //Draw blood spatter on player
            if (game.isPlayerRecentlyWounded() == true)
            {
                int row,col;
                row = bloodSprite / COLSIZE;
                col = bloodSprite - (row * COLSIZE);

                tx = getTX(col, height);
                tx2 = getTX(col + 1, height);
                ty = getTX(row, width);
                ty2 = getTX(row + 1, width);


                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(centerX, centerY);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(centerX, centerY + 40);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(centerX + 40, centerY + 40);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(centerX + 40, centerY);
            }

            //Draw the Security Cameras
            for (int i = 0; i < cameras.size(); i++)
            {
                if (cameras.get(i) != null)
                {
                    int nx = cameras.get(i).getX();
                    int ny = cameras.get(i).getY();
                    int nid = cameras.get(i).getID();

                    int x,y,mx,my;
                    x = (nx - PlayerX + (centerX));
                    y = (ny - PlayerY + (centerY));
                    mx = x + 40;
                    my = y + 40;

                    int row,col;
                    row = nid / COLSIZE;
                    col = nid - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);


                    //Draw tile
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);

                }
            }

            //Draw all Bullets
            game.drawingBullets = true;
            for (int i = 0; i < bullets.size(); i++)
            {
                if (i < bullets.size())
                {
                    try
                    {
                        int row,col;
                        row = bullets.get(i).getID() / COLSIZE;
                        col = bullets.get(i).getID() - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);


                        int ix, iy;
                        ix = bullets.get(i).getX();
                        iy = bullets.get(i).getY();

                        int x,y,mx,my;
                        x = ((ix) - PlayerX + (centerX));
                        y = ((iy) - PlayerY + (centerY));
                        mx = x + 40;
                        my = y + 40;

                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);
                    }
                    catch(Exception e)
                    {

                    }
                }
            }            

            for (int i = 0; i < explosions.size(); i++)
            {
                int row,col;
                row = explosionSprite / COLSIZE;
                col = explosionSprite - (row * COLSIZE);

                tx = getTX(col, height);
                tx2 = getTX(col + 1, height);
                ty = getTX(row, width);
                ty2 = getTX(row + 1, width);

                int ix, iy, x, y, mx, my;
                ix = explosions.get(i).x;
                iy = explosions.get(i).y;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                ix = explosions.get(i).x0;
                iy = explosions.get(i).y0;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                ix = explosions.get(i).x1;
                iy = explosions.get(i).y1;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                ix = explosions.get(i).x2;
                iy = explosions.get(i).y2;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                ix = explosions.get(i).x3;
                iy = explosions.get(i).y3;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                ix = explosions.get(i).x4;
                iy = explosions.get(i).y4;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                ix = explosions.get(i).x5;
                iy = explosions.get(i).y5;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                ix = explosions.get(i).x6;
                iy = explosions.get(i).y6;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                ix = explosions.get(i).x7;
                iy = explosions.get(i).y7;

                x = ((ix) - PlayerX + (centerX));
                y = ((iy) - PlayerY + (centerY));
                mx = x + 40;
                my = y + 40;

                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);
            }
            game.drawingBullets = false;

            int row,col;
            row = white / COLSIZE;
            col = white - (row * COLSIZE);

            tx = getTX(col, height);
            tx2 = getTX(col + 1, height);
            ty = getTX(row, width);
            ty2 = getTX(row + 1, width);


            if (game.gas)
            {
                GL11.glColor4f(1.0f, 1.0f, 0, 0.5f);
                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(-10.0f, -10.0f);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(-10.0f, 610.0f);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(810.0f,610);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(810.0f,-10.0f);
            }

            if (game.midnight)
            {
                if (!game.nightVision)
                {
                    GL11.glColor4f(0, 0, 0, 0.9f);
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(-10.0f, -10.0f);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(-10.0f, 610.0f);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(810.0f,610);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(810.0f,-10.0f);
                }
            }

            if (game.semiDarkness)
            {
                if (!game.nightVision)
                {
                    GL11.glColor4f(0, 0, 0, 0.7f);
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(-10.0f, -10.0f);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(-10.0f, 610.0f);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(810.0f,610);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(810.0f,-10.0f);
                }
            }

            if (game.snow)
            {
                row = snow / COLSIZE;
                col = snow - (row * COLSIZE);

                float fix = 0.001f;

                tx = getTX(col, height) + fix;
                tx2 = getTX(col + 1, height) - fix;
                ty = getTX(row, width) + fix;
                ty2 = getTX(row + 1, width) - fix;

                GL11.glColor4f(0.85f, 0.85f, 0.85f, 0.7f);

                //Draw Particles
                weatherParticleSystem.snow();
                for(int i = 0; i < weatherParticleSystem.particles.length; i++)
                {
                    float pX = weatherParticleSystem.particles[i].x;
                    float pY = weatherParticleSystem.particles[i].y;

                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(pX, pY);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(pX, pY + 40);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(pX + 40, pY + 40);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(pX + 40, pY);
                }
            }
            else if (game.rain)
            {
                row = rain / COLSIZE;
                col = rain - (row * COLSIZE);

                float fix = 0.001f;

                tx = getTX(col, height) + fix;
                tx2 = getTX(col + 1, height) - fix;
                ty = getTX(row, width) + fix;
                ty2 = getTX(row + 1, width) - fix;

                GL11.glColor4f(0.85f, 0.85f, 0.85f, 0.7f);

                //Draw Particles
                weatherParticleSystem.rain();
                for(int i = 0; i < weatherParticleSystem.particles.length; i++)
                {
                    float pX = weatherParticleSystem.particles[i].x;
                    float pY = weatherParticleSystem.particles[i].y;

                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(pX, pY);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(pX, pY + 40);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(pX + 40, pY + 40);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(pX + 40, pY);
                }
            }
                        
            
            //Draw health bars
            /*
            row = white / COLSIZE;
            col = white - (row * COLSIZE);

            tx = getTX(col, height);
            tx2 = getTX(col + 1, height);
            ty = getTX(row, width);
            ty2 = getTX(row + 1, width);


            int x,y,mx,my,maxx;
            x = 0;
            y = 0;
            mx = x + game.getPlayerHealth();
            my = y + 16;
            maxx = x + game.getPlayerMaxHealth();
            GL11.glColor3f(0, 1.0f, 0.7f);

            //Draw healthbar
            GL11.glTexCoord2f(tx, ty);
            GL11.glVertex2f(x, y);
            GL11.glTexCoord2f(tx, ty2);
            GL11.glVertex2f(x, my);
            GL11.glTexCoord2f(tx2, ty2);
            GL11.glVertex2f(mx,my);
            GL11.glTexCoord2f(tx2, ty);
            GL11.glVertex2f(mx,y);

            //Draw border
            //row = borderbox / COLSIZE;
            //col = borderbox - (row * COLSIZE);

            //tx = getTX(col, height);
            //tx2 = getTX(col + 1, height);
            //ty = getTX(row, width);
            //ty2 = getTX(row + 1, width);


            GL11.glColor4f(0, 0, 0, 0.5f);
            //GL11.glTexCoord2f(tx, ty);
            GL11.glVertex2f(x, y);
            //GL11.glTexCoord2f(tx, ty2);
            GL11.glVertex2f(x, my);
            //GL11.glTexCoord2f(tx2, ty2);
            GL11.glVertex2f(maxx,my);
            //GL11.glTexCoord2f(tx2, ty);
            GL11.glVertex2f(maxx,y);

            if ((game.isPlayerInWater()) | (game.gas))
            {
                x = maxx + 16;
                mx = x + game.getPlayerOxygen();
                maxx = x + 100;

                row = white / COLSIZE;
                col = white - (row * COLSIZE);

                tx = getTX(col, height);
                tx2 = getTX(col + 1, height);
                ty = getTX(row, width);
                ty2 = getTX(row + 1, width);


                GL11.glColor3f(0, 0.7f, 1.0f);
                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                //row = borderbox / COLSIZE;
                //col = borderbox - (row * COLSIZE);

                //tx = getTX(col, height);
                //tx2 = getTX(col + 1, height);
                //ty = getTX(row, width);
                //ty2 = getTX(row + 1, width);


                GL11.glColor4f(0, 0, 0, 0.5f);
                //GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                //GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                //GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(maxx,my);
                //GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(maxx,y);

                String o2 = "Oxygen";
                addString(o2, x, 12, 12, 1.0f, 1.0f, 1.0f, 1.0f, true);
            }
            */

            int x,y,mx,my,maxx;
            if (game.fightBoss() & (boss != null))
            {
                //Draw health bars
                row = white / COLSIZE;
                col = white - (row * COLSIZE);

                tx = getTX(col, height);
                tx2 = getTX(col + 1, height);
                ty = getTX(row, width);
                ty2 = getTX(row + 1, width);


                x = 0;
                y = 0;
                mx = x + (boss.getCurrentHealth() >> 2);
                my = y + 16;
                maxx = x + (boss.getMaxHealth() >> 2);
                GL11.glColor3f(1.0f, 0.9f, 0.0f);

                //Draw healthbar
                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

                //Draw border
                //row = borderbox / COLSIZE;
                //col = borderbox - (row * COLSIZE);

                //tx = getTX(col, height);
                //tx2 = getTX(col + 1, height);
                //ty = getTX(row, width);
                //ty2 = getTX(row + 1, width);


                GL11.glColor4f(0, 0, 0, 0.5f);
                //GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                //GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                //GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(maxx,my);
                //GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(maxx,y);
                addString(boss.dispName, 0, 16, 12, 1.0f, 0, 0, 1.0f, true);
            }
        }

        GL11.glColor3f(0, 0, 0);
        GL11.glEnd();

        // restore the model view matrix to prevent contamination
        GL11.glPopMatrix();



        
        GL11.glPushMatrix();
        overlay.texture.bind();
        GL11.glColor3f(0, 0, 0);
        GL11.glTranslatef(0, 0, 0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); //GL_ONE_MINUS_SRC_ALPHA
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glColor3f(0.1f, 0.1f, 0.1f);
            GL11.glTexCoord2f(0, 0);
            GL11.glVertex2f(600, 0);
            GL11.glTexCoord2f(0, 1.0f);
            GL11.glVertex2f(1000, 0);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex2f(1000, 800);
            GL11.glTexCoord2f(1.0f, 0);
            GL11.glVertex2f(600, 800);
        }
        GL11.glEnd();
        
        // restore the model view matrix to prevent contamination
        GL11.glPopMatrix();
        




        //Health and Oxygen bars
        GL11.glPushMatrix();
        spritesheet.texture.bind();
        //GL11.glColor3f(0, 0, 0);
        GL11.glTranslatef(0, 0, 0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA); //GL_ONE_MINUS_SRC_ALPHA
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glColor3f(0, 0, 0);

            //Draw health bars
            int _row = white / COLSIZE;
            int _col = white - (_row * COLSIZE);

            float _tx = getTX(_col, height);
            float _tx2 = getTX(_col + 1, height);
            float _ty = getTX(_row, width);
            float _ty2 = getTX(_row + 1, width);


            int _x, _y, _mx, _my, _maxx;
            _x = 605;
            _y = 160;
            _mx = _x + game.getPlayerHealth();
            _my = _y + 16;
            _maxx = _x + game.getPlayerMaxHealth();
            GL11.glColor3f(0, 1.0f, 0.7f);

            //Draw healthbar
            GL11.glTexCoord2f(_tx, _ty);
            GL11.glVertex2f(_x, _y);
            GL11.glTexCoord2f(_tx, _ty2);
            GL11.glVertex2f(_x, _my);
            GL11.glTexCoord2f(_tx2, _ty2);
            GL11.glVertex2f(_mx,_my);
            GL11.glTexCoord2f(_tx2, _ty);
            GL11.glVertex2f(_mx, _y);

            GL11.glColor3f(0, 0, 0);
            GL11.glVertex2f(_x, _y);
            GL11.glVertex2f(_x, _my);
            GL11.glVertex2f(_maxx,_my);
            GL11.glVertex2f(_maxx, _y);

            if ((game.isPlayerInWater()) | (game.gas))
            {
                _x = 605;
                _y = 200;
                _my = _y + 16;
                _mx = _x + game.getPlayerOxygen();
                _maxx = _x + 100;

                _row = white / COLSIZE;
                _col = white - (_row * COLSIZE);

                _tx = getTX(_col, height);
                _tx2 = getTX(_col + 1, height);
                _ty = getTX(_row, width);
                _ty2 = getTX(_row + 1, width);


                GL11.glColor3f(0, 0.7f, 1.0f);
                GL11.glTexCoord2f(_tx, _ty);
                GL11.glVertex2f(_x, _y);
                GL11.glTexCoord2f(_tx, _ty2);
                GL11.glVertex2f(_x, _my);
                GL11.glTexCoord2f(_tx2, _ty2);
                GL11.glVertex2f(_mx,_my);
                GL11.glTexCoord2f(_tx2, _ty);
                GL11.glVertex2f(_mx,_y);

                GL11.glColor3f(0, 0, 0);
                GL11.glVertex2f(_x, _y);
                GL11.glVertex2f(_x, _my);
                GL11.glVertex2f(_maxx,_my);
                GL11.glVertex2f(_maxx,_y);

                String o2 = "Oxygen";
                addString(o2, _x, 212, 12, 1.0f, 1.0f, 1.0f, 1.0f, true);
            }
        }

        GL11.glEnd();
        // restore the model view matrix to prevent contamination
        GL11.glPopMatrix();


        //if (showRadar)
        {
            GL11.glPushMatrix();
            // bind to the appropriate texture for this sprite
            spritesheet.texture.bind();

            // translate to the right location and prepare to draw
            GL11.glTranslatef(0, 0, 0);
            GL11.glEnable(GL11.GL_BLEND);

            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(600, 455, 200, 155);

            // draw a quad textured to match the sprites
            GL11.glBegin(GL11.GL_QUADS);
            int row,col;
            float tx,ty,tx2,ty2;

            //Draw the radar
            GL11.glColor3f(0, 0, 0);
            //GL11.glTexCoord2f(0, 0); //Must push a texture coordinate to force the rectangle to draw
            GL11.glVertex2f(600, 0);
            GL11.glVertex2f(800, 0);
            GL11.glVertex2f(800, 155);
            GL11.glVertex2f(600, 155);
            GL11.glColor3f(1, 1, 1);

            if (!game.jam)
            {
                int PX = game.getPlayerX() / 5; //was /8
                int PY = game.getPlayerY() / 5; //was /8

                row = radarSolid / COLSIZE;
                col = radarSolid - (row * COLSIZE);

                tx = getTX(col, height);
                tx2 = getTX(col + 1, height);
                ty = getTX(row, width);
                ty2 = getTX(row + 1, width);


                for(int j = getRMinY(PlayerTileY); j < getRMaxY(PlayerTileY, tileMap.length); j++)
                {
                    for(int i = getRMinX(PlayerTileX); i < getRMaxX(PlayerTileX, tileMap[0].length); i++)
                    {

                        if (obstacleMatrix[j][i] == 0)
                        {
                            //tileMap[j][i].draw(g, ((i*5) - (PX) + (90)) + 12, ((j*5) - (PY) + (75)) + 12);
                            int x,y,mx,my;
                            //x = ((i*5) + 610 - (PX) + (90));
                            //y = ((j*5) - (PY) + (75));
                            x = ((i*8) + 610 - (PX) + (90));
                            y = ((j*8) - (PY) + (75));
                            mx = x + 40;
                            my = y + 40;

                            //Draw tile
                            GL11.glTexCoord2f(tx, ty);
                            GL11.glVertex2f(x, y);
                            GL11.glTexCoord2f(tx, ty2);
                            GL11.glVertex2f(x, my);
                            GL11.glTexCoord2f(tx2, ty2);
                            GL11.glVertex2f(mx,my);
                            GL11.glTexCoord2f(tx2, ty);
                            GL11.glVertex2f(mx,y);
                        }

                    }
                }

                for(int i = 0; i < NPCs.size(); i++)
                {
                    boolean ally = NPCs.get(i).ally;

                    if (!ally)
                    {
                        if ((NPCs.get(i).getStatus() != NPCStatus.SLEEP) && (NPCs.get(i).getStatus() != NPCStatus.TRANQUILIZED_SLEEP))
                        {
                            int x=0,y=0,mx,my;
                            int ix, iy;
                            ix = NPCs.get(i).getX() / 5;
                            iy = NPCs.get(i).getY() / 5;

                            GL11.glColor4f(1.0f, 0, 0, 0.4f);
                            if (NPCs.get(i).getDirection() == Direction.UP)
                            {
                                //g.fillArc(((NPCs.get(i).getX()/8) - (PlayerX) + (90)) - 11, ((NPCs.get(i).getY()/8) - (PlayerY) + (75)) - 28, 50, 80, 55, 70);
                                row = vconeup / COLSIZE;
                                col = vconeup - (row * COLSIZE);
                                //x = (ix - (PX) + (90)) - 18;
                                //y = (iy - (PY) + (75)) - 39;
                                x = (ix - (PX) + (90)) - 29;
                                y = (iy - (PY) + (75)) - 62;

                            }
                            else if (NPCs.get(i).getDirection() == Direction.DOWN)
                            {
                                //g.fillArc(((NPCs.get(i).getX()/8) - (PlayerX) + (90)) - 11, ((NPCs.get(i).getY()/8) - (PlayerY) + (75)) - 23, 50, 80, 235, 70);
                                row = vconedown / COLSIZE;
                                col = vconedown - (row * COLSIZE);
                                x = (ix - (PX) + (90)) - 29;
                                y = (iy - (PY) + (75)) + 7;
                            }
                            else if (NPCs.get(i).getDirection() == Direction.LEFT)
                            {
                                //g.fillArc(((NPCs.get(i).getX()/8) - (PlayerX) + (90)) - 29, ((NPCs.get(i).getY()/8) - (PlayerY) + (75)) - 11, 80, 50, 145, 70);
                                row = vconeleft / COLSIZE;
                                col = vconeleft - (row * COLSIZE);
                                x = (ix - (PX) + (90)) - 62;
                                y = (iy - (PY) + (75)) - 27;
                            }
                            else if (NPCs.get(i).getDirection() == Direction.RIGHT)
                            {
                                //g.fillArc(((NPCs.get(i).getX()/8) - (PlayerX) + (90)) - 24, ((NPCs.get(i).getY()/8) - (PlayerY) + (75)) - 11, 80, 50, 325, 70);
                                row = vconeright / COLSIZE;
                                col = vconeright - (row * COLSIZE);
                                x = (ix - (PX) + (90)) + 6;
                                y = (iy - (PY) + (75)) - 27;
                            }

                            x+=610;
                            //mx = x + 40;
                            //my = y + 40;
                            mx = x + 64;
                            my = y + 64;

                            float fix = 0.001f;
                            tx = getTX(col, height) + fix;
                            tx2 = getTX(col + 1, height) - fix;
                            ty = getTX(row, width) + fix;
                            ty2 = getTX(row + 1, width) - fix;


                            //Draw tile
                            GL11.glTexCoord2f(tx, ty);
                            GL11.glVertex2f(x, y);
                            GL11.glTexCoord2f(tx, ty2);
                            GL11.glVertex2f(x, my);
                            GL11.glTexCoord2f(tx2, ty2);
                            GL11.glVertex2f(mx,my);
                            GL11.glTexCoord2f(tx2, ty);
                            GL11.glVertex2f(mx,y);

                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        }

                        row = NPCSprite / COLSIZE;
                        col = NPCSprite - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);
                    }
                    else
                    {
                        row = playerSprite / COLSIZE;
                        col = playerSprite - (row * COLSIZE);

                        tx = getTX(col, height);
                        tx2 = getTX(col + 1, height);
                        ty = getTX(row, width);
                        ty2 = getTX(row + 1, width);
                    }

                    int x,y,mx,my;
                    int ix, iy;
                    ix = NPCs.get(i).getX() / 5;
                    iy = NPCs.get(i).getY() / 5;
                    x = ((ix) + 610 - (PX) + (90));
                    y = ((iy) - (PY) + (75));
                    mx = x + 40;
                    my = y + 40;

                    //Draw tile
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);
                }

                if (game.fightBoss() & (boss != null))
                {
                    row = NPCSprite / COLSIZE;
                    col = NPCSprite - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);

                    int x,y,mx,my;
                    int ix, iy;
                    ix = boss.getX() / 5;
                    iy = boss.getY() / 5;
                    x = ((ix) + 610 - (PX) + (90));
                    y = ((iy) - (PY) + (75));
                    mx = x + 40;
                    my = y + 40;

                    //Draw tile
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);
                }

                for(int i = 0; i < cameras.size(); i++)
                {
                    row = NPCSprite / COLSIZE;
                    col = NPCSprite - (row * COLSIZE);

                    tx = getTX(col, height);
                    tx2 = getTX(col + 1, height);
                    ty = getTX(row, width);
                    ty2 = getTX(row + 1, width);


                    int x,y,mx,my;
                    int ix, iy;
                    ix = cameras.get(i).getX() / 5;
                    iy = cameras.get(i).getY() / 5;
                    x = ((ix) + 610 - (PX) + (90));
                    y = ((iy) - (PY) + (75));
                    mx = x + 40;
                    my = y + 40;

                    //Draw tile
                    GL11.glTexCoord2f(tx, ty);
                    GL11.glVertex2f(x, y);
                    GL11.glTexCoord2f(tx, ty2);
                    GL11.glVertex2f(x, my);
                    GL11.glTexCoord2f(tx2, ty2);
                    GL11.glVertex2f(mx,my);
                    GL11.glTexCoord2f(tx2, ty);
                    GL11.glVertex2f(mx,y);

                    if ((!game.isPlayerSpotted()) || (cameras.get(i).hasGun()))
                    {
                        GL11.glColor4f(0, 0.2f, 1.0f, 0.4f);
                        if (cameras.get(i).getDirection() == Direction.UP)
                        {
                            //g.fillArc(((NPCs.get(i).getX()/8) - (PlayerX) + (90)) - 11, ((NPCs.get(i).getY()/8) - (PlayerY) + (75)) - 28, 50, 80, 55, 70);
                            row = vconeup / COLSIZE;
                            col = vconeup - (row * COLSIZE);
                            x = (ix - (PX) + (90)) - 29;
                            y = (iy - (PY) + (75)) - 66;

                        }
                        else if (cameras.get(i).getDirection() == Direction.DOWN)
                        {
                            //g.fillArc(((NPCs.get(i).getX()/8) - (PlayerX) + (90)) - 11, ((NPCs.get(i).getY()/8) - (PlayerY) + (75)) - 23, 50, 80, 235, 70);
                            row = vconedown / COLSIZE;
                            col = vconedown - (row * COLSIZE);
                            x = (ix - (PX) + (90)) - 29;
                            y = (iy - (PY) + (75)) + 11;
                        }
                        else if (cameras.get(i).getDirection() == Direction.LEFT)
                        {
                            //g.fillArc(((NPCs.get(i).getX()/8) - (PlayerX) + (90)) - 29, ((NPCs.get(i).getY()/8) - (PlayerY) + (75)) - 11, 80, 50, 145, 70);
                            row = vconeleft / COLSIZE;
                            col = vconeleft - (row * COLSIZE);
                            x = (ix - (PX) + (90)) - 66;
                            y = (iy - (PY) + (75)) - 27;
                        }
                        else if (cameras.get(i).getDirection() == Direction.RIGHT)
                        {
                            //g.fillArc(((NPCs.get(i).getX()/8) - (PlayerX) + (90)) - 24, ((NPCs.get(i).getY()/8) - (PlayerY) + (75)) - 11, 80, 50, 325, 70);
                            row = vconeright / COLSIZE;
                            col = vconeright - (row * COLSIZE);
                            x = (ix - (PX) + (90)) + 10;
                            y = (iy - (PY) + (75)) - 27;
                        }

                        x+=610;
                        mx = x + 64;
                        my = y + 64;

                        float fix = 0.001f;
                        tx = getTX(col, height) + fix;
                        tx2 = getTX(col + 1, height) - fix;
                        ty = getTX(row, width) + fix;
                        ty2 = getTX(row + 1, width) - fix;


                        //Draw tile
                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(x, y);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(x, my);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(mx,my);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(mx,y);

                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                }

                if (game.mineDetector)
                {
                    for(int i = 0; i < items.size(); i++)
                    {
                        if (items.get(i).getType() == ItemType.LANDMINE)
                        {
                            int cx, cy;
                            cx = items.get(i).getX();
                            cy = items.get(i).getY();
                            
                            row = NPCSprite / COLSIZE;
                            col = NPCSprite - (row * COLSIZE);

                            tx = getTX(col, height);
                            tx2 = getTX(col + 1, height);
                            ty = getTX(row, width);
                            ty2 = getTX(row + 1, width);


                            int x,y,mx,my;
                            int ix, iy;
                            ix = cx / 5;
                            iy = cy / 5;
                            x = ((ix) + 610 - (PX) + (90));
                            y = ((iy) - (PY) + (75));
                            mx = x + 40;
                            my = y + 40;

                            //Draw tile
                            GL11.glTexCoord2f(tx, ty);
                            GL11.glVertex2f(x, y);
                            GL11.glTexCoord2f(tx, ty2);
                            GL11.glVertex2f(x, my);
                            GL11.glTexCoord2f(tx2, ty2);
                            GL11.glVertex2f(mx,my);
                            GL11.glTexCoord2f(tx2, ty);
                            GL11.glVertex2f(mx,y);
                            
                        }
                    }
                }

                //Draw player on radar
                //playerSprite.draw(g, 102, 90);
                row = playerSprite / COLSIZE;
                col = playerSprite - (row * COLSIZE);

                tx = getTX(col, height);
                tx2 = getTX(col + 1, height);
                ty = getTX(row, width);
                ty2 = getTX(row + 1, width);


                int x,y,mx,my;
                x = 700;
                y = 78;
                mx = x + 40;
                my = y + 40;

                //Draw tile
                GL11.glTexCoord2f(tx, ty);
                GL11.glVertex2f(x, y);
                GL11.glTexCoord2f(tx, ty2);
                GL11.glVertex2f(x, my);
                GL11.glTexCoord2f(tx2, ty2);
                GL11.glVertex2f(mx,my);
                GL11.glTexCoord2f(tx2, ty);
                GL11.glVertex2f(mx,y);

            }
            else
            {
                addString("JAMMED", 665, 70, 16, 0, 1.0f, 0, 1.0f, true);
            }

            GL11.glEnd();

            // restore the model view matrix to prevent contamination
            GL11.glPopMatrix();
            
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }


        String ph = "Health";

        String wp = game.getSelectedWeaponStr();;
        String wpammo = "";
        String wpsilencer = game.getPlayer().getWeapon().getSilencerStr();
        if (game.getPlayer().getWeapon().getType() != ItemType.KNIFE)
        {
            wpammo = game.getAmmo() + "/" + game.getMaxAmmo();
        }

        String it;
        if (game.isMedkitEquipped())
        {
            it = game.getSelectedItemStr() + ": " + game.getNumMedkits() + "/" + game.getMaxNumMedkits();
        }
        else
        {
            it = game.getSelectedItemStr();
        }
        

        addString(ph, 605, 172, 12, 1.0f, 0, 0, 1.0f, true);
        addString(wp, 800, 464, 16, 1.0f, 0, 0, 1.0f, false);
        addString(wpammo, 800, 494, 16, 1.0f, 0, 0, 1.0f, false);
        addString(wpsilencer, 800, 524, 16, 1.0f, 0, 0, 1.0f, false);
        addString(it, 800, 584, 16, 1.0f, 0, 0, 1.0f, false);

        if ((game.paused) && (!game.inDialog))
        {
            this.addString("PAUSED", 250, 250, 16, 1.0f, 1.0f, 1.0f, 1.0f, true);
        }

        if (game.message != null)
        {
            //if (showRadar)
            {
                addString(game.mtype.toString(), 800, 235, 16, 1.0f, 1.0f, 1.0f, 1.0f, false);
                addString(game.message, 800, 255, 12, 1.0f, 1.0f, 1.0f, 1.0f, false);
            }
            if (game.lastMessageDate + 3000 < System.currentTimeMillis())
            {
                game.resetHUDMessage();
            }
        }
        
        //Draw the strings over everything
        TextWriter.drawStrings(strings, text.texture);

        if (game.inDialog)
        {
            Dialog dlg = game.getDialog();
            
            int dlgSize = dlg.dialog.get(dialogPage).size();
            boolean remainingPages = false;

            if (dialogPage < dlg.dialog.size() - 1)
            {
                dlgSize++;
                remainingPages = true;
            }

            this.resetStrings();
            
            short startPos = (dlg.onTop == true) ? (short)0:(short)384;
            short yp = (short)(startPos + 32);

            /*
            if (dlg.title.length() > 0)
            {
                //yp = startPos;
                this.addString(dlg.title, 5, startPos, 16, 0, 1.0f, 0.5f, 1.0f, true);
                dlgSize++;
            }
             */

            if (dlg.dialog.get(dialogPage).speaker.length() > 0)
            {
                this.addString(dlg.dialog.get(dialogPage).speaker, 5, startPos, 16, 0, 1.0f, 0.5f, 1.0f, true);
                dlgSize++;
            }

            if ((!remainingPages) && (dlg.confirmation))
            {
                dlgSize+=2;
            }

            drawDialogBackground(dlgSize, (float)startPos);

            for(int i = 0; i < dlg.dialog.get(dialogPage).size(); i++)
            {
                this.addString(dlg.dialog.get(dialogPage).get(i), 5, yp, 16, 1.0f, 1.0f, 1.0f, 1.0f, true);
                yp += 16;
            }

            if (remainingPages)
            {
                this.addString(nextArrow, 600, yp, 36, 0, 1.0f, 1.0f, 1.0f, false);
            }
            else
            {
                if (dlg.onTop == false)
                {
                    yp = 584;
                }
                
                if (dlg.confirmation)
                {
                    yp += 16;
                    this.addString(dlg.choiceString, 5, yp, 16, 0, 1.0f, 0.5f, 1.0f, true);
                }
                else
                {
                    this.addString("END", 600, yp, 16, 0, 1.0f, 1.0f, 1.0f, false);
                }
            }
            
            TextWriter.drawStrings(strings, text.texture);
        }


        boolean firePressed = window.isKeyPressed(KeyEvent.VK_SPACE);
        boolean upPressed = window.isKeyPressed(KeyEvent.VK_W);
        boolean downPressed = window.isKeyPressed(KeyEvent.VK_S);
        boolean leftPressed = window.isKeyPressed(KeyEvent.VK_A);
        boolean rightPressed = window.isKeyPressed(KeyEvent.VK_D);

        //boolean minusPressed = window.isKeyPressed(KeyEvent.VK_MINUS);
        //boolean equalsPressed = window.isKeyPressed(KeyEvent.VK_EQUALS);
        //boolean commaPressed = window.isKeyPressed(KeyEvent.VK_COMMA);
        //boolean periodPressed = window.isKeyPressed(KeyEvent.VK_PERIOD);
        boolean ePressed = window.isKeyPressed(KeyEvent.VK_E);
        boolean rPressed = window.isKeyPressed(KeyEvent.VK_R);
        boolean qPressed = window.isKeyPressed(KeyEvent.VK_Q);
        boolean fPressed = window.isKeyPressed(KeyEvent.VK_F);
        boolean shiftPressed = window.isKeyPressed(KeyEvent.VK_SHIFT);
        boolean ctrlPressed = window.isKeyPressed(KeyEvent.VK_CONTROL);
        boolean enterPressed = window.isKeyPressed(KeyEvent.VK_ENTER);
        boolean savePressed = window.isKeyPressed(KeyEvent.VK_F5);
        boolean tildePressed = window.isKeyPressed(KeyEvent.VK_BACK_QUOTE);
        boolean onePressed = window.isKeyPressed(KeyEvent.VK_1);
        boolean twoPressed = window.isKeyPressed(KeyEvent.VK_2);
        boolean threePressed = window.isKeyPressed(KeyEvent.VK_3);
        boolean fourPressed = window.isKeyPressed(KeyEvent.VK_4);

        //if ((upPressed) && (!downPressed))
        if (!game.inDialog)
        {
            if (upPressed)
            {
                game.movingUp = true;
                game.movingDown = false;
                game.movingLeft = false;
                game.movingRight = false;

                if (!moveStack.contains(Direction.UP))
                {
                    moveStack.push(Direction.UP);
                }
            }

            //if ((downPressed) && (!upPressed))
            if (downPressed)
            {
                game.movingUp = false;
                game.movingDown = true;
                game.movingLeft = false;
                game.movingRight = false;

                if (!moveStack.contains(Direction.DOWN))
                {
                    moveStack.push(Direction.DOWN);
                }
            }

            //if ((leftPressed) && (!rightPressed))
            if (leftPressed)
            {
                game.movingUp = false;
                game.movingDown = false;
                game.movingLeft = true;
                game.movingRight = false;

                if (!moveStack.contains(Direction.LEFT))
                {
                    moveStack.push(Direction.LEFT);
                }
            }

            //if ((rightPressed) && (!leftPressed))
            if (rightPressed)
            {
                game.movingUp = false;
                game.movingDown = false;
                game.movingLeft = false;
                game.movingRight = true;

                if (!moveStack.contains(Direction.RIGHT))
                {
                    moveStack.push(Direction.RIGHT);
                }
            }

            if (shiftPressed)
            {
                if (!stanceToggled)
                {
                    if ((!game.paused) && (!game.playerOnPath))
                    {
                        game.changeStance();
                        stanceToggled = true;
                    }
                }
            }
            else
            {
                stanceToggled = false;
            }

            if (firePressed)
            {
                game.movingUp = false;
                game.movingDown = false;
                game.movingLeft = false;
                game.movingRight = false;


                if ((!game.paused) && (!game.playerOnPath))
                {
                    if (game.getPlayerStance() == Stance.UPRIGHT)
                    {
                        if (!triggerPulled)
                        {
                            IT4.Weapon weapon = game.getPlayer().getWeapon();
                            if (weapon != null)
                            {
                                if (weapon.fireRate > 1)
                                {
                                    game.firingWeapon();
                                }
                                else
                                {
                                    game.playerAttack(100 - weapon.accuracy);
                                }
                            }
                            else
                            {
                                game.playerAttack(10);
                            }

                            triggerPulled = true;

                        }

                        //playerSprite = game.getPlayerSprite();
                    }
                }

            }
            else
            {
                if (triggerPulled)
                {
                    game.resetPlayerAnimation();
                    game.notFiringWeapon();
                    game.setPlayerAttacking(false);
                    game.setPlayerMoving(false);
                }
                triggerPulled = false;

            }

            if (!upPressed)
            {
                if (game.movingUp)
                {
                    game.resetPlayerAnimation();
                }

                game.movingUp = false;

                if (moveStack.contains(Direction.UP))
                {
                    moveStack.remove(Direction.UP);
                }
                this.resumeMovement();
            }

            if (!downPressed)
            {
                if (game.movingDown)
                {
                    game.resetPlayerAnimation();

                }
                game.movingDown = false;
                if (moveStack.contains(Direction.DOWN))
                {
                    moveStack.remove(Direction.DOWN);
                }
                this.resumeMovement();
            }

            if (!leftPressed)
            {
                if (game.movingLeft)
                {
                    game.resetPlayerAnimation();
                }
                game.movingLeft = false;
                if (moveStack.contains(Direction.LEFT))
                {
                    moveStack.remove(Direction.LEFT);
                }
                this.resumeMovement();

            }

            if (!rightPressed)
            {
                if (game.movingRight)
                {
                    game.resetPlayerAnimation();
                }
                if (moveStack.contains(Direction.RIGHT))
                {
                    moveStack.remove(Direction.RIGHT);
                }
                this.resumeMovement();
                game.movingRight = false;
            }

            if (onePressed)
            {
                if (!primaryWeaponSwitched)
                {
                    game.changeWeaponOrItem(1);
                    primaryWeaponSwitched = true;
                }
            }
            else
            {
                primaryWeaponSwitched = false;
            }

            if (twoPressed)
            {
                if (!secondaryWeaponSwitched)
                {
                    game.changeWeaponOrItem(2);
                    secondaryWeaponSwitched = true;
                }
            }
            else
            {
                secondaryWeaponSwitched = false;
            }

            if (threePressed)
            {
                if (!explosiveWeaponSwitched)
                {
                    game.changeWeaponOrItem(3);
                    explosiveWeaponSwitched = true;
                }
            }
            else
            {
                explosiveWeaponSwitched = false;
            }

            if (fourPressed)
            {
                if (!itemSwitched)
                {
                    game.changeWeaponOrItem(4);
                    itemSwitched = true;
                }
            }
            else
            {
                itemSwitched = false;
            }

            /*
            if (commaPressed)
            {
                if (!itemSwitched)
                {
                    game.changeItem(-1);
                    itemSwitched = true;
                }
            }
            else
            {
                itemSwitched = false;
            }

            if (periodPressed)
            {
                if (!itemSwitched2)
                {
                    game.changeItem(1);
                    itemSwitched2 = true;
                }
            }
            else
            {
                itemSwitched2 = false;
            }
             */

            if (ePressed)
            {
                if (!itemUsed)
                {
                    game.useMedkit();
                    itemUsed = true;
                }
            }
            else
            {
                itemUsed = false;
            }

            if (fPressed)
            {
                if (!actionTaken)
                {
                    game.actionTaken();
                    actionTaken = true;
                }
            }
            else
            {
                actionTaken = false;
            }

            if (enterPressed)
            {
                if (!pauseToggled)
                {
                    if (game.paused)
                    {
                        game.paused = false;
                    }
                    else
                    {
                        game.paused = true;
                    }
                    pauseToggled = true;
                }
            }
            else
            {
                pauseToggled = false;
            }

            if (qPressed)
            {
                if (!showObjs)
                {
                    showObjs = true;
                    game.printObjectives();
                }
            }
            else
            {
                showObjs = false;
            }

            if (tildePressed)
            {
                if (!dispConsole)
                {
                    if (Intruder.debug)
                    {
                        game.openConsole();
                        dispConsole = true;
                    }
                }
            }
            else
            {
                dispConsole = false;
            }

            if (rPressed)
            {
                if (!showRadarPressed)
                {
                    /*
                    if (showRadar)
                    {
                        showRadar = false;
                    }
                    else
                    {
                        showRadar = true;
                    }
                     * 
                     */
                    showRadarPressed = true;
                }
            }
            else
            {
                showRadarPressed = false;
            }

            if (ctrlPressed)
            {
                if (game.getPlayerStance() == Stance.UPRIGHT)
                {
                    if (!meleePerformed)
                    {
                        if ((!game.paused) && (!game.playerOnPath))
                        {
                            game.meleeAttack();
                            meleePerformed = true;
                        }
                    }
                }
            }
            else
            {
                if (meleePerformed)
                {
                    game.resetPlayerAnimation();
                    meleePerformed = false;
                }

            }

            if (savePressed)
            {
                if (!saveRequested)
                {
                    game.save();
                    saveRequested = true;
                }
            }
            else
            {
                saveRequested = false;
            }

        }
        else
        {
            Dialog dlg = game.getDialog();
            
            if (firePressed)
            {
                if (!triggerPulled)
                {
                    triggerPulled = true;

                    if (dialogPage < dlg.dialog.size() - 1)
                    {
                        dialogPage++;
                    }
                    else
                    {
                        dialogPage = 0;
                        game.closeDialog();
                    }
                }
            }
            else
            {
                triggerPulled = false;
            }

            if (fPressed)
            {
                if (!actionTaken)
                {
                    if (dlg.event != null)
                    {
                        dlg.choice = true;
                        game.closeDialog();
                    }
                    actionTaken = true;
                }
            }
            else
            {
                actionTaken = false;
            }

            if (enterPressed)
            {
                if (!pauseToggled)
                {
                    pauseToggled = true;

                    if (dialogPage < dlg.dialog.size() - 1)
                    {
                        dialogPage++;
                    }
                    else
                    {
                        dialogPage = 0;
                        game.closeDialog();
                    }
                }
            }
            else
            {
                pauseToggled = false;
            }

            if (ctrlPressed)
            {
                if (!meleePerformed)
                {
                    meleePerformed = true;

                    if (dialogPage < dlg.dialog.size() - 1)
                    {
                        dialogPage++;
                    }
                    else
                    {
                        dialogPage = 0;
                        game.closeDialog();
                    }
                }
            }
            else
            {
                meleePerformed = false;
            }

            if (qPressed)
            {
                if (!showObjs)
                {
                    showObjs = true;
                    if (dialogPage < dlg.dialog.size() - 1)
                    {
                        dialogPage++;
                    }
                    else
                    {
                        dialogPage = 0;
                        game.closeDialog();
                    }
                }
            }
            else
            {
                showObjs = false;
            }
        }        
    }

    public void windowClosed()
    {
        System.out.println("Closing...");
        game.returnToMainMenu(null);
    }

}
