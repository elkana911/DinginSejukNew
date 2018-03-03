package com.elkana.customer.screen.register;


import com.elkana.dslibrary.pojo.user.UserAddress;

public interface ListenerAddressList {
    void onSelectAddress(UserAddress address);

    void onAddAddress();

}
