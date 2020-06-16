/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astaralgorithm;

import astaralgorithm.board.Board;
import astaralgorithm.board.Board.Tile;
import astaralgorithm.ui.BaseGraphics;
import astaralgorithm.utils.GraphNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author lukasarlauskas
 */

public class AStarAlgorithm extends BaseGraphics {
    
    TextField x = null;
    TextField y = null;
    TextField Startx = null;
    TextField Starty = null;
    TextField Endx = null;
    TextField Endy = null;
    TextField percentage = null;
    int X = 5;
    int Y = 5;
    float Prcntg = 0.7F;
    Board<String> b = null;
    Tile start;
    Tile end;
    PauseTransition pause;
    Button step;
    List<Tile> tiles = new ArrayList<>();
    Tile curr = null;
    Button search;
    boolean Path = false;
    int index = 0;
    boolean execution = true;
    boolean connected = false;
    public void drawMap()
    {
        try
        {
            X = Integer.parseInt(x.getText());
            Y = Integer.parseInt(y.getText());
            Prcntg = Float.parseFloat(percentage.getText());
        }
        catch(Exception e)
        {
            X=10;
            Y=10;
            Prcntg = 0.5f;
        }
        if(Prcntg<0)
        {
            Prcntg = Math.abs(Prcntg);
        }
        if(Prcntg > 1)
        {
            Prcntg = Prcntg / 100;
        }
        if(Prcntg>100)
        {
            Prcntg = 0.5f;
        }
        b = new Board(X,Y,Prcntg);
        drawing();
        connected = false;
        execution = true;
    }
    
    public void Connection()
    {
        if(b!=null)
        {
            clearCanvas();
            drawing();
            b.resetBoard();
        }
        b.Connect();
        gc.setStroke(Color.GREEN);
        gc.setFill(Color.BLUE);
        for(int xp = 0; xp < X; xp++)
        {
            for(int yp = 0; yp < Y; yp++)
            {
                if(b.GetTile(xp, yp).getElement().equals("Open"))
                {
                    gc.fillOval(xp*20+7.5, yp*20+7.5, 5, 5);
                    for(Object tile : b.GetTile(xp,yp).getAdjacentVertices())
                    {
                        Tile t = (Tile) tile;
                        gc.strokeLine(xp*20+10, yp*20+10, t.GetX()*20+10, t.GetY()*20+10);
                    }
                }
            }
        }
        if(!connected)
        {
            b.calculateHeuristicDistancesAdjacents();
            ChooseThePoints();
            b.calculateHeuristicDistances(end);
            b.calculateStartDist(start, end);
            connected = true;
        }
    }
    public void drawing()
    {
        clearCanvas();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        for(int yp=0; yp<Y; yp++)
        {
            for(int xp=0; xp<X; xp++)
            {
                gc.setFill(Color.BLACK);
                if(b.GetTile(xp, yp).getElement().toString().equals("Closed"))
                {
                    gc.fillRect(xp*20, yp*20, 20, 20);
                }
                else{
                    gc.strokeRect(xp*20, yp*20, 20, 20);
                }
            }
        }
        for(int i = 0; i < X * 20; i = i+20)
        {
            gc.strokeText((""+i/20), i, Y*20+10);
        }
        for(int i = 0; i < Y *20; i= i +20)
        {
            gc.strokeText(""+i/20, X*20+10, i+20);
        }
        if(Startx==null)
        {
            Startx = addTextField("Start(x): ", "", 50);
            Starty = addTextField("Start(y): ", "", 50);
            Endx = addTextField("End(x): ", "", 50);
            Endy = addTextField("End(y): ", "", 50);
            addButton("Connect the tiles", e->Connection());
            search = addButton("Search", e->Exec());
            step = addButton("Stepping", e-> Step());
            addButton("L block", e-> LBlock());
        }
    }
    public void LBlock()
    {
        if(!Startx.getText().equals("") && !Starty.getText().equals("") && !Endx.getText().equals("") && !Endy.getText().equals(""))
        {
            int startx = Integer.parseInt(Startx.getText());
            int starty = Integer.parseInt(Starty.getText());
            int endx = Integer.parseInt(Endx.getText());
            int endy = Integer.parseInt(Endy.getText());
            for(int i = startx; i <= endx; i++)
            {
                b.GetTile(i, starty).ChangeElement("Closed");
            }
            for(int i = starty; i <= endy; i++)
            {
                b.GetTile(endx, i).ChangeElement("Closed");
            }
            clearCanvas();
            if(connected) Connection();
            else drawing();
        }
    }
    public void Step()
    { 
        if(!search.isDisabled()) search.disarm();
        
        if(execution)
        {
            if(!AStar()) new Alert(Alert.AlertType.ERROR, "Kelias nerastas").showAndWait();
            execution = false;
        }
        if(tiles.isEmpty() && curr==start)
        {
            new Alert(Alert.AlertType.ERROR, "Nothing to be done here").showAndWait();
        }
        if(index>=tiles.size() && tiles.size()!=0)
        {
            if(!Path)
            {
                Path=true;
                curr=end;
            }
            else
            {
                if(curr!=start)
                {
                    gc.setStroke(Color.BLUE);
                    gc.setLineWidth(3);
                    Tile prec = curr.getPrec();
                    gc.strokeLine(curr.GetX()*20+10, curr.GetY()*20+10, prec.GetX()*20+10, prec.GetY()*20+10);
                    curr = prec;
                }
                else
                {
                    new Alert(Alert.AlertType.CONFIRMATION, "Kelias rastas.").showAndWait();
                }
            }
            return;
        }
        Tile tile = tiles.get(index);
        gc.setFill(Color.rgb(tile.getRed(), tile.getGreen(), 0));
        gc.fillOval(tile.GetX()*20+5, tile.GetY()*20+5, 10, 10);
        index++;
        
    }
    public void ChooseThePoints()
    {
        if(!Startx.getText().equals("") && !Starty.getText().equals("") && !Endx.getText().equals("") && !Endy.getText().equals(""))
        {
            if(!b.GetTile((Integer.parseInt(Startx.getText())), Integer.parseInt(Starty.getText())).getElement().equals("Closed") &&
                    !b.GetTile((Integer.parseInt(Endx.getText())), Integer.parseInt(Endy.getText())).getElement().equals("Closed"))
            {
                start = b.GetTile((Integer.parseInt(Startx.getText())), Integer.parseInt(Starty.getText()));
                end = b.GetTile((Integer.parseInt(Endx.getText())), Integer.parseInt(Endy.getText()));
                start.setStart();
                end.setEnd();
                gc.setFill(Color.RED);
                gc.fillOval(start.GetX()*20+5, start.GetY()*20+5, 10, 10);
                gc.setFill(Color.GREEN);
                gc.fillOval(end.GetX()*20+5, end.GetY()*20+5, 10, 10);
                return;
            }
        }
        int startstart = 0;
        int startendX = (int) Math.floor(0.25*X);
        int startendY = (int) Math.floor(0.25*Y);
        int endstartX = (int) Math.floor(0.75*X);
        int endstartY = (int) Math.floor(0.75*Y);
        Random rnd = new Random();
        int xs = rnd.nextInt(startendX);
        int ys = rnd.nextInt(Y);
        while(b.GetTile(xs, ys).getElement().equals("Closed"))
        {
            xs = rnd.nextInt(startendX);
            ys = rnd.nextInt(Y);
        }
        start = b.GetTile(xs, ys);
        b.GetTile(xs, ys).setStart();
        gc.setFill(Color.RED);
        gc.fillOval(xs*20+5, ys*20+5, 10, 10);
        xs = rnd.nextInt(startendX) + endstartX;
        ys = rnd.nextInt(Y);
        while(b.GetTile(xs, ys).getElement().equals("Closed"))
        {
            xs = rnd.nextInt(startendX)+endstartX;
            ys = rnd.nextInt(Y);
        }
        end = b.GetTile(xs, ys);
        b.GetTile(xs, ys).setEnd();
        gc.setFill(Color.GREEN);
        gc.fillOval(xs*20+5, ys*20+5, 10, 10);
    }
    
    public void Exec()
    {
        if(AStar())
        {
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(3);
            Tile tile = end;
            for(Tile til : tiles)
            {
                gc.setFill(Color.rgb(til.getRed(), til.getGreen(), 0));
                gc.fillOval(til.GetX()*20+5, til.GetY()*20+5, 10, 10);
            }
            while(tile!=start)
            {
                Tile prec = tile.getPrec();
                gc.strokeLine(tile.GetX()*20+10, tile.GetY()*20+10, prec.GetX()*20+10, prec.GetY()*20+10);
                tile = prec;
            }
            
        }
        else new Alert(Alert.AlertType.ERROR, "Kelias nerastas.").showAndWait();
    }
    
    
    
    public boolean AStar()
    {
        ArrayList<Tile> open = new ArrayList<>(); //kinda kaip prioritetine eile
        //startines pozicijos paruosimas
        start.calcF(0, 0);
        start.setG(0);
        start.VisitTile(start);
        start.setPrec(start);
        //pridedam prie steko
        open.add(start);
        pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e->{
            System.out.println("Bb d");
        });
        while(!open.isEmpty())
        {
            Tile q = findMinF(open);
            q.VisitTile(q.getPrec());
            if(q.getIsEnd())
            {
                return true;
            }
            open.remove(q);
            for(Object t : q.getAdjacentVertices())
            {
                Tile tile = (Tile) t;
                
                float tentativeG = q.getG() + tile.getDistanceUntil(q);
                if(tentativeG < tile.getG())
                {
                    tile.VisitTile(q);
                    tile.setPrec(q);
                    tile.setG(tentativeG);
                    tile.calcF(tile.getG(), tile.euclideanHeuristic(end.GetX(), end.GetY()));
                    //gc.fillOval(tile.GetX()*20+5, tile.GetY()*20+5, 10, 10);
                    tile.calculateTileColor();
                    
                    if(tile.getIsEnd()) end = tile;
                    if(!open.contains(tile)) open.add(tile);
                    
                }
                if(!tiles.contains(tile)) tiles.add(tile);
                System.out.println(tiles.size());
            }
        }
        return false;
    }
    public Tile findMinF(ArrayList<Tile> list)
    {
        Tile min = list.get(0);
        for(Tile t : list)
        {
            if(min.getF() > t.getF())
            {
                min = t;
            }
        }
        return min;
    }
    
    @Override
    public void createControls() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        x = addTextField("X: ", "5", 40);
        y = addTextField("Y: ", "5", 40);
        percentage = addTextField("Percentage: ", "0.3", 50);
        addButton("Draw a map", e->drawMap());
        addButton("Clear", e->clearCanvas());
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
