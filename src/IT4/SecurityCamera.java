/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class SecurityCamera extends ITObject
{
    private Direction direction;
    private Waypoint start;
    private Waypoint end;
    private int currWaypoint = 0;
    private boolean fixed;
    public boolean remove = false;

    //Used for checking for player
    public int xOffset = 0;
    public int yOffset = 0;

    private static final int NORMAL_WAIT = 1;
    private static final int WP_WAIT = 100;
    private int wait = 0;
    public int health = 125;
    public boolean alive = true;
    public int weaponDamage = 30;
    public byte currentShotInterval = 15;

    public int spottedPlayerReaction = 0;

    public SecurityCameraType type = SecurityCameraType.NORMAL;

    public SecurityCamera(short id, int x, int y, Direction dir, Waypoint way1, Waypoint way2, boolean isFixed, SecurityCameraType camType)
    {
        super(id, x, y);
        direction = dir;
        start = way1;
        end = way2;
        fixed = isFixed;
        type = camType;

        if (direction == Direction.UP)
        {
            yOffset = -40;
            //xOffset = 20;
        }
        if (direction == Direction.DOWN)
        {
            yOffset = 40;
            //xOffset = 20;
        }
        if (direction == Direction.LEFT)
        {
            xOffset = -40;
            //yOffset = 20;
        }
        if (direction == Direction.RIGHT)
        {
            xOffset = 40;
            //yOffset = 20;
        }
    }

    public SecurityCamera copy()
    {
        return new SecurityCamera(this.getID(), this.getX(), this.getY(), this.direction, this.start, this.end, this.fixed, this.type);
    }

    public Direction getDirection()
    {
        return direction;
    }

    public void receiveDamage(int dmg)
    {
        health -= dmg;

        if (health <= 0)
        {
            alive = false;
        }
    }

    public boolean hasGun()
    {
        return ((type == SecurityCameraType.GUN) || (type == SecurityCameraType.GUN_DRONE));
    }

    public Bullet attack(short bulletSprite, int playerX, int playerY, int shot, int rank)
    {
        if (hasGun())
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
                offsetY = -40;
            }
            else if (this.getDirection() == Direction.DOWN)
            {
                offsetY = 40;
            }
            else if (this.getDirection() == Direction.LEFT)
            {
                offsetX = -40;
            }
            else if (this.getDirection() == Direction.RIGHT)
            {
                offsetX = 40;
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
        else
        {
            return null;
        }
    }

    public void move(float delta)
    {
        if (!fixed)
        {
            if (wait <= 0)
            {
                if (currWaypoint == 0)
                {
                    if ((direction == Direction.UP) || (direction == Direction.DOWN))
                    {
                        if (end.getXPos() * 40 > this.getX())
                        {
                            //setX(this.getX() + 1);
                            move(0.5f, 0, delta);
                        }
                        else if (end.getXPos() * 40 < this.getX())
                        {
                            //setX(this.getX() - 1);
                            move(-0.5f, 0, delta);
                        }
                        else
                        {
                            currWaypoint = 1;
                            wait = WP_WAIT;
                        }
                    }
                    else
                    {
                        if (end.getYPos() * 40 > this.getY())
                        {
                            //setY(this.getY() + 1);
                            move(0, 0.5f, delta);
                        }
                        else if (end.getYPos() * 40 < this.getY())
                        {
                            //setY(this.getY() - 1);
                            move(0, -0.5f, delta);
                        }
                        else
                        {
                            currWaypoint = 1;
                            wait = WP_WAIT;
                        }
                    }
                }
                else
                {
                    if ((direction == Direction.UP) || (direction == Direction.DOWN))
                    {
                        if (start.getXPos() * 40 > this.getX())
                        {
                            //setX(this.getX() + 1);
                            move(0.5f, 0, delta);
                        }
                        else if (start.getXPos() * 40 < this.getX())
                        {
                            //setX(this.getX() - 1);
                            move(-0.5f, 0, delta);
                        }
                        else
                        {
                            currWaypoint = 0;
                            wait = WP_WAIT;
                        }
                    }
                    else
                    {
                        if (start.getYPos() * 40 > this.getY())
                        {
                            //setY(this.getY() + 1);
                            move(0, 0.5f, delta);
                        }
                        else if (start.getYPos() * 40 < this.getY())
                        {
                            //setY(this.getY() - 1);
                            move(0, -0.5f, delta);
                        }
                        else
                        {
                            currWaypoint = 0;
                            wait = WP_WAIT;
                        }
                    }
                }

                if (wait <= 0)
                {
                    wait = NORMAL_WAIT;
                }
            }
            else
            {
                wait--;
            }
        }

    }
}
