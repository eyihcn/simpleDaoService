package service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import entity.TestTable;

@Controller
@RequestMapping("sale/product")
public class ProductController extends CRUDService<TestTable> {

}
