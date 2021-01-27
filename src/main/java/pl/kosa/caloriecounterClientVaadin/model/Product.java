package pl.kosa.caloriecounterClientVaadin.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

@Component
public class Product {

    private String name;
    private BigDecimal protein;
    private BigDecimal carbohydrates;
    private BigDecimal fat;
    private BigDecimal calories;

    public Product() {

    }

    public Product(String name, BigDecimal protein, BigDecimal carbohydrates, BigDecimal fat) {
        this.name = name;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.calories = (protein.multiply(new BigDecimal("4"))).add((carbohydrates.multiply(new BigDecimal("4"))))
                .add(fat.multiply(new BigDecimal("9")));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getProtein() {
        return protein;
    }

    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }

    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }

    public BigDecimal getCalories() {
        return calories;
    }

    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }

    public void setMacroValuesForGivenGrams(BigDecimal gram) {
        BigDecimal newValue = gram.divide(new BigDecimal("100"), 2 , RoundingMode.HALF_EVEN);
        this.setProtein(this.getProtein().multiply(newValue).setScale(2,RoundingMode.HALF_EVEN));
        this.setCarbohydrates(this.getCarbohydrates().multiply(newValue).setScale(2,RoundingMode.HALF_EVEN));
        this.setFat(this.getFat().multiply(newValue).setScale(2,RoundingMode.HALF_EVEN));
        this.setCalories(this.getCalories().multiply(newValue).setScale(2,RoundingMode.HALF_EVEN));
    }

    @Override
    public String toString() {
        return "Product [name=" + name + ", protein=" + protein + ", carbohydrates=" + carbohydrates + ", fat=" + fat
                + ", calories=" + calories + "]";
    }

    public String nameFirstLetterToUpperCase() {
        String first = this.name.substring(0,1).toUpperCase();
        return (first+this.name.substring(1));
    }
}


