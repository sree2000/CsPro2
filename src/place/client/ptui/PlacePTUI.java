package place.client.ptui;

import place.PlaceBoard;

import java.util.Observable;
import java.util.Observer;

public class PlacePTUI implements Observer {

    // board
    PlaceBoard board = new PlaceBoard(10);
    //

    @Override
    public void update(Observable o, Object arg) {

    }

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Usage: java PlaceClient host port username");
        }
    }
}
