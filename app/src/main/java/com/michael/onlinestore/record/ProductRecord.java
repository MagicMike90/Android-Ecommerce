package com.michael.onlinestore.record;

import com.orm.SugarRecord;

/**
 * Created by michael on 11/02/2016.
 */
public class ProductRecord extends SugarRecord{

    //Adding properties.
    private Long id;
    private String title;
    private String description;
    private String imagePath;
    private double price;

    //@Ignore
    //String name;

    // defining a relationship
    CustomerRecord customer;

    //must provide an empty constructor
    public ProductRecord() {}
}
