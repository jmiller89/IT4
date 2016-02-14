/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Worm extends NPC
{
    public Worm(int x, int y, Direction d, NPCStatus s, Path p)
    {
        super((short)277, x, y, d, 32, s, false, 48, p, GuardType.WORM);
    }
    
    @Override
    public NPC copy()
    {
        Worm g = new Worm(this.getX(), this.getY(), this.getDirection(), this.getStatus(), this.getPath().copy());

        g.bodyArmor = this.bodyArmor;

        return g;
    }
}
