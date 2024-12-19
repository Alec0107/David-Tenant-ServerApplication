package OLD;

import SERVER.Models.Product;
import SERVER.Service.DTService;

import java.util.List;

public class ProductHandler_ {

    public List<Product> fetchProducts() {
        DTService dtService = new DTService();
        return dtService.fetchProducts();

    }

}
