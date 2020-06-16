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
 * @param <N>
 */
public abstract class GraphNode<N> {
        protected N element;
        protected ArrayList<GraphNode<N>> adjacentVertices;
        protected GraphNode<N> precadent = null;
        public GraphNode()
        {
            
        }
        public GraphNode(N element)
        {
            this.element = element;
        }
        public GraphNode(N element, ArrayList<GraphNode<N>> adjacentVertices)
        {
            this.element = element;
            this.adjacentVertices = adjacentVertices;
        }
        public abstract ArrayList<? extends GraphNode<N>> getAdjacentVertices();
        public abstract N getElement();
        public abstract boolean validatePrecadent(GraphNode<N> prec);
        public abstract boolean addAdjacentVertice(GraphNode<N> node);
        public boolean checkIfAdjacent(N element)
        {
            if(element==null) return false;
            if(adjacentVertices == null || adjacentVertices.isEmpty()) return false;
            for(GraphNode<N>  node : adjacentVertices)
            {
                if(node.element.equals(element)) return true;
            }
            return false;
        }
}
