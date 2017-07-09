package com.yyuap.mkb.fileUtil;

import java.io.File;

public class FileUtil {

    public static void getFiles(String filePath) {
        File root = new File(filePath);
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                /*
                 * 递归调用
                 */
                getFiles(file.getAbsolutePath());
                //filelist.add(file.getAbsolutePath());
                System.out.println("显示" + filePath + "下所有子目录及其文件" + file.getAbsolutePath());
            } else {
                System.out.println("显示" + filePath + "下所有子目录" + file.getAbsolutePath());
            }
        }
    }

}
