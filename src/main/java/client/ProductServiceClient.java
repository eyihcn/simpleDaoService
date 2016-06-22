package client;

import org.springframework.stereotype.Component;

import entity.Product;
@Component
@ServiceCode("EYICH")
@ModelName("sale")
public class ProductServiceClient extends DaoServiceClient<Product, Long>{

}
