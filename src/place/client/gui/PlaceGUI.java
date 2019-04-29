package place.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.paint.*;
import place.PlaceBoard;
import place.PlaceColor;
import place.PlaceTile;
import place.client.NetworkClient;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * The GUI representaion of the Place Client
 *
 * @author John McCarroll & Sree Jupudy
 */

public class PlaceGUI extends Application implements Observer {

    /* holds state of the board for view */
    PlaceBoard board;
    /* the helper class that handles all the networking with the server */
    NetworkClient connection;
    /* the username of the client */
    String username;
    /* the gridpane that holds tile representations for the view */
    GridPane grid;
    /* the current selected color */
    PlaceColor currentColor;
    /* list of color selection buttons */
    ArrayList<Button> buttonList;
    /* list of labels for color selection buttons */
    ArrayList<String> labelList;
    /* list of colors for color selection buttons */
    ArrayList<String> colorList;

    /**
     * Initializes color selection buttons, server connection, view board, and activates NetworkClient to listen for
     * updates
     */
    @Override
    public void init(){
        // establish color selection buttons
        currentColor = PlaceColor.WHITE;

        buttonList = new ArrayList();
        colorList = buildColorList();
        labelList = buildLabelList();

        for (int i = 0; i < 16; i++){
            buttonList.add(new Button(labelList.get(i)));
            buttonList.get(i).setStyle(colorList.get(i));
            buttonList.get(i).setOnAction(new ButtonHandler1());
        }


        //establish connection from command line args
        List<String> args = getParameters().getRaw();
        this.connection = new NetworkClient(args.get(0), Integer.parseInt(args.get(1)), args.get(2));
        this.username = args.get(2);

        // board
        this.board = receiveBoard();

        // run
        Thread input = new Thread(this.connection);
        input.start();

    }

    /**
     * Sets up GUI display and event handling
     * @param primaryStage fx stage
     */
    @Override
    public void start(Stage primaryStage) {
        // display

        int DIM = 10;
        BorderPane pane = new BorderPane();
        grid = new GridPane();

        // canvas
        for (int i = 0; i < DIM; i++){
            for (int j = 0; j < DIM; j++){
                Rectangle rect = new Rectangle();
                grid.add(rect, i, j);
                rect.setWidth(50);
                rect.setHeight(50);
                rect.setOnMouseClicked(new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent t) {
                        connection.sendTile(grid.getChildren().indexOf(rect) % board.DIM,grid.getChildren().indexOf(rect) / board.DIM, currentColor, username);
                    }
                });

            }
        }
        update(new Observable(), new Object());

        // color bar
        HBox bar = new HBox();
        for (Button btn : buttonList){
            bar.getChildren().add(btn);
        }

        pane.setCenter(grid);
        pane.setBottom(bar);
        primaryStage.setScene(new Scene(pane));
        primaryStage.setTitle(username);

        primaryStage.show();

    }

    /**
     * An Observer method that updates the view's tiles and tooltips any time the proxy model (in NetworkClient) is
     * updated.
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        this.board = this.connection.getModel();
        for (int i = 0; i < board.DIM; i++){
            for (int j = 0; j < board.DIM; j++){
                // set colors
                PlaceColor tileColor = board.getTile(i,j).getColor();

                Color color = Color.rgb(tileColor.getRed(), tileColor.getGreen(), tileColor.getBlue());
                Rectangle rect = (Rectangle) grid.getChildren().get(j*board.DIM + i%board.DIM);
                rect.setFill(color);

                // set tooltip
                PlaceTile tile = board.getTile(i,j);
                int row = tile.getRow();
                int col = tile.getCol();
                String user = tile.getOwner();
                long time = tile.getTime();
                Timestamp stamp = new Timestamp(time);
                int year = stamp.getYear() + 1900;
                int month = stamp.getMonth() + 1;
                int date = stamp.getDate();
                int hour = stamp.getHours();
                int min = stamp.getMinutes();
                int sec = stamp.getSeconds();

                Tooltip.install(rect, new Tooltip("(" + row + "," + col + ")" + "\n"+ user+ "\n" +month + "-" + date + "-" + year + "\n" + hour + ":" + min + ":" + sec));
            }
        }
    }

    /**
     * The stop method is called upon exitting the GUI window - stops NetworkClient and program.
     */
    @Override
    public void stop(){
        connection.setRunning(false);
        Platform.exit();
        System.exit(0);
    }

    /**
     * A helper method to fill colorList with -fx-background-color strings for coloring color selection buttons
     * @return filled list of colors
     */
    public ArrayList<String> buildColorList(){
        ArrayList<String> colors = new ArrayList<>();
        colors.add("-fx-background-color: rgb(0, 0, 0)");
        colors.add("-fx-background-color: rgb(128, 128, 128)");
        colors.add("-fx-background-color: rgb(192, 192, 192)");
        colors.add("-fx-background-color: rgb(255, 255, 255)");
        colors.add("-fx-background-color: rgb(128, 0, 0)");
        colors.add("-fx-background-color: rgb(255, 0, 0)");
        colors.add("-fx-background-color: rgb(128, 128, 0)");
        colors.add("-fx-background-color: rgb(255, 255, 0)");
        colors.add("-fx-background-color: rgb(0, 128, 0)");
        colors.add("-fx-background-color: rgb(0, 255, 0)");
        colors.add("-fx-background-color: rgb(0, 128, 128)");
        colors.add("-fx-background-color: rgb(0, 255, 255)");
        colors.add("-fx-background-color: rgb(0, 0, 128)");
        colors.add("-fx-background-color: rgb(0, 0, 255)");
        colors.add("-fx-background-color: rgb(128, 0, 128)");
        colors.add("-fx-background-color: rgb(255, 0, 255)");

        return colors;
    }

    /**
     * A helper method to fill labelList with labels for color selection buttons
     * @return filled list of labels
     */
    public ArrayList<String> buildLabelList(){
        ArrayList<String> labels = new ArrayList<>();
        labels.add("0");
        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");
        labels.add("7");
        labels.add("8");
        labels.add("9");
        labels.add("A");
        labels.add("B");
        labels.add("C");
        labels.add("D");
        labels.add("E");
        labels.add("F");

        return labels;
    }

    /**
     * get board from server
     * @return newest version of model
     */
    public PlaceBoard receiveBoard(){
        return connection.getBoard(this);
    }

    /**
     * The main method - launches fx application
     * @param args launch specification
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceGUI host port username");
            System.exit(-1);
        } else {

            System.out.println("launching app");
            Application.launch(args);
        }
    }

    /**
     * Class for handling events generated from GUI buttons - the controller
     */
    class ButtonHandler1 implements EventHandler<ActionEvent> {

        /**
         * Changes currentColor according to designated button event
         *
         * @param event The event
         */
        @Override
        public void handle( ActionEvent event ) {

            Button btn = (Button) event.getSource();
            switch (btn.getText()){
                case "0":
                    currentColor = PlaceColor.BLACK;
                    break;
                case "1":
                    currentColor = PlaceColor.GRAY;
                    break;
                case "2":
                    currentColor = PlaceColor.SILVER;
                    break;
                case "3":
                    currentColor = PlaceColor.WHITE;
                    break;
                case "4":
                    currentColor = PlaceColor.MAROON;
                    break;
                case "5":
                    currentColor = PlaceColor.RED;
                    break;
                case "6":
                    currentColor = PlaceColor.OLIVE;
                    break;
                case "7":
                    currentColor = PlaceColor.YELLOW;
                    break;
                case "8":
                    currentColor = PlaceColor.GREEN;
                    break;
                case "9":
                    currentColor = PlaceColor.LIME;
                    break;
                case "A":
                    currentColor = PlaceColor.TEAL;
                    break;
                case "B":
                    currentColor = PlaceColor.AQUA;
                    break;
                case "C":
                    currentColor = PlaceColor.NAVY;
                    break;
                case "D":
                    currentColor = PlaceColor.BLUE;
                    break;
                case "E":
                    currentColor = PlaceColor.PURPLE;
                    break;
                case "F":
                    currentColor = PlaceColor.FUCHSIA;
                    break;
            }


        }
    }

}
