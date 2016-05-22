package client;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import entity.Product;
@Component
@Scope("prototype")
@ModelCode(modelName="sale",serviceCode="EYICH")
public class ProductServiceClient extends DaoServiceClient<Product, Long>{

}
