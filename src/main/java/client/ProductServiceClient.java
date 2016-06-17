package client;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import entity.Product;
@Component
@Scope("prototype")
@ServiceCode("EYICH")
@ModelName("sale")
public class ProductServiceClient extends ServiceClient<Product, Long>{

}
