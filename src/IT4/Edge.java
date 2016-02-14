/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IT4;

/**
 *
 * @author Jim
 */
public class Edge
{
    public final Vertex target;
    public final byte weight;

    public Edge(Vertex argTarget, byte argWeight)
    {
        target = argTarget;
        weight = argWeight;
    }
}
