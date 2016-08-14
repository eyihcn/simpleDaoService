package action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import service.BaseService;
import entity.Product;

@Controller
@RequestMapping("sale/Product")
public class ProductController extends BaseService<Product, Long> {

}
