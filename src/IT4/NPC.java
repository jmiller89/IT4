//NPC = Non-Player Character

package IT4;


/**
 *
 * @author Jim (Admin)
 */
public abstract class NPC extends ITCharacter
{
    private NPCStatus status;
    private boolean friendly;
    private int weaponDamage;
    private Path path;
    private GuardType type;
    
    public short timeToWaitSuspect = 150;
    public short timeToWaitBloodSpatter = 15;
    
    public boolean pathFound = false;

    //public Vertex[][] vertices;
    public byte currentShotInterval = 15;
    public byte animationIterations = 6;
    
    //public byte[][] obstacleMatrix;
    public boolean trapped = false;
    public int viewDistance = 320;
    public boolean correcting = false;

    public boolean initialized;

    public boolean spawned = false;

    public boolean following = false;

    public boolean firstAI = true;

    public long tranqedDate = 0;
    public int tranqTimeMillis = 0;

    public float movementDelta = 1.0f;

    public boolean markedForDeath = false;
    public byte markedForDeathIters = 8;

    public NPC(short id, int x, int y, Direction d, int chlth, NPCStatus s, boolean friend, int weaponDmg, Path p, GuardType t)
    {
        super(id, x, y, d, chlth, true);
        status = s;
        friendly = friend;
        weaponDamage = weaponDmg;
        path = p;
        type = t;
        initialized = false;

        /*
        if ((type == GuardType.FEMALE_ALLY) || (type == GuardType.FEMALE_ALLY_PRISONER) || (type == GuardType.WOMAN1) || (type == GuardType.WOMAN2)
           || (type == GuardType.WOMAN3) || (type == GuardType.ISLAND_GUY) || (type == GuardType.CHIEF) || (type == GuardType.SPECIAL))
         * 
         */

        if (friendly)
        {
            ally = true;

            if (path.getStartingWaypoint().getBehavior() == WaypointBehavior.FOLLOW_PLAYER)
            {
                following = true;
                //followingPath = new Path();
            }
        }
    }

    public NPCStatus getStatus()
    {
        return status;
    }

    public boolean isFriendly()
    {
        return friendly;
    }

    public void setStatus(NPCStatus npcs)
    {
        if (npcs != null)
        {
            status = npcs;
            if (status == NPCStatus.TRANQUILIZED_SLEEP)
            {
                tranqedDate = System.currentTimeMillis();
            }
        }
    }

    public int getWeaponDamage()
    {
        return weaponDamage;
    }

    public Path getPath()
    {
        return path;
    }

    public GuardType getType()
    {
        return type;
    }
    
    public abstract NPC copy();

    public Bullet attack(short bulletSprite, int playerX, int playerY, int shot, int rank)
    {
        int offsetX = 0;
        int offsetY = 0;

        int dx;
        int dy;
        float distance;
        float vX = 0.0f;
        float vY = 0.0f;

        if (this.getDirection() == Direction.UP)
        {
            offsetY = -20;
        }
        else if (this.getDirection() == Direction.DOWN)
        {
            offsetY = 20;
        }
        else if (this.getDirection() == Direction.LEFT)
        {
            offsetX = -20;
        }
        else if (this.getDirection() == Direction.RIGHT)
        {
            offsetX = 20;
        }

        dx = playerX - this.getX();
        dy = playerY - this.getY();

        distance = (float)Math.sqrt((double)((dx * dx) + (dy * dy)));

        vX = dx/distance;
        vY = dy/distance;

        if (weaponDamage == 0)
        {
            bulletSprite = 191;
            vX = vX / 2;
            vY = vY / 2;
        }

        Bullet b = new Bullet(this.getX() + offsetX, this.getY() + offsetY,
                vX, vY, bulletSprite, weaponDamage, false, false, 900, rank, 0);

        if (weaponDamage == 0)
        {
            b.explosive = true;
        }

        return b;
    }

    public void suspect(Direction attackFrom)
    {
        Direction oldDirection = this.getDirection();
        if (attackFrom == Direction.UP)
        {
            changeDirection(Direction.DOWN);
        }
        else if (attackFrom == Direction.DOWN)
        {
            changeDirection(Direction.UP);
        }
        else if (attackFrom == Direction.LEFT)
        {            
            changeDirection(Direction.RIGHT);            
        }
        else if (attackFrom == Direction.RIGHT)
        {
            changeDirection(Direction.LEFT);
        }

        suspectUpdateID();

        if ((oldDirection != getDirection()) || (getStatus() == NPCStatus.SLEEP))
        {            
            status = NPCStatus.SUSPICIOUS;
        }
    }    

    private void suspectUpdateID()
    {
        if (this.getType() == GuardType.LIGHT)
        {
            if (this.getDirection() == Direction.UP)
            {
                this.setID(19);
            }
            else if (this.getDirection() == Direction.DOWN)
            {
                this.setID(20);
            }
            else if (this.getDirection() == Direction.LEFT)
            {
                this.setID(21);
            }
            else if (this.getDirection() == Direction.RIGHT)
            {
                this.setID(22);
            }
        }
        else if (this.getType() == GuardType.MEDIUM)
        {
            if (this.getDirection() == Direction.UP)
            {
                this.setID(27);
            }
            else if (this.getDirection() == Direction.DOWN)
            {
                this.setID(28);
            }
            else if (this.getDirection() == Direction.LEFT)
            {
                this.setID(29);
            }
            else if (this.getDirection() == Direction.RIGHT)
            {
                this.setID(30);
            }
        }
        else if (this.getType() == GuardType.HEAVY)
        {
            if (this.getDirection() == Direction.UP)
            {
                this.setID(35);
            }
            else if (this.getDirection() == Direction.DOWN)
            {
                this.setID(36);
            }
            else if (this.getDirection() == Direction.LEFT)
            {
                this.setID(37);
            }
            else if (this.getDirection() == Direction.RIGHT)
            {
                this.setID(38);
            }
        }
        else if (this.getType() == GuardType.SCIENTIST1)
        {
            if (this.getDirection() == Direction.UP)
            {
                this.setID(162);
            }
            else if (this.getDirection() == Direction.DOWN)
            {
                this.setID(163);
            }
            else if (this.getDirection() == Direction.LEFT)
            {
                this.setID(164);
            }
            else if (this.getDirection() == Direction.RIGHT)
            {
                this.setID(165);
            }
        }
        else if (this.getType() == GuardType.SCIENTIST2)
        {
            if (this.getDirection() == Direction.UP)
            {
                this.setID(170);
            }
            else if (this.getDirection() == Direction.DOWN)
            {
                this.setID(171);
            }
            else if (this.getDirection() == Direction.LEFT)
            {
                this.setID(172);
            }
            else if (this.getDirection() == Direction.RIGHT)
            {
                this.setID(173);
            }
        }
        
    }

    protected void setPath(Path p)
    {
        path = p;
    }

    public boolean isPathAttainable()
    {
        boolean attainable = false;

        if (((getX()+19)/40) == (path.getStartingWaypoint().getXPos()) && ((getY()+19)/40) == (path.getStartingWaypoint().getYPos()))
        {
            attainable = true;
        }

        return attainable;
    }
}
