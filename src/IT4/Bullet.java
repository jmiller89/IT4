//This class represents an individual Bullet fired from a Weapon (other than a knife)

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class Bullet extends ITObject
{
    private float deltaX;
    private float deltaY;

    private int damage;
    private boolean tranquilizer;
    
    private int distTraversed;
    private boolean status;
    private boolean playerBullet;

    public boolean explosive = false;
    public boolean buckshot = false;

    private int range;
    public int rank;
    public int staminaDamage;

    public Bullet(int x, int y, float dX, float dY, short bid, int dmg, boolean sleep, boolean fromPlayer, int range_, int rank, int staminaDamage_)
    {
        super((short)181, x, y);
        fX = (float)x;
        fY = (float)y;
        damage = dmg;
        tranquilizer = sleep;
        distTraversed = 0;
        status = true;
        playerBullet = fromPlayer;
        deltaX = dX * 10;
        deltaY = dY * 10;
        range = range_;
        staminaDamage = staminaDamage_;
        this.rank = rank;
        this.setID(bid);
    }

    //Returns the amount of damage this bullet can inflict
    public int getDamage()
    {
        return damage;
    }

    //Returns true if it puts the enemy to sleep (Wristwatch)
    public boolean getTranquilizer()
    {
        return tranquilizer;
    }

    public void setStatus(boolean s)
    {
        status = s;
    }

    public boolean isPlayerBullet()
    {
        return playerBullet;
    }

    //Returns true if it has not exceeded its range and if it has not yet collided with another ITObject
    public boolean getStatus()
    {
        return ((status) & (distTraversed < range));        
    }

    public float getDeltaX()
    {
        return deltaX;
    }

    public float getDeltaY()
    {
        return deltaY;
    }

    //Use this to update the Bullet's position
    public void move()
    {
        move(deltaX, deltaY);
        distTraversed += 10;
    }

    public void nullify()
    {
        status = false;
    }

}
