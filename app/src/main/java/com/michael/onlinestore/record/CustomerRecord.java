package com.michael.onlinestore.record;

import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by michael on 11/02/2016.
 */
public class CustomerRecord extends SugarRecord {
    String firstname;
    String lastname;
    String address;

    public CustomerRecord() {}

    // Get all products of this customer
    List<ProductRecord> getProduct() {
        return ProductRecord.find(ProductRecord.class, "customer = ?", String.valueOf(getId()));
    }
}
