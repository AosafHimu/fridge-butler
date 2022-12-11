package comp5216.sydney.edu.fridgebutler.Recipe.Model;

import java.io.Serializable;

/**
 * Ingredient class to retrieve data from Spoonacular
 * Attribute name has to be the same as the data in query
 */
public class Ingredient implements Serializable {

    private String name;
    private String image;
    private String amount;
    private String unit;
    private String id;
    private String original;

    public Ingredient() {}

    public Ingredient(String IngredientName) {
        this.name = IngredientName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    };

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getOriginal() {
        return this.original;
    }

}