package com.batch.test.processor;

import com.batch.test.domain.Product;
import org.springframework.batch.item.ItemProcessor;


public class FilterDataItemProcessor implements ItemProcessor<Product,Product> {
    @Override
    public Product process(Product product) throws Exception {
        if (product.getProductPrice()>100){
            return product;
        }
        else{
            return null;
        }
    }
}
