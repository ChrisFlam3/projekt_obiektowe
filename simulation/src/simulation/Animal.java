package simulation;

import utils.Direction;
import utils.Vector2;

import java.util.Random;

class Animal extends MapObject{
    int childNum;
    int lifeTime;
    boolean touched;
    Direction orientation;
    double energy;
    Genotype genes;

    public Animal(Vector2 pos,double energy){
        super(pos);
        Random gen=new Random();
        orientation=new Direction(gen.nextInt(9));
        this.energy=energy;
        genes=new Genotype();

    }
    //child generating
    public Animal(Animal parent1,Animal parent2){
        super(parent1.pos);
        Random gen=new Random();
        orientation=new Direction(gen.nextInt(9));
        Direction offset=new Direction(gen.nextInt(8));
        this.pos=this.pos.sum(offset.getDirVec());
        this.energy=parent1.energy/4+parent2.energy/4;
        parent1.energy-=parent1.energy/4;
        parent2.energy-=parent2.energy/4;

        genes=new Genotype(parent1,parent2);


    }


    //check if dead, update stats
    public boolean checkStatus(){
        if(this.energy<=0){
            this.energy=0;
            observer.notPresent(this);
            return true;
        }
        lifeTime++;
        return false;
    }
    //move, inform observer and update stats
    public void move(int width,int height){
        touched=false;
        energy--;
        if(energy<0)
            energy=0;
        Vector2 oldPos=this.pos;

        Random gen=new Random();
        int rand=gen.nextInt(31);
        int sum=0;
        int i=0;
        for(;i<8;i++){
            sum+=genes.genes[i];
            if(sum>=rand)
                break;

        }

        orientation.rotate(i);
        this.pos=this.pos.sum(orientation.getDirVec());
        this.pos.x=this.pos.x%width;
        if(this.pos.x<0)
            this.pos.x=width+this.pos.x;
        this.pos.y=this.pos.y%height;
        if(this.pos.y<0)
            this.pos.y=height+this.pos.y;
        observer.positionChanged(oldPos,this);



    }




}
