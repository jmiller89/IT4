/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GFX;

/**
 *
 * @author Jim
 */
public class Particle
{
    public float x = 0;
    public float y = 0;
    public float dx = 0;
    public float dy = 0;
    public boolean active = false;
    public boolean initialized = false;
    public float maxY;

    public Particle(float ymax)
    {
        maxY = ymax;
    }

    public void move()
    {
        x += dx;
        y += dy;

        if (y > maxY)
        {
            active = false;
        }
    }
}
