/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Chief extends NPC
{
    public Chief(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)334, x, y, d, 100, s, true, 0, p, GuardType.CHIEF);
    }

    @Override
    public NPC copy()
    {
        Chief g = new Chief(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
