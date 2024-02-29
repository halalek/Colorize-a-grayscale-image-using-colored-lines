import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private BorderPane firstScene;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button uploadImage;
    @FXML
    private StackPane mainPane;
    @FXML
    private VBox vBox;


    @FXML
    private Canvas drawPane;
    private GraphicsContext gc;

    final FileChooser fileChooser = new FileChooser();
    Colorize colorize = new Colorize();

    int pixelX;
    int pixelY;
    int r, g, b;


    //to center the image
    Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
    double width = 500;
    double height = 200;

    Image image;
    BufferedImage inputImg;
    BufferedImage output;
    String imgPath;

    //Canny parameters
    private static final double CANNY_THRESHOLD_RATIO = .2; //Suggested range .2 - .4
    private static final int CANNY_STD_DEV = 1;             //Range 1-3


    public void uploadImage() throws IOException {
        File file = fileChooser.showOpenDialog(uploadImage.getScene().getWindow());
        if (file != null) {

            imgPath = file.toURI().toString();
            String inputImgPath = imgPath.substring(6);
            inputImgPath  = inputImgPath.replace("%20", " ");

            inputImg = ImageIO.read(new File(inputImgPath));

            image = new Image(imgPath);

            mainPane.getChildren().remove(vBox);
            colorPicker.setVisible(true);
            drawPane.setWidth(image.getWidth());
            drawPane.setHeight(image.getHeight());
            gc.drawImage(image, gc.getCanvas().getWidth() / 2 - image.getWidth() / 2, gc.getCanvas().getHeight() / 2 - image.getHeight() / 2, image.getWidth(), image.getHeight());

            drawLine();

        }
    }


    public static String toRGBCode( Color color )
    {
        return String.format( "#%02X%02X%02X",
                (int)( color.getRed() * 255 ),
                (int)( color.getGreen() * 255 ),
                (int)( color.getBlue() * 255 ) );
    }

    public void changeColor(ActionEvent e) {

        Color pickedColor = colorPicker.getValue();
        String s = toRGBCode(pickedColor);

        r = Integer.valueOf( s.substring( 1, 3 ), 16 );
        g = Integer.valueOf( s.substring( 3, 5 ), 16 );
        b = Integer.valueOf( s.substring( 5, 7 ), 16 );

        System.out.println(r);
        System.out.println(g);
        System.out.println(b);


        gc.setStroke(pickedColor);
    }

    public void drawLine() {

        drawPane.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        gc.beginPath();
                        gc.moveTo(e.getX(), e.getY());

                        pixelX = (int) e.getX();
                        pixelY = (int) e.getY();

                        gc.stroke();
                    }
                });
        drawPane.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent e) {
                        gc.lineTo(e.getX(), e.getY());
                        gc.stroke();
                    }
                });

        drawPane.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>() {

                    @Override
                    public void handle(MouseEvent e) {
                        output = colorize.colorizeImage(inputImg, pixelX, pixelY, r, g, b);
//                        image = (image) output;
                        try {
                            colorize.saveImage(imgPath, output);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                        inputImg = output;
                    }
                });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("drawPane initialized");

        gc = drawPane.getGraphicsContext2D();
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(5);

    }
}


