package action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import service.CRUDService;
import dao.ProductDao;
import entity.Product;

@Controller
@Scope("prototype")
@RequestMapping("sale/product")
public class ProductController extends CRUDService<Product, Long> {

	@Autowired()
	@Qualifier("productDao")
	public void setCommonDao(ProductDao productDao) {
		super.setCommonDao(productDao);
	}
}
