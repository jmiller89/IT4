package GFX;

/**
 * An interface describing any class that wishes to be notified 
 * as the game window renders. 
 * 
 */
public interface GameWindowCallback
{
	/**
	 * Notification that game should initialize any resources it
	 * needs to use. This includes loading sprites.
	 */
	public void initialize();
	
	/**
	 * Notification that the display is being rendered. The implementor
	 * should render the scene and update any game logic
	 */
	public void frameRendering();
	
	/**
	 * Notification that game window has been closed.
	 */
	public void windowClosed();

}