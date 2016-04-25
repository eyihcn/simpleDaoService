package dao;

import org.springframework.stereotype.Repository;

import entity.Product;

@Repository("productDao")
public class ProductDao extends BaseMongoDaoImpl<Product, Long> {

}
