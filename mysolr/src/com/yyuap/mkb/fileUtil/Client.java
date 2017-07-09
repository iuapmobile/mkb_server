/**
 * 
 */
package com.yyuap.mkb.fileUtil;

import java.io.IOException;
import java.sql.SQLException;

import com.yyuap.mkb.pl.DBManager;

/**
 * @author gct
 * @created 2017-5-20
 */
public class Client {

	public static void main(String[] args) throws IOException, SQLException {
		DBManager saveData2DB = new DBManager();
		//saveData2DB.saveKB(Common.EXCEL_PATH, KBINDEXTYPE.KBINDEX);
		System.out.println("end");
	}
}
