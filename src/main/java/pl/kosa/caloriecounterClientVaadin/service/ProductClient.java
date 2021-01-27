package pl.kosa.caloriecounterClientVaadin.service;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import pl.kosa.caloriecounterClientVaadin.model.Product;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
public class ProductClient {

    private final String baseUrl = "http://localhost:8091/";
    private final KeycloakRestTemplate keycloakRestTemplate;

    @Autowired
    public ProductClient(KeycloakRestTemplate keycloakRestTemplate) {
        this.keycloakRestTemplate = keycloakRestTemplate;
    }

    public List<Product> getAllProducts() {
        URI uri = URI.create(baseUrl + "products");
        return Arrays.asList(keycloakRestTemplate.exchange(uri, HttpMethod.GET, null, Product[].class).getBody());

    }

    public Product getProductByName(String name) {
        URI uri = URI.create(baseUrl + "products/" + name);
        return keycloakRestTemplate.exchange(uri, HttpMethod.GET, null, Product.class).getBody();
    }

    public ResponseEntity<Product> saveProduct(Product product) {
        URI uri = URI.create(baseUrl + "products/");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Product> entity = new HttpEntity<>(product, headers);
        return keycloakRestTemplate.exchange(uri, HttpMethod.POST, entity, Product.class);
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<String> deleteProduct(String name) {
        URI uri = URI.create(baseUrl + "products/" + name);
        System.out.println(uri.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(name, headers);
        return keycloakRestTemplate.exchange(uri, HttpMethod.DELETE, entity, String.class);
    }
}

