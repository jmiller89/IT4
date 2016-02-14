//This contains everything related to the Player

package IT4;

/**
 *
 * @author Jim (Admin)
 */

//import java.awt.Point;
import java.util.Random;

public class Player extends ITCharacter
{
    public int maxHealth;
    public Backpack stuff;
    public int oxygen;
    public Random rng;
    public int objectives = 0;
    public boolean gasMask = false;
    private Weapon knife = Weapon.create(ItemType.KNIFE, 1);

    //Ctor
    public Player(short id, int x, int y, Direction d)
    {
        super(id, x, y, d, 100, false);
        
        maxHealth = 100;
        stuff = new Backpack();
        stance = Stance.UPRIGHT;
        oxygen = 100;
        rng = new Random();
    }

    //Copy ctor
    public Player(Player p)
    {
        super(p.getID(), p.getX(), p.getY(), p.getDirection(), p.getMaxHealth(), false);
        this.setHealth(p.getCurrentHealth());
        this.stuff = new Backpack(p.stuff);
        stance = Stance.UPRIGHT;
        oxygen = 100;
        maxHealth = p.getMaxHealth();
        rng = new Random();
        objectives = p.objectives;
    }

    public boolean hasGasMask()
    {
        return stuff.hasGasMask();
    }

    public boolean hasNVG()
    {
        return stuff.hasNVG();
    }

    public boolean hasBodyArmor()
    {
        return stuff.hasBodyArmor();
    }

    public boolean hasMineDetector()
    {
        return stuff.hasMineDetector();
    }

    public void stripItems()
    {
        stuff.clear();
    }

    public void changeItem(int change)
    {
        stuff.changeItem(change);
    }

    /*
    public void changeWeapon(int change)
    {
        stuff.changeWeapon(change);
    }
     * 
     */

    public void changeWeapon(int code)
    {
        switch (code)
        {
            case 1:
                stuff.selectedWeapon = stuff.primary;
                break;
            case 2:
                stuff.selectedWeapon = stuff.sidearm;
                break;
            case 3:
                stuff.selectedWeapon = stuff.explosive;
                break;
            default:
                break;
        }
    }

    public int getOxygen()
    {
        return oxygen;
    }

    public void setOxygen(int o2)
    {
        oxygen = o2;
    }

    public void setMaxHealth(int mh)
    {
        if (mh > 0)
        {
            maxHealth = mh;
        }
    }

    public int getMaxHealth()
    {
        return maxHealth;
    }

    public boolean heal()
    {
        boolean mkused = false;

        if (stuff.useHealthKit(getCurrentHealth(), maxHealth) == true)
        {
            this.addHealth(maxHealth);
            mkused = true;
        }

        return mkused;
    }

    public boolean giveHealthKit()
    {
        return stuff.addHealthKit();
    }

    public int getNumHealthKits()
    {
        return stuff.getNumHealthKits();
    }

    public int getMaxNumHealthKits()
    {
        return stuff.getMaxNumHealthKits();
    }

    public Stance getStance()
    {
        return stance;
    }

    public void changeStance(boolean forceprone)
    {
        if (forceprone)
        {
            stance = Stance.PRONE;
        }
        else
        {
            if (stance == Stance.PRONE)
            {
                stance = Stance.UPRIGHT;
            }
            else
            {
                stance = Stance.PRONE;
            }
        }
    }

    public Weapon givePrimary(Weapon w)
    {
        return stuff.addPrimary(w);
    }

    public Weapon giveSecondary(Weapon w)
    {
        return stuff.addSecondary(w);
    }

    public Weapon giveExplosive(Weapon w)
    {
        return stuff.addExplosive(w);
    }

    public void giveSecondarySilencer()
    {
        stuff.attachSecondarySilencer();
    }

    public void givePrimarySilencer()
    {
        stuff.attachPrimarySilencer();
    }

    public boolean givePrimaryAmmo()
    {
        return stuff.givePrimaryAmmo();
    }

    public boolean giveSecondaryAmmo()
    {
        return stuff.giveSecondaryAmmo();
    }

    public boolean givePrimaryMag(Weapon w)
    {
        return stuff.givePrimaryMag(w);
    }

    public boolean giveSecondaryMag(Weapon w)
    {
        return stuff.giveSecondaryMag(w);
    }

    public boolean giveExplosiveAmmo(Weapon w)
    {
        return stuff.giveExplosiveAmmo(w);
    }

    public boolean givePrimaryMag()
    {
        return stuff.givePrimaryMag();
    }

    public boolean giveSecondaryMag()
    {
        return stuff.giveSecondaryMag();
    }

    public boolean giveExplosiveAmmo()
    {
        return stuff.giveExplosiveAmmo();
    }
    

    public void giveBoosterKit()
    {
        maxHealth += 10;
        fullHeal(maxHealth);
    }

    /*
    public void setCapacity(Byte cap)
    {
        Byte x = 0;
        while (x < cap)
        {
            giveBoosterKit();
            x++;
        }
    }
     *
     */

    /*
    public void setAmmo(short[] ammo)
    {
        stuff.setAmmo(ammo);
    }
     * 
     */

    public void setItems(byte[] items)
    {
        stuff.setItems(items);
    }

    public void replenish()
    {
        fullHeal(maxHealth);
    }

    public void giveMineDetector()
    {
        stuff.addMineDetector();
    }

    public void giveBodyArmor()
    {
        stuff.addBodyArmor();
    }

    public void giveNVG()
    {
        stuff.addNVG();
    }

    public void giveGasMask()
    {
        stuff.addGasMask();
    }

    public boolean isAREquipped()
    {
        if (stuff.getSelectedWeapon() == null)
        {
            return false;
        }
        return (stuff.getSelectedWeapon().getType() == ItemType.ASSAULT_RIFLE);
    }

    public boolean isSMGEquipped()
    {
        if (stuff.getSelectedWeapon() == null)
        {
            return false;
        }
        return (stuff.getSelectedWeapon().getType() == ItemType.SMG);
    }

    public boolean isShotgunEquipped()
    {
        if (stuff.getSelectedWeapon() == null)
        {
            return false;
        }
        return (stuff.getSelectedWeapon().getType() == ItemType.SHOTGUN);
    }

    public boolean isGrenadeEquipped()
    {
        if (stuff.getSelectedWeapon() == null)
        {
            return false;
        }
        return (stuff.getSelectedWeapon().getType() == ItemType.GRENADE);
    }

    public boolean isC4Equipped()
    {
        if (stuff.getSelectedWeapon() == null)
        {
            return false;
        }
        return (stuff.getSelectedWeapon().getType() == ItemType.C4);
    }

    public boolean isPistolEquipped()
    {
        if (stuff.getSelectedWeapon() == null)
        {
            return false;
        }
        return (stuff.getSelectedWeapon().getType() == ItemType.PISTOL);
    }

    public boolean isTranqEquipped()
    {
        if (stuff.getSelectedWeapon() == null)
        {
            return false;
        }
        return (stuff.getSelectedWeapon().getType() == ItemType.TRANQ_PISTOL);
    }

    public boolean isMedkitEquipped()
    {
        return (stuff.getSelectedItem().getType() == ItemType.MEDKIT);
    }

    public int getAmmo()
    {
        if (stuff.getSelectedWeapon() != null)
        {
            return stuff.getSelectedWeapon().getAmmo();
        }
        return 0;
    }

    public Weapon getWeapon()
    {
        if (stuff.getSelectedWeapon() != null)
        {
            return stuff.getSelectedWeapon();
        }
        else
        {
            return knife;
        }
    }

    public Item getItem()
    {
        return stuff.getSelectedItem();
    }

    public void giveCardKey(ItemType cardType)
    {
        stuff.setSecurityCardLevel(cardType);
    }

    public void resetCardKey()
    {
        stuff.resetSecurityCardLevel();
    }

    public int getCardKey()
    {
        return stuff.getSecurityCardLevel();
    }

    public Bullet attack(short bulletSprite, int targetX, int targetY, int shot, int rank)
    {
        int offsetX = 0;
        int offsetY = 0;

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

        if (stuff.getSelectedWeapon().getType() != ItemType.KNIFE)
        {
            if (stuff.getSelectedWeapon().getType() != ItemType.SHOTGUN)
            {
                stuff.getSelectedWeapon().subtractAmmo();
            }

            int dx;
            int dy;
            float distance;
            float vX = 0.0f;
            float vY = 0.0f;

            if ((this.getDirection() == Direction.UP))
            {
                vX = 0.0f;
                vY = -1.0f;
            }
            if (this.getDirection() == Direction.DOWN)
            {
                vX = 0.0f;
                vY = 1.0f;
            }
            else if (this.getDirection() == Direction.LEFT)
            {
                vY = 0.0f;
                vX = -1.0f;
            }
            else if (this.getDirection() == Direction.RIGHT)
            {
                vY = 0.0f;
                vX = 1.0f;
            }            

            int spread = 0;
            if (shot != 0)
            {
                if ((shot < 70) && (shot > -70))
                {
                    spread = (shot - rng.nextInt(shot * 2));
                }                
                else
                {
                    if (shot == 70)
                    {
                        shot = 50;
                    }
                    if (shot == -70)
                    {
                        shot = -50;
                    }                    

                    spread = (shot / 10) * 7;
                }
            }

            if ((this.getDirection() == Direction.UP) || (this.getDirection() == Direction.DOWN))
            {
                targetX += spread;
            }
            else if ((this.getDirection() == Direction.LEFT) || (this.getDirection() == Direction.RIGHT))
            {
                targetY += spread;
            }

            dx = this.getX() - targetX;
            dy = this.getY() - targetY;

            distance = (float)Math.sqrt((double)((dx * dx) + (dy * dy)));

            vX = dx/distance;
            vY = dy/distance;

            if (stuff.getSelectedWeapon().getType() == ItemType.GRENADE)
            {
                vX = vX / 4;
                vY = vY / 4;
            }            
            
            Bullet b = new Bullet(this.getX() + offsetX, this.getY() + offsetY, vX, vY,
                    bulletSprite, stuff.getSelectedWeapon().getDamage(), stuff.getSelectedWeapon().getSleep(), true, stuff.getSelectedWeapon().getRange(), rank);

            if (stuff.getSelectedWeapon().getType() == ItemType.GRENADE)
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

    public void subtractAmmo()
    {
        stuff.getSelectedWeapon().subtractAmmo();
    }
}
