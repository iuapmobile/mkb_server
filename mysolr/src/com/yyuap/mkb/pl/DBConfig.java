/**
 * 
 */
package com.yyuap.mkb.pl;

/**
 * @author gct
 * @created 2017-5-20
 */
public class DBConfig {

    // connect the database
    public String driver = "com.mysql.jdbc.Driver";

    public String dbname = "iuapkb_manu";
    public String username = "root";
    public String password = "1234qwer";// mac

    public String ip = "127.0.0.1";
    public String port = "3306";

    public DBConfig() {
        this.driver = "com.mysql.jdbc.Driver";
    }

    public String getUlr() {
        String url = "jdbc:mysql://" + this.ip + ":" + this.port + "/" + this.dbname
                + "?useUnicode=true&characterEncoding=utf-8";
        return url;
    }

    public String getDbname() {
        return this.dbname;
    }

    public void setDbName(String dname) {
        this.dbname = dname;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String uname) {
        this.username = uname;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String psw) {
        this.password = psw;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

}
