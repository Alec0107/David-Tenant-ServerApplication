package SERVER.Handlers;

import SERVER.Models.ProductModels.Product;
import SERVER.Service.DTService;
import org.codehaus.groovy.classgen.ReturnAdder;

import java.util.List;

public class ProductHandler_ {

    public List<Product> fetchProducts() {
        DTService dtService = new DTService();
        return dtService.fetchProducts();

    }

}
