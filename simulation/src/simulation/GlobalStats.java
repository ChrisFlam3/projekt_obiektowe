package simulation;

import control.Controller;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import utils.Average;

public class GlobalStats {
    int animalNum;
    int grassNum;
    int parentNum;
    int[] genotypes=new int[8];

    Average avgLifeExpectancy =new Average();
    private Average avgChildren=new Average();
    Average avgEnergy=new Average();


    GlobalStats(Controller controller){
        this.animalNumChart=controller.animalNumChart;
        this.grassNumChart=controller.GrassNumChart;
        animalNumChart.getData().addAll(animalsSeries);
        grassNumChart.getData().add(grassSeries);
    }

    //charts
    private StackedAreaChart animalNumChart;
    private XYChart.Series animalsSeries = new XYChart.Series();

    private StackedAreaChart grassNumChart;
    private XYChart.Series grassSeries = new XYChart.Series();



    //update stats including visualization
    public void update(Controller controller,long turn,double delay,double energyLoss){
       // avgEnergy.sumAdd(animalNum*energyLoss*-1);
        avgEnergy.divSet(animalNum);
        if(turn%(int)(1000/delay)!=0)
            return;
        if(turn%1000==0){
            animalsSeries.getData().remove(1,animalsSeries.getData().size()-1);
            grassSeries.getData().remove(1,grassSeries.getData().size()-1);
            NumberAxis axisX=((NumberAxis)animalNumChart.getXAxis());
            axisX.setAutoRanging(false);
            axisX.setLowerBound(turn-500);
            axisX.setUpperBound(turn+1000);
            axisX=((NumberAxis)grassNumChart.getXAxis());
            axisX.setAutoRanging(false);
            axisX.setLowerBound(turn-500);
            axisX.setUpperBound(turn+1000);
        }

        animalsSeries.getData().add(new XYChart.Data<>(turn,animalNum));
        grassSeries.getData().add(new XYChart.Data<>(turn,grassNum));

        controller.lifeTimeExpectancy.setText(String.format("%.3f",avgLifeExpectancy.avg));
        avgChildren.sumSet(parentNum);
        avgChildren.divSet(animalNum-parentNum);

        controller.avarageChildrenNumber.setText(String.format("%.3f",avgChildren.avg));

        controller.avarageEnergy.setText(String.format("%.3f",avgEnergy.avg));

        int max=0;
        int maxIdx=0;
        for(int i=0;i<8;i++)
            if(genotypes[i]>max){
                max=genotypes[i];
                maxIdx=i;
            }

        controller.dominatingGenotype.setText(Integer.toString(maxIdx));
        System.out.println(animalNum);
    }
}
