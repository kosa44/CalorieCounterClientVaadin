package pl.kosa.caloriecounterClientVaadin.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import pl.kosa.caloriecounterClientVaadin.model.Product;
import pl.kosa.caloriecounterClientVaadin.service.ProductClient;


@Route(value=ProductGui.ROUTE)
public class ProductGui extends VerticalLayout {

    public final static String ROUTE = "productgui";

    private ComboBox<Product> productBox;
    private TextField textFieldGrams;
    private Button buttonAdd;
    private Button buttonRemove;
    private Button buttonClear;
    private Grid<Product> sumGrid;
    private List<Product> chosenList;
    private Button buttonLogout;


    public ProductGui(@Autowired ProductClient productClient) {

        productBox = new ComboBox<>("Products");
        productBox.setItemLabelGenerator(Product::nameFirstLetterToUpperCase);
        productBox.setItems(productClient.getAllProducts());
        textFieldGrams = new TextField("Grams");
        buttonAdd = new Button("Add");
        buttonRemove = new Button("Remove");
        buttonClear = new Button("Clear");
        sumGrid = new Grid<>(Product.class);
        chosenList = new ArrayList<>();
        buttonLogout = new Button("Logout");

        add(productBox);
        add(textFieldGrams);
        add(buttonAdd);
        add(buttonRemove);
        add(buttonClear);
        add(sumGrid);
        add(buttonLogout);
        sumGrid.setItems(chosenList);
        sumGrid.setColumns("name", "protein", "carbohydrates", "fat", "calories");
        sumGrid.getColumnByKey("name").setFooter("Total");

        buttonAdd.addClickListener(clickEvent -> addToGrid(productClient));
        buttonRemove.addClickListener(clickEvent -> removeFromGrid(sumGrid));
        buttonClear.addClickListener(clickEvent -> clearGrid(sumGrid));
//        buttonLogout.addClickListener(clickEvent -> UI.getCurrent().navigate(LoginView.class));

    }

    public void addToGrid(ProductClient productClient) {
        if (!textFieldGrams.getValue().equals("")) {
            Product added = productClient.getProductByName(productBox.getValue().getName());
            added.setMacroValuesForGivenGrams(new BigDecimal(textFieldGrams.getValue().toString()));
            added.setName(added.nameFirstLetterToUpperCase());
            chosenList.add(added);
            sumGrid.getDataProvider().refreshAll();
            updateFooters();
        } else {
            System.out.println("Annotation - number must be bigger than 0");
        }
    }

    public void removeFromGrid(Grid<Product> grid) {
        Product toRemove = (Product) grid.asSingleSelect().getValue();
        chosenList.remove(toRemove);
        grid.getDataProvider().refreshAll();
        updateFooters();
    }

    public void clearGrid(Grid<Product> grid) {
        chosenList.clear();
        grid.getDataProvider().refreshAll();
        updateFooters();
    }

    public BigDecimal getProteinTotal() {
        BigDecimal total = new BigDecimal("0");
        for (Product product : chosenList) {
            total = total.add(product.getProtein());
        }
        return total;
    }

    public BigDecimal getCarbohydratesTotal() {
        BigDecimal total = new BigDecimal("0");
        for (Product product : chosenList) {
            total = total.add(product.getCarbohydrates());
        }
        return total;
    }

    public BigDecimal getFatsTotal() {
        BigDecimal total = new BigDecimal("0");
        for (Product product : chosenList) {
            total = total.add(product.getFat());
        }
        return total;
    }
    public BigDecimal getCaloriesTotal() {
        BigDecimal total = new BigDecimal("0");
        for (Product product : chosenList) {
            total = total.add(product.getCalories());
        }
        return total;
    }

    public void updateFooters() {
        sumGrid.getColumnByKey("protein").setFooter(getProteinTotal().toString());
        sumGrid.getColumnByKey("carbohydrates").setFooter(getCarbohydratesTotal().toString());
        sumGrid.getColumnByKey("fat").setFooter(getFatsTotal().toString());
        sumGrid.getColumnByKey("calories").setFooter(getCaloriesTotal().toString());
    }

}

