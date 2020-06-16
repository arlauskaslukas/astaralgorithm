/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astaralgorithm.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 *
 * @author lukasarlauskas
 * @param <E> satisfies comparable interface
 */
public class UndirectedGraph<E> implements Graph<E> {

    //Adjacency matrix  basically stores elements that are adjacent to each other.
    //it's only relevant for undirected graph, because for the directed graphs there's incoming and outgoing edges.
    private ArrayList<ArrayList<UndirectedGraphNode<E>>> adjacencyMatrix = null;
    //stores all the vertices, so undirectedgraph class is sort of like container class.
    protected ArrayList<UndirectedGraphNode<E>> vertices;
    //stores edge matrix - which element is connected to which 
    protected ArrayList<ArrayList<UndirectedGraphNode<E>>> edgeMatrix = null;
    
    public ArrayList<ArrayList<UndirectedGraphNode<E>>> getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    @Override
    public ArrayList<UndirectedGraphNode<E>> getVertices() {
        return vertices;
    }

    public ArrayList<ArrayList<UndirectedGraphNode<E>>> getEdges() {
        if(edgeMatrix!=null) return edgeMatrix;
        return calculateEdges(adjacencyMatrix);
    }
    public ArrayList<ArrayList<UndirectedGraphNode<E>>> calculateEdges(ArrayList<ArrayList<UndirectedGraphNode<E>>> adjacencyMatrix)
    {
        ArrayList<ArrayList<UndirectedGraphNode<E>>> edges = new ArrayList<>();
        //two rows
        edges.add(new ArrayList<>());
        edges.add(new ArrayList<>());
        int index = 0;
        for(int i = 0; i < adjacencyMatrix.size(); i++)
        {
            if(adjacencyMatrix.size() > 1)
            {
                for(int j = 1; j < adjacencyMatrix.get(i).size(); j++)
                {
                    //structure of edges:
                    // fromNode1|fromNode2|fromNode3|...
                    // toNode1  |toNode2  |toNode3  |...
                    edges.get(0).add(adjacencyMatrix.get(i).get(0));
                    edges.get(1).add(adjacencyMatrix.get(i).get(j));
                }
            }
        }
        return edges;
    }
    //calculates adjacency matrix from given edges
    public void calculateAdjacencyMatrix(ArrayList<ArrayList<UndirectedGraphNode<E>>> edges) {
        //if we call this method, apparently we want to create a new matrix
        adjacencyMatrix = new ArrayList<>();
        //edges are prevalidated in such way that first and second rows are of the same length
        for(int index = 0; index < edges.get(0).size(); index++)
        {
            //add the adjacency to the vertex
            edges.get(0).get(index).addAdjacentVertice(edges.get(1).get(index));
            //if list of adjacencies hasn't been created
            if(!checkIfNodeHasAdjacencyList(edges.get(0).get(index)))
            {
                //for easier adding we use the additional array
                ArrayList array = new ArrayList<>();
                array.add(0,edges.get(0).get(index));
                array.add(edges.get(1).get(index));
                adjacencyMatrix.add(array);
                //skip any further actions in the iteration
                continue;
            }
            //if the list exists, add to corresponding list
            addToCorresponding(edges.get(0).get(index), edges.get(1).get(index));
        }
    }
    public void addToCorresponding(UndirectedGraphNode<E> start, UndirectedGraphNode<E> end)
    {
        //search for appropriate adjacencies list
        for(ArrayList<UndirectedGraphNode<E>> list : adjacencyMatrix)
        {
            if(list.get(0).equals(start))
            {
                //if the node hasn't been added before, add it.
                if(!list.contains(end)) list.add(end);
            }
        }
    }
    public boolean checkIfNodeHasAdjacencyList(UndirectedGraphNode<E> node)
    {
        for(ArrayList<UndirectedGraphNode<E>> list : adjacencyMatrix)
        {
            if(list.get(0).equals(node)) return true;
        }
        return false;
    }

    @Override
    public GraphNode<E> getVertice(int index) {
        return vertices.get(index);
    }
    
    //specific class for undirectedgraph
    public class UndirectedGraphNode<N> extends GraphNode<N>
    {
        public UndirectedGraphNode()
        {
            adjacentVertices = new ArrayList<>();
        }
        public UndirectedGraphNode(N element)
        {
            this.element = element;
        }
        public UndirectedGraphNode(N element, ArrayList<GraphNode<N>> adjacentVertices)
        {
            this.element = element;
            this.adjacentVertices = adjacentVertices;
        }
        public UndirectedGraphNode(N element, ArrayList<GraphNode<N>> adjacentVertices, UndirectedGraphNode<N> precadent)
        {
            this.element = element;
            this.adjacentVertices = adjacentVertices;
            if(validatePrecadent(precadent)) this.precadent = precadent;
        }

        @Override
        public ArrayList<? extends GraphNode<N>> getAdjacentVertices() {
            return adjacentVertices;
        }

        @Override
        public N getElement() {
            return element;
        }
        @Override
        public boolean validatePrecadent(GraphNode<N> prec)
        {
            return adjacentVertices.contains(prec);
        }

        @Override
        public boolean addAdjacentVertice(GraphNode<N> node) 
        {
            if(element == null) return false;
            if(this.checkIfAdjacent(node.element)) return false;
            if(adjacentVertices == null) adjacentVertices = new ArrayList<>();
            adjacentVertices.add(node);
            return true;
        }
    }
}
