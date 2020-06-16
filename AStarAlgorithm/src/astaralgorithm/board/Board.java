/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astaralgorithm.board;

import astaralgorithm.utils.GraphNode;
import astaralgorithm.utils.UndirectedGraph;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author lukasarlauskas
 * @param <E>
 */
public class Board<E extends String> extends UndirectedGraph<E> {
    private int XSize;
    private int YSize;
    private float FillPercentage;
    private double StartEnd = 0;
    private double HalfDist = 0;
    
    private List<List<Tile<String>>> Tiles = new ArrayList<>();
    
    public void resetBoard()
    {
        StartEnd = 0;
        HalfDist = 0;
        for(List list : Tiles)
        {
            for(Object tile : list)
            {
                Tile t = (Tile) tile;
                tile = new Tile(t.X, t.Y, (String)t.getElement());
            }
        }
    }
    
    public Tile GetTile(int x, int y)
    {
        return Tiles.get(y).get(x);
    }
    
    public void Connect()
    {
        for(int x = 0; x < XSize; x++)
        {
            for(int y = 0; y < YSize; y++)
            {
                if(GetTile(x,y).getElement().toString().equals("Open"))
                    GetTile(x,y).SearchForAdjacent();
            }
        }
    }
    
    private void PrepareMap()
    {
        for(int y=0; y < YSize; y++)
        {
            List<Tile<String>> line = new ArrayList<>();
            for(int x = 0; x < XSize; x++)
            {
                line.add(new Tile<>(x,y,"Open"));
            }
            Tiles.add(line);
        }
    }
    
    private boolean VerticalCheck(int x, int y)
    {
        int quantity = 0;
        for(int ytemp = 0; ytemp < YSize; ytemp++)
        {
            if(GetTile(x,ytemp).getElement().equals("Closed"))
            {
                quantity++;
            }
        }
        System.out.println(FillPercentage);
        return ((float)quantity/YSize) < FillPercentage;
    }
    
    private boolean HorizontalCheck(int x, int y)
    {
        int quantity = 0;
        for(int xtemp = 0; xtemp < XSize; xtemp++)
        {
            if(GetTile(xtemp,y).getElement().toString().equals("Closed"))
            {
                quantity++;
            }
        }
        //System.out.println(((double)quantity/XSize));
        return ((double)quantity/XSize) < FillPercentage;
    }
    
    private void NewBoardGeneration()
    {
        PrepareMap();
        for(int y=0; y < YSize; y++)
        {
            for(int x = 0; x < XSize; x++)
            {
                //vert horiz < percentage?
                double rand = Math.random();
                if(VerticalCheck(x,y) && HorizontalCheck(x,y) && rand < FillPercentage)
                {
                    this.GetTile(x,y).ChangeElement("Closed");
                }
            }
        }
    }
    
    public void calculateStartDist(Tile start, Tile end)
    {
        StartEnd = start.heuristicUntilGoal;
        HalfDist = StartEnd/2;
    }
    
    public void calculateHeuristicDistances(Tile end)
    {
        for(List<Tile<String>> tiles : Tiles)
        {
            for(Tile<String> tile : tiles)
            {
                if(tile.getElement().equals("Closed")) continue;
                tile.heuristicUntilGoal = tile.euclideanHeuristic(end.X, end.Y);
            }
        }
    }
    
    public void calculateHeuristicDistancesAdjacents()
    {
        for(List<Tile<String>> tiles : Tiles)
        {
            for(Tile<String> tile : tiles)
            {
                if(tile.getElement().equals("Closed")) continue;
                for(GraphNode<String> adj : tile.getAdjacentVertices())
                {
                    Tile tadj = (Tile) adj;
                    tile.distancesUntilAdjacents.add(tile.euclideanHeuristic(tadj.X, tadj.Y));
                }
            }
        }
    }
    
    public Board(int x, int y, float percentage)
    {
        XSize = x;
        YSize = y;
        FillPercentage = percentage;
        Tiles = new ArrayList<>();
        NewBoardGeneration();
    }
    
    public Board()
    {
        
    }
    
    public class Tile<E extends String> extends UndirectedGraphNode<E> 
    {
        int X;
        int Y;
        boolean isVisited = false;
        boolean isStart = false;
        private boolean isEnd = false;
        private ArrayList<Float> distancesUntilAdjacents = new ArrayList<>();
        private float heuristicUntilGoal = 0f;
        private float f = 0.0f;
        private float g = 10000f;
        private int green = 255;
        private int red = 255;
        
        public void calculateTileColor()
        {
            if(heuristicUntilGoal > HalfDist)
            {
                green = green - (int) (((heuristicUntilGoal-HalfDist)/HalfDist)*255);
            }
            else if(heuristicUntilGoal < HalfDist)
            {
                red = red - (int) (((HalfDist-heuristicUntilGoal)/HalfDist)*255);
            }
            if(red<0) red = 0;
            if(green<0) green = 0;
            if(green>255) green = 255;
            if(red>255) red = 255;
        }
        public int getRed()
        {
            return red;
        }
        public int getGreen()
        {
            return green;
        }
        
        public int searchInAdjacents(Tile tile)
        {
            for(int index = 0; index < this.getAdjacentVertices().size(); index++)
            {
                if(tile.GetX() == ((Tile)this.getAdjacentVertices().get(index)).X && tile.GetY()==((Tile)this.getAdjacentVertices().get(index)).Y)
                {
                    return index;
                }
            }
            return -1;
        }
        
        public float getDistanceUntil(Tile prec)
        {
            return distancesUntilAdjacents.get(searchInAdjacents(prec));
        }
        
        public void CalculateTotalDistance(Tile prec)
        {
            f = heuristicUntilGoal + distancesUntilAdjacents.get(searchInAdjacents(prec));
            this.setPrec(prec);
        }
        
        public void setPrec(Tile tile)
        {
            precadent = tile;
        }
        public boolean getVisited()
        {
            return isVisited;
        }
        public Tile getPrec()
        {
            return (Tile) precadent;
        }
        public void setG(float val)
        {
            g=val;
        }
        
        public void calcF(float g, float h)
        {
            f = g + h;
        }
        public void calcG(Tile prec)
        {
            g = euclideanHeuristic(prec.X, prec.Y) + prec.g;
        }
        public float getG()
        {
            return g;
        }
        public float euclideanHeuristic(int xg, int yg)
        {
            int diffX = xg*20-X*20;
            int diffY = yg*20-Y*20;
            return (float) Math.sqrt((diffX*diffX) + diffY*diffY);
        }
        public float getF()
        {
            return f;
        }
        
        
        
        public void VisitTile(Tile prec)
        {
            if(isStart)
            {
                isVisited = true;
                return;
            }
            if(this.validatePrecadent(prec))
            {
                isVisited = true;
            }
        }
        
        public boolean getIsEnd()
        {
            return isEnd;
        }
        public void setEnd()
        {
            isEnd=true;
        }
        public boolean getIsStart()
        {
            return isStart;
        }
        public void setStart()
        {
            isStart=true;
        }
        public int GetX()
        {
            return X;
        }
        public int GetY()
        {
            return Y;
        }
        public Tile()
        {
            
        }
        public Tile(int x, int y, E elem)
        {
            X=x;
            Y=y;
            element=elem;
            adjacentVertices = new ArrayList<>();distancesUntilAdjacents = new ArrayList<>();
            heuristicUntilGoal = 0f;
            f = 0.0f;
            g = 10000f;
            green = 255;
            red = 255;
            isStart = false;
            isVisited = false;
            isEnd = false;
            
        }
        public void ChangeElement(E elem)
        {
            element = elem;
        }
        
        public void SearchForAdjacent()
        {
            /*
                NW  N   NE
                W   X   E
                SW  X   SE
            */
            for(int x = X-1; x<=X+1; x++)
            {
                for(int y=Y-1; y<=Y+1; y++)
                {
                    if(x>=0 && x < XSize && y >=0 && y < YSize && !GetTile(x,y).getElement().toString().equals("Closed"))
                    {
                        adjacentVertices.add(GetTile(x,y));
                    }
                }
            }
        }
    }
}
