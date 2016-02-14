/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class FemaleAllyPrisoner extends NPC
{
    public FemaleAllyPrisoner(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)269, x, y, d, 100, s, true, 0, p, GuardType.FEMALE_ALLY_PRISONER);
    }

    @Override
    public NPC copy()
    {
        FemaleAllyPrisoner g = new FemaleAllyPrisoner(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;
        g.following = this.following;

        return g;
    }
}
