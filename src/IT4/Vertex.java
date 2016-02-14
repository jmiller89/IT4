/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Vertex implements Comparable
{
    public Edge[] adjacencies;
    public float minDistance = Float.MAX_VALUE;
    public Vertex previous;
    public int x;
    public int y;
    public boolean closed = false;

    public Vertex(int posX, int posY)
    {
        x = posX;
        y = posY;
    }

    public int compareTo(Object o)
    {
        Vertex other = (Vertex)o;

        if (minDistance > other.minDistance)
        {
            return 1;
        }
        else if (minDistance < other.minDistance)
        {
            return -1;
        }
        
        return 0;
    }
}

