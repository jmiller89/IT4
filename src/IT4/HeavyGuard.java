/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class HeavyGuard extends NPC
{
    public HeavyGuard(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)35, x, y, d, 71, s, false, 40, p, GuardType.HEAVY);
    }

    @Override
    public NPC copy()
    {
        HeavyGuard g = new HeavyGuard(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
