/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GFX;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author Jim
 */
public class TextWriter
{
    private static char[] buf = new char[128];
    public static void drawStrings(IT3String[] strs, Texture t)
    {
        // store the current model matrix
        GL11.glPushMatrix();

        // bind to the appropriate texture for this sprite
        t.bind();

        // translate to the right location and prepare to draw
        GL11.glTranslatef(0, 0, 0);
        //GL11.glColor3f(1.0f,0,0);

        //Transparancy:
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float height = t.getHeight();
        float width = t.getWidth();

        
        
        // draw a quad textured to match the sprite
        GL11.glBegin(GL11.GL_QUADS);
        {
            for(int j = 0; j < strs.length; j++)
            {
                IT3String s = strs[j];
                if (!s.visible)
                {
                    break;
                }

                GL11.glColor4f(s.r,s.g,s.b,s.a);
                int sz = s.textSize;
                
                int x = s.x;
                int y = s.y;
                int oz = ((sz) >> 1)-2;
                
                s.text.getChars(0, s.text.length(), buf, 0);

                if (s.forward)
                {
                    for(int i = 0; i < s.text.length(); i++)
                    {
                        float tx,tx2,ty,ty2;
                        float ax,bx;
                        int yoff = 0;
                        
                        if (buf[i] == 'y')
                        {
                            yoff = 2;
                        }
                        else if (buf[i] == 'p')
                        {
                            yoff = 1;
                        }
                        else if (buf[i] == 'g')
                        {
                            yoff = 1;
                        }
                        else if (buf[i] == 'q')
                        {
                            yoff = 1;
                        }

                        ax = (i * sz) + x; //was * 10
                        bx = ((i + 1) * sz) + x; //was * 10

                        ax-=(oz * i);
                        bx-=(oz * i);

                        int row,col;
                        row = buf[i] >> 4;
                        col = buf[i] - (row << 4);

                        tx = getTX(col, 16, height);
                        tx2 = getTX(col + 1, 16, height);
                        ty = getTX(row, 16, width);
                        ty2 = getTX(row + 1, 16, width);

                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(ax, y + yoff);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(ax, y + yoff + 16);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(bx,y + yoff + 16);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(bx,y + yoff);
                    }
                }
                else
                {
                    for(int i = s.text.length() - 1, h = 0; i >= 0; i--, h++)
                    {
                        float tx,tx2,ty,ty2;
                        float ax,bx;

                        int yoff = 0;

                        if (buf[i] == 'y')
                        {
                            yoff = 2;
                        }
                        else if (buf[i] == 'p')
                        {
                            yoff = 1;
                        }
                        else if (buf[i] == 'g')
                        {
                            yoff = 1;
                        }
                        else if (buf[i] == 'q')
                        {
                            yoff = 1;
                        }

                        ax = x - (h * sz); //was * 10
                        bx = x - ((h + 1) * sz); //was * 10

                        ax+=(oz * h);
                        bx+=(oz * h);

                        int row,col;
                        row = buf[i] >> 4;
                        col = buf[i] - (row << 4);

                        tx = getTX(col, 16, height);
                        tx2 = getTX(col + 1, 16, height);
                        ty = getTX(row, 16, width);
                        ty2 = getTX(row + 1, 16, width);

                        GL11.glTexCoord2f(tx, ty);
                        GL11.glVertex2f(bx, y + yoff);
                        GL11.glTexCoord2f(tx, ty2);
                        GL11.glVertex2f(bx, y + yoff + 16);
                        GL11.glTexCoord2f(tx2, ty2);
                        GL11.glVertex2f(ax,y + yoff + 16);
                        GL11.glTexCoord2f(tx2, ty);
                        GL11.glVertex2f(ax,y + yoff);
                    }
                }
            }
        }
        GL11.glEnd();

        // restore the model view matrix to prevent contamination
        GL11.glPopMatrix();
    }

    private static float getTX(float index, int colsize, float wdt)
    {
        return (index / colsize) * wdt;
    }
}
