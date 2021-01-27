package pl.kosa.caloriecounterClientVaadin.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.textfield.NumberField;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.kosa.caloriecounterClientVaadin.model.Product;
import pl.kosa.caloriecounterClientVaadin.service.ProductClient;


@Route(value = ProductGuiAdmin.ROUTE)
public class ProductGuiAdmin extends VerticalLayout {

    public final static String ROUTE = "productguiadmin";

    private MenuBar menuBar;
    private ComboBox<Product> productBox;
    private TextField textFieldGrams;
    private Button buttonAdd;
    private Button buttonRemove;
    private Button buttonClear;
    private Grid<Product> sumGrid;
    private List<Product> chosenList;


    public ProductGuiAdmin(@Autowired ProductClient productClient) {

        createMenuBar(productClient);

        productBox = new ComboBox<>("Products");
        productBox.setItemLabelGenerator(Product::nameFirstLetterToUpperCase);
        productBox.setItems(productClient.getAllProducts());
        textFieldGrams = new TextField("Grams");
        buttonAdd = new Button("Add");
        buttonRemove = new Button("Remove");
        buttonClear = new Button("Clear");
        sumGrid = new Grid<>(Product.class);
        chosenList = new ArrayList<>();

        add(menuBar);
        add(productBox);
        add(textFieldGrams);
        add(buttonAdd);
        add(buttonRemove);
        add(buttonClear);
        add(sumGrid);
        sumGrid.setItems(chosenList);
        sumGrid.setColumns("name", "protein", "carbohydrates", "fat", "calories");
        sumGrid.getColumnByKey("name").setFooter("Total");

        buttonAdd.addClickListener(clickEvent -> addProductsToGrid(productClient));
        buttonRemove.addClickListener(clickEvent -> removeFromGrid(sumGrid));
        buttonClear.addClickListener(clickEvent -> clearGrid(sumGrid));

    }

    private void createMenuBar(ProductClient productClient) {
        menuBar = new MenuBar();
        menuBar.setOpenOnHover(true);

        Text selected = new Text("");
        Div message = new Div(new Text("Selected: "), selected);

        MenuItem welcome = menuBar.addItem("Hello "+getLoggedInUserName()+"!");
        MenuItem product = menuBar.addItem("Products");
        Anchor logout = new Anchor(
                "http://localhost:8090/auth/realms/caloriecounter/protocol/openid-connect/logout?redirect_uri="+"http://localhost:8092/",
                "Logout");
        menuBar.addItem(logout);

        SubMenu productSubMenu = product.getSubMenu();
        MenuItem add = productSubMenu.addItem("Add product");
        MenuItem delete = productSubMenu.addItem("Delete Product");

        Dialog addProductDialog = new Dialog();
        TextField name = new TextField("Name");
        NumberField protein = new NumberField("Protein");
        NumberField fat = new NumberField("Fat");
        NumberField carbohydrates = new NumberField("Carbohydrates");
        Button addNewProductButton = new Button("Add");
        addNewProductButton.addClickListener(e -> productClient.saveProduct(new Product(
                name.getValue(), new BigDecimal(protein.getValue().toString()), new BigDecimal(fat.getValue().toString()), new BigDecimal(carbohydrates.getValue().toString()
        ))));
        addProductDialog.add(name);
        addProductDialog.add(protein);
        addProductDialog.add(fat);
        addProductDialog.add(carbohydrates);
        addProductDialog.add(addNewProductButton);

        add.addClickListener(e -> addProductDialog.open());

        ComboBox<Product> deleteBox = new ComboBox<>("Products");
        deleteBox.setItemLabelGenerator(Product::nameFirstLetterToUpperCase);
        deleteBox.setItems(productClient.getAllProducts());
        deleteBox.setLabel("Choose product to delete");
        SubMenu deleteSubMenu = delete.getSubMenu();
        MenuItem deleteList = deleteSubMenu.addItem(deleteBox);
        Button deleteProduct = new Button("Delete product");
        deleteProduct.addClickListener(e -> productClient.deleteProduct(deleteBox.getValue().getName()));
        deleteList.addComponentAsFirst(deleteProduct);


    }

    private void addProductsToGrid(ProductClient productClient) {
        if (!textFieldGrams.getValue().equals("")) {
            Product added = productClient.getProductByName(productBox.getValue().getName());
            added.setMacroValuesForGivenGrams(new BigDecimal(textFieldGrams.getValue()));
            added.setName(added.nameFirstLetterToUpperCase());
            chosenList.add(added);
            sumGrid.getDataProvider().refreshAll();
            updateFooters();
        } else {
            //todo
            System.out.println("Annotation - number must be bigger than 0");
        }
    }

    private void removeFromGrid(Grid<Product> grid) {
        Product toRemove = (Product) grid.asSingleSelect().getValue();
        chosenList.remove(toRemove);
        grid.getDataProvider().refreshAll();
        updateFooters();
    }

    private void clearGrid(Grid<Product> grid) {
        chosenList.clear();
        grid.getDataProvider().refreshAll();
        updateFooters();
    }

    private BigDecimal getProteinTotal() {
        BigDecimal total = new BigDecimal("0");
        for (Product product : chosenList) {
            total = total.add(product.getProtein());
        }
        return total;
    }

    private BigDecimal getCarbohydratesTotal() {
        BigDecimal total = new BigDecimal("0");
        for (Product product : chosenList) {
            total = total.add(product.getCarbohydrates());
        }
        return total;
    }

    private BigDecimal getFatsTotal() {
        BigDecimal total = new BigDecimal("0");
        for (Product product : chosenList) {
            total = total.add(product.getFat());
        }
        return total;
    }

    private BigDecimal getCaloriesTotal() {
        BigDecimal total = new BigDecimal("0");
        for (Product product : chosenList) {
            total = total.add(product.getCalories());
        }
        return total;
    }

    private void updateFooters() {
        sumGrid.getColumnByKey("protein").setFooter(getProteinTotal().toString());
        sumGrid.getColumnByKey("carbohydrates").setFooter(getCarbohydratesTotal().toString());
        sumGrid.getColumnByKey("fat").setFooter(getFatsTotal().toString());
        sumGrid.getColumnByKey("calories").setFooter(getCaloriesTotal().toString());
    }

    private String getLoggedInUserName(){
        KeycloakPrincipal principal =
                (KeycloakPrincipal) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();

        KeycloakSecurityContext keycloakSecurityContext =
                principal.getKeycloakSecurityContext();

        return keycloakSecurityContext.getIdToken().getPreferredUsername();
    }
}

