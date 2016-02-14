/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class IslandGuy extends NPC
{
    public IslandGuy(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)342, x, y, d, 100, s, true, 0, p, GuardType.ISLAND_GUY);
    }

    @Override
    public NPC copy()
    {
        IslandGuy g = new IslandGuy(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
