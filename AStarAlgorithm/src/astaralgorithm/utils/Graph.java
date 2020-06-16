/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astaralgorithm.utils;

import java.util.ArrayList;

/**
 *
 * @author lukasarlauskas
 */
public interface Graph<E> {
    
    public static final int MAXVERTICES = 30*30;
    //returns vertices
    public ArrayList<? extends GraphNode<E>> getVertices();
    //return i-nth vertice
    public GraphNode<E> getVertice(int index);
    
}
