package com.ks0100.common.constant.create_enums;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CreateStatusJson {
	public static String createStatusJson(List<Status> list){
		StringBuilder sb = new StringBuilder("define(function(){ \n");
		  sb.append("\t var common = {\n");
		  sb.append("\t\t initStatus:function(name){\n");
		  sb.append("\t\t\tvar constant = {\n");
		  for(Status s:list) {
			  sb.append("\t\t\t\t\""+s.getId()+"\":{\n");
			  sb.append("\t\t\t\t\t\" content\":\""+s.getContent()+"\",\n");
			  sb.append("\t\t\t\t\t\" EnumVariable\":\""+s.getEnumVariable()+"\",\n");
			  sb.append("\t\t\t\t\t\" enumName\":\""+s.getEnumName()+"\",\n");
			  if(s.getId().equals(list.get(list.size()-1).getId())){
				  sb.append("\t\t\t\t}\n");
			  }else{
				  sb.append("\t\t\t\t},\n");
			  }
			  
		  }
		  sb.append("\t\t\t}\n");
		  sb.append("\t\treturn constant[name];\n");
		  sb.append("\t\t},\n");
		  sb.append("\t};\n");
		  sb.append("\treturn common;\n");
		  sb.append("});");
		  return sb.toString();
	}
	
	public static void createStatusJson(List<Status> list,String path){
		String JSFile = createStatusJson(list);
		FileOutputStream  fs = null;
		try{
			fs = new FileOutputStream(path);
			byte[] b = JSFile.getBytes();
			for(int i=0;i<b.length;i++){
				fs.write(b[i]);
			}
			fs.flush();
			fs.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
