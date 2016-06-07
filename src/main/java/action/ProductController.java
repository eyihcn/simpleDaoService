package action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import service.BaseService;
import dao.ProductDao;
import entity.Product;

@Controller
@RequestMapping("sale/Product")
public class ProductController extends BaseService<Product, Long> {

	@Autowired()
	@Qualifier("productDao")
	public void setCommonDaoInter(ProductDao productDao) {
		super.setCommonDaoInter(commonDaoInter);
	}
	
}
