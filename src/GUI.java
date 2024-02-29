import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class GUI extends Application {

    //for the canvas
    @FXML
    public Canvas drawPane = new Canvas();
    public GraphicsContext gc = drawPane.getGraphicsContext2D();

    @Override
    public void start(Stage firstStage) throws Exception {

        BorderPane firstRoot = FXMLLoader.load(getClass().getResource("Stage.fxml"));
        firstStage.setTitle("Colorizing Your Grayscale Image");

        firstStage.setScene(new Scene(firstRoot,
                Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight()));
        firstStage.show();

    }

    public static void main(){
        launch();
    }



}
