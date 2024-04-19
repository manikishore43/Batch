package com.batch.test.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.Iterator;
import java.util.List;

public class ProductNameReader implements ItemReader<String> {
    private Iterator<String> productListIterator;

    public ProductNameReader(List<String> productList){
        this.productListIterator= productList.iterator();
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return this.productListIterator.hasNext()?this.productListIterator.next():null;
    }
}
