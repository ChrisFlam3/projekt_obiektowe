package utils;

//utility class to handle and managing avarages
public class Average {
    double sum;
    int div;
    public double avg;

    public Average(){}
    public void sumAdd(double add){
        this.sum+=add;
        this.avg=(double)this.sum/this.div;
    }

    public void divAdd(int add){
        this.div+=add;
        if(this.div<=0)
            this.div=1;
        this.avg=(double)this.sum/this.div;
    }

    public void sumSet(double sum){
        this.sum=sum;
        this.avg=(double)this.sum/this.div;
    }

    public void divSet(int div){
        this.div=div;
        if(this.div<=0)
            this.div=1;
        this.avg=(double)this.sum/this.div;
    }

    public void addToAvg(int add){
        this.sum+=add;
        this.div++;
        this.avg=(double)this.sum/this.div;
    }
}
