package comp5216.sydney.edu.fridgebutler.Recipe.Model;

/***************************************************************************************
 *    Title:  FridgePal
 *    Author: amailk
 *    Date:  2019-08-02
 *    Code version: N/A
 *    Availability: https://github.com/amailk/fridge-pal-android
 *
 ***************************************************************************************/
/**
 * Step class to retrieve each instruction from Spoonacular
 * Attribute name has to be the same as the data in query
 */
public class Step {

    int number;
    String step;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}