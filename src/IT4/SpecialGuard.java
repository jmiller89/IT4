/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class SpecialGuard extends NPC
{
    public SpecialGuard(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)350, x, y, d, 100, s, true, 0, p, GuardType.SPECIAL);
    }

    @Override
    public NPC copy()
    {
        SpecialGuard g = new SpecialGuard(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
