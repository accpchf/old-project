package com.ks0100.wp.constant;

public abstract class BusinessConstant {
	
	//手机加解密用的IvParameterSpec,服务端和手机端约定一致
	public static byte[] IV  ="abcdefghijklmnop".getBytes();
	
	public static final String INVITED_URL="/user/invitedToJoinOrg/";
	
	public static final String INVITEDPRO_URL="/user/invitedToJoinPro/";
	public static final String ACTIVE_URL="/user/activate.htm";
	public static final String RESET_URL="/user/toResetPassword.htm";
	/**
	 * 性别编码
	 */
	public static final String SEX_UNKOWN = "00800";
	public static final String SEX_MAN = "00801";
	public static final String SEX_WOMAN = "00802";
	
	public static final String EMAIL_FTL_ACTIVE="active";
	public static final String EAMIL_FTL_RESETPASSWORD = "resetPassword";
	public static final String EAMIL_FTL_ORGRESETPASSWORD = "orgResetPassword";
	public static final String EAMTL_TITTLE_ACTIVE = "workboard激活邮件";
	public static final String EAMTL_TITTLE_RESET = "workboard重置密码邮件";
	
	  /** 成员统计导出文件名--任务 **/
	public static final String XLS_FOR_USER_EXPORT_FILENAME_TASK = "按成员统计--任务.xls";
	  /** 项目统计导出文件名--任务 **/
	public static final String XLS_FOR_PROJECT_EXPORT_FILENAME_TASK = "按项目统计--任务.xls";
	/** 团队统计导出文件名 --任务**/
	public static final String XLS_FOR_TEAM_EXPORT_FILENAME_TASK = "按团队统计--任务.xls";
	 /** 成员统计导出文件名 -- 工时 **/
	public static final String XLS_FOR_USER_EXPORT_FILENAME_TIME = "按成员统计--工时.xls";
	/** 项目统计导出文件名--工时 **/
	public static final String XLS_FOR_PROJECT_EXPORT_FILENAME_TIME = "按项目统计--工时.xls";
	/** 团队统计导出文件名--工时 **/
	public static final String XLS_FOR_TEAM_EXPORT_FILENAME_TIME = "按团队统计--工时.xls";
	/**
	 * 
	 * @param permitConstant
	 * @param modelId
	 * @return
	 */
	public static String findPermitStr(String permitConstant,String modelId){
		return permitConstant+":"+modelId;
	}
	/**
	 * 文件保存类型，00500文件库文件，00501会议文件，00502任务附件，00503聊天文件
	 */
	public static class FileType{
		public static final String FILESAVETYPE_MEETING = "00501";
		public static final String FILESAVETYPE_TASK = "00502";
		public static final String FILESAVETYPE_CHAT = "00503";
		public static final String FILESAVETYPE_FILELIBRARY = "00500";
	}
	
	/**
	 * 任务常量
	 */
	public static class TaskConstant {
		// 任务进行中
		public static final String TASK_DONING = "00000";
		// 任务完成
		public static final String TASK_DONE = "00001";
		// 任务待审核
		public static final String TASK_TO_CHECK = "00002";
		//任务未通过
		public static final String TASK_NO_PASS = "00003";
		// 任务优先级--一般
		public static final String TASK_PRIORITY = "00100";
		// 任务难度--一般
		public static final String TASK_LEVEL = "00201";
		// 所有成员可见
		public static final String TASK_VISIBLE_STATUS_ALL = "00300";
		// 参与者可见
		public static final String TASK_VISIBLE_STATUS_PARTNER = "00301";
		// 检查项状态--未完成
		public static final String TASK_CHECK_LIST_STATUS_NO = "00400";
		// 检查项状态--完成
		public static final String TASK_CHECK_LIST_STATUS_DONE = "00401";
	}
	
	
	public class PermitCodeAndId{
		private String permitCode;
		private int id;
		public String getPermitCode() {
			return permitCode;
		}
		public void setPermitCode(String permitCode) {
			this.permitCode = permitCode;
		}
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		
	}
	
	/**
	 * 	 			 										
	 * @author chen haifeng
	 *
	 */
	public static class RoleConstant {
		// 组织管理员
		public static final String ORG_ADMIN = "ORG_ADMIN";
		// 组织成员
		public static final String ORG_MEMBER = "ORG_MEMBER";
		// 组织监督
		public static final String ORG_SUPERVISER = "ORG_SUPERVISER";
		//项目管理员
		public static final String PRJ_ADMIN = "PRJ_ADMIN";
		//项目成员
		public static final String PRJ_MEMBER = "PRJ_MEMBER";
		//项目监督
		public static final String PRJ_SUPERVISER = "PRJ_SUPERVISER";
		//团队管理员
		public static final String TEAM_ADMIN = "TEAM_ADMIN";
		//团队成员
		public static final String TEAM_MEMBER = "TEAM_MEMBER";
		//任务管理员
		public static final String TASK_ADMIN = "TASK_ADMIN";
		//任务执行者
		public static final String TASK_EXECUTOR = "TASK_EXECUTOR";
		//任务参与者
		public static final String TASK_MEMBER = "TASK_MEMBER";
		//任务板管理员
		public static final String TASK_BOARD_ADMIN = "TASK_BOARD_ADMIN";
		//文件管理员
		public static final String FILE_ADMIN = "FILE_ADMIN";
		//会议管理员
		public static final String MEETING_ADMIN = "MEETING_ADMIN";
		//会议参与者
		public static final String MEETING_MEMBER = "MEETING_MEMBER";
		//任务列管理员
		public static final String TASK_LINE_ADMIN = "TASK_LINE_ADMIN";
		
	}
	
	/**
	 *
	 * @author chen haifeng
	 *
	 */
	public static class PermitConstant {
		
		//组织管理员访问
		public static final String ORG_ADMIN_ACCESS ="ORG:ADMIN_ACCESS";
		//组织成员和监督员访问
		public static final String ORG_ACCESS ="ORG:ACCESS";
		//组织名称菜单
		public static final String ORG_MENU ="ORG:MENU";
		//组织设置菜单
		public static final String ORG_SET_MENU ="ORG:SET:MENU";
		//组织成员菜单
		public static final String ORG_MEMBER_MENU ="ORG:MEMBER:MENU";
		//设置团队菜单
		public static final String ORG_TEAM_MENU ="ORG:TEAM:MENU";
		//查看组织统计菜单
		public static final String ORG_STATISTICS_MENU ="ORG:STATISTICS:MENU";
		//修改组织
		public static final String ORG_SET_UPDATE ="ORG:SET:UPDATE";
		//退出组织
		public static final String ORG_SET_QUIT ="ORG:SET:QUIT";
		//删除组织
		public static final String ORG_SET_DELETE ="ORG:SET:DELETE";
		//设置组织成员
		public static final String ORG_MEMBER_SET ="ORG:MEMBER:SET";
		//新建团队
		public static final String ORG_TEAM_CREATE ="ORG:TEAM:CREATE";
		//设置团队
		public static final String ORG_TEAM_SET ="ORG:TEAM:SET";
		//项目弹窗
		public static final String PRJ_MENU ="PRJ:MENU";
		//项目只读
		public static final String PRJ_READ_ONLY ="PRJ:READ_ONLY";
		//退出项目
		public static final String PRJ_QUIT ="PRJ:QUIT";
		//设置项目
		public static final String PRJ_SET = "PRJ:SET";
		//项目访问权限
		public static final String PRJ_ACCESS = "PRJ:ACCESS";
		//项目高级访问权限
		public static final String PRJ_HIGH_ACCESS = "PRJ:HIGH_ACCESS";
		//项目监督员访问
		public static final String PRJ_SUPERVISER_ACCESS = "PRJ:SUPERVISER_ACCESS";
		//项目管理员访问
		public static final String PRJ_ADMIN_ACCESS = "PRJ:ADMIN_ACCESS";
		//任务板设置
		public static final String TASK_BOARD_SET = "TASK_BOARD:SET";
		//任务板管理员访问
		public static final String TASK_BOARD_ADMIN_ACCESS = "TASK_BOARD:ADMIN_ACCESS";
		//任务移动 不同任务列
		public static final String TASK_MOVE_TO_LINE = "TASK:MOVE_TO_LINE";
		//任务可见性设置
		public static final String TASK_VISIBLE = "TASK:VISIBLE";
		//任务移动 不同任务板
		public static final String TASK_MOVE_TO_BOARD = "TASK:MOVE_TO_BOARD";
		//任务删除
		public static final String TASK_DELETE = "TASK:DELETE";
		//提交审核
		public static final String TASK_COMMIT = "TASK:COMMIT";
		//通过审核
		public static final String TASK_PASS = "TASK:PASS";
		//不通过审核
		public static final String TASK_NOT_PASS = "TASK:NOT_PASS";
		//修改任务截止日期
		public static final String TASK_SET_DATE = "TASK:SET_DATE";
		//修改任务优先级
		public static final String TASK_PRIORITY = "TASK:PRIORITY";
		//更新任务
		public static final String TASK_SET = "TASK:SET";
		//任务在同一列排序
		public static final String TASK_SORT = "TASK:SORT";
		//更新任务
		public static final String TASK_UPDATE_ADMIN = "TASK:UPDATE_ADMIN";
		//修改执行者
		public static final String TASK_UPDATE_EXECUTOR = "TASK:UPDATE_EXECUTOR";
		//添加删除参与者
		public static final String TASK_UPDATE_MEMBER = "TASK:UPDATE_MEMBER";
		//进入聊天
		public static final String TASK_CHAT_VIEW = "TASK_CHAT:VIEW";
		//文件或文件夹设置
		public static final String FILE_SET = "FILE:SET";
		//会议设置权
		public static final String MEETING_SET = "MEETING:SET";
		//编辑会议
		public static final String MEETING_UPDATE = "MEETING:UPDATE";
	}
	
	public static class TableNameConstant{
		//项目表
		public static final String TABLE_PROJECT = "tbl_project";
		//任务表
		public static final String TABLE_TASK = "tbl_task";
		//会议表
		public static final String TABLE_MEETING = "tbl_project_meeting";
	}
	/**
	 * 周报内容
	 * @author zhang pengju
	 *
	 */
	public static class WeekReportContent{
		//工作列表
		public static final String WORK_LIST = "00901";
		//工作总结
		public static final String WORK_SUMMARY = "00902";
		//风险或问题
		public static final String WORK_RISK = "00900";
		//下周工作或计划
		public static final String NEXT_WORK_PLAN = "00903";
	}
	
	public static class AddProjectUserFrom{
		//通过邀请链接加入项目
		public static final String FROM_URL = "from_url";
		//管理员把成员加入项目
		public static final String FROM_PROJECT_ADMIN = "FROM_PROJECT_ADMIN";
	}
}
