package com.czd.core.service.impl;

import com.czd.annotation.MyService;
import com.czd.core.service.TestService;

/**
 * @author: czd
 * @create: 2019-10-22 10:35
 */
@MyService
public class TestServiceImpl implements TestService {

    public String getSomething() {
        return "Congratulation,you success!! ";
    }
}
