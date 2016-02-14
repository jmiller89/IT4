/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GFX;

/**
 *
 * @author Jim
 */
public class IT3String
{
    String text = "";
    int x = 0;
    int y = 0;
    int textSize = 16;
    float r=0.0f,g=0.0f,b=0.0f,a=1.0f;
    boolean visible = false;
    boolean forward = true;

    public void reset()
    {
        text = "";
        x = 0;
        y = 0;
        visible = false;
    }
}
