/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class CrawlDoor extends Door
{
    //Door(short id, int locX, int locY, int secLev, int objLev, boolean drk)
    public CrawlDoor(short id, int locX, int locY)
    {
        super(id, locX, locY, 0, 0, false);
        crawl = true;
    }

    @Override
    public void open()
    {
        openPos = 40;
        opening = false;
        closing = false;
    }

    @Override
    public void close()
    {
        openPos = 0;
        opening = false;
        closing = false;
    }

    @Override
    public int getOpenPos()
    {
        return 0;
    }
}
