package simulation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utils.Vector2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Grid implements IStateObserver {

    int height = 0;
    int width = 0;
    double cellSize=7.2;


    public double zoom=1;
    public double translateX;
    public double translateY;


    private Vector2 junglePos;
    private Vector2 jungleSize;
    HashMap<Vector2, ArrayList<MapObject>> grid = new HashMap<>();

    public Grid(int w, int h, Vector2 jPos, Vector2 jSize,double translateX,double translateY) {
        this.width = w;
        this.height = h;
        this.junglePos = jPos;
        this.jungleSize = jSize;
        this.translateX=translateX;
        this.translateY=translateY;
        this.cellSize=720/(double)Math.max(w,h);

    }

    public void addObject(MapObject obj) {
        ArrayList<MapObject> values = grid.get(obj.pos);
        if (values == null) {
            values = new ArrayList<MapObject>();
            values.add(obj);
            grid.put(obj.pos, values);
        } else {
            values.add(obj);
        }

        obj.addObserver(this);
    }

    public void removeObject(MapObject obj) {
        ArrayList<MapObject> values = grid.get(obj.pos);
        values.remove(obj);
    }


    @Override
    public void positionChanged(Vector2 oldPosition, MapObject obj) {
        ArrayList<MapObject> values = grid.get(oldPosition);
        values.remove(obj);

        this.addObject(obj);
    }

    @Override
    public void notPresent(MapObject obj) {
        this.removeObject(obj);
    }

    public boolean isOccupied(Vector2 pos){
        if(grid.get(pos)!=null&&grid.get(pos).size()>0)
            return true;
        return false;
    }

    public boolean isOccupiedByAnimal(Vector2 pos){
        if(grid.get(pos)!=null&&grid.get(pos).size()>0&&grid.get(pos).get(0) instanceof Animal)
            return true;
        return false;
    }

    //check and remove if there is grass on given position
    public boolean eatGrassOnPos(Vector2 pos){

        if(grid.get(pos)!=null&&grid.get(pos).get(0) instanceof Grass){
            grid.get(pos).remove(0);
            return true;}
        return false;
    }

    public int numberOfObjectsOnPos(Vector2 pos){
        if(grid.get(pos)!=null){
            return  grid.get(pos).size();
        }
        return 0;
    }
    //feed strongest animals on position by given energy divided by their number
    public void feedThemAll(Vector2 pos,double energy){
        ArrayList<MapObject> objectList=(ArrayList<MapObject>)grid.get(pos);
        double max=0;
        int strongestNum=0;
        for(int i=0;i<objectList.size();i++)
            if(((Animal)objectList.get(i)).energy>max)
                max=((Animal)objectList.get(i)).energy;

        for(int i=0;i<objectList.size();i++)
            if(((Animal)objectList.get(i)).energy==max)
                strongestNum++;

        for(int i=0;i<objectList.size();i++)
            if(((Animal)objectList.get(i)).energy==max)
                ((Animal)objectList.get(i)).energy+=energy/strongestNum;

    }
    //get strongest pair of animals (if exist) which satisfies minimum energy requirement
    public Animal[] getStrongestPair(Vector2 pos,double minEnergy){
        Animal[] strongest=new Animal[2];
        ArrayList<MapObject> objectList=(ArrayList<MapObject>)grid.get(pos);
        if(objectList.get(0) instanceof Grass)
            return null;
        double max=0;
        int idx=0;
        for(int i=0;i<objectList.size();i++) {
            ((Animal) objectList.get(i)).touched=true;
            if (((Animal) objectList.get(i)).energy > max)
                max = ((Animal) objectList.get(i)).energy;
        }
        for(int i=0;i<objectList.size()&&idx<2;i++)
            if(((Animal)objectList.get(i)).energy==max){
                strongest[idx]=(Animal)objectList.get(i);
                idx++;
            }
        if(idx==1){
            double max2=0;
            for(int i=0;i<objectList.size();i++)
                if(((Animal)objectList.get(i)).energy>max2&&((Animal)objectList.get(i)).energy!=max)
                    max2=((Animal)objectList.get(i)).energy;

            for(int i=0;i<objectList.size();i++)
                if(((Animal)objectList.get(i)).energy==max2){
                    strongest[idx]=(Animal)objectList.get(i);
                    break;
                }

        }

        if(strongest[0]==null||strongest[0].energy<minEnergy||strongest[1]==null||strongest[1].energy<minEnergy)
            return null;
        return strongest;
    }
    public Animal getStrongestAnimal(Vector2 pos){
        ArrayList<MapObject> objectList=(ArrayList<MapObject>)grid.get(pos);
        double max=0;
        int idx=-1;
        for(int i=0;i<objectList.size();i++) {
            if (objectList.get(i) instanceof Animal&&((Animal) objectList.get(i)).energy > max)
                max = ((Animal) objectList.get(i)).energy;
                idx=i;
        }
        if(idx==-1)
            return null;
        return (Animal)objectList.get(idx);
    }
    //draw grid and associated objects using given context
    public void visualize(GraphicsContext context,double startEnergy) {

        context.setFill(Color.RED);
        context.setStroke(Color.BLACK);
        context.setLineWidth(0.1);


        for (Map.Entry element : grid.entrySet()) {
            Vector2 pos=(Vector2)element.getKey();
            ArrayList<MapObject> objectList=(ArrayList<MapObject>)element.getValue();

            if(objectList.size()!=0&&objectList.get(0) instanceof Animal){
                context.setFill(Color.color((((Animal) objectList.get(0)).energy>startEnergy?1: Math.abs(((Animal) objectList.get(0)).energy/startEnergy)),0,0));

                context.fillRect(translateX+pos.x*cellSize,translateY+pos.y*cellSize,cellSize,cellSize);
            }else if(objectList.size()!=0&&objectList.get(0) instanceof Grass){
                context.setFill(Color.GREEN);
                context.fillRect(translateX+pos.x*cellSize,translateY+pos.y*cellSize,cellSize,cellSize);
                context.setFill(Color.RED);
            }

        }
        context.setLineWidth(1);
        context.strokeRect(translateX, translateY, width*cellSize, height*cellSize);
        context.setLineWidth(0.1);
        }
        //change zoom transformation for given grid, overall it is not used but it supports multiple viewports
        public void zoomChange(double zoom){
            if(this.zoom<0)
                this.zoom=-1/this.zoom;
            this.translateX=(this.translateX+((Math.max(width,height) * cellSize) / this.zoom * (this.zoom/2-0.5)))/this.zoom;
            this.translateY=(this.translateY+((Math.max(width,height) * cellSize) / this.zoom * (this.zoom/2-0.5)))/this.zoom;
            this.cellSize=this.cellSize/this.zoom;


            this.zoom+=zoom;
            if(this.zoom<0)
                this.zoom=-1/this.zoom;
        this.cellSize=this.cellSize*this.zoom;

                this.translateX = this.translateX*this.zoom-(Math.max(width,height) * cellSize) / this.zoom * (this.zoom/2-0.5);
                this.translateY =this.translateY*this.zoom-(Math.max(width,height) * cellSize) / this.zoom * (this.zoom/2-0.5);



        }
        //print the background, maps bounds and grid lines according to current viewport transformation
        public void printBackground(GraphicsContext context){
            context.setStroke(Color.BLACK);
            context.setLineWidth(0.1);
            if(this.zoom>=1&&Math.max(width,height)*Math.max(width,height)/(this.zoom*this.zoom)<50000){
                context.setFill(Color.BISQUE);


                context.fillRect(translateX%cellSize - cellSize, (translateY%cellSize) - cellSize, cellSize*Math.max(width+2,height+2), cellSize*Math.max(width+2,height+2));
                context.setFill(Color.RED);
                for (int x = -1; x < Math.max(width+2,height+2)/this.zoom; x++)
                    for (int y = -1; y < Math.max(width+2,height+2)/this.zoom; y++)
                        context.strokeRect(translateX%cellSize+x * cellSize, (translateY%cellSize)+y * cellSize, cellSize, cellSize);
        }else{
                context.setFill(Color.BISQUE);
                context.fillRect((translateX%cellSize)-cellSize, (translateY%cellSize)- cellSize, cellSize*Math.max(width+2,height+2)*1/this.zoom, cellSize*Math.max(width+2,height+2)*1/this.zoom);
                context.setFill(Color.RED);
    }
    }
}