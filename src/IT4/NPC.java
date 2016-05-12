//NPC = Non-Player Character

package IT4;


/**
 *
 * @author Jim (Admin)
 */
public class NPC extends ITCharacter
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

    public boolean juggernaut = false;

    public final short initialID;

    public Dialog dialog;

    public static NPC create(GuardType t, int x, int y, Direction d, NPCStatus s, boolean friend, Path p)
    {
        short id = 0;
        int currHealth = 0;
        int weaponDmg = 0;
        float npc_walkspeed = 0.5f;
        float npc_runspeed = 0.95f;
        boolean juggernaut = false;
        
        //LIGHT, MEDIUM, HEAVY, SPECIAL, BOSS0, BOSS1, BOSS2, BOSS3, BOSS4, NAZI_HUNTER, SCIENTIST1, SCIENTIST2, PROMINENT_SCIENTIST,
        //FEMALE_ALLY, FEMALE_ALLY_WORKOUT, OLD_MAN, UN_GUY, CHIEF, MUTANT1, MUTANT2, CRIPPLE, EVA, MAN, WOMAN1, WOMAN2, WOMAN3

        switch(t)
        {
            case LIGHT:
                id = 19;
                currHealth = 22;
                weaponDmg = 26;
                break;
            case MEDIUM:
                id = 27;
                currHealth = 72;
                weaponDmg = 40;
                break;
            case HEAVY:
                id = 35;
                currHealth = 100;
                weaponDmg = 40;
                break;
            case SPECIAL:
                id = 350;
                currHealth = 123;
                weaponDmg = 50;
                break;
            case BOSS0:
                id = 242;
                currHealth = 250;
                weaponDmg = 50;
                break;
            case BOSS1:
                id = 119;
                currHealth = 250;
                weaponDmg = 50;
                break;
            case BOSS2:
                id = 127;
                currHealth = 250;
                weaponDmg = 50;
                break;
            case BOSS3:
                id = 135;
                currHealth = 250;
                weaponDmg = 0;
                break;
            case BOSS4:
                id = 226;
                currHealth = 250;
                weaponDmg = 50;
                break;
            case NAZI_HUNTER:
                id = 234;
                currHealth = 100;
                weaponDmg = 50;
                break;
            case SCIENTIST1:
                id = 162;
                currHealth = 15;
                weaponDmg = 20;
                npc_runspeed = 0.75f;
                break;
            case SCIENTIST2:
                id = 170;
                currHealth = 15;
                weaponDmg = 20;
                npc_runspeed = 0.75f;
                break;
            case PROMINENT_SCIENTIST:
                id = 421;
                currHealth = 56;
                weaponDmg = 20;
                break;
            case FEMALE_ALLY:
                id = 261;
                currHealth = 120;
                weaponDmg = 25;
                break;
            case FEMALE_ALLY_WORKOUT:
                id = 269;
                currHealth = 120;
                weaponDmg = 25;
                break;
            case OLD_MAN:
                id = 405;
                currHealth = 30;
                weaponDmg = 40;
                break;
            case UN_GUY:
                id = 342;
                currHealth = 40;
                weaponDmg = 20;
                break;
            case CHIEF:
                id = 334;
                currHealth = 40;
                weaponDmg = 20;
                break;
            case MUTANT1:
                id = 277;
                currHealth = 192;
                weaponDmg = 60;
                npc_walkspeed = 0.75f;
                npc_runspeed = 0.99f;
                break;
            case MUTANT2:
                id = 285;
                currHealth = 512;
                weaponDmg = 90;
                npc_walkspeed = 0.75f;
                npc_runspeed = 0.99f;
                break;
            case CRIPPLE:
                id = 413;
                currHealth = 100;
                weaponDmg = 25;
                npc_runspeed = 0.75f;
                break;
            case EVA:
                id = 429;
                currHealth = 15;
                weaponDmg = 20;
                break;
            case MAN:
                id = 437;
                currHealth = 15;
                weaponDmg = 20;
                break;
            case WOMAN1:
                id = 310;
                currHealth = 15;
                weaponDmg = 20;
                break;
            case WOMAN2:
                id = 318;
                currHealth = 15;
                weaponDmg = 20;
                break;
            case WOMAN3:
                id = 326;
                currHealth = 15;
                weaponDmg = 20;
                break;
            case JUGGERNAUT:
                id = 459;
                currHealth = 310;
                weaponDmg = 20;
                npc_runspeed = 0.75f;
                juggernaut = true;
                break;
            case JUGGERNAUT2:
                id = 467;
                currHealth = 630;
                weaponDmg = 25;
                npc_runspeed = 0.75f;
                juggernaut = true;
                break;
            default:
                id = 19;
                currHealth = 22;
                weaponDmg = 25;
                break;
        }

        NPC toSpawn = new NPC(id, x, y, d, currHealth, s, friend, weaponDmg, p, t, npc_walkspeed, npc_runspeed, juggernaut);
        return toSpawn;
    }

    public NPC(short id, int x, int y, Direction d, int chlth, NPCStatus s, boolean friend, int weaponDmg, Path p, GuardType t, float npc_walkspeed, float npc_runspeed, boolean isJuggernaut)
    {
        super(id, x, y, d, chlth, true, npc_walkspeed, npc_runspeed);
        initialID = id;
        status = s;
        friendly = friend;
        weaponDamage = weaponDmg;
        path = p;
        type = t;
        initialized = false;
        juggernaut = isJuggernaut;

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

    public NPC copy()
    {
        //(short id, int x, int y, Direction d, int chlth, NPCStatus s, boolean friend, int weaponDmg, Path p, GuardType t)
        NPC n = new NPC(this.initialID, this.getX(), this.getY(), this.getDirection(), this.getCurrentHealth(), this.getStatus(), this.friendly, this.getWeaponDamage(), this.getPath().copy(), this.getType(), this.NPC_WALKSPEED, this.NPC_RUNSPEED, this.juggernaut);
        n.bodyArmor = this.bodyArmor;
        n.dialog = this.dialog;

        return n;
    }

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
        if (!friendly)
        {
            if (this.getDirection() == Direction.UP)
            {
                this.setID(initialID);
            }
            else if (this.getDirection() == Direction.DOWN)
            {
                this.setID(initialID+1);
            }
            else if (this.getDirection() == Direction.LEFT)
            {
                this.setID(initialID+2);
            }
            else if (this.getDirection() == Direction.RIGHT)
            {
                this.setID(initialID+3);
            }
        }
    }

    public void setPath(Path p)
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
