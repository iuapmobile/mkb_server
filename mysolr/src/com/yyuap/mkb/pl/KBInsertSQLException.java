package com.yyuap.mkb.pl;

public class KBInsertSQLException  extends KBSQLException{
    public KBInsertSQLException(String reason) {
        super(reason);
        super.kbExceptionCode = 1336;
    }

}
