package place.client.model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import place.PlaceBoard;

public class Model extends PlaceBoard implements Observable {

    public Model(int DIM){
        super(DIM);
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        //how to implement?
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }


}
