/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class Item extends ITObject
{
    public ItemType type;
    public boolean toRemove = false;
    public int rank;
    public boolean isWeapon = false;
    public boolean touched = false;
    public String description = "";
    public String weaponName = "";

    private static String getName(ItemType itemT, Player p)
    {        
        switch(itemT)
        {
            case BODY_ARMOR:
            {
                return "B. Armor";
            }
            case GASMASK:
            {
                return "Gas Mask";
            }
            case NVG:
            {
                return "NVGs";
            }
            case MEDKIT:
            {
                return "Medkit";
            }
            case CARDKEY_1:
            {
                return "Card Lv. 1";
            }
            case CARDKEY_2:
            {
                return "Card Lv. 2";
            }
            case CARDKEY_3:
            {
                return "Card Lv. 3";
            }
            case CARDKEY_4:
            {
                return "Card Lv. 4";
            }
            case CARDKEY_5:
            {
                return "Card Lv. 5";
            }
            case CARDKEY_6:
            {
                return "Card Lv. 6";
            }
            case CARDKEY_7:
            {
                return "Card Lv. 7";
            }
            case CARDKEY_8:
            {
                return "Card Lv. 8";
            }
            case CARDKEY_9:
            {
                return "Card Lv. 9";
            }
            case CARDKEY_10:
            {
                return "Card Lv. 10";
            }
            case PRIMARY_AMMO:
            {
                String retval = "Pr.Ammo";

                if (p != null)
                {
                    if (p.stuff.primary != null)
                    {
                        retval += " x " + p.stuff.primary.magCapacity * 3;
                    }
                }

                return retval;
            }
            case SECONDARY_AMMO:
            {
                String retval = "Sd.Ammo";

                if (p != null)
                {
                    if (p.stuff.sidearm != null)
                    {
                        retval += " x " + p.stuff.sidearm.magCapacity * 3;
                    }
                }

                return retval;
            }
            case PRIMARY_MAG:
            {
                String retval = "Pr.Ammo";

                if (p != null)
                {
                    if (p.stuff.primary != null)
                    {
                        retval += " x " + p.stuff.primary.magCapacity;
                    }
                }

                return retval;
            }
            case SECONDARY_MAG:
            {
                String retval = "Sd.Ammo";

                if (p != null)
                {
                    if (p.stuff.sidearm != null)
                    {
                        retval += " x " + p.stuff.sidearm.magCapacity;
                    }
                }

                return retval;
            }
            case ASSAULT_RIFLE:
            {
                return "A.Rifle";
            }
            case BOOSTER_KIT:
            {
                return "Booster Kit";
            }
            case GRENADE:
            {
                return "Grenade";
            }
            case C4:
            {
                return "C4";
            }
            case PISTOL:
            {
                return "Pistol";
            }
            case SHOTGUN:
            {
                return "Shotgun";
            }
            case SECONDARY_SILENCER:
            {
                return "Sd.Suppressor";
            }
            case SMG:
            {
                return "SMG";
            }
            case TRANQ_PISTOL:
            {
                return "T.Pistol";
            }
            case PRIMARY_SILENCER:
            {
                return "Pr.Suppressor";
            }
            case MINE_DETECTOR:
            {
                return "Mine Detector";
            }
        }

        return "";
    }

    public Item(short id, int x, int y, ItemType it, int rank)
    {
        super(id, x, y);
        type = it;
        this.rank = rank;
    }

    public ItemType getType()
    {
        return type;
    }

    public boolean shouldRemove()
    {
        return toRemove;
    }

    public void flagForRemoval()
    {
        toRemove = true;
    }

    public Item copy()
    {
        Item copy = new Item(this.getID(), this.getX(), this.getY(), this.type, this.rank);
        copy.description = this.description;
        copy.weaponName = this.weaponName;
        return copy;
    }

    public String toString(Player p)
    {
        String retval = "";
        retval += Item.getName(type, p);

        if (isWeapon)
        {
            retval = weaponName;
        }

        return retval;
    }
}
