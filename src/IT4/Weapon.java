//This is a parent class to all Weapons

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class Weapon extends Item
{
    public static Weapon create(int x, int y, ItemType it, int rank)
    {
        String name = "Knife";
        int damage = 16;
        int accuracy = 100;
        int range = 40;
        int staminaDamage = 0;
        int magCapacity = 0;
        int totalCapacity = 0;
        int fireRate = 1;
        int silencerDurability = 0;
        boolean suppressed = false;
        String description = "Standard Combat Knife.";
        short id = 73;

        if (it == ItemType.PISTOL)
        {
            id = 71;

            if (rank == 1)
            {
                //22|85|800|0|7|35|1|14|false
                name = "M1911A1";
                damage = 26;
                accuracy = 80;
                range = 800;
                staminaDamage = 0;
                magCapacity = 7;
                totalCapacity = 35;
                fireRate = 1;
                silencerDurability = 14;
                suppressed = false;
                id = 449;

                description = "Pistol Rank 1. American origin, .45 caliber. Antiquated design that combines reliable stopping power with decent accuracy. Has a magazine capacity of 7 rounds. Can attach low-durability suppressors.";
            }
            else if (rank == 2)
            {
                //35|90|800|0|10|50|1|30|false
                name = "CP45";
                damage = 36;
                accuracy = 85;
                range = 800;
                staminaDamage = 0;
                magCapacity = 10;
                totalCapacity = 50;
                fireRate = 1;
                silencerDurability = 30;
                suppressed = false;
                id = 71;

                description = "Pistol Rank 2. Belgian origin, .45 caliber. Modern design with high stopping power and good accuracy. Has a magazine capacity of 10 rounds. Can attach medium-durability suppressors.";
            }
            else
            {
                //40|90|800|0|12|60|1|999|true
                name = "SQP";
                damage = 41;
                accuracy = 90;
                range = 800;
                staminaDamage = 0;
                magCapacity = 12;
                totalCapacity = 60;
                fireRate = 1;
                silencerDurability = 999;
                suppressed = true;
                id = 450;

                description = "Pistol Rank 3. German origin, .45 caliber. Custom design with high stopping power and excellent accuracy. Has a magazine capacity of 12 rounds. Equipped with an infinitely-durable suppressor.";
            }
        }
        else if (it == ItemType.TRANQ_PISTOL)
        {
            id = 250;

            if (rank == 1)
            {
                //1|80|240|12|3|9|0|9|true
                name = "TX-1";
                damage = 1;
                accuracy = 80;
                range = 240;
                staminaDamage = 12;
                magCapacity = 3;
                totalCapacity = 9;
                fireRate = 0;
                silencerDurability = 9;
                suppressed = true;

                description = "Tranq. Pistol Rank 1. Modified to fire tranquilizer darts. Unknown origin. Darts contain a weak anesthetic. Suffers from limited range. Has a magazine capacity of 3 darts. Equipped with a low-durability suppressor.";
            }
            else if (rank == 2)
            {
                //1|85|300|20|3|12|0|12|true
                name = "TX-2";
                damage = 1;
                accuracy = 85;
                range = 300;
                staminaDamage = 20;
                magCapacity = 3;
                totalCapacity = 12;
                fireRate = 0;
                silencerDurability = 12;
                suppressed = true;

                description = "Tranq. Pistol Rank 2. Modified to fire tranquilizer darts. Unknown origin. Darts contain an upgraded anesthetic. Suffers from limited range. Has a magazine capacity of 3 darts. Equipped with a medium/low-durability suppressor.";
            }
            else
            {
                //1|90|360|30|5|20|0|30|true
                name = "TX-3";
                damage = 1;
                accuracy = 90;
                range = 360;
                staminaDamage = 30;
                magCapacity = 5;
                totalCapacity = 20;
                fireRate = 0;
                silencerDurability = 30;
                suppressed = true;

                description = "Tranq. Pistol Rank 3. Modified to fire tranquilizer darts. Unknown origin. Darts contain a longer-lasting anesthetic. Features an improved effective range. Has a magazine capacity of 5 darts. Equipped with a medium durability suppressor.";
            }
        }
        else if (it == ItemType.SMG)
        {
            id = 205;

            if (rank == 1)
            {
                //20|50|720|0|20|100|92|10|false
                name = "PC9";
                damage = 20;
                accuracy = 50;
                range = 720;
                staminaDamage = 0;
                magCapacity = 20;
                totalCapacity = 100;
                fireRate = 92;
                silencerDurability = 20;
                suppressed = false;
                id = 451;

                description = "SMG Rank 1. American origin, civilain model. Retro-fitted for moderately fast fully-automatic fire. Features weak stopping power and poor accuracy. Has a magazine capacity of 20 rounds. Can attach low-durability suppressors.";
            }
            else if (rank == 2)
            {
                //25|70|840|0|40|160|98|90|false
                name = "MP467";
                damage = 25;
                accuracy = 70;
                range = 840;
                staminaDamage = 0;
                magCapacity = 40;
                totalCapacity = 160;
                fireRate = 98;
                silencerDurability = 120;
                suppressed = false;
                id = 205;

                description = "SMG Rank 2. German origin, personal defense weapon. Preferred weapon of the opposition special forces. Features fast fully-automatic fire. Boasts improved stopping power and accuracy. Has a magazine capacity of 40 rounds. Can attach medium-durability suppressors.";
            }
            else
            {
                //25|65|800|0|40|200|99|250|true
                name = "PDW-90";
                damage = 25;
                accuracy = 65;
                range = 800;
                staminaDamage = 0;
                magCapacity = 50;
                totalCapacity = 250;
                fireRate = 99;
                silencerDurability = 250;
                suppressed = true;
                id = 452;

                description = "SMG Rank 3. Belgian origin, personal defense weapon. Features fully-automatic fire at a blistering speed. Decent stopping power and accuracy. Has a magazine capacity of 50 rounds. Equipped with a high-durability suppressor.";
            }
        }
        else if (it == ItemType.ASSAULT_RIFLE)
        {
            id = 73;

            if (rank == 1)
            {
                //30|85|1200|0|20|100|1|20|false
                name = "CR-16";
                damage = 30;
                accuracy = 85;
                range = 1200;
                staminaDamage = 0;
                magCapacity = 20;
                totalCapacity = 100;
                fireRate = 1;
                silencerDurability = 20;
                suppressed = false;
                id = 453;

                description = "Assault Rifle Rank 1. American origin, civilian model. Features semi-automatic fire with good stopping power and accuracy. Has a magazine capacity of 20 rounds. Can attach a low-durability suppressor.";
            }
            else if (rank == 2)
            {
                //40|90|1200|0|30|150|95|30|false
                name = "FARA 83";
                damage = 40;
                accuracy = 90;
                range = 1200;
                staminaDamage = 0;
                magCapacity = 30;
                totalCapacity = 150;
                fireRate = 95;
                silencerDurability = 30;
                suppressed = false;
                id = 454;

                description = "Assault Rifle Rank 2. Argentine origin. Standard-issue rifle of the enemy. Features fully-automatic fire with good stopping power and accuracy. Has a magazine capacity of 30 rounds. Can attach a low-durability suppressor.";
            }
            else
            {
                //50|95|1000|0|30|180|98|60|false
                name = "MK-4";
                damage = 50;
                accuracy = 95;
                range = 1000;
                staminaDamage = 0;
                magCapacity = 30;
                totalCapacity = 180;
                fireRate = 98;
                silencerDurability = 60;
                suppressed = false;
                id = 455;

                description = "Assault Carbine Rank 3. American origin. Features improved fully-automatic fire with high stopping power and impeccable accuracy. Has a magazine capacity of 30 rounds. Can attach a medium-durability suppressor.";
            }
        }
        else if (it == ItemType.SHOTGUN)
        {
            id = 206;

            if (rank == 1)
            {
                //15|--|550|0|5|20|1|0|false
                name = "WS-08";
                damage = 15;
                accuracy = 70;
                range = 550;
                staminaDamage = 0;
                magCapacity = 5;
                totalCapacity = 20;
                fireRate = 1;
                silencerDurability = 0;
                suppressed = false;
                id = 456;

                description = "Shotgun Rank 1. American origin. Features semi-automatic fire. Does high damage at close ranges. Has a magazine capacity of 5 shells.";
            }
            else if (rank == 2)
            {
                //20|70|550|0|8|32|1|0|false
                name = "SAS-12";
                damage = 20;
                accuracy = 70;
                range = 550;
                staminaDamage = 0;
                magCapacity = 8;
                totalCapacity = 32;
                fireRate = 1;
                silencerDurability = 0;
                suppressed = false;
                id = 457;

                description = "Shotgun Rank 2. Italian origin. Features semi-automatic fire. Does very high damage at close ranges. Has a magazine capacity of 8 shells.";
            }
            else
            {
                //15|70|400|0|15|75|87|0|false
                name = "SSG-12000";
                damage = 15;
                accuracy = 70;
                range = 400;
                staminaDamage = 0;
                magCapacity = 15;
                totalCapacity = 75;
                fireRate = 87;
                silencerDurability = 0;
                suppressed = false;
                id = 458;

                description = "Shotgun Rank 3. Russian origin. Does high damage at close ranges. Boasts a fully-automatic rate of fire at the cost of a reduced effective range. Has a magazine capacity of 15 shells.";
            }
        }
        else if (it == ItemType.GRENADE)
        {
            id = 191;
            
            if (rank == 1)
            {
                name = "Grenade";
                damage = 0;
                accuracy = 90;
                range = 800; //Travels at half speed, real range is 400
                staminaDamage = 0;
                magCapacity = 1;
                totalCapacity = 4;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "Standard-issue fragmentation grenade.";
            }
            else if (rank == 2)
            {
                name = "Grenade R2";
                damage = 0;
                accuracy = 90;
                range = 800; //Travels at half speed, real range is 400
                staminaDamage = 0;
                magCapacity = 1;
                totalCapacity = 4;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "Standard-issue fragmentation grenade. Features improved damage.";
            }
            else
            {
                name = "Grenade R3";
                damage = 0;
                accuracy = 90;
                range = 800; //Travels at half speed, real range is 400
                staminaDamage = 0;
                magCapacity = 1;
                totalCapacity = 6;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "High-explosive fragmentation grenade. Improved damage and capacity.";
            }
        }
        else if (it == ItemType.C4)
        {
            id = 368;
            
            if (rank == 1)
            {
                name = "C4";
                damage = 0;
                accuracy = 100;
                range = 0; //Placed
                staminaDamage = 0;
                magCapacity = 1;
                totalCapacity = 4;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "C4 Plastic Explosives. Press [Space] to set, and [Ctrl] to detonate.";
            }
            else if (rank == 2)
            {
                name = "C4 R2";
                damage = 0;
                accuracy = 100;
                range = 0; //Placed
                staminaDamage = 0;
                magCapacity = 1;
                totalCapacity = 4;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "C4 Plastic Explosives. Press [Space] to set, and [Ctrl] to detonate. Features improved damage.";
            }
            else
            {
                name = "C4 R3";
                damage = 0;
                accuracy = 100;
                range = 0; //Placed
                staminaDamage = 0;
                magCapacity = 1;
                totalCapacity = 6;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "C4 Plastic Explosives. Press [Space] to set, and [Ctrl] to detonate. Improved damage and capacity.";
            }
        }
        else
        {
            rank = 0;
        }

        Weapon w = new Weapon(id, x, y, it, rank, name, damage, accuracy, range, staminaDamage, magCapacity, totalCapacity, fireRate, silencerDurability, suppressed);
        w.description = description;
        return w;
    }

    public static Weapon create(ItemType it, int rank)
    {        
        return create(0, 0, it, rank);
    }

    public static Weapon load(String ser)
    {
        ItemType it = ItemType.PISTOL;
        int rank = 1;
        int ammoInMag = 0;
        int ammo = 0;
        int suppressor = 0;

        //this.type.toString() + "|" + rank + "|" + ammoInMag + "|" + ammo + "|" + suppressor;
        String[] splits = ser.split("\\|");

        if (splits.length < 5)
        {
            return null;
        }

        it = ItemType.valueOf(splits[0]);
        rank = Integer.parseInt(splits[1]);
        ammoInMag = Integer.parseInt(splits[2]);
        ammo = Integer.parseInt(splits[3]);
        suppressor = Integer.parseInt(splits[4]);

        Weapon w = create(it, rank);

        w.ammoInMag = ammoInMag;
        w.ammo = ammo;
        w.suppressor = suppressor;

        return w;
    }

    //Damage|Accuracy|Range|StaminaDamage|Mag.Capacity|TotalCapacity|FireRate|SilencerDurability|DefaultSilenced
    public int damage;
    public int accuracy;
    public int range;
    public int staminaDamage;
    public int magCapacity;
    public int ammoInMag;
    public int fireRate;
    public int suppressorDurability;
    public int suppressor;
    public int maxAmmo;
    public int ammo;

    public Weapon(short id, int x, int y, ItemType type, int rank, String name, int damage, int accuracy, int range, int staminaDamage, int magCapacity, int totalCapacity, int fireRate, int silencerDurability, boolean suppressed)
    {
        super(id, x, y, type, rank);
        this.weaponName = name;
        this.type = type;
        this.damage = damage;
        this.accuracy = accuracy;
        this.range = range;
        this.staminaDamage = staminaDamage;
        this.magCapacity = magCapacity;
        this.maxAmmo = totalCapacity;
        this.fireRate = fireRate;
        this.suppressorDurability = silencerDurability;

        if (suppressed)
        {
            this.suppressor = suppressorDurability;
        }
        else
        {
            this.suppressor = 0;
        }
        
        this.ammoInMag = magCapacity;
        this.ammo = magCapacity;
        this.isWeapon = true;
    }

    public Weapon(Weapon w)
    {
        super(w.getID(), w.getX(), w.getY(), w.getType(), w.rank);
        this.weaponName = w.weaponName;
        this.type = w.type;
        this.damage = w.damage;
        this.accuracy = w.accuracy;
        this.range = w.range;
        this.staminaDamage = w.staminaDamage;
        this.magCapacity = w.magCapacity;
        this.maxAmmo = w.maxAmmo;
        this.fireRate = w.fireRate;
        this.suppressorDurability = w.suppressorDurability;
        this.suppressor = w.suppressor;

        this.ammoInMag = w.ammoInMag;
        this.ammo = w.ammo;
        this.isWeapon = true;
        this.description = w.description;
    }

    @Override
    public Weapon copy()
    {
        return new Weapon(this);
    }
    
    public int getDamage()
    {
        return damage;
    }

    public boolean getSleep()
    {
        return this.staminaDamage > 0;
    }

    public int getRange()
    {
        return range;
    }

    public void attachSuppressor()
    {
        this.suppressor = suppressorDurability;
    }

    public boolean isSuppressed()
    {
        return ((this.suppressor > 0) || (this.type == ItemType.KNIFE));
    }

    public int addAmmo(int add)
    {
        int oldAmmo = ammo;

        ammo = ammo + (add * magCapacity);

        //Bind the weapon's ammo to its maxAmmo value
        if (ammo > maxAmmo)
        {
            ammo = maxAmmo;
        }
        
        return (ammo - oldAmmo);
    }

    public int addExactAmmo(int add)
    {
        int oldAmmo = ammo;

        ammo = ammo + add;

        //Bind the weapon's ammo to its maxAmmo value
        if (ammo > maxAmmo)
        {
            ammo = maxAmmo;
        }

        return (ammo - oldAmmo);
    }

    public void subtractAmmo()
    {
        //1 shot
        ammo--;

        if (suppressor < 999)
        {
            suppressor--;
        }

        //Do not allow ammo to be less than 0
        if (ammo < 0)
        {
            ammo = 0;
        }

        if (suppressor < 0)
        {
            suppressor = 0;
        }
    }

    public int getAmmo()
    {
        return ammo;
    }

    public void setAmmo(int a)
    {
        ammo = a;
    }

    public int getMaxAmmo()
    {
        return maxAmmo;
    }

    @Override
    public String toString()
    {
        String retval = weaponName;
        if (damage > 0)
        {
            if (suppressor > 0)
            {
                retval += "[S]";
            }
        }
        return retval;
    }

    public String getSilencerStr()
    {
        String retval = "";
        if ((damage > 0) && (suppressor > 0))
        {
            if (suppressor < 999)
            {
                retval = "[S-" + suppressor + "]";
            }
        }
        return retval;
    }

    public String serialize()
    {
        return this.type.toString() + "|" + rank + "|" + ammoInMag + "|" + ammo + "|" + suppressor;
    }
}
