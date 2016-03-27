package GFX;

import IT4.Game;
import IT4.HUDMessageType;
import IT4.Stance;
import java.awt.event.KeyEvent;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;


/**
 * An implementation of GameWindow that will use OPENGL (JOGL) to 
 * render the scene. Its also responsible for monitoring the keyboard
 * using AWT.
 * 
 */
public class LWJGLGameWindow implements GameWindow, Runnable
{

    private Game game;
    private byte timeToDisplayBlood = 14;
    private byte timeToProtectPlayer = 50;
    private byte timeToChangeO2 = 6;
    private byte timeToChangeHealth = 100;
    private byte timeToFireWeapon = 2;
    private static final String RED_ALERT = "You are out of Oxygen!";
    private static final String WARNING = "Oxygen level is below 30%";
    private int shot = 0;
  
	/** The callback which should be notified of window events */
	private GameWindowCallback callback;
  
	/** True if the game is currently "running", i.e. the game loop is looping */
	//private boolean gameRunning = true;
  
	/** The width of the game display area */
	private int width;
  
	/** The height of the game display area */
	private int height;

	/** The loader responsible for converting images into OpenGL textures */
	private TextureLoader textureLoader;
  
	/** Title of window, we get it before our window is ready, so store it till needed */
	//private String title;
	
	/**
	 * Create a new game window that will use OpenGL to 
	 * render our game.
	 */
	public LWJGLGameWindow(Game g)
        {
            game = g;
	}
	
	/**
	 * Retrieve access to the texture loader that converts images
	 * into OpenGL textures. Note, this has been made package level
	 * since only other parts of the JOGL implementations need to access
	 * it.
	 * 
	 * @return The texture loader that can be used to load images into
	 * OpenGL textures.
	 */
	TextureLoader getTextureLoader()
        {
		return textureLoader;
	}
	
	/**
	 * Set the title of this window.
	 *
	 * @param title The title to set on this window
	 */
	public void setTitle(String title)
        {
	    //this.title = title;
	    if(Display.isCreated())
            {
	    	Display.setTitle(title);
	    }
	}

	/**
	 * Set the resolution of the game display area.
	 *
	 * @param x The width of the game display area
	 * @param y The height of the game display area
	 */
	public void setResolution(int x, int y)
        {
		width = x;
		height = y;
	}
	
	/**
	 * Sets the display mode for fullscreen mode
	 */
	private boolean setDisplayMode()
        {
		try
                {
			// get modes
			DisplayMode[] dm = org.lwjgl.util.Display.getAvailableDisplayModes(
					width, height, -1, -1, -1, -1, 60, 60);

			org.lwjgl.util.Display.setDisplayMode(dm, new String[] {
					"width=" + width,
					"height=" + height,
					"freq=" + 60,
					"bpp=" + org.lwjgl.opengl.Display.getDisplayMode().getBitsPerPixel() });

			return true;
		} 
                catch (Exception e)
                {
			e.printStackTrace();
			System.out.println("Unable to enter fullscreen, continuing in windowed mode");
		}

		return false;
	}
	
	/**
	 * Start the rendering process. This method will cause the display to redraw
	 * as fast as possible.
	 */
	public void startRendering()
        {
		try
                {
			setDisplayMode();
			Display.create();
			Display.setFullscreen(Game.FULLSCREEN);
                        Display.setVSyncEnabled(true);

                        System.out.println("Max texture size: " + GL11.GL_MAX_TEXTURE_SIZE);

			// grab the mouse, dont want that hideous cursor when we're playing!
			Mouse.setGrabbed(true);
  
			// enable textures since we're going to use these for our sprites
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			// disable the OpenGL depth test since we're rendering 2D graphics
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			
			GL11.glOrtho(0, width, height, 0, -1, 1);
			
			textureLoader = new TextureLoader();
			
			if (callback != null)
                        {
				callback.initialize();
			}

                        //Gray background
                        //GL11.glClearColor(0.25f, 0.25f, 0.25f, 0.0f);

		} 
                catch (LWJGLException le)
                {
			callback.windowClosed();
		}
    
		//gameLoop();
                this.run();
	}

	/**
	 * Register a callback that will be notified of game window
	 * events.
	 *
	 * @param callback The callback that should be notified of game
	 * window events. 
	 */
	public void setGameWindowCallback(GameWindowCallback callback)
        {
		this.callback = callback;
	}
	
	/**
	 * Check if a particular key is current pressed.
	 *
	 * @param keyCode The code associated with the key to check 
	 * @return True if the specified key is pressed
	 */
	public boolean isKeyPressed(int keyCode)
        {            
            boolean ctrl = false;
            boolean shift = false;
            boolean up = false;
            boolean down = false;
            boolean left = false;
            boolean right = false;

            // apparently, someone at decided not to use standard
            // keycode, so we have to map them over:
            switch(keyCode)
            {
                case KeyEvent.VK_SPACE:
                    keyCode = Keyboard.KEY_SPACE;
                    break;
                case KeyEvent.VK_W:
                    //keyCode = Keyboard.KEY_W;
                    up = true;
                    break;
                case KeyEvent.VK_S:
                    //keyCode = Keyboard.KEY_S;
                    down = true;
                    break;
                case KeyEvent.VK_A:
                    //keyCode = Keyboard.KEY_A;
                    left = true;
                    break;
                case KeyEvent.VK_D:
                    //keyCode = Keyboard.KEY_D;
                    right = true;
                    break;
                case KeyEvent.VK_UP:
                    //keyCode = Keyboard.KEY_UP;
                    up = true;
                    break;
                case KeyEvent.VK_DOWN:
                    //keyCode = Keyboard.KEY_DOWN;
                    down = true;
                    break;
                case KeyEvent.VK_LEFT:
                    //keyCode = Keyboard.KEY_LEFT;
                    left = true;
                    break;
                case KeyEvent.VK_RIGHT:
                    right = true;
                    //keyCode = Keyboard.KEY_RIGHT;
                    break;
                case KeyEvent.VK_SHIFT:
                    shift = true;
                    break;
                case KeyEvent.VK_MINUS:
                    keyCode = Keyboard.KEY_MINUS;
                    break;
                case KeyEvent.VK_EQUALS:
                    keyCode = Keyboard.KEY_EQUALS;
                    break;
                case KeyEvent.VK_COMMA:
                    keyCode = Keyboard.KEY_COMMA;
                    break;
                case KeyEvent.VK_PERIOD:
                    keyCode = Keyboard.KEY_PERIOD;
                    break;
                case KeyEvent.VK_E:
                    keyCode = Keyboard.KEY_E;
                    break;
                case KeyEvent.VK_F:
                    keyCode = Keyboard.KEY_F;
                    break;
                case KeyEvent.VK_R:
                    keyCode = Keyboard.KEY_R;
                    break;
                case KeyEvent.VK_CONTROL:
                    ctrl = true;
                    break;
                case KeyEvent.VK_ENTER:
                    keyCode = Keyboard.KEY_RETURN;
                    break;
                case KeyEvent.VK_F5:
                    keyCode = Keyboard.KEY_F5;
                    break;
                case KeyEvent.VK_F6:
                    keyCode = Keyboard.KEY_F6;
                    break;
                case KeyEvent.VK_Q:
                    keyCode = Keyboard.KEY_Q;
                    break;
                case KeyEvent.VK_BACK_QUOTE:
                    keyCode = Keyboard.KEY_GRAVE;
                    break;
                case KeyEvent.VK_1:
                    keyCode = Keyboard.KEY_1;
                    break;
                case KeyEvent.VK_2:
                    keyCode = Keyboard.KEY_2;
                    break;
                case KeyEvent.VK_3:
                    keyCode = Keyboard.KEY_3;
                    break;
                case KeyEvent.VK_4:
                    keyCode = Keyboard.KEY_4;
                    break;

            }

            if (ctrl)
            {
                return org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) | org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);
            }
            if (shift)
            {
                return org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) | org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
            }

            if (up)
            {
                return org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_UP) | org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_W);
            }
            if (down)
            {
                return org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_DOWN) | org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_S);
            }
            if (left)
            {
                return org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_LEFT) | org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_A);
            }
            if (right)
            {
                return org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_RIGHT) | org.lwjgl.input.Keyboard.isKeyDown(Keyboard.KEY_D);
            }

            return org.lwjgl.input.Keyboard.isKeyDown(keyCode);
	}



	/**
	 * Run the main game loop. This method keeps rendering the scene
	 * and requesting that the callback update its screen.
	 */
	private void gameLoop()
        {
            while (game.running)
            {
                // clear screen
                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);
                GL11.glLoadIdentity();

                // let subsystem paint
                if (callback != null)
                {
                    callback.frameRendering();
                }

                game.lastPlayerTileX = game.getPlayerTileX();
                game.lastPlayerTileY = game.getPlayerTileY();

                if (game.running)
                {
                    if ((!game.paused) && (!game.playerOnPath))
                    {
                        int lx = game.getPlayerTileX();
                        int ly = game.getPlayerTileY();

                        if (game.lurch)
                        {
                            if (game.lurchVector.x > 0)
                            {
                                game.moveRight();
                            }
                            else if (game.lurchVector.x < 0)
                            {
                                game.moveLeft();
                            }
                            else if (game.lurchVector.y > 0)
                            {
                                game.moveDown();
                            }
                            else if (game.lurchVector.y < 0)
                            {
                                game.moveUp();
                            }
                            game.lurch = false;
                        }

                        if (game.movingUp)
                        {
                            game.moveUp();
                        }

                        if (game.movingDown)
                        {
                            game.moveDown();
                        }

                        if (game.movingLeft)
                        {
                            game.moveLeft();
                        }

                        if (game.movingRight)
                        {
                            game.moveRight();
                        }

                        if (!game.loading) //Don't use old lx and ly values when changing maps
                        {
                            synchronized (game.followerLock)
                            {
                                if (game.getPlayerTileX() != lx)
                                {
                                    game.lastPlayerTileX = lx;
                                    game.lastPlayerTileY = game.getPlayerTileY();
                                }
                                else if (game.getPlayerTileY() != ly)
                                {
                                    game.lastPlayerTileY = ly;
                                    game.lastPlayerTileX = game.getPlayerTileX();
                                }
                            }
                        }

                        if (!game.isPlayerAlive())
                        {
                            Keyboard.destroy();
                            game.movingUp = false;
                            game.movingDown = false;
                            game.movingLeft = false;
                            game.movingRight = false;
                            game.clearMovementStack();
                            
                            try
                            {
                                Keyboard.create();
                            }
                            catch (LWJGLException ex)
                            {
                                //Logger.getLogger(LWJGLGameWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        if (game.isPlayerRecentlyWounded() == true)
                        {
                            timeToDisplayBlood--;
                        }

                        if (timeToDisplayBlood <= 0)
                        {
                            game.checkPlayerStatus();
                            game.setPlayerNoLongerWounded();
                            timeToDisplayBlood = 6;
                        }

                        if (game.getFiringWeapon())
                        {
                            IT4.Weapon wp = game.getPlayer().getWeapon();
                            if (wp != null)
                            {
                                if (wp.fireRate > 1)
                                {
                                    if (timeToFireWeapon > 0)
                                    {
                                        timeToFireWeapon--;
                                    }
                                    else
                                    {
                                        //timeToFireWeapon = 2;
                                        timeToFireWeapon = (byte)(100-wp.fireRate);

                                        if (wp.type == IT4.ItemType.ASSAULT_RIFLE)
                                        {
                                            game.playerAttack(shot);
                                            if (shot < 60)
                                            {
                                                shot+=(100-wp.accuracy);
                                            }
                                        }
                                        else
                                        {
                                            game.playerAttack(100 - wp.accuracy);
                                        }
                                    }
                                }
                                else
                                {
                                    shot = 0;
                                }
                            }
                        }
                        else
                        {
                            shot = 0;
                        }


                        /*
                        if ((game.getFiringWeapon() == true) && (game.isAREquipped()))
                        {
                            if (timeToFireWeapon > 0)
                            {
                                timeToFireWeapon--;
                            }
                            else
                            {
                                timeToFireWeapon = 2;
                                game.playerAttack(shot);
                                if (shot < 60)
                                {
                                    shot+=10;
                                }
                            }

                        }
                        else if (game.getFiringWeapon() && game.isSMGEquipped())
                        {
                            if (timeToFireWeapon > 0)
                            {
                                timeToFireWeapon--;
                            }
                            else
                            {
                                timeToFireWeapon = 2;
                                game.playerAttack(35);
                            }
                        }
                        else
                        {
                            shot = 0;
                        }
                         * 
                         */


                        

                        if (((game.isPlayerInWater() == true) && (game.getPlayerStance() == Stance.PRONE)) || (game.gas))
                        {
                            if (timeToChangeO2 > 0)
                            {
                                timeToChangeO2--;
                            }
                            else
                            {
                                if (game.isGasMaskEquipped())
                                {
                                    timeToChangeO2 = 48;
                                }
                                else
                                {
                                    timeToChangeO2 = 6;
                                }

                                game.setPlayerOxygen(game.getPlayerOxygen() - 1);
                            }

                        }
                        else
                        {
                            if (game.getPlayerOxygen() < 100)
                            {
                                game.setPlayerOxygen(game.getPlayerOxygen() + 1);
                            }

                            if (!game.isPlayerSpotted())
                            {
                                if (game.getPlayerStance() == Stance.PRONE)
                                {
                                    if (timeToChangeHealth > 0)
                                    {
                                        timeToChangeHealth--;
                                    }
                                    else
                                    {
                                        game.regenerateHealth();
                                        timeToChangeHealth = 100;
                                    }
                                }
                            }
                        }

                        if ((game.getPlayerOxygen() < 30) && (game.getPlayerOxygen() > 0))
                        {
                            game.setHUDMessage(WARNING, HUDMessageType.WARNING);
                        }
                        else if (game.getPlayerOxygen() <= 0)
                        {
                            if (timeToChangeO2 > 0)
                            {
                                timeToChangeO2--;
                            }
                            else
                            {
                                timeToChangeO2 = 3;
                                game.drownPlayer();
                                game.setPlayerOxygen(0);
                                game.checkPlayerStatus();

                                game.setHUDMessage(RED_ALERT, HUDMessageType.DANGER);
                            }

                        }
                        else
                        {
                            if (game.isPlayerInWater())
                            {
                                game.resetHUDMessage();
                            }
                        }

                        if (game.isPlayerProtected())
                        {
                            if (this.timeToProtectPlayer > 0)
                            {
                                timeToProtectPlayer--;
                            }
                            else
                            {
                                game.stopProtectingPlayer();
                                timeToProtectPlayer = 50;
                            }
                        }

                    }
                    else
                    {
                        game.movingUp = false;
                        game.movingDown = false;
                        game.movingLeft = false;
                        game.movingRight = false;
                        game.clearMovementStack();
                        game.notFiringWeapon();                                                
                    }

                    if (game.getDoors() != null)
                    {
                        for(int i = 0; i < game.getDoors().length; i++)
                        {
                            if (game.getDoors()[i].isOpening())
                            {
                                game.getDoors()[i].open();
                            }
                            else if (game.getDoors()[i].isClosing())
                            {
                                game.getDoors()[i].close();
                            }
                        }
                    }

                    //game.render();
                    // update window contents
                    if (game.running)
                    {
                        Display.update();

                        if (Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
                        {
                                //gameRunning = false;
                                Display.destroy();
                                callback.windowClosed();
                        }
                    }

                    try
                    {
                        //Thread.sleep(25);
                        //Thread.sleep(10);
                        Thread.sleep(Game.SLEEPTIME);
                    }
                    catch(InterruptedException e)
                    {
                        System.out.println("Interrupted Exception!");
                    }
                }			
            }
	}

    public void run()
    {
        this.setTitle("The Endling's Artifice");
        gameLoop();
    }

}