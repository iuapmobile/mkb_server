package com.yyuap.mkb.cbo;

public class CommonSQL {

    public static final String DRIVER = "com.mysql.jdbc.Driver";
    
    public static final String DB_NAME = "mkb";
    public static final String USERNAME = "root";
    public static final String PASSWORD = "1234qwer";//mac
    public static final String IP = "127.0.0.1";
    public static final String PORT = "3306";
    
    public static final String URL = "jdbc:mysql://" + IP + ":" + PORT + "/" + DB_NAME +"?useUnicode=true&characterEncoding=utf-8";
    
   //租户API调用
    public static final String SELECT_TENANT_BY_TID_SQL = "select * from mkb.u_tenant where apiKey = ?";
    
    //租户使用者登陆使用
    public static final String SELECT_TENANT_BY_TUSERNAME_SQL = "select * from u_tenant where tusername = ? and tpassword = ?";
    
   
    
}