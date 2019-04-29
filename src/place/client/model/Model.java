package place.client.model;

import place.PlaceBoard;
import place.PlaceTile;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class Model extends PlaceBoard {

    private boolean isChanged;
    private ArrayList<Observer> observerList;

    public Model(int DIM){
        super(DIM);
        isChanged = false;
        observerList = new ArrayList<>();
    }

    @Override
    public void setTile(PlaceTile tile){
        super.setTile(tile);
        setChanged();
        notifyObservers();
    }

    public void setChanged(){
        isChanged = true;
    }

    public void clearChanged(){
        isChanged = false;
    }

    public void notifyObservers(){
        for (Observer obs : observerList){
            obs.update(new Observable(), new Object());
        }
        clearChanged();
    }

    public void addObserver(Observer observer){
        observerList.add(observer);
    }

}
