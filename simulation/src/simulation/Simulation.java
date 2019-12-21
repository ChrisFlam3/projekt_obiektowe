package simulation;
import control.Controller;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import utils.Vector2;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class Simulation {


    private long time;
    private int width;
    private int height;
    public int mapNum;
    private Vector2 jungleSize;
    private double startEnergy;
    private double moveEnergy;
    private double plantEnergy;
    private double jungleRatio;
    public int startAnimalNum;
    public Canvas canvas;
    public Grid[] grid;
    private long turn;
    public boolean pause;
    public double delay=20;
    public boolean pick=false;
    public double pickX;
    public double pickY;
    private ArrayList<ArrayList<Animal>> animalList=new ArrayList<ArrayList<Animal>>();
    private GlobalStats stats;

    public Simulation(String json,Controller controller){
        try {
            JSONObject parser = (JSONObject)new JSONParser().parse(new FileReader(json));
            this.width=((Long)parser.get("width")).intValue();
            this.height=((Long)parser.get("height")).intValue();
            this.mapNum=((Long)parser.get("mapNum")).intValue();
            this.startEnergy=((Double)parser.get("startEnergy"));
            this.moveEnergy=((Double)parser.get("moveEnergy"));
            this.plantEnergy=((Double)parser.get("plantEnergy"));
            this.jungleRatio=((Double)parser.get("jungleRatio"));
            this.startAnimalNum=((Long)parser.get("startAnimalNum")).intValue();

        }
        catch(Exception error){
            System.out.println("File read error "+error.getMessage());
        }
        this.jungleSize=new Vector2(((Double)(width*jungleRatio)).intValue(),((Double)(height*jungleRatio)).intValue());
        this.grid=new Grid[mapNum];
        for(int i=0;i<mapNum;i++) {
            if(Math.sqrt(mapNum)==(int)Math.sqrt(mapNum))
                this.grid[i] = new Grid(this.width, this.height, new Vector2(width / 2, height / 2), new Vector2((int) (this.width * this.jungleRatio), (int) (this.height * this.jungleRatio)), i%Math.sqrt(mapNum) * this.width * 720/(double)Math.max(this.width,this.height),i/(int)Math.sqrt(mapNum) * this.height *720/(double)Math.max(this.width,this.height) );
            else
            this.grid[i] = new Grid(this.width, this.height, new Vector2(width / 2, height / 2), new Vector2((int) (this.width * this.jungleRatio), (int) (this.height * this.jungleRatio)), i * this.width * 720/(double)Math.max(this.width,this.height), 0);
        this.animalList.add(new ArrayList<Animal>());
        }
        this.stats=new GlobalStats(controller);

    }
    //initialize basic state by adding starting animals to maps
    public void initialize(int startAnimals,long time){

        Random gen=new Random();
        gen.nextInt(jungleSize.x);


        for(int y=0;y<mapNum;y++){

            int counter=0;
            for(int i=0;i<startAnimals;i++){
                if(counter>20)
                    break;
                Vector2 pos=new Vector2(width/2+gen.nextInt(Math.max(jungleSize.x/2,1))*(gen.nextBoolean()?-1:1),height/2+gen.nextInt(Math.max(jungleSize.y/2,1))*(gen.nextBoolean()?-1:1));
                if(grid[y].isOccupied(pos)==true){
                    counter++;
                    i--;
                    continue;
                }
                Animal basic=new Animal(pos,startEnergy);
                grid[y].addObject(basic);
                animalList.get(y).add(basic);
                this.stats.genotypes[basic.genes.dominating]++;
                stats.animalNum++;
        }}
        this.time=time;
        stats.avgEnergy.sumSet(mapNum*startAnimals*startEnergy);
        stats.avgEnergy.divSet(mapNum*startAnimals);
    }

    public void simulate(long time, Controller controller){
        //timer handling
        if(pause==true||time-this.time<delay)
            return;
        this.time=time;
        turn++;


        for(int y=0;y<mapNum;y++){
            if(animalList.get(y).size()==0)
                continue;
            //clear dead and update stats
        for(int i=0;i<animalList.get(y).size();i++){
            if(animalList.get(y).get(i).checkStatus()==true){
                stats.avgLifeExpectancy.addToAvg(animalList.get(y).get(i).lifeTime);
                if(animalList.get(y).get(i).childNum>0)
                    stats.parentNum--;
                this.stats.genotypes[animalList.get(y).get(i).genes.dominating]--;
                animalList.get(y).remove(i);
                stats.animalNum--;


            }
        }


        //move phaze

        for(int i=0;i<animalList.get(y).size();i++) {
            double beforeEnergy=animalList.get(y).get(i).energy;
            animalList.get(y).get(i).move(width, height);
            stats.avgEnergy.sumAdd( animalList.get(y).get(i).energy-beforeEnergy);

        }
        //eating and update stats

            for(int i=0;i<animalList.get(y).size();i++)
                if(grid[y].eatGrassOnPos(animalList.get(y).get(i).pos)==true){
                    grid[y].feedThemAll(animalList.get(y).get(i).pos,plantEnergy);
                    stats.avgEnergy.sumAdd(plantEnergy);
                    stats.grassNum--;
                }


        //clone and update stats

        for(int i=0;i<animalList.get(y).size();i++){
            if(animalList.get(y).get(i).touched==false&&grid[y].numberOfObjectsOnPos(animalList.get(y).get(i).pos)>1){
                Animal[] strongest=grid[y].getStrongestPair(animalList.get(y).get(i).pos,startEnergy/2);
                if(strongest==null)
                    continue;
                Animal child=new Animal(strongest[0],strongest[1]);
                grid[y].addObject(child);
                animalList.get(y).add(child);
                stats.animalNum++;
                if(strongest[0].childNum==0)
                    stats.parentNum++;
                if(strongest[1].childNum==0)
                    stats.parentNum++;
                strongest[0].childNum++;
                strongest[1].childNum++;

                this.stats.genotypes[child.genes.dominating]++;
            }
        }


        //add grass (2 in jungle, 2 outside) and update stats
        Random gen=new Random();
        int counter=0;

            counter=0;
            for (int i = 0; i < 2; i++) {
                if(counter>20)
                    break;
                Vector2 pos = new Vector2(width / 2 + gen.nextInt(Math.max(jungleSize.x/2,1)) * (gen.nextBoolean() ? -1 : 1), height / 2 + gen.nextInt(Math.max(jungleSize.y/2,1)) * (gen.nextBoolean() ? -1 : 1));
                if (grid[y].isOccupied(pos) == true) {
                    counter++;
                    i--;
                    continue;
                }

                grid[y].addObject(new Grass(pos));
                stats.grassNum++;
            }
            counter=0;
            for (int i = 0; i < 2; i++) {
                if(counter>20)
                    break;
                Vector2 pos = new Vector2( gen.nextInt(width) , gen.nextInt(height));
                if(grid[y].isOccupied(pos)==true||(pos.x<width/2+jungleSize.x/2&&pos.x>width/2-jungleSize.x/2&&pos.y<height/2+jungleSize.y/2&&pos.y>height/2-jungleSize.y/2)){
                    counter++;
                    i--;
                    continue;
                }

                grid[y].addObject(new Grass(pos));
                stats.grassNum++;
            }

        }
        //calculate advanced stats and update visualization
        stats.update(controller,turn,delay,moveEnergy);
    }
    //visualize animation
    public void visualize(GraphicsContext context){
        this.grid[0].printBackground(context);
        for(int i=0;i<this.mapNum;i++)
            this.grid[i].visualize(context,this.startEnergy);

        if(pick==true){

            if(this.picker(context,pickX,pickY)==false)
                pick=false;
        }

    }

    //pick animal (and display its data) from given screen space coords using viewport transformations
    public boolean picker(GraphicsContext context,double x,double y){
        x=x*1/grid[0].zoom;
        y=y*1/grid[0].zoom;
        double offsetX=grid[0].translateX;
        double offsetY=grid[0].translateY;
        offsetX=offsetX/grid[0].zoom;
        offsetY=offsetY/grid[0].zoom;
        double totalX=x-offsetX;
        double totalY=y-offsetY;
        double zoom=grid[0].zoom;


        //System.out.println((int)(totalX/(grid[0].width*grid[0].cellSize/zoom))+" "+(int)(totalY/(grid[0].height*grid[0].cellSize/zoom)));

        int mapX=(int)((totalX%(grid[0].width*grid[0].cellSize/zoom))/(grid[0].cellSize/zoom));
        int mapY=(int)((totalY%(grid[0].height*grid[0].cellSize/zoom))/(grid[0].cellSize/zoom));
        int gridNum;
        //System.out.println("You picked grid "+gridNum+" on position "+mapX+" "+mapY);

        if(Math.sqrt(mapNum)==(int)Math.sqrt(mapNum))
            gridNum=(int)(totalX/(grid[0].width*grid[0].cellSize/zoom))+(int)(totalY/(grid[0].height*grid[0].cellSize/zoom))*(int)Math.sqrt(mapNum);
        else
            gridNum=(int)(totalX/(grid[0].width*grid[0].cellSize/zoom));

        if(grid[gridNum].isOccupiedByAnimal(new Vector2(mapX,mapY))==true){
            Animal selected=grid[gridNum].getStrongestAnimal(new Vector2(mapX,mapY));

            String genotype="";
            for(int i=0;i<8;i++)
                for(int z=0;z<selected.genes.genes[i];z++)
                    genotype+=Integer.toString(i);

            context.setFill(Color.BLACK);

            context.setFont(Font.font("Arial",20));
            context.fillText("Energy: "+Double.toString(selected.energy),20,30);
            context.fillText("Genotype: "+genotype,20,60);
            context.fillText("Children number: "+Integer.toString(selected.childNum),20,90);
            context.setFill(Color.RED);

            return true;
        }else return false;



    }
}
