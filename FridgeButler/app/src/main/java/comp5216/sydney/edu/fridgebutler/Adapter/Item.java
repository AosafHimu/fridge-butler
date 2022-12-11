package comp5216.sydney.edu.fridgebutler.Adapter;

/**
 * Interface for data call back to deal with asynchronous data call
 * Interface used for getting items from Firebase
 */
public class Item {
    private String name;
    private String expiryDate;
    private String docRef;

    //Constuctor for user input ingredients
    public Item(String name, String expiryDate, String docRef) {
        this.name = name;
        this.expiryDate = expiryDate;
        this.docRef = docRef;

    }

    //Constuctor for ingredients retrieved from Spoonacular
    public Item(String name, String docRef) {
        this.name = name;
        this.docRef = docRef;

    }

    //return Item's name
    public String getName() {
        return name;
    }

    //return Item's expiry date
    public String getExpiryDate() {
        return expiryDate;
    }

    //get document reference from firebase
    public String getDocRef() {
        return docRef;
    }

}