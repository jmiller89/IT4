/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim (Admin)
 */
public class LightGuard extends NPC
{
    public LightGuard(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)19, x, y, d, 22, s, false, 25, p, GuardType.LIGHT);
    }

    @Override
    public NPC copy()
    {
        LightGuard g = new LightGuard(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
