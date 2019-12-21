package control;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import simulation.Simulation;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        //app setup, resource initialization
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = (Parent)fxmlLoader.load();
        Controller controller = fxmlLoader.<Controller>getController();

        primaryStage.setTitle("Simulation");
        primaryStage.setResizable(false);
        Simulation simulation=new Simulation("param.json",controller);
        controller.setSimulation(simulation);
        primaryStage.show();


        primaryStage.setScene(new Scene(root, 960, 720));
        Scene scene = primaryStage.getScene();
        Canvas canvas=(Canvas)scene.lookup("#simulationCanvas");
        simulation.canvas=canvas;
        GraphicsContext context = canvas.getGraphicsContext2D();
        simulation.initialize(simulation.startAnimalNum,System.currentTimeMillis());

        //main loop
        new AnimationTimer(){
            public void handle(long currentNanoTime)
            {
                context.clearRect(0,0,960,720);
                simulation.visualize(context);
                simulation.simulate(System.currentTimeMillis(),controller);
            }

        }.start();
            primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
