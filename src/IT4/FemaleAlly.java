/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class FemaleAlly extends NPC
{
    public FemaleAlly(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)261, x, y, d, 100, s, true, 20, p, GuardType.FEMALE_ALLY);
    }

    @Override
    public NPC copy()
    {
        FemaleAlly g = new FemaleAlly(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;
        g.following = this.following;

        return g;
    }
}
