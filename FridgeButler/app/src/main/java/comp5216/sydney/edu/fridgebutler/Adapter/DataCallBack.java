package comp5216.sydney.edu.fridgebutler.Adapter;

import java.util.ArrayList;

/**
 * Interface for data call back to deal with asynchronous data call
 * Interface used for getting items from Firebase
 */
public interface DataCallBack {
    void onComplete(ArrayList < Item > item);
}