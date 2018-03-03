package com.elkana.customer.exception;

/**
 * Created by Eric on 07-Nov-17.
 */

public class OrderAlreadyExists extends RuntimeException {
    public OrderAlreadyExists(String alamat, String mitra, String date) {
        super("Order constraint for Address:" + alamat + ",Mitra:" + mitra + ",DateOfService:" + date);
    }
}
