/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Woman3 extends NPC
{
    public Woman3(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)326, x, y, d, 100, s, true, 0, p, GuardType.WOMAN3);
    }

    @Override
    public NPC copy()
    {
        Woman3 g = new Woman3(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
