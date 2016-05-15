/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class Boss extends NPC
{
    private Dialog pre;
    private Dialog post;
    public byte speed = 15;
    public byte shotInterval = 40;
    public String name = "Boss";
    public String dispName = "Boss";
    private int maxHealth;
    public Warp event = null;

    public Boss(short bossID, int x, int y, GuardType bossType,
                int bossHealth, int bossDamage, byte speed, int viewDistance, boolean bdyArmor, Warp killEvent)
    {
        super(bossID, x, y, Direction.DOWN, bossHealth, NPCStatus.ALERT, false, bossDamage, null, bossType, 0.5f, 1.0f, false);
        this.BOSS = true;
        this.bodyArmor = bdyArmor;
        this.viewDistance = viewDistance;
        this.speed = speed;
        this.event = killEvent;

        //Set the speed of the gun based on the damage it does.
        //I know this is kind of strange, but I really didn't feel like adding
        //Another field to the boss element in the data file...
        if ((this.getWeaponDamage() >= 20) && (this.getWeaponDamage() < 25))
        {
            shotInterval = 5;
        }
        else if (this.getWeaponDamage() >= 25)
        {
            shotInterval = 50;
        }
        else //(Less than 20 damage per hit)
        {
            shotInterval = 20;
        }

        maxHealth = this.getCurrentHealth();

    }

    @Override
    public NPC copy()
    {
        return new Boss(this.getID(), this.getX(), this.getY(),
                this.getType(), this.getCurrentHealth(), this.getWeaponDamage(), this.speed, this.viewDistance, this.bodyArmor, this.event);
    }

    public Boss copyBoss()
    {
        Boss b = new Boss(this.getID(), this.getX(), this.getY(), this.getType(), this.getMaxHealth(), this.getWeaponDamage(), this.speed, this.viewDistance, this.bodyArmor, this.event);

        b.name = this.name;
        b.dispName = this.dispName;

        b.pre = new Dialog(this.pre, "Boss fight", false);
        b.post = new Dialog(this.post, "Boss defeated", false);

        return b;
    }

    public void setPreDialog(Dialog d)
    {
        pre = d;
    }

    public void setPostDialog(Dialog d)
    {
        post = d;
    }

    public Dialog getPreDialog()
    {
        return pre;
    }

    public Dialog getPostDialog()
    {
        return post;
    }

    public int getMaxHealth()
    {
        return maxHealth;
    }
}
