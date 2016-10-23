//This is the backpack carried by the player

package IT4;

/**
 *
 * @author Jim (Admin)
 */

public class Backpack
{
    //This may be updated in the future
    public Weapon sidearm;
    public Weapon primary;
    public Weapon explosive;
    
    public Item[] items;
    public Weapon selectedWeapon;
    public Item selectedItem;
    public int maxHealthKits;
    public int healthKits;
    public int securityCardLevel;
    public int currItemIndex = 0;
    
    //Ctor
    public Backpack()
    {
        sidearm = null;
        primary = null;
        explosive = null;

        selectedWeapon = null;

        items = new Item[6];
        items[0] = new Item((short)70, 0, 0, ItemType.MEDKIT, 1);
        items[1] = null;
        items[2] = null;
        items[3] = null;
        items[4] = null;
        items[5] = null;

        selectedItem = items[0];

        maxHealthKits = 3;
        healthKits = 0;
        securityCardLevel = 0;
    }

    //Copy ctor
    public Backpack(Backpack b)
    {
        if (b.sidearm != null)
        {
            sidearm = b.sidearm.copy();
        }

        if (b.primary != null)
        {
            primary = b.primary.copy();
        }

        if (b.explosive != null)
        {
            explosive = b.explosive.copy();
        }

        if (b.selectedWeapon != null)
        {
            if ((b.selectedWeapon.type == ItemType.PISTOL) || (b.selectedWeapon.type == ItemType.TRANQ_PISTOL))
            {
                selectedWeapon = sidearm;
            }
            else if((b.selectedWeapon.type == ItemType.ASSAULT_RIFLE) || (b.selectedWeapon.type == ItemType.SMG) || (b.selectedWeapon.type == ItemType.SHOTGUN))
            {
                selectedWeapon = primary;
            }
            else if((b.selectedWeapon.type == ItemType.GRENADE) || (b.selectedWeapon.type == ItemType.C4))
            {
                selectedWeapon = explosive;
            }
        }

        items = new Item[6];
        items[0] = new Item((short)70, 0, 0, ItemType.MEDKIT, 1);

        if (b.hasGasMask())
        {
            addGasMask();
        }
        if (b.hasNVG())
        {
            addNVG();
        }
        if (b.hasBodyArmor())
        {
            addBodyArmor();
        }
        if (b.hasMineDetector())
        {
            addMineDetector();
        }

        if (b.selectedItem.getType() == ItemType.GASMASK)
        {
            selectedItem = items[2];
        }
        else if (b.selectedItem.getType() == ItemType.NVG)
        {
            selectedItem = items[3];
        }
        else if (b.selectedItem.getType() == ItemType.BODY_ARMOR)
        {
            selectedItem = items[4];
        }
        else if (b.selectedItem.getType() == ItemType.MINE_DETECTOR)
        {
            selectedItem = items[5];
        }
        else if ((b.selectedItem.getType() == ItemType.CARDKEY_1) || (b.selectedItem.getType() == ItemType.CARDKEY_2)
                || (b.selectedItem.getType() == ItemType.CARDKEY_3) || (b.selectedItem.getType() == ItemType.CARDKEY_4)
                || (b.selectedItem.getType() == ItemType.CARDKEY_5) || (b.selectedItem.getType() == ItemType.CARDKEY_6)
                || (b.selectedItem.getType() == ItemType.CARDKEY_7) || (b.selectedItem.getType() == ItemType.CARDKEY_8)
                || (b.selectedItem.getType() == ItemType.CARDKEY_9) || (b.selectedItem.getType() == ItemType.CARDKEY_10))
        {
            selectedItem = items[1];
        }
        else
        {
            selectedItem = items[0];
        }

        currItemIndex = b.currItemIndex;
        
        maxHealthKits = b.getMaxNumHealthKits();
        healthKits = b.getNumHealthKits();
        setSecurityCardLevel(b.getSecurityCardType());

    }

    public void clear()
    {
        healthKits = 0;
        securityCardLevel = 0;
        selectedWeapon = null;
        selectedItem = items[0];

        sidearm = null;
        primary = null;
        explosive = null;

        for(int j = 1; j < items.length; j++)
        {
            items[j] = null;
        }
    }

    public boolean giveSecondaryAmmo()
    {
        if (sidearm != null)
        {
            return sidearm.addAmmo(3) != 0;
        }
        return false;
    }

    public boolean givePrimaryAmmo()
    {
        if (primary != null)
        {
            return primary.addAmmo(3) != 0;
        }
        return false;
    }

    public boolean giveSecondaryMag()
    {
        if (sidearm != null)
        {
            return sidearm.addAmmo(1) != 0;
        }
        return false;
    }

    public boolean givePrimaryMag()
    {
        if (primary != null)
        {
            return primary.addAmmo(1) != 0;
        }
        return false;
    }

    public boolean giveExplosiveAmmo()
    {
        if (explosive != null)
        {
            return (explosive.addAmmo(1) != 0);
        }

        return false;
    }

    public boolean giveSecondaryMag(Weapon w)
    {
        int ammo = 0;

        if (w != null)
        {
            ammo = w.ammo;
        }

        if (sidearm != null)
        {
            return sidearm.addExactAmmo(ammo) != 0;
        }
        return false;
    }

    public boolean givePrimaryMag(Weapon w)
    {
        int ammo = 0;

        if (w != null)
        {
            ammo = w.ammo;
        }

        if (primary != null)
        {
            return primary.addExactAmmo(ammo) != 0;
        }
        return false;
    }

    public boolean giveExplosiveAmmo(Weapon w)
    {
        int ammo = 0;

        if (w != null)
        {
            ammo = w.ammo;
        }

        if (explosive != null)
        {
            return (explosive.addExactAmmo(ammo) != 0);
        }

        return false;
    }

    public void setSecurityCardLevel(ItemType it)
    {
        if (it != null)
        {
            boolean ccs = false;
            if (selectedItem == items[1])
            {
                ccs = true;
            }

            if (it == ItemType.CARDKEY_1)
            {
                if (securityCardLevel < 1)
                {
                    securityCardLevel = 1;
                    items[1] = new Item((short)75, 0, 0, it, 1);
                }
            }
            else if (it == ItemType.CARDKEY_2)
            {
                if (securityCardLevel < 2)
                {
                    securityCardLevel = 2;
                    items[1] = new Item((short)76, 0, 0, it, 2);
                }
            }
            else if (it == ItemType.CARDKEY_3)
            {
                if (securityCardLevel < 3)
                {
                    securityCardLevel = 3;
                    items[1] = new Item((short)77, 0, 0, it, 3);
                }
            }
            else if (it == ItemType.CARDKEY_4)
            {
                if (securityCardLevel < 4)
                {
                    securityCardLevel = 4;
                    items[1] = new Item((short)201, 0, 0, it, 4);
                }
            }
            else if (it == ItemType.CARDKEY_5)
            {
                if (securityCardLevel < 5)
                {
                    securityCardLevel = 5;
                    items[1] = new Item((short)202, 0, 0, it, 5);
                }
            }

            else if (it == ItemType.CARDKEY_6)
            {
                if (securityCardLevel < 6)
                {
                    securityCardLevel = 6;
                    items[1] = new Item((short)75, 0, 0, it, 6);
                }
            }
            else if (it == ItemType.CARDKEY_7)
            {
                if (securityCardLevel < 7)
                {
                    securityCardLevel = 7;
                    items[1] = new Item((short)76, 0, 0, it, 7);
                }
            }
            else if (it == ItemType.CARDKEY_8)
            {
                if (securityCardLevel < 8)
                {
                    securityCardLevel = 8;
                    items[1] = new Item((short)77, 0, 0, it, 8);
                }
            }
            else if (it == ItemType.CARDKEY_9)
            {
                if (securityCardLevel < 9)
                {
                    securityCardLevel = 9;
                    items[1] = new Item((short)201, 0, 0, it, 9);
                }
            }
            else if (it == ItemType.CARDKEY_10)
            {
                if (securityCardLevel < 10)
                {
                    securityCardLevel = 10;
                    items[1] = new Item((short)202, 0, 0, it, 10);
                }
            }

            if (ccs)
            {
                selectedItem = items[1];
            }
        }
        else
        {
            securityCardLevel = 0;
        }
    }

    public void resetSecurityCardLevel()
    {
        boolean ccs = false;
        if (selectedItem == items[1])
        {
            ccs = true;
        }

        items[1] = null;
        securityCardLevel = 0;

        if (ccs)
        {
            selectedItem = items[0];
        }
    }

    public int getSecurityCardLevel()
    {
        return securityCardLevel;
    }

    public ItemType getSecurityCardType()
    {
        if (items[1] != null)
        {
            return items[1].getType();
        }
        else
        {
            return null;
        }
    }

    public Weapon addPrimary(Weapon w)
    {
        if (w == null)
        {
            return null;
        }
        
        Weapon drop = null;
        if (primary != null)
        {
            drop = this.primary.copy();
        }
        this.primary = w.copy();

        if (drop != null)
        {
            if (this.primary.getType() == drop.getType())
            {
                this.primary.addExactAmmo(drop.ammo);
                drop = null;
            }
        }

        return drop;
    }

    public Weapon addSecondary(Weapon w)
    {
        if (w == null)
        {
            return null;
        }

        Weapon drop = null;
        if (sidearm != null)
        {
            drop = this.sidearm.copy();
        }
        this.sidearm = w.copy();

        if (drop != null)
        {
            if (this.sidearm.getType() == drop.getType())
            {
                this.sidearm.addExactAmmo(drop.ammo);
                drop = null;
            }
        }

        return drop;
    }

    public Weapon addExplosive(Weapon w)
    {
        if (w == null)
        {
            return null;
        }

        Weapon drop = null;
        if (explosive != null)
        {
            drop = this.explosive.copy();
        }
        this.explosive = w.copy();

        if (drop != null)
        {
            if (this.explosive.getType() == drop.getType())
            {
                this.explosive.addExactAmmo(drop.ammo);
                drop = null;
            }
        }

        return drop;
    }

    public void attachSecondarySilencer()
    {
        if (sidearm != null)
        {
            sidearm.attachSuppressor();
        }
    }

    public void attachPrimarySilencer()
    {
        if (primary != null)
        {
            primary.attachSuppressor();
        }
    }

    public void addGasMask()
    {
        items[2] = new Item((short)204, 0, 0, ItemType.GASMASK, 1);
    }

    public void addNVG()
    {
        items[3] = new Item((short)203, 0, 0, ItemType.NVG, 1);
    }

    public void addBodyArmor()
    {
        items[4] = new Item((short)207, 0, 0, ItemType.BODY_ARMOR, 1);
    }

    public void addMineDetector()
    {
        items[5] = new Item((short)296, 0, 0, ItemType.MINE_DETECTOR, 1);
    }

    /*
    public void changeWeapon(int change)
    {
        int count = 0;
        boolean found = false;

        while ((!found) && (count < weapons.length))
        {
            if ((currWeaponIndex + change) < 0)
            {
                currWeaponIndex = weapons.length - 1;

                if (weapons[currWeaponIndex] != null)
                {
                    found = true;
                    selectedWeapon = weapons[currWeaponIndex];
                }
            }
            else if ((currWeaponIndex + change) >= weapons.length)
            {
                currWeaponIndex = 0;

                if (weapons[currWeaponIndex] != null)
                {
                    found = true;
                    selectedWeapon = weapons[currWeaponIndex];
                }
            }
            else
            {
                currWeaponIndex+=change;

                if (weapons[currWeaponIndex] != null)
                {
                    found = true;
                    selectedWeapon = weapons[currWeaponIndex];
                }
            }

            count++;
        }
    }
     * 
     */

    public Weapon getSelectedWeapon()
    {
        return selectedWeapon;
    }

    public void changeItem(int change)
    {
        int count = 0;
        boolean found = false;
        
        while ((!found) && (count < items.length))
        {
            if ((currItemIndex + change) < 0)
            {
                currItemIndex = items.length - 1;

                if (items[currItemIndex] != null)
                {
                    found = true;
                    selectedItem = items[currItemIndex];
                }
            }
            else if ((currItemIndex + change) >= items.length)
            {
                currItemIndex = 0;

                if (items[currItemIndex] != null)
                {
                    found = true;
                    selectedItem = items[currItemIndex];
                }
            }
            else
            {
                currItemIndex+=change;
                
                if (items[currItemIndex] != null)
                {
                    found = true;
                    selectedItem = items[currItemIndex];
                }
            }

            count++;
        }
    }

    public Item getSelectedItem()
    {
        return selectedItem;
    }

    public void setNumMaxHealthKits(int hk)
    {
        if (hk > 0)
        {
            maxHealthKits = hk;
        }
    }

    public int getNumHealthKits()
    {
        return healthKits;
    }

    public int getMaxNumHealthKits()
    {
        return maxHealthKits;
    }

    public boolean addHealthKit()
    {
        healthKits++;

        if (healthKits > maxHealthKits)
        {
            healthKits = maxHealthKits;
            return false;
        }

        return true;
    }

    public boolean useHealthKit(int currHealth, int maxHealth)
    {
        if (currHealth < maxHealth)
        {
            if (healthKits > 0)
            {
                healthKits--;
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    /*
    public void setAmmo(short[] ammo)
    {
        for(int i = 0; i < weapons.length; i++)
        {
            if (weapons[i] != null)
            {
                weapons[i].setAmmo(ammo[i]);
            }
        }
    }
     * 
     */

    public void setItems(byte[] itemList)
    {
        for(int i = 0; i < items.length; i++)
        {
            if (itemList[i] > 0)
            {
                if (i == 0)
                {
                    this.healthKits = itemList[i];
                }
                else if (i == 1)
                {
                    ItemType it = null;
                    
                    if (itemList[i] == 1)
                    {
                        it = ItemType.CARDKEY_1;
                    }
                    else if (itemList[i] == 2)
                    {
                        it = ItemType.CARDKEY_2;
                    }
                    else if (itemList[i] == 3)
                    {
                        it = ItemType.CARDKEY_3;
                    }
                    else if (itemList[i] == 4)
                    {
                        it = ItemType.CARDKEY_4;
                    }
                    else if (itemList[i] == 5)
                    {
                        it = ItemType.CARDKEY_5;
                    }
                    else if (itemList[i] == 6)
                    {
                        it = ItemType.CARDKEY_6;
                    }
                    else if (itemList[i] == 7)
                    {
                        it = ItemType.CARDKEY_7;
                    }
                    else if (itemList[i] == 8)
                    {
                        it = ItemType.CARDKEY_8;
                    }
                    else if (itemList[i] == 9)
                    {
                        it = ItemType.CARDKEY_9;
                    }
                    else if (itemList[i] == 10)
                    {
                        it = ItemType.CARDKEY_10;
                    }

                    if (it != null)
                    {
                        this.setSecurityCardLevel(it);
                    }
                }
                else if (i == 2)
                {
                    this.addGasMask();
                }
                else if (i == 3)
                {
                    this.addNVG();
                }
                else if (i == 4)
                {
                    this.addBodyArmor();
                }
                else if (i == 5)
                {
                    this.addMineDetector();
                }
            }
        }
    }

    public boolean hasGasMask()
    {
        return (items[2] != null);
    }

    public boolean hasNVG()
    {
        return (items[3] != null);
    }

    public boolean hasBodyArmor()
    {
        return (items[4] != null);
    }

    public boolean hasMineDetector()
    {
        return (items[5] != null);
    }

}
