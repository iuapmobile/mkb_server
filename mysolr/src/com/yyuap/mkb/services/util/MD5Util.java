package com.yyuap.mkb.services.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import com.yyuap.mkb.log.MKBLogger;


public class MD5Util
{
    
	 public static String encode(String text){  
         
         try {  
             MessageDigest digest = MessageDigest.getInstance("md5");  
             byte[] result = digest.digest(text.getBytes());  
             StringBuilder sb =new StringBuilder();  
             for(byte b:result){  
                 int number = b&0xff;  
                 String hex = Integer.toHexString(number);  
                 if(hex.length() == 1){  
                     sb.append("0"+hex);  
                 }else{  
                     sb.append(hex);  
                 }  
             }  
             return sb.toString();  
         } catch (NoSuchAlgorithmException e) {  
             // TODO Auto-generated catch block  
             MKBLogger.error("Exception:" + e.toString());  
         }  
       
     return "" ;  
 }  
    /**
     * md5 加密
     * 
     * @param text
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String md5(String text)
    {
        MessageDigest msgDigest = null;
        try
        {
            msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update(text.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException("System doesn't support UTF-8.");
        }
        
        byte[] bytes = msgDigest.digest();
        
        byte tb;
        char low;
        char high;
        char tmpChar;
        
        String md5Str = new String();
        
        for (int i = 0; i < bytes.length; i++)
        {
            tb = bytes[i];
            
            tmpChar = (char)((tb >>> 4) & 0x000f);
            
            if (tmpChar >= 10)
            {
                high = (char)(('a' + tmpChar) - 10);
            }
            else
            {
                high = (char)('0' + tmpChar);
            }
            
            md5Str += high;
            tmpChar = (char)(tb & 0x000f);
            
            if (tmpChar >= 10)
            {
                low = (char)(('a' + tmpChar) - 10);
            }
            else
            {
                low = (char)('0' + tmpChar);
            }
            
            md5Str += low;
        }
        return md5Str;
    }
    
    public static String jsmd5(String text)
    {
        MessageDigest msgDigest = null;
        try
        {
            msgDigest = MessageDigest.getInstance("MD5");
            msgDigest.update(text.getBytes("UTF-8"));
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException("System doesn't support UTF-8.");
        }
        byte[] bytes = msgDigest.digest();
        
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes)
        {
            sb.append(Integer.toHexString((b >>> 4) & 15));
            sb.append(Integer.toHexString(b & 15));
        }
        return sb.toString();
    }
    
    /**
     * 对一个文件获取md5值
     * 
     * @return md5串
     */
    public static String getFileMD5(File file)
    {
        MessageDigest MD5 = null;
        try
        {
            MD5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8 * 1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1)
            {
                MD5.update(buffer, 0, length);
                break;
            }
            fileInputStream.close();
            return new String(Hex.encodeHex(MD5.digest()));
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Generate file MD5 failure:" + e.getMessage(), e);
        }
        finally
        {
        	if (fileInputStream != null)
            try
            {
                    fileInputStream.close();
            }
            catch (IOException e)
            {
            }
        }
    }
    
    /**
     * 对一个文件获取md5值
     * 
     * @return md5串
     */
    public static String getPartFileMD5(File file)
    {
        MessageDigest MD5 = null;
        try
        {
            MD5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new IllegalStateException("System doesn't support MD5 algorithm.");
        }
        
        FileInputStream fileInputStream = null;
        try
        {
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8 * 8 * 1024];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1)
            {
                MD5.update(buffer, 0, length);
                break;
            }
            
            return new String(Hex.encodeHex(MD5.digest()));
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Generate file MD5 failure:" + e.getMessage(), e);
        }
        finally
        {
        	if (fileInputStream != null)
            try
            {
                    fileInputStream.close();
            }
            catch (IOException e)
            {
            }
        }
    }
}
