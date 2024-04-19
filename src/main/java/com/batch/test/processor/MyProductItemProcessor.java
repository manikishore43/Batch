package com.batch.test.processor;

import com.batch.test.domain.Product;
import org.springframework.batch.item.ItemProcessor;


public class MyProductItemProcessor implements ItemProcessor<Product,Product> {
    @Override
    public Product process(Product pr) throws Exception {
        System.out.println("Processor is executed"+pr);
        return pr;
    }
}
