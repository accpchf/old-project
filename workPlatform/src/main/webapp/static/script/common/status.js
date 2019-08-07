define(function(){ 
	 var common = {
		 initStatus:function(name){
			var constant = {
				"00000":{
					" content":"进行中",
					" EnumVariable":"DONING",
					" enumName":"TaskStatus",
				},
				"00001":{
					" content":"完成",
					" EnumVariable":"COMPLETE",
					" enumName":"TaskStatus",
				},
				"00002":{
					" content":"待审核",
					" EnumVariable":"TO_CHECK",
					" enumName":"TaskStatus",
				},
				"00003":{
					" content":"不通过",
					" EnumVariable":"NO_PASS",
					" enumName":"TaskStatus",
				},
				"00100":{
					" content":"一般",
					" EnumVariable":"LEVEL0",
					" enumName":"TaskPriority",
				},
				"00101":{
					" content":"紧急",
					" EnumVariable":"LEVEL1",
					" enumName":"TaskPriority",
				},
				"00200":{
					" content":"简单",
					" EnumVariable":"LEVEL0",
					" enumName":"TaskLevel",
				},
				"00201":{
					" content":"一般",
					" EnumVariable":"LEVEL1",
					" enumName":"TaskLevel",
				},
				"00202":{
					" content":"困难",
					" EnumVariable":"LEVEL2",
					" enumName":"TaskLevel",
				},
				"00300":{
					" content":"成员可见",
					" EnumVariable":"PRJ_MEMBER",
					" enumName":"TaskVisible",
				},
				"00301":{
					" content":"仅参与者可见",
					" EnumVariable":"TASK_MEMBER",
					" enumName":"TaskVisible",
				},
				"00400":{
					" content":"未完成",
					" EnumVariable":"NO_COMPLETE",
					" enumName":"TaskCheckListStatus",
				},
				"00401":{
					" content":"完成",
					" EnumVariable":"COMPLETE",
					" enumName":"TaskCheckListStatus",
				},
				"00500":{
					" content":"文件库文件",
					" EnumVariable":"FILE_LIB",
					" enumName":"FileType",
				},
				"00501":{
					" content":"会议附件",
					" EnumVariable":"METTING",
					" enumName":"FileType",
				},
				"00502":{
					" content":"任务附件",
					" EnumVariable":"TASK",
					" enumName":"FileType",
				},
				"00503":{
					" content":"聊天附件",
					" EnumVariable":"CHAT",
					" enumName":"FileType",
				},
				"00600":{
					" content":"创建任务",
					" EnumVariable":"TASK_CREATE",
					" enumName":"ActionRecord",
				},
				"00601":{
					" content":"移动任务到不同的列",
					" EnumVariable":"TASK_MOVE_TO_LINE",
					" enumName":"ActionRecord",
				},
				"00602":{
					" content":"移动任务到不同的任务板",
					" EnumVariable":"TASK_MOVE_TO_BOARD",
					" enumName":"ActionRecord",
				},
				"00603":{
					" content":"转换任务的可见性",
					" EnumVariable":"TASK_CHANGE_VISIBLE",
					" enumName":"ActionRecord",
				},
				"00604":{
					" content":"删除任务",
					" EnumVariable":"TASK_DELETE",
					" enumName":"ActionRecord",
				},
				"00605":{
					" content":"更新任务内容",
					" EnumVariable":"TASK_CONTENT_UPDATE",
					" enumName":"ActionRecord",
				},
				"00606":{
					" content":"更新任务备忘",
					" EnumVariable":"TASK_REMARK_UPDATE",
					" enumName":"ActionRecord",
				},
				"00607":{
					" content":"更新任务截止日期",
					" EnumVariable":"TASK_DATE_UPDATE",
					" enumName":"ActionRecord",
				},
				"00608":{
					" content":"更新任务优先级",
					" EnumVariable":"TASK_PRIORITY_UPDATE",
					" enumName":"ActionRecord",
				},
				"00609":{
					" content":"更新任务执行者",
					" EnumVariable":"TASK_EXECUTOR_UPDATE",
					" enumName":"ActionRecord",
				},
				"00610":{
					" content":"添加任务参与者",
					" EnumVariable":"TASK_MEMBER_CREATE",
					" enumName":"ActionRecord",
				},
				"00611":{
					" content":"移除任务参与者",
					" EnumVariable":"TASK_MEMBER_DELETE",
					" enumName":"ActionRecord",
				},
				"00612":{
					" content":"添加任务检查项",
					" EnumVariable":"TASK_CHECKLIST_CREATE",
					" enumName":"ActionRecord",
				},
				"00613":{
					" content":"删除任务检查项",
					" EnumVariable":"TASK_CHECKLIST_DELETE",
					" enumName":"ActionRecord",
				},
				"00614":{
					" content":"更新任务检查项",
					" EnumVariable":"TASK_CHECKLIST_UPDATE",
					" enumName":"ActionRecord",
				},
				"00615":{
					" content":"创建了任务的子任务",
					" EnumVariable":"TASK_CHILDREN_CREATE",
					" enumName":"ActionRecord",
				},
				"00616":{
					" content":"删除了任务的子任务",
					" EnumVariable":"TASK_CHILDREN_DELETE",
					" enumName":"ActionRecord",
				},
				"00617":{
					" content":"上传了任务的附件",
					" EnumVariable":"TASK_ATTACHMENT_UPLOAD",
					" enumName":"ActionRecord",
				},
				"00618":{
					" content":"删除了任务的附件",
					" EnumVariable":"TASK_ATTACHMENT_DELETE",
					" enumName":"ActionRecord",
				},
				"00619":{
					" content":"在文件库里上传了文件",
					" EnumVariable":"FLIE_LIB_UPLOAD",
					" enumName":"ActionRecord",
				},
				"00620":{
					" content":"在文件库里删除了文件",
					" EnumVariable":"FLIE_LIB_DELETE",
					" enumName":"ActionRecord",
				},
				"00621":{
					" content":"更新了文件库里文件或文件夹名称",
					" EnumVariable":"FLIE_LIB_UPDATE",
					" enumName":"ActionRecord",
				},
				"00622":{
					" content":"移动了文件库文件的路径",
					" EnumVariable":"FLIE_LIB_MOVE",
					" enumName":"ActionRecord",
				},
				"00623":{
					" content":"创建了项目的会议",
					" EnumVariable":"METTING_CREATE",
					" enumName":"ActionRecord",
				},
				"00624":{
					" content":"删除了项目的会议",
					" EnumVariable":"METTING_DELETE",
					" enumName":"ActionRecord",
				},
				"00625":{
					" content":"每周第一次填写了个人周报",
					" EnumVariable":"PERSONAL_REPORT_CREATE",
					" enumName":"ActionRecord",
				},
				"00626":{
					" content":"每周第一次填写了项目周报",
					" EnumVariable":"PRJ_REPORT_CREATE",
					" enumName":"ActionRecord",
				},
				"00627":{
					" content":"创建了项目",
					" EnumVariable":"PRJ_CREATE",
					" enumName":"ActionRecord",
				},
				"00628":{
					" content":"更新了项目的名称",
					" EnumVariable":"PRJ_NAME_UPDATE",
					" enumName":"ActionRecord",
				},
				"00629":{
					" content":"更新了项目的开始时间",
					" EnumVariable":"PRJ_BEGIN_DATE_UPDATE",
					" enumName":"ActionRecord",
				},
				"00630":{
					" content":"更新了项目的结束时间",
					" EnumVariable":"PRJ_END_DATE_UPDATE",
					" enumName":"ActionRecord",
				},
				"00631":{
					" content":"更新了项目的简介",
					" EnumVariable":"PRJ_DETAIL_UPDATE",
					" enumName":"ActionRecord",
				},
				"00632":{
					" content":"更新了项目的图标",
					" EnumVariable":"PRJ_LOGO_UPDATE",
					" enumName":"ActionRecord",
				},
				"00633":{
					" content":"添加了项目的成员",
					" EnumVariable":"PRJ_MEMBER_CREATE",
					" enumName":"ActionRecord",
				},
				"00634":{
					" content":"删除了项目的成员",
					" EnumVariable":"PRJ_MEMBER_DELETE",
					" enumName":"ActionRecord",
				},
				"00635":{
					" content":"添加了项目的团队",
					" EnumVariable":"TEAM_CREATE",
					" enumName":"ActionRecord",
				},
				"00636":{
					" content":"删除了项目的团队",
					" EnumVariable":"TEAM_DELETE",
					" enumName":"ActionRecord",
				},
				"00637":{
					" content":"打开项目的链接邀请成员",
					" EnumVariable":"PRJ_URL_OPEN",
					" enumName":"ActionRecord",
				},
				"00638":{
					" content":"关闭项目的链接邀请",
					" EnumVariable":"PRJ_URL_CLOSE",
					" enumName":"ActionRecord",
				},
				"00639":{
					" content":"成员通过项目链接加入项目",
					" EnumVariable":"PRJ_URL_JOIN",
					" enumName":"ActionRecord",
				},
				"00640":{
					" content":"退出了项目",
					" EnumVariable":"PRJ_QUIT",
					" enumName":"ActionRecord",
				},
				"00641":{
					" content":"删除了项目",
					" EnumVariable":"PRJ_DELETE",
					" enumName":"ActionRecord",
				},
				"00642":{
					" content":"上传了会议附件",
					" EnumVariable":"MEETING_FILE_UPLOAD",
					" enumName":"ActionRecord",
				},
				"00643":{
					" content":"删除了会议附件",
					" EnumVariable":"MEETING_FILE_DELETE",
					" enumName":"ActionRecord",
				},
				"00644":{
					" content":"设置项目成员为管理员",
					" EnumVariable":"PRJ_ADMIN_CREATE",
					" enumName":"ActionRecord",
				},
				"00645":{
					" content":"移除项目成员的管理员角色",
					" EnumVariable":"PRJ_ADMIN_DELETE",
					" enumName":"ActionRecord",
				},
				"00646":{
					" content":"设置项目成员为监督员",
					" EnumVariable":"PRJ_SUPERVISER_CREATE",
					" enumName":"ActionRecord",
				},
				"00647":{
					" content":"移除项目成员的监督员角色",
					" EnumVariable":"PRJ_SUPERVISER_DELETE",
					" enumName":"ActionRecord",
				},
				"00648":{
					" content":"暂停了项目",
					" EnumVariable":"PRJ_STATUS_SUSPEND",
					" enumName":"ActionRecord",
				},
				"00649":{
					" content":"完成了项目",
					" EnumVariable":"PRJ_STATUS_COMPLETE",
					" enumName":"ActionRecord",
				},
				"00650":{
					" content":"重启了项目",
					" EnumVariable":"PRJ_STATUS_RESTART",
					" enumName":"ActionRecord",
				},
				"00651":{
					" content":"提交审核",
					" EnumVariable":"TASK_TO_CHECK",
					" enumName":"ActionRecord",
				},
				"00652":{
					" content":"通过审核",
					" EnumVariable":"TASK_PASS",
					" enumName":"ActionRecord",
				},
				"00653":{
					" content":"不通过审核",
					" EnumVariable":"TASK_NO_PASS",
					" enumName":"ActionRecord",
				},
				"00654":{
					" content":"创建了任务列",
					" EnumVariable":"TASK_LINE_CREATE",
					" enumName":"ActionRecord",
				},
				"00655":{
					" content":"更新了任务列",
					" EnumVariable":"TASK_LINE_UPDATE",
					" enumName":"ActionRecord",
				},
				"00656":{
					" content":"删除了任务列",
					" EnumVariable":"TASK_LINE_DELETE",
					" enumName":"ActionRecord",
				},
				"00657":{
					" content":"创建了任务板",
					" EnumVariable":"TASK_BOARD_CREATE",
					" enumName":"ActionRecord",
				},
				"00658":{
					" content":"更新了任务板管理员",
					" EnumVariable":"TASK_BOARD_ADMIN_UPDATE",
					" enumName":"ActionRecord",
				},
				"00659":{
					" content":"更新了任务板描述",
					" EnumVariable":"TASK_BOARD_REMARK_UPDATE",
					" enumName":"ActionRecord",
				},
				"00660":{
					" content":"更新了任务板名称",
					" EnumVariable":"TASK_BOARD_NAME_UPDATE",
					" enumName":"ActionRecord",
				},
				"00661":{
					" content":"删除了任务板",
					" EnumVariable":"TASK_BOARD_DELETE",
					" enumName":"ActionRecord",
				},
				"00700":{
					" content":"进行中",
					" EnumVariable":"DOING",
					" enumName":"ProjectSatus",
				},
				"00701":{
					" content":"完成",
					" EnumVariable":"COMPLETE",
					" enumName":"ProjectSatus",
				},
				"00702":{
					" content":"暂停",
					" EnumVariable":"STOP",
					" enumName":"ProjectSatus",
				},
				"00703":{
					" content":"删除",
					" EnumVariable":"DELETE",
					" enumName":"ProjectSatus",
				},
				"00800":{
					" content":"未知",
					" EnumVariable":"UNKNOW",
					" enumName":"UserGender",
				},
				"00801":{
					" content":"男",
					" EnumVariable":"MALE",
					" enumName":"UserGender",
				},
				"00802":{
					" content":"女",
					" EnumVariable":"FEMALE",
					" enumName":"UserGender",
				},
				"00900":{
					" content":"风险或问题",
					" EnumVariable":"RISK_QUESTION",
					" enumName":"PrjReportType",
				},
				"00901":{
					" content":"工作列表",
					" EnumVariable":"WORK_LIST",
					" enumName":"PrjReportType",
				},
				"00902":{
					" content":"工作总结",
					" EnumVariable":"WORK_SUMMARY",
					" enumName":"PrjReportType",
				},
				"00903":{
					" content":"下周工作计划",
					" EnumVariable":"WORK_PLAN",
					" enumName":"PrjReportType",
				}
			}
		return constant[name];
		},
	};
	return common;
});