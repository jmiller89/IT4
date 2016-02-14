/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Woman1 extends NPC
{
    public Woman1(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)310, x, y, d, 100, s, true, 0, p, GuardType.WOMAN1);
    }

    @Override
    public NPC copy()
    {
        Woman1 g = new Woman1(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
