package action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import service.CRUDService;
import dao.BaseMongoDao;
import entity.Product;

@Controller
@RequestMapping("sale/Product")
public class ProductController extends CRUDService<Product, Long> {

	@Autowired()
	@Qualifier("productDao")
	public void setCommonDao(BaseMongoDao<Product, Long> productDao) {
		super.setCommonDao(productDao);
	}
}
