package com.yyuap.mkb.pl;

import java.sql.SQLException;

public class KBSQLException extends SQLException {
    public KBSQLException(String reason) {
        // TODO Auto-generated constructor stub
        super(reason);
    }

    protected int kbExceptionCode = 1000;
    
    public int getKBExceptionCode() {
        return this.kbExceptionCode;
    }
}
