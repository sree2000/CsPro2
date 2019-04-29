package place.client.model;

import place.PlaceBoard;
import place.PlaceTile;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * The Model class acts as a wrapper class for PlaceBoard in order to implement Observable methods.
 *
 * @authoer John McCarroll & Sree Jupudy
 */

public class Model extends PlaceBoard {

    /* boolean to denote if board has been changed */
    private boolean isChanged;
    /* list of observers to model */
    private ArrayList<Observer> observerList;

    /**
     * Calls super constructor and sets up observable states
     * @param DIM dimensions of the square board
     */
    public Model(int DIM){
        super(DIM);
        isChanged = false;
        observerList = new ArrayList<>();
    }

    /**
     * Overridden to notify observers of changes to board
     * @param tile the new tile
     */
    @Override
    public void setTile(PlaceTile tile){
        super.setTile(tile);
        setChanged();
        notifyObservers();
    }

    /**
     * Setter method for isChanged boolean
     */
    public void setChanged(){
        isChanged = true;
    }

    /**
     * setter method for isChanged boolean
     */
    public void clearChanged(){
        isChanged = false;
    }

    /**
     * Iterates through list of observers and calls update on each
     */
    public void notifyObservers(){
        if (isChanged) {
            for (Observer obs : observerList) {
                obs.update(new Observable(), new Object());
            }
            clearChanged();
        }
    }

    /**
     * Adds observer to list of observers
     * @param observer
     */
    public void addObserver(Observer observer){
        observerList.add(observer);
    }

}
