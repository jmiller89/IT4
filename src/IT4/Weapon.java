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
        int fireSound = -999;

        if (it == ItemType.PISTOL)
        {
            id = 71;
            fireSound = SFX.PISTOL_GUNSHOT;

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
            else if (rank == 3)
            {
                //41|90|800|0|12|60|1|999|true
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

                description = "Pistol Rank 3. German origin, .45 caliber. Tactical design with high stopping power and excellent accuracy. Has a magazine capacity of 12 rounds. Equipped with an infinitely-durable suppressor.";
            }
            else if (rank == 4)
            {
                //24|60|640|0|17|85|96|68|false
                name = "J18";
                damage = 24;
                accuracy = 60;
                range = 640;
                staminaDamage = 0;
                magCapacity = 17;
                totalCapacity = 85;
                fireRate = 96;
                silencerDurability = 68;
                suppressed = false;
                id = 553;

                description = "Machine Pistol Rank 4. Austrian origin, 9mm. Features fast fully automatic fire, at the cost of accuracy. 9mm bullet has reduced stopping power. Has a magazine capacity of 17 rounds. Can attach medium-durability suppressors.";
            }
            else
            {
                //55|92|800|0|7|56|1|999|true
                name = "M1911A1 Custom";
                damage = 55;
                accuracy = 92;
                range = 800;
                staminaDamage = 0;
                magCapacity = 7;
                totalCapacity = 56;
                fireRate = 1;
                silencerDurability = 999;
                suppressed = true;
                id = 556;

                description = "Pistol Rank 5. American origin, .45 caliber. Custom design with high stopping power and near-perfect accuracy. Has a magazine capacity of 7 rounds. Equipped with an infinitely-durable suppressor.";
            }
        }
        else if (it == ItemType.TRANQ_PISTOL)
        {
            id = 250;
            fireSound = SFX.PISTOL_GUNSHOT;

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
            else if (rank == 3)
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
            else if (rank == 4)
            {
                //1|90|400|40|5|20|0|30|true
                name = "TX-4";
                damage = 1;
                accuracy = 90;
                range = 400;
                staminaDamage = 40;
                magCapacity = 5;
                totalCapacity = 20;
                fireRate = 0;
                silencerDurability = 30;
                suppressed = true;

                description = "Tranq. Pistol Rank 4. Modified to fire tranquilizer darts. Unknown origin. Darts contain a longer-lasting anesthetic. Features an improved effective range. Has a magazine capacity of 5 darts. Equipped with a medium durability suppressor.";
            }
            else
            {
                //1|90|400|40|5|25|0|999|true
                name = "TX-5";
                damage = 1;
                accuracy = 90;
                range = 400;
                staminaDamage = 40;
                magCapacity = 5;
                totalCapacity = 25;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "Tranq. Pistol Rank 5. Modified to fire tranquilizer darts. Unknown origin. Darts contain a long-lasting anesthetic. Features a higher total ammo capacity. Has a magazine capacity of 5 darts. Equipped with an infinitely durable suppressor.";
            }
        }
        else if (it == ItemType.SMG)
        {
            id = 205;
            fireSound = SFX.PISTOL_GUNSHOT;

            if (rank == 1)
            {
                //20|60|720|0|20|100|94|10|false
                name = "FMK-3";
                damage = 20;
                accuracy = 60;
                range = 640;
                staminaDamage = 0;
                magCapacity = 20;
                totalCapacity = 100;
                fireRate = 93;
                silencerDurability = 20;
                suppressed = false;
                id = 451;

                description = "SMG Rank 1. Argentine origin, 9mm. Features moderately fast fully-automatic fire. Has weak stopping power and poor accuracy. Has a magazine capacity of 20 rounds. Can attach low-durability suppressors.";
            }
            else if (rank == 2)
            {
                //25|70|840|0|40|160|98|90|false
                name = "MP467";
                damage = 25;
                accuracy = 70;
                range = 760;
                staminaDamage = 0;
                magCapacity = 40;
                totalCapacity = 160;
                fireRate = 98;
                silencerDurability = 120;
                suppressed = false;
                id = 205;

                description = "SMG Rank 2. German origin, 4.6mm personal defense weapon. Preferred weapon of the opposition special forces. Features fast fully-automatic fire. Boasts improved stopping power and accuracy. Has a magazine capacity of 40 rounds. Can attach medium-durability suppressors.";
            }
            else if (rank == 3)
            {
                //25|65|800|0|40|200|99|250|true
                name = "PDW-90";
                damage = 25;
                accuracy = 65;
                range = 640;
                staminaDamage = 0;
                magCapacity = 50;
                totalCapacity = 250;
                fireRate = 99;
                silencerDurability = 255;
                suppressed = true;
                id = 452;

                description = "SMG Rank 3. Belgian origin, 5.7mm personal defense weapon. Features fully-automatic fire at a blistering speed. Decent stopping power and accuracy. Has a magazine capacity of 50 rounds. Equipped with a high-durability suppressor.";
            }
            else if (rank == 4)
            {
                //55|76|640|0|30|150|93|999|true
                name = "M3A1SD";
                damage = 55;
                accuracy = 76;
                range = 640;
                staminaDamage = 0;
                magCapacity = 30;
                totalCapacity = 150;
                fireRate = 92;
                silencerDurability = 999;
                suppressed = true;
                id = 554;

                description = "SMG Rank 4. American origin, .45 caliber. Modern update of a dated design. Features slow fully-automatic fire, improving accuracy. Has high stopping power. Has a magazine capacity of 30 rounds. Equipped with a suppressor that never wears out.";
            }
            else
            {
                //41|80|760|0|40|200|98|999|true
                name = "MP467SD";
                damage = 41;
                accuracy = 80;
                range = 760;
                staminaDamage = 0;
                magCapacity = 40;
                totalCapacity = 200;
                fireRate = 98;
                silencerDurability = 999;
                suppressed = true;
                id = 555;

                description = "SMG Rank 5. German origin, 4.6mm personal defense weapon. The ultimate special ops weapon. Features fast fully-automatic fire. Boasts high accuracy, and decent stopping power. Has a magazine capacity of 40 rounds. Equipped with a suppressor that never wears out.";
            }
        }
        else if (it == ItemType.ASSAULT_RIFLE)
        {
            id = 73;
            fireSound = SFX.RIFLE_GUNSHOT;

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

                description = "Assault Rifle Rank 1. American origin, 5.56mm civilian model. Features semi-automatic fire with good stopping power and accuracy. Has a magazine capacity of 20 rounds. Can attach a low-durability suppressor.";
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

                description = "Assault Rifle Rank 2. Argentine origin, 5.56mm. Standard-issue rifle of the enemy. Features fully-automatic fire with good stopping power and accuracy. Has a magazine capacity of 30 rounds. Can attach a low-durability suppressor.";
            }
            else if (rank == 3)
            {
                //50|95|1000|0|30|180|98|60|false
                name = "MK-4";
                damage = 50;
                accuracy = 95;
                range = 1000;
                staminaDamage = 0;
                magCapacity = 30;
                totalCapacity = 180;
                fireRate = 97;
                silencerDurability = 60;
                suppressed = false;
                id = 455;

                description = "Assault Carbine Rank 3. American origin, 5.56mm. Features improved fully-automatic fire with high stopping power and impeccable accuracy. Has a magazine capacity of 30 rounds. Can attach a medium-durability suppressor.";
            }
            else if (rank == 4)
            {
                //62|85|1000|0|30|180|94|90|false
                name = "RK-104u";
                damage = 62;
                accuracy = 85;
                range = 1000;
                staminaDamage = 0;
                magCapacity = 30;
                totalCapacity = 180;
                fireRate = 95;
                silencerDurability = 90;
                suppressed = false;
                id = 552;

                description = "Assault Carbine Rank 4. Russian origin, 7.62mm. Features fully-automatic fire with very high stopping power, but average accuracy. Has a magazine capacity of 30 rounds. Can attach a medium-durability suppressor.";
            }
            else
            {
                //70|88|1200|0|20|120|95|100|false
                name = "SABR";
                damage = 70;
                accuracy = 90;
                range = 1200;
                staminaDamage = 0;
                magCapacity = 20;
                totalCapacity = 120;
                fireRate = 96;
                silencerDurability = 120;
                suppressed = false;
                id = 73;

                description = "Assault Rifle Rank 5. Belgian origin, 7.62mm. Features fully-automatic fire with very high stopping power and good accuracy. Has a magazine capacity of 20 rounds. Can attach a high-durability suppressor.";
            }
        }
        else if (it == ItemType.SHOTGUN)
        {
            id = 206;
            fireSound = SFX.SHOTGUN_GUNSHOT;

            if (rank == 1)
            {
                //15|70|440|0|5|20|1|0|false
                name = "WS-08";
                damage = 15;
                accuracy = 70;
                range = 440;
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
                //20|70|480|0|8|32|1|0|false
                name = "SAS-12";
                damage = 20;
                accuracy = 70;
                range = 480;
                staminaDamage = 0;
                magCapacity = 8;
                totalCapacity = 32;
                fireRate = 1;
                silencerDurability = 0;
                suppressed = false;
                id = 457;

                description = "Shotgun Rank 2. Italian origin. Features semi-automatic fire. Does very high damage at close ranges. Has a magazine capacity of 8 shells.";
            }
            else if (rank == 3)
            {
                //25|70|360|0|15|75|88|0|false
                name = "SSG-12000";
                damage = 25;
                accuracy = 70;
                range = 360;
                staminaDamage = 0;
                magCapacity = 15;
                totalCapacity = 75;
                fireRate = 88;
                silencerDurability = 0;
                suppressed = false;
                id = 458;

                description = "Shotgun Rank 3. Russian origin. Does high damage at close ranges. Boasts a fully-automatic rate of fire at the cost of a reduced effective range. Has a magazine capacity of 15 shells.";
            }
            else if (rank == 4)
            {
                //35|70|480|0|8|32|1|8|false
                name = "SAS-12 Tactical";
                damage = 35;
                accuracy = 70;
                range = 480;
                staminaDamage = 0;
                magCapacity = 8;
                totalCapacity = 32;
                fireRate = 1;
                silencerDurability = 8;
                suppressed = false;
                id = 457;

                description = "Shotgun Rank 4. Italian origin. Features semi-automatic fire. Does very high damage at close ranges. Has a magazine capacity of 8 shells. Can attach low-durability suppressors.";
            }
            else
            {
                //35|70|360|0|15|75|89|15|false
                name = "SSG-12500";
                damage = 35;
                accuracy = 70;
                range = 360;
                staminaDamage = 0;
                magCapacity = 15;
                totalCapacity = 75;
                fireRate = 89;
                silencerDurability = 15;
                suppressed = false;
                id = 458;

                description = "Shotgun Rank 5. Russian origin. Does very high damage at close ranges. Boasts a fully-automatic rate of fire at the cost of a reduced effective range. Has a magazine capacity of 15 shells. Can attach low-durability suppressors.";
            }
        }
        else if (it == ItemType.GRENADE)
        {
            id = 510;
            
            if (rank == 1)
            {
                name = "Grenade";
                damage = 0;
                accuracy = 90;
                range = 800; //Travels at half speed, real range is 400
                staminaDamage = 0;
                magCapacity = 4;
                totalCapacity = 12;
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
                magCapacity = 4;
                totalCapacity = 16;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "Standard-issue fragmentation grenade. Features improved damage and capacity.";
            }
            else if (rank == 3)
            {
                name = "Grenade R3";
                damage = 0;
                accuracy = 90;
                range = 800; //Travels at half speed, real range is 400
                staminaDamage = 0;
                magCapacity = 4;
                totalCapacity = 20;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "High-explosive fragmentation grenade. Improved damage and capacity.";
            }
            else if (rank == 4)
            {
                name = "Grenade R4";
                damage = 0;
                accuracy = 90;
                range = 1000; //Travels at half speed, real range is 500
                staminaDamage = 0;
                magCapacity = 6;
                totalCapacity = 30;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "High-explosive fragmentation grenade. Improved damage. Compact design allows for increased range and higher capacity.";
            }
            else
            {
                name = "Grenade R5";
                damage = 0;
                accuracy = 90;
                range = 1000; //Travels at half speed, real range is 500
                staminaDamage = 0;
                magCapacity = 6;
                totalCapacity = 36;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "High-explosive fragmentation grenade. Improved damage. Compact design allows for increased range and higher capacity.";
            }
        }
        else if (it == ItemType.C4)
        {
            id = 510;
            
            if (rank == 1)
            {
                name = "C4";
                damage = 0;
                accuracy = 100;
                range = 0; //Placed
                staminaDamage = 0;
                magCapacity = 4;
                totalCapacity = 8;
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
                magCapacity = 4;
                totalCapacity = 12;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "C4 Plastic Explosives. Press [Space] to set, and [Ctrl] to detonate. Features improved damage and capacity.";
            }
            else if (rank == 3)
            {
                name = "C4 R3";
                damage = 0;
                accuracy = 100;
                range = 0; //Placed
                staminaDamage = 0;
                magCapacity = 4;
                totalCapacity = 16;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "C4 Plastic Explosives. Press [Space] to set, and [Ctrl] to detonate. Improved damage and capacity.";
            }
            else if (rank == 4)
            {
                name = "C4 R4";
                damage = 0;
                accuracy = 100;
                range = 0; //Placed
                staminaDamage = 0;
                magCapacity = 4;
                totalCapacity = 20;
                fireRate = 0;
                silencerDurability = 999;
                suppressed = true;

                description = "C4 Plastic Explosives. Press [Space] to set, and [Ctrl] to detonate. Improved damage and capacity.";
            }
            else
            {
                name = "C4 R5";
                damage = 0;
                accuracy = 100;
                range = 0; //Placed
                staminaDamage = 0;
                magCapacity = 4;
                totalCapacity = 24;
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

        Weapon w = new Weapon(id, x, y, it, rank, name, damage, accuracy, range, staminaDamage, magCapacity, totalCapacity, fireRate, silencerDurability, suppressed, fireSound);
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
    public int sound = SFX.PISTOL_GUNSHOT;

    public Weapon(short id, int x, int y, ItemType type, int rank, String name, int damage, int accuracy, int range, int staminaDamage, int magCapacity, int totalCapacity, int fireRate, int silencerDurability, boolean suppressed, int sound_)
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
        this.sound = sound_;

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
        this.sound = w.sound;

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
