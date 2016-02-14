/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Scientist1 extends NPC
{

    public Scientist1(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)162, x, y, d, 7, s, false, 10, p, GuardType.SCIENTIST1);
    }

    @Override
    public NPC copy()
    {
        Scientist1 g = new Scientist1(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }

}
