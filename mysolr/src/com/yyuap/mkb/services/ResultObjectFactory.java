package com.yyuap.mkb.services;



public class ResultObjectFactory {

    public ResultObjectFactory() {

    }

    public ResultObject create() {
        ResultObject ro = new ResultObject();
        ro.setStatus(0);
        return ro;
    }

    public ResultObject create(int val) {
        ResultObject ro = new ResultObject();
        ro.setStatus(val);
        return ro;
    }

}
