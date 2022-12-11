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
 * Recipe class to retrieve each recipe from Spoonacular
 * Attribute name has to be the same as the data in query
 */
public class Recipe {
    String id;
    String title;
    String image;
    String instructions;
    List < Ingredient > extendedIngredients;
    List < Instruction > analyzedInstructions;

    public Recipe(String id, String image) {
        this.id = id;
        this.image = image;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List < Instruction > getAnalyzedInstructions() {
        return analyzedInstructions;
    }

    public void setStepByStepInstructions(List < Instruction > analyzedInstructions) {
        this.analyzedInstructions = analyzedInstructions;
    }

    public List < Ingredient > getExtendedIngredients() {
        return extendedIngredients;
    }

    public void setExtendedIngredients(List < Ingredient > extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

}