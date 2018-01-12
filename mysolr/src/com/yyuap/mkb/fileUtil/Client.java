/**
 * 
 */
package com.yyuap.mkb.fileUtil;

import com.yyuap.mkb.log.MKBLogger;
import com.yyuap.mkb.pl.DBManager;

/**
 * @author gct
 * @created 2017-5-20
 */
public class Client {

	public static void main(String args[]) {
		 try {
				DBManager saveData2DB = new DBManager();
				//saveData2DB.saveKB(Common.EXCEL_PATH, KBINDEXTYPE.KBINDEX);
				
				MKBLogger.info("end");
		  } catch (Exception e) {
			  	MKBLogger.error("Client Exception:" + e.toString());
		  }
		}
}
