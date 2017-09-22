package com.yyuap.mkb.fileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XMLParseDataConfig {

	public File getXmlFile(String xmlpath) throws Exception{  
	    File xmlFile=new File(xmlpath);//根据指定的路径创建file对象  
	    return xmlFile;
	}
	public Document getDocument(File xmlFile) throws Exception{  
		SAXReader sax=new SAXReader();//创建一个SAXReader对象  
//	    File xmlFile=new File(xmlpath);//根据指定的路径创建file对象  
	    Document document=sax.read(xmlFile);//获取document对象,如果文档无节点，则会抛出Exception提前结束  
	    return document;
	}
	
	/**
	 * 查找指定元素
	 * @param xmlpath
	 * @return
	 * @throws Exception
	 */
	public Element getElement(Document document,String eleMentPath) throws Exception{  
	   
//	    String xpath ="/dataConfig/dataSource[@type='JdbcDataSource']";//查询属性type='JdbcDataSource'的dataSource
//	    List<Element> dataSource = document.selectNodes(xpath);
//	    String xpath ="//dataSource[@type='JdbcDataSource']";
	    Element element = (Element) document.selectSingleNode(eleMentPath);
//	    this.editAttribute(element);//对指定名字的节点进行属性的添加删除修改  
//	    this.saveDocument(document, xmlFile);//把改变的内存中的document真正保存到指定的文件中  
	    return element;
	}
	
    /** 
	* 对指定的节点属性进行删除、添加、修改 
	* @author chenleixing 
	*/  
	public void editAttribute(Element element,String eleAttrName,String eleAttrValue){  
		
		Attribute attrName=element.attribute(eleAttrName);//获取此节点指定的属性,无此节点的会报NullPointerException  
		attrName.setValue(eleAttrValue);//更改此属性值  
	     
//	   Attribute attrName=dataSource.attribute("name");//获取此节点指定的属性,无此节点的会报NullPointerException  
//	   attrName.setValue("name");//更改此属性值  
//	   
//	   Attribute attrUrl=dataSource.attribute("url");//获取此节点指定的属性,无此节点的会报NullPointerException  
//	   attrUrl.setValue("url");//更改此属性值  
//	   
//	   Attribute attrUser=dataSource.attribute("user");//获取此节点指定的属性,无此节点的会报NullPointerException  
//	   attrUser.setValue("user");//更改此属性值  
//	   
//	   Attribute attrPassword=dataSource.attribute("password");//获取此节点指定的属性,无此节点的会报NullPointerException  
//	   attrPassword.setValue("password");//更改此属性值  
	     
	}  
	/** 
	* 把改变的domcument对象保存到指定的xml文件中 
	*/  
	public void saveDocument(Document document,File xmlFile) throws IOException{  
		Writer osWrite=new OutputStreamWriter(new FileOutputStream(xmlFile));//创建输出流  
		OutputFormat format = OutputFormat.createPrettyPrint();  //获取输出的指定格式    
		format.setEncoding("UTF-8");//设置编码 ，确保解析的xml为UTF-8格式  
		XMLWriter writer = new XMLWriter(osWrite,format);//XMLWriter 指定输出文件以及格式    
		writer.write(document);//把document写入xmlFile指定的文件(可以为被解析的文件或者新创建的文件)    
		writer.flush();  
		writer.close();
	}
}
