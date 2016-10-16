/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Explosion
{
    private static final int DURATION = 7;
    public int timeRemaining = DURATION;
    public boolean status = true;
    public int rank = 1;

    public int x0 = 0;
    public int y0 = 0;
    public int x1 = 0;
    public int y1 = 0;
    public int x2 = 0;
    public int y2 = 0;
    public int x3 = 0;
    public int y3 = 0;
    public int x4 = 0;
    public int y4 = 0;
    public int x5 = 0;
    public int y5 = 0;
    public int x6 = 0;
    public int y6 = 0;
    public int x7 = 0;
    public int y7 = 0;

    private float a0 = 0.0f;
    private float b0 = 0.0f;
    private float a1 = 0.0f;
    private float b1 = 0.0f;
    private float a2 = 0.0f;
    private float b2 = 0.0f;
    private float a3 = 0.0f;
    private float b3 = 0.0f;
    private float a4 = 0.0f;
    private float b4 = 0.0f;
    private float a5 = 0.0f;
    private float b5 = 0.0f;
    private float a6 = 0.0f;
    private float b6 = 0.0f;
    private float a7 = 0.0f;
    private float b7 = 0.0f;

    public int x;
    public int y;

    public Explosion(int x, int y, int rank)
    {
        this.x = x;
        this.y = y;
        this.rank = rank;

        x0 = x;
        y0 = y;
        x1 = x;
        y1 = y;
        x2 = x;
        y2 = y;
        x3 = x;
        y3 = y;
        x4 = x;
        y4 = y;
        x5 = x;
        y5 = y;
        x6 = x;
        y6 = y;
        x7 = x;
        y7 = y;
        
        a0 = x;
        b0 = y;
        a1 = x;
        b1 = y;
        a2 = x;
        b2 = y;
        a3 = x;
        b3 = y;
        a4 = x;
        b4 = y;
        a5 = x;
        b5 = y;
        a6 = x;
        b6 = y;
        a7 = x;
        b7 = y;
        
    }

    public void scatter(float delta)
    {
        if (status)
        {
            a0 -= (5.0f * delta);
            a1 += (5.0f * delta);
            b2 -= (5.0f * delta);
            b3 += (5.0f * delta);
            a4 += (3.5f * delta);
            b4 += (3.5f * delta);
            a5 -= (3.5f * delta);
            b5 -= (3.5f * delta);
            a6 += (3.5f * delta);
            b6 -= (3.5f * delta);
            a7 -= (3.5f * delta);
            b7 += (3.5f * delta);

            x0 = (int)a0;
            y0 = (int)b0;
            x1 = (int)a1;
            y1 = (int)b1;
            x2 = (int)a2;
            y2 = (int)b2;
            x3 = (int)a3;
            y3 = (int)b3;
            x4 = (int)a4;
            y4 = (int)b4;
            x5 = (int)a5;
            y5 = (int)b5;
            x6 = (int)a6;
            y6 = (int)b6;
            x7 = (int)a7;
            y7 = (int)b7;

            timeRemaining--;
        }

        if (timeRemaining <= 0)
        {
            status = false;
        }
    }
    
}
