package action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import service.CRUDService;
import dao.BaseMongoDaoImpl;
import entity.Product;

@Controller
@RequestMapping("sale/product")
public class ProductController extends CRUDService<Product, Long> {

	@Override
	@Autowired()
	@Qualifier("commonDao")
	public void setCommonDao(BaseMongoDaoImpl<Product, Long> commonDao) {
		super.setCommonDao(commonDao);
	}
}
