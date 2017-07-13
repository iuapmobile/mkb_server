package com.yyuap.mkb.pl;

public class KBDelSQLException extends KBSQLException {

    public KBDelSQLException(String reason) {
        super(reason);
        super.kbExceptionCode = 1175;
    }

}
