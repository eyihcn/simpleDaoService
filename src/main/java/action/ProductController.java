package action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import service.CRUDService;
import entity.Product;

@Controller
@RequestMapping("sale/product")
public class ProductController extends CRUDService<Product, Long> {

}
