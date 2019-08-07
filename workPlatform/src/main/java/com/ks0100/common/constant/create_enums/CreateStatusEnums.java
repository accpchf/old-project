package com.ks0100.common.constant.create_enums;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 把数据库里状态码转出来到txt文件里，
 * 按照"status_id, status_class, status_content, status_desc, status_table, status_column, status_enum_name, status_enum_variable"这种格式
 * 如 "00000,000,女,性别,tbl_user,sex,Sex,WOMAN"。
 * 把文件放到根目录下，可以打印出枚举类。
 * @author chen haifeng
 *
 */
public class CreateStatusEnums {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateStatusEnums.class);
  public static final String STATUSCLASS_INTERFACE_NAME = "StatusEnumsInterface";
  public static final String STATUSCLASS_INTERFACE_IMPORT = "com.ks0100.common.constant.read_enums."+STATUSCLASS_INTERFACE_NAME;
  public static final String STATUSCLASS_CLASS_NAME = "StatusEnums";

  public static void main(String[] args) {
   System.out.println(toSqlFromText("contant.txt", "utf-8", "com.ks0100.wp.constant"));
  }

  private static Map<String, Map<String, Status>> createMap(List<Status> list){
	  Map<String, Map<String, Status>> enumNames = new HashMap<String, Map<String, Status>>();
	  for(Status status:list){
		  String key = status.getEnumName() + "/" + status.getStable() + "/" + status.getScolumn() + "/"
				+ status.getSdesc() + "/" + status.getStatusClass();
			if(!enumNames.containsKey(key)) {
			  Map<String, Status> enumVariables = new HashMap<String, Status>();
			  enumVariables.put(status.getEnumVariable(), status);
			  enumNames.put(key, enumVariables);
			} else {
			  Map<String, Status> enumVariables = enumNames.get(key);
			  enumVariables.put(status.getEnumVariable(), status);
			  enumNames.put(key, enumVariables);
			}
	  }
	  return enumNames;
  }
  private static Map<String, Map<String, Status>> createMap(String fileName, String encode) {
	Map<String, Map<String, Status>> enumNames = new HashMap<String, Map<String, Status>>();
	try {
	  InputStreamReader input = new InputStreamReader(new FileInputStream(
		  new File(fileName).getAbsoluteFile()), encode);
	  BufferedReader in = new BufferedReader(input);
	  try {
		String s;
		String[] temp;
		Status status;
		String key = "";
		while((s = in.readLine()) != null) {
		  if(!"".equals(s)) {
			temp = s.split(",");//每一行字段用，分隔
			status = new Status(temp);
			key = status.getEnumName() + "/" + status.getStable() + "/" + status.getScolumn() + "/"
				+ status.getSdesc() + "/" + status.getStatusClass();
			if(!enumNames.containsKey(key)) {
			  Map<String, Status> enumVariables = new HashMap<String, Status>();
			  enumVariables.put(status.getEnumVariable(), status);
			  enumNames.put(key, enumVariables);
			} else {
			  Map<String, Status> enumVariables = enumNames.get(key);
			  enumVariables.put(status.getEnumVariable(), status);
			  enumNames.put(key, enumVariables);
			}
		  }
		}
	  } finally {
		in.close();
	  }
	} catch(IOException e) {
	  //throw new RuntimeException(e);
		LOGGER.error("",e);
	}

	return enumNames;
  }
  
  public static String toSqlFromText(List<Status> list,String packages) {
	  Map<String, Map<String, Status>> enums = createMap(list);
	  return toSqlFromText(enums,packages);
  }
  
  public static String toSqlFromText(String fileName, String encode, String packages) {
	  Map<String, Map<String, Status>> enums = createMap(fileName, encode);
	  return toSqlFromText(enums,packages);
  }
  
  public static void createClass(String content,String path){
	  FileOutputStream fs = null;
	  try{
		  fs = new FileOutputStream(path);
		  byte[] b = content.getBytes();
		  for(int i=0;i<b.length;i++){
			  fs.write(b[i]);
		  }
		  fs.flush();
		  fs.close();
	  }catch(IOException e){
		  e.printStackTrace();
	  }
  }
  
  private static String toSqlFromText(Map<String, Map<String, Status>> enums, String packages) {

	//Map<String, Map<String, Status>> enums = createMap(fileName, encode);

	StringBuilder sb = new StringBuilder("package " + packages + ";\n\n");

	sb.append("import " + STATUSCLASS_INTERFACE_IMPORT + ";\n");
	sb.append("import java.util.ArrayList;\n");
	sb.append("import java.util.List;\n");
	sb.append("import java.util.HashMap;\n");
	sb.append("import java.util.Map;\n");
	sb.append("import org.apache.commons.lang3.StringUtils;\n");
	sb.append("/**\n");
	sb.append(" * 状态码枚举类，通过代码生成的\n");
	sb.append(" * @author chen haifeng\n");
	sb.append(" *\n");
	sb.append(" */\n");
	sb.append("public class " + STATUSCLASS_CLASS_NAME + " { \n");
	String[] key;
	String enumName = "", table = "", column = "", desc = "", statusClass = "";
	String temp;
	for(Map.Entry<String, Map<String, Status>> enum1 : enums.entrySet()) {
	  // System.out.println(enum1.getKey());
	  temp = enum1.getKey();
	  key = temp.split("/");
	  enumName = key[0];
	  table = key[1];
	  column = key[2];
	  desc = key[3];
	  statusClass = key[4];
	  sb.append("   // " + desc + "\n");
	  sb.append("  public  enum " + enumName + " implements " + STATUSCLASS_INTERFACE_NAME
		  + " { \n");
	  Map<String, Status> enumVariables = enum1.getValue();

	  int i = 0;
	  for(Map.Entry<String, Status> enumVariable : enumVariables.entrySet()) {
		Status status = enumVariable.getValue();
		sb.append("\t" + enumVariable.getKey() + "(\"" + status.getId() + "\"," + "\""
			+ status.getContent() + "\",\"" + status.getContent1() + "\")");
		i++;
		if(i < enumVariables.size()) {
		  sb.append(",");
		} else {
		  sb.append(";");
		}
		sb.append("\n");
	  }

	  sb.append("\t" + "public static final  String TABLE=\"" + table + "\";\n");
	  sb.append("\t" + "public static final  String COLUMN=\"" + column + "\";\n");
	  sb.append("\t" + "public static final  String CLASS=\"" + statusClass + "\";\n");
	  sb.append("\t" + "public static final  String DESC=\"" + desc + "\";\n");

	  sb.append("\t" + "private  " + enumName + "(String code,String text,String text1){\n");
	  sb.append("\t\t" + "this.code = code;\n");
	  sb.append("\t\t" + "this.text = text;\n");
	  sb.append("\t\t" + "this.text1 = text1;\n");
	  sb.append("\t" + "}\n");

	  sb.append("\t" + "private final  String code; \n");
	  sb.append("\t" + "private final  String text; \n");
	  sb.append("\t" + "private final  String text1; \n");

	  sb.append("\t" + "@Override \n");
	  sb.append("\t" + "public   String getText() { \n");
	  sb.append("\t\t" + "return text; \n");
	  sb.append("\t" + "}\n");
	  sb.append("\t" + "@Override \n");
	  sb.append("\t" + "public   String getText1() { \n");
	  sb.append("\t\t" + "return text1; \n");
	  sb.append("\t" + "}\n");

	  sb.append("\t" + "public static List<Map<String,String>> loadEnumList(){\n");
	  sb.append("\t\t" + "List<Map<String,String>> list=new ArrayList<Map<String,String>>();\n");
	  sb.append("\t\t" + "for(" + enumName + " e :" + enumName + ".values()){\n");
	  sb.append("\t\t\t" + "Map<String,String> map=new HashMap<String,String>();\n");
	  sb.append("\t\t\t" + "map.put(\"code\", e.getCode());\n");
	  sb.append("\t\t\t" + "map.put(\"text\", e.getText());\n");
	  sb.append("\t\t\t" + "map.put(\"text1\", e.getText1());\n");
	  sb.append("\t\t\t" + "list.add(map);\n");
	  sb.append("\t\t" + "}\n");
	  sb.append("\t\t" + "return list;\n");
	  sb.append("\t}\n");

	  sb.append("\t" + "public  static String GET_TEXT(String code) { \n");
	  sb.append("\t\t" + " if(StringUtils.isBlank(code)) {\n");
	  sb.append("\t\t   return \"\";\n");
	  sb.append("\t\t }\n");
	  sb.append("\t\t" + "for(" + enumName + " e :" + enumName + ".values()){\n");
	  sb.append("\t\t    " + "if(code.equals(e.getCode())){\n");
	  sb.append("\t\t\t" + "return e.getText() ;\n");
	  sb.append("\t\t    " + "}\n");
	  sb.append("\t\t" + "}\n");
	  sb.append("\t\t" + "return \"\";\n");
	  sb.append("\t" + "} \n");

	  sb.append("\t" + "public  static String GET_TEXT1(String code) { \n");
	  sb.append("\t\t" + " if(StringUtils.isBlank(code)) {\n");
	  sb.append("\t\t   return \"\";\n");
	  sb.append("\t\t }\n");
	  sb.append("\t\t" + "for(" + enumName + " e :" + enumName + ".values()){\n");
	  sb.append("\t\t    " + "if(code.equals(e.getCode())){\n");
	  sb.append("\t\t\t" + "return e.getText1() ;\n");
	  sb.append("\t\t    " + "}\n");
	  sb.append("\t\t" + "}\n");
	  sb.append("\t\t" + "return \"\";\n");
	  sb.append("\t" + "} \n");

	  sb.append("\t" + "@Override \n");
	  sb.append("\t" + "public  String getCode() {\n");
	  sb.append("\t\t" + "return code;\n");
	  sb.append("\t" + "}\n");
	  sb.append("\t" + "@Override \n");
	  sb.append("\t" + "public  String getTable() {\n");
	  sb.append("\t\t" + "return TABLE;\n");
	  sb.append("\t" + "}\n");
	  sb.append("\t" + "@Override \n");
	  sb.append("\t" + "public  String getColumn() {\n");
	  sb.append("\t\t" + "return COLUMN;\n");
	  sb.append("\t" + "}\n");
	  sb.append("\t" + "@Override \n");
	  sb.append("\t" + "public  String getClassName() {\n");
	  sb.append("\t\t" + "return CLASS;\n");
	  sb.append("\t" + "}\n");
	  sb.append("\t" + "@Override \n");
	  sb.append("\t" + "public  String getDesc() {\n");
	  sb.append("\t\t" + "return DESC;\n");
	  sb.append("\t" + "}\n");

	  sb.append("   };\n\n");
	}

	sb.append("  public static void main(String[] args) {\n");
	sb.append("    //System.out.println(" + STATUSCLASS_CLASS_NAME + ".Sex.COLUMN);//获取状态码所在列名\n");
	sb.append("    //System.out.println(" + STATUSCLASS_CLASS_NAME + ".Sex.TABLE);//获取状态码所在表名\n");
	sb.append("    //System.out.println(" + STATUSCLASS_CLASS_NAME
		+ ".Sex.WOMAN.getText());//获取枚举的文字\n");
	sb.append("    //System.out.println(" + STATUSCLASS_CLASS_NAME
		+ ".Sex.WOMAN.getCode());//获取枚举的编码\n");
	sb.append("    //System.out.println(" + STATUSCLASS_CLASS_NAME
		+ ".Sex.GET_TEXT(\"00000\"));//根据编码获取文字\n");
	sb.append("  \n");
	sb.append("  }\n");
	sb.append("}\n");
	return sb.toString();
  }

}
