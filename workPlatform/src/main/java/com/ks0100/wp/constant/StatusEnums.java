package com.ks0100.wp.constant;

import com.ks0100.common.constant.read_enums.StatusEnumsInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
/**
 * 状态码枚举类，通过代码生成的
 * @author chen haifeng
 *
 */
public class StatusEnums { 
   // 项目周报内容
  public  enum PrjReportType implements StatusEnumsInterface { 
	WORK_LIST("00901","工作列表","null"),
	WORK_PLAN("00903","下周工作计划","null"),
	RISK_QUESTION("00900","风险或问题","null"),
	WORK_SUMMARY("00902","工作总结","null");
	public static final  String TABLE="tbl_project_w_report_content";
	public static final  String COLUMN="type";
	public static final  String CLASS="009";
	public static final  String DESC="项目周报内容";
	private  PrjReportType(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(PrjReportType e :PrjReportType.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(PrjReportType e :PrjReportType.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(PrjReportType e :PrjReportType.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 用户性别
  public  enum UserGender implements StatusEnumsInterface { 
	FEMALE("00802","女","null"),
	UNKNOW("00800","未知","null"),
	MALE("00801","男","null");
	public static final  String TABLE="tbl_user";
	public static final  String COLUMN="gender";
	public static final  String CLASS="008";
	public static final  String DESC="用户性别";
	private  UserGender(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(UserGender e :UserGender.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(UserGender e :UserGender.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(UserGender e :UserGender.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 任务可见
  public  enum TaskVisible implements StatusEnumsInterface { 
	TASK_MEMBER("00301","仅参与者可见","null"),
	PRJ_MEMBER("00300","成员可见","null");
	public static final  String TABLE="tbl_task";
	public static final  String COLUMN="visible_status";
	public static final  String CLASS="003";
	public static final  String DESC="任务可见";
	private  TaskVisible(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(TaskVisible e :TaskVisible.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskVisible e :TaskVisible.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskVisible e :TaskVisible.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 动态场景描述
  public  enum ActionRecord implements StatusEnumsInterface { 
	TASK_CONTENT_UPDATE("00605","更新任务内容","null"),
	TASK_CHECKLIST_DELETE("00613","删除任务检查项","null"),
	TASK_LINE_DELETE("00656","删除了任务列","null"),
	TASK_CHANGE_VISIBLE("00603","转换任务的可见性","null"),
	PRJ_SUPERVISER_DELETE("00647","移除项目成员的监督员角色","null"),
	TASK_DATE_UPDATE("00607","更新任务截止日期","null"),
	FLIE_LIB_UPDATE("00621","更新了文件库里文件或文件夹名称","null"),
	TASK_MOVE_TO_BOARD("00602","移动任务到不同的任务板","null"),
	MEETING_FILE_UPLOAD("00642","上传了会议附件","null"),
	TEAM_DELETE("00636","删除了项目的团队","null"),
	METTING_CREATE("00623","创建了项目的会议","null"),
	TASK_REMARK_UPDATE("00606","更新任务备忘","null"),
	PRJ_URL_OPEN("00637","打开项目的链接邀请成员","null"),
	TASK_MOVE_TO_LINE("00601","移动任务到不同的列","null"),
	TASK_CHILDREN_DELETE("00616","删除了任务的子任务","null"),
	TASK_CHILDREN_CREATE("00615","创建了任务的子任务","null"),
	TASK_BOARD_NAME_UPDATE("00660","更新了任务板名称","null"),
	TASK_PRIORITY_UPDATE("00608","更新任务优先级","null"),
	PRJ_DELETE("00641","删除了项目","null"),
	TASK_BOARD_DELETE("00661","删除了任务板","null"),
	TASK_DELETE("00604","删除任务","null"),
	PERSONAL_REPORT_CREATE("00625","每周第一次填写了个人周报","null"),
	PRJ_ADMIN_CREATE("00644","设置项目成员为管理员","null"),
	FLIE_LIB_MOVE("00622","移动了文件库文件的路径","null"),
	FLIE_LIB_UPLOAD("00619","在文件库里上传了文件","null"),
	TASK_NO_PASS("00653","不通过审核","null"),
	PRJ_SUPERVISER_CREATE("00646","设置项目成员为监督员","null"),
	TASK_EXECUTOR_UPDATE("00609","更新任务执行者","null"),
	TASK_MEMBER_DELETE("00611","移除任务参与者","null"),
	TASK_MEMBER_CREATE("00610","添加任务参与者","null"),
	TASK_LINE_UPDATE("00655","更新了任务列","null"),
	PRJ_NAME_UPDATE("00628","更新了项目的名称","null"),
	TASK_BOARD_REMARK_UPDATE("00659","更新了任务板描述","null"),
	PRJ_REPORT_CREATE("00626","每周第一次填写了项目周报","null"),
	TASK_CHECKLIST_UPDATE("00614","更新任务检查项","null"),
	PRJ_END_DATE_UPDATE("00630","更新了项目的结束时间","null"),
	PRJ_CREATE("00627","创建了项目","null"),
	TASK_BOARD_ADMIN_UPDATE("00658","更新了任务板管理员","null"),
	TASK_ATTACHMENT_UPLOAD("00617","上传了任务的附件","null"),
	PRJ_MEMBER_DELETE("00634","删除了项目的成员","null"),
	PRJ_STATUS_RESTART("00650","重启了项目","null"),
	TASK_LINE_CREATE("00654","创建了任务列","null"),
	METTING_DELETE("00624","删除了项目的会议","null"),
	TASK_PASS("00652","通过审核","null"),
	TASK_CHECKLIST_CREATE("00612","添加任务检查项","null"),
	PRJ_BEGIN_DATE_UPDATE("00629","更新了项目的开始时间","null"),
	PRJ_DETAIL_UPDATE("00631","更新了项目的简介","null"),
	PRJ_URL_JOIN("00639","成员通过项目链接加入项目","null"),
	TASK_CREATE("00600","创建任务","null"),
	PRJ_MEMBER_CREATE("00633","添加了项目的成员","null"),
	PRJ_URL_CLOSE("00638","关闭项目的链接邀请","null"),
	TASK_BOARD_CREATE("00657","创建了任务板","null"),
	PRJ_STATUS_COMPLETE("00649","完成了项目","null"),
	TEAM_CREATE("00635","添加了项目的团队","null"),
	PRJ_QUIT("00640","退出了项目","null"),
	MEETING_FILE_DELETE("00643","删除了会议附件","null"),
	PRJ_ADMIN_DELETE("00645","移除项目成员的管理员角色","null"),
	TASK_ATTACHMENT_DELETE("00618","删除了任务的附件","null"),
	FLIE_LIB_DELETE("00620","在文件库里删除了文件","null"),
	PRJ_LOGO_UPDATE("00632","更新了项目的图标","null"),
	PRJ_STATUS_SUSPEND("00648","暂停了项目","null"),
	TASK_TO_CHECK("00651","提交审核","null");
	public static final  String TABLE="tbl_project_action_record";
	public static final  String COLUMN="useage";
	public static final  String CLASS="006";
	public static final  String DESC="动态场景描述";
	private  ActionRecord(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(ActionRecord e :ActionRecord.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(ActionRecord e :ActionRecord.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(ActionRecord e :ActionRecord.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 任务优先级
  public  enum TaskPriority implements StatusEnumsInterface { 
	LEVEL1("00101","紧急","null"),
	LEVEL0("00100","一般","null");
	public static final  String TABLE="tbl_task";
	public static final  String COLUMN="priority";
	public static final  String CLASS="001";
	public static final  String DESC="任务优先级";
	private  TaskPriority(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(TaskPriority e :TaskPriority.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskPriority e :TaskPriority.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskPriority e :TaskPriority.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 任务状态
  public  enum TaskStatus implements StatusEnumsInterface { 
	TO_CHECK("00002","待审核","null"),
	COMPLETE("00001","完成","null"),
	DONING("00000","进行中","null"),
	NO_PASS("00003","不通过","null");
	public static final  String TABLE="tbl_task";
	public static final  String COLUMN="status";
	public static final  String CLASS="000";
	public static final  String DESC="任务状态";
	private  TaskStatus(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(TaskStatus e :TaskStatus.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskStatus e :TaskStatus.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskStatus e :TaskStatus.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 文件保存类型
  public  enum FileType implements StatusEnumsInterface { 
	METTING("00501","会议附件","null"),
	TASK("00502","任务附件","null"),
	FILE_LIB("00500","文件库文件","null"),
	CHAT("00503","聊天附件","null");
	public static final  String TABLE="tbl_attachment";
	public static final  String COLUMN="file_type";
	public static final  String CLASS="005";
	public static final  String DESC="文件保存类型";
	private  FileType(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(FileType e :FileType.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(FileType e :FileType.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(FileType e :FileType.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 检查项状态
  public  enum TaskCheckListStatus implements StatusEnumsInterface { 
	COMPLETE("00401","完成","null"),
	NO_COMPLETE("00400","未完成","null");
	public static final  String TABLE="tbl_task_check_list";
	public static final  String COLUMN="status";
	public static final  String CLASS="004";
	public static final  String DESC="检查项状态";
	private  TaskCheckListStatus(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(TaskCheckListStatus e :TaskCheckListStatus.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskCheckListStatus e :TaskCheckListStatus.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskCheckListStatus e :TaskCheckListStatus.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 任务难度
  public  enum TaskLevel implements StatusEnumsInterface { 
	LEVEL1("00201","一般","null"),
	LEVEL0("00200","简单","null"),
	LEVEL2("00202","困难","null");
	public static final  String TABLE="tbl_task";
	public static final  String COLUMN="level";
	public static final  String CLASS="002";
	public static final  String DESC="任务难度";
	private  TaskLevel(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(TaskLevel e :TaskLevel.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskLevel e :TaskLevel.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(TaskLevel e :TaskLevel.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

   // 项目状态
  public  enum ProjectSatus implements StatusEnumsInterface { 
	DOING("00700","进行中","null"),
	STOP("00702","暂停","null"),
	DELETE("00703","删除","null"),
	COMPLETE("00701","完成","null");
	public static final  String TABLE="tbl_project";
	public static final  String COLUMN="status";
	public static final  String CLASS="007";
	public static final  String DESC="项目状态";
	private  ProjectSatus(String code,String text,String text1){
		this.code = code;
		this.text = text;
		this.text1 = text1;
	}
	private final  String code; 
	private final  String text; 
	private final  String text1; 
	@Override 
	public   String getText() { 
		return text; 
	}
	@Override 
	public   String getText1() { 
		return text1; 
	}
	public static List<Map<String,String>> loadEnumList(){
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for(ProjectSatus e :ProjectSatus.values()){
			Map<String,String> map=new HashMap<String,String>();
			map.put("code", e.getCode());
			map.put("text", e.getText());
			map.put("text1", e.getText1());
			list.add(map);
		}
		return list;
	}
	public  static String GET_TEXT(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(ProjectSatus e :ProjectSatus.values()){
		    if(code.equals(e.getCode())){
			return e.getText() ;
		    }
		}
		return "";
	} 
	public  static String GET_TEXT1(String code) { 
		 if(StringUtils.isBlank(code)) {
		   return "";
		 }
		for(ProjectSatus e :ProjectSatus.values()){
		    if(code.equals(e.getCode())){
			return e.getText1() ;
		    }
		}
		return "";
	} 
	@Override 
	public  String getCode() {
		return code;
	}
	@Override 
	public  String getTable() {
		return TABLE;
	}
	@Override 
	public  String getColumn() {
		return COLUMN;
	}
	@Override 
	public  String getClassName() {
		return CLASS;
	}
	@Override 
	public  String getDesc() {
		return DESC;
	}
   };

  public static void main(String[] args) {
    //System.out.println(StatusEnums.Sex.COLUMN);//获取状态码所在列名
    //System.out.println(StatusEnums.Sex.TABLE);//获取状态码所在表名
    //System.out.println(StatusEnums.Sex.WOMAN.getText());//获取枚举的文字
    //System.out.println(StatusEnums.Sex.WOMAN.getCode());//获取枚举的编码
    //System.out.println(StatusEnums.Sex.GET_TEXT("00000"));//根据编码获取文字
  
  }
}
