/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class MediumGuard extends NPC
{
    public MediumGuard(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)27, x, y, d, 51, s, false, 40, p, GuardType.MEDIUM);
    }

    @Override
    public NPC copy()
    {
        MediumGuard g = new MediumGuard(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
