package place.client.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.paint.*;
import place.PlaceBoard;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PlaceGUI extends Application implements Observer {

    //local board representation
    PlaceBoard board;

    // color selection buttons
    ArrayList<Button> buttonList;

    Button aqua;
    Button black;
    Button blue;
    Button fuchsia;
    Button gray;
    Button green;
    Button lime;
    Button maroon;
    Button navy;
    Button olive;
    Button purple;
    Button red;
    Button silver;
    Button teal;
    Button white;
    Button yellow;

    @Override
    public void init(){
        //

        buttonList = new ArrayList();

        aqua = new Button("0");
        aqua.setStyle("-fx-background-color: Green");
        black = new Button("1");
        blue = new Button("2");
        fuchsia = new Button("3");
        gray = new Button("4");
        green = new Button("5");
        lime = new Button("6");
        maroon = new Button("7");
        navy = new Button("8");
        olive = new Button("9");
        purple = new Button("A");
        red = new Button("B");
        silver = new Button("C");
        teal = new Button("D");
        white = new Button("E");
        yellow = new Button("F");

        buttonList.add(aqua);
        buttonList.add(black);
        buttonList.add(blue);
        buttonList.add(fuchsia);
        buttonList.add(gray);
        buttonList.add(green);
        buttonList.add(lime);
        buttonList.add(maroon);
        buttonList.add(navy);
        buttonList.add(olive);
        buttonList.add(purple);
        buttonList.add(red);
        buttonList.add(silver);
        buttonList.add(teal);
        buttonList.add(white);
        buttonList.add(yellow);


        //establish connection and model?


    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // display

        int DIM = 10;
        BorderPane pane = new BorderPane();
        GridPane grid = new GridPane();
        /*Canvas canvas = new Canvas(DIM*10, DIM*10);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.GREEN)*/

        // canvas
        for (int i = 0; i < DIM; i++){
            for (int j = 0; j < DIM; j++){
                Rectangle rect = new Rectangle();
                grid.add(rect, i, j);
                rect.setWidth(50);
                rect.setHeight(50);
                if ((i+j) % 2 == 0){
                    rect.setFill(Color.GREEN);
                } else {
                    rect.setFill(Color.YELLOW);
                }
            }
        }

        // color bar
        HBox bar = new HBox();
        for (Button btn : buttonList){
            bar.getChildren().add(btn);
        }

        pane.setCenter(grid);
        pane.setBottom(bar);
        primaryStage.setScene(new Scene(pane));
        primaryStage.setTitle("TEST");

        primaryStage.show();



    }

    @Override
    public void update(Observable o, Object arg) {

    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceGUI host port username");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }


}
