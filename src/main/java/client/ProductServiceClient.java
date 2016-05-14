package client;

import org.springframework.stereotype.Component;

import entity.Product;
@Component
@ModelCode(modelName="sale",serviceCode="EYICH")
public class ProductServiceClient extends DaoServiceClient<Product, Long>{

}
