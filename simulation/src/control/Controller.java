package control;

import javafx.scene.canvas.Canvas;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import simulation.Simulation;

//Controller class for user input handling
public class Controller {
    double mouseX;
    double mouseY;
     Simulation simulation;

     //interface handlers, public for fxml link
    public javafx.scene.control.Button pauseButton;
    public javafx.scene.control.Button playButton;
    public StackedAreaChart animalNumChart;
    public StackedAreaChart GrassNumChart;
    public Label dominatingGenotype;
    public Label lifeTimeExpectancy;
    public Label avarageEnergy;
    public Label avarageChildrenNumber;
    public Canvas simulationCanvas;
    public Slider speedSlider;



    //user interaction functions
    public void drag(MouseEvent mouseEvent) {
        for(int i=0;i<this.simulation.mapNum;i++){
        this.simulation.grid[i].translateX+=mouseEvent.getX()-mouseX;
        this.simulation.grid[i].translateY+=mouseEvent.getY()-mouseY;
        }
        mouseX=mouseEvent.getX();
        mouseY=mouseEvent.getY();
    }

    public void press(MouseEvent mouseEvent) {
        mouseX=mouseEvent.getX();
        mouseY=mouseEvent.getY();

        if(mouseEvent.isSecondaryButtonDown()==true){

            simulation.pick=true;
                    simulation.pickX=mouseX;
                    simulation.pickY=mouseY;}

    }


    public void scroll(ScrollEvent zoomEvent) {
        double change=zoomEvent.getTextDeltaY()/10;
        if(this.simulation.grid[0].zoom+change<10&&this.simulation.grid[0].zoom+change>0.2) {
        for(int i=0;i<this.simulation.mapNum;i++){
            this.simulation.grid[i].zoomChange(change);
        }
        }

    }

    public void setSimulation(Simulation simulation){
        this.simulation=simulation;
    }

    public void playButtonAction(javafx.event.ActionEvent event) {
        this.simulation.pause=false;
    }

    public void pauseButtonAction(javafx.event.ActionEvent event) {
        this.simulation.pause=true;
    }

    public void testing(MouseEvent mouseEvent){
        //System.out.println("value: "+speedSlider.getValue());
        simulation.delay=speedSlider.getValue();
    }

}
