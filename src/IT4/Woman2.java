/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Woman2 extends NPC
{
    public Woman2(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)318, x, y, d, 100, s, true, 0, p, GuardType.WOMAN2);
    }

    @Override
    public NPC copy()
    {
        Woman2 g = new Woman2(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
