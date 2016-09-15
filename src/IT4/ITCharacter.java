//This is a parent class for the Player and All NPCs

package IT4;

/**
 *
 * @author Jim (Admin)
 */

import java.awt.Point;

public abstract class ITCharacter extends ITObject
{
    private Direction dir;
    private int currentHealth;
    private boolean NPC;
    public boolean isInWater = false;
    public boolean isInTallGrass = false;
    public boolean isRecentlyWounded = false;
    public boolean BOSS = false;
    private long lastDamaged = 0;
    public boolean bodyArmor = false;
    public boolean ally = false;
    public int wayX;
    public int wayY;
    public boolean guardStopped = false;
    public short timeToWait = 0;
    public Stance stance = Stance.UPRIGHT;

    public float NPC_RUNSPEED = 0.95f;
    public float NPC_WALKSPEED = 0.5f;
    
    //For door checking
    public Point doorXY = new Point(-1,-1);
    public Door lastDoor = null;

    public ITCharacter(short id, int x, int y, Direction d, int chlth, boolean nonPlayerCharacter, float npc_walkspeed, float npc_runspeed)
    {
        super(id, x, y);
        dir = d;
        currentHealth = chlth;
        NPC = nonPlayerCharacter;
        NPC_WALKSPEED = npc_walkspeed;
        NPC_RUNSPEED = npc_runspeed;
    }

    public void checkDoorStatus()
    {
        if (lastDoor != null)
        {
            if (((this.getTileX() > lastDoor.getTileX() + 1) || (this.getTileX() < lastDoor.getTileX() - 1))
                || ((this.getTileY() > lastDoor.getTileY() + 1) || (this.getTileY() < lastDoor.getTileY() - 1)))
            {
                //System.out.println("CLOSING");
                //lastDoor.closing = true;
                //lastDoor.opening = false;
                lastDoor.closing();
            }
            if (lastDoor.isClosed())
            {
                lastDoor = null;
            }
        }
    }

    public void closeDoor()
    {
        if (lastDoor != null)
        {
            if (!lastDoor.isClosed())
            {
                //lastDoor.closing = true;
                //lastDoor.opening = false;
                lastDoor.closing();
            }
        }
        
        lastDoor = null;
    }

    public Direction getDirection()
    {
        return dir;
    }

    public void changeDirection(Direction d)
    {
        if (d != null)
        {
            dir = d;
        }
    }

    public int getCurrentHealth()
    {
        return currentHealth;
    }

    public void setHealth(int h)
    {
        currentHealth = h;
    }

    //Call this when a bullet collides w/ the character
    public int receiveDamage(int dmg)
    {
        if (bodyArmor)
        {
            //dmg = (dmg + 1) / 2;
            float damage = (dmg * 0.7f);
            dmg = (int)damage;
        }

        
        if (dmg > 1)
        {
            isRecentlyWounded = true;
        }
        
        if (BOSS)
        {
            if (dmg > 0)
            {                
                long currentDate = System.currentTimeMillis();

                //This gives the boss a 2 second invincibility period between shots
                if (currentDate >= lastDamaged + 2000)
                {
                    currentHealth = currentHealth - dmg;
                    lastDamaged = currentDate;
                }
            }
        }
        else
        {
            //Can't kill Allies
            if (ally)
            {
                dmg = 0;
            }
            
            currentHealth = currentHealth - dmg;
        }

        if (currentHealth > 0)
        {
            if (NPC)
            {
                System.out.println("NPC received damage! Health = " + currentHealth);
            }
            else
            {
                System.out.println("Player received damage! Health = " + currentHealth);
            }
        }
        else
        {
            if (NPC)
            {
                System.out.println("NPC has died.");
            }
            else
            {
                System.out.println("Player has died. Game Over.");
            }
        }
        
        return currentHealth;
    }

    public void addHealth(int maxHealth)
    {
        currentHealth = currentHealth + 50;

        //Bind currentHealth to maxHealth
        if (currentHealth > maxHealth)
        {
            currentHealth = maxHealth;
        }
    }

    public void fullHeal(int maxHealth)
    {
        currentHealth = maxHealth;
    }

    public boolean isNPC()
    {
        return NPC;
    }

    public abstract Bullet attack(short bulletSprite, int targetX, int targetY, int shot, int rank);
}
