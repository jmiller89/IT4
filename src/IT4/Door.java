/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Door extends ITObject
{
    protected int openPos = 0;
    private int securityLevel = 0;
    protected boolean opening = false;
    protected boolean closing = false;
    private boolean dark = false;
    private int objectiveLevel = 0;
    public boolean crawl = false;

    public Door(short id, int locX, int locY, int secLev, int objLev, boolean drk)
    {
        super(id, locX, locY);
        securityLevel = secLev;
        dark = drk;
        objectiveLevel = objLev;
    }

    public boolean isDark()
    {
        return dark;
    }

    public void open()
    {
        if (openPos < 40)
        {
            openPos+=2;
            //System.out.println("Opening " + openPos);
        }
        else
        {
            opening = false;
        }
    }

    public void close()
    {
        if (openPos > 0)
        {
            openPos-=2;
        }
        else
        {
            closing = false;
        }
    }

    public void forceClose()
    {
        openPos = 0;
        opening = false;
        closing = false;
    }

    public boolean isOpen()
    {
        return (openPos > 39);
    }

    public boolean isClosed()
    {
        return (openPos < 1);
    }

    public boolean isOpening()
    {
        return opening;
    }

    public boolean isClosing()
    {
        return closing;
    }

    public void opening()
    {
        opening = true;
        closing = false;
    }

    public void closing()
    {
        if (!opening)
        {
            opening = false;
            closing = true;
        }
    }

    public int getOpenPos()
    {
        return openPos;
    }

    public int getSecuritylevel()
    {
        return securityLevel;
    }

    public int getObjectiveLevel()
    {
        return objectiveLevel;
    }
}
