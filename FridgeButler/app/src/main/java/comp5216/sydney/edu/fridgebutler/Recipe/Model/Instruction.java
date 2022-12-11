package comp5216.sydney.edu.fridgebutler.Recipe.Model;

import java.util.List;

/***************************************************************************************
 *    Title:  FridgePal
 *    Author: amailk
 *    Date:  2019-08-02
 *    Code version: N/A
 *    Availability: https://github.com/amailk/fridge-pal-android
 *
 ***************************************************************************************/

/**
 * Instruction class to retrieve cooking instruction from Spoonacular
 * Attribute name has to be the same as the data in query
 */
public class Instruction {

    List < Step > steps;

    public List < Step > getSteps() {
        return steps;
    }

    public void setSteps(List < Step > steps) {
        this.steps = steps;
    }

}