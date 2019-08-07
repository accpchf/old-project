package com.ks0100.wp.audit;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ks0100.common.util.PictureUtil;
import com.ks0100.common.util.StringUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.AddProjectUserFrom;
import com.ks0100.wp.constant.StatusEnums.ActionRecord;
import com.ks0100.wp.constant.StatusEnums.ProjectSatus;
import com.ks0100.wp.constant.StatusEnums.TaskStatus;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.entity.Meeting;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.entity.ProjectWeeklyReport;
import com.ks0100.wp.entity.Task;
import com.ks0100.wp.entity.TaskBoard;
import com.ks0100.wp.entity.TaskCheckList;
import com.ks0100.wp.entity.TaskLine;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.entity.UserWeekReport;
import com.ks0100.wp.service.LoginService;
import com.ks0100.wp.service.ProjectActionRecordService;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.TaskService;

@Aspect
@Component
public class AuditServiceAspectJ {
  private Logger log = Logger.getLogger(AuditServiceAspectJ.class);
  
  @Autowired
  private ProjectActionRecordService projectActionRecordService;
  @Autowired
  private TaskService taskService;
  @Autowired
  private LoginService loginService;
  @Autowired
  private ProjectService projectService;

  // @Pointcut("@annotation(com.ks0100.wp.audit.AuditServiceAnnotation)")
  // public void dataLogPointcut() {}

  @Around("@annotation(com.ks0100.wp.audit.AuditServiceAnnotation)")
  public Object doLog(ProceedingJoinPoint joinPoint) throws Throwable {
	Object result = null;
	Signature signature = joinPoint.getSignature();
	MethodSignature methodSignature = (MethodSignature)signature;
	Method m = methodSignature.getMethod();
	AuditServiceAnnotation annotation = m.getAnnotation(AuditServiceAnnotation.class);
	if(annotation != null) {
	  switch(annotation.aspectJ_type()) {
		case AFTER:
		  result = joinPoint.proceed();
		  audit(annotation, joinPoint, result);
		  break;
		default:
		  audit(annotation, joinPoint, result);
		  result = joinPoint.proceed();
		  break;
	  }
	  log.info("data aop end");
	}
	return result;
  }

  private void audit(AuditServiceAnnotation annotation, JoinPoint joinPoint, Object result)
	  throws Exception {
	Object[] oArray = joinPoint.getArgs();
	// 根据annotation定义的参数类型，匹配需要的参数
	Object objArgs1 = null, objArgs2 = null, objArgs3 = null;
	if(annotation.argIndex1() > 0) {
	  objArgs1 = oArray[annotation.argIndex1() - 1];
	} else {
	  for(Object objArgs : oArray) {
		if(objArgs != null && objArgs.getClass().getName().equals(annotation.argType1())) {
		  objArgs1 = objArgs;
		}
	  }
	}
	if(annotation.argIndex2() > 0) {
	  objArgs2 = oArray[annotation.argIndex2() - 1];
	} else {
	  for(Object objArgs : oArray) {
		if(objArgs != null && objArgs.getClass().getName().equals(annotation.argType2())) {
		  objArgs2 = objArgs;
		}
	  }
	}
	if(annotation.argIndex3() > 0) {
	  objArgs3 = oArray[annotation.argIndex3() - 1];
	} else {
	  for(Object objArgs : oArray) {
		if(objArgs != null && objArgs.getClass().getName().equals(annotation.argType3())) {
		  objArgs3 = objArgs;
		}
	  }
	}

	ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	ProjectActionRecord record = new ProjectActionRecord();
	record.setUserId(currentUser.getUserId());
	record.setUserName(currentUser.getName());
	record.setCreatedBy(currentUser.getUserId());
	switch(annotation.useage()) {
	  case u00600:
		log.info("--------------USEAGE_TYPE.u00600--------");
		u00600(record, objArgs1);
		if((Integer)objArgs2 > 0) {
		  u00615((Integer)objArgs2, objArgs1);
		}
		if(objArgs3!=null){
			u00603(objArgs3,objArgs1);
		}
		break;
	  case u00601:
		log.info("--------------USEAGE_TYPE.u00601--------");
		if(((Integer)objArgs1).compareTo((Integer)objArgs2) != 0) {
		  if((Integer)objArgs1 >= 0)
			u00601(record, objArgs3, objArgs2);
		  else u00602(record, objArgs3, objArgs2);
		}
		break;
	  case u00604:
		log.info("--------------USEAGE_TYPE.u00604--------");
		u00604(record, objArgs1);
		break;
	  case u00605:
		log.info("--------------USEAGE_TYPE.u00605--------");
		u00605(record, objArgs1, objArgs2, objArgs3);
		break;
	  case u00609:
		log.info("--------------USEAGE_TYPE.u00609--------");
		u00609(record, result, objArgs1, objArgs2);
		break;
	  case u00610:
		log.info("--------------USEAGE_TYPE.u00610--------");
		u00610(record, objArgs1, objArgs2, objArgs3);
		break;
	  case u00612:
		log.info("--------------USEAGE_TYPE.u00612--------");
		u00612(record, objArgs1, objArgs2);
		break;
	  case u00613:
		log.info("--------------USEAGE_TYPE.u00613--------");
		u00613(record, objArgs1);
		break;
	  case u00614:
		log.info("--------------USEAGE_TYPE.u00614--------");
		u00614(record, objArgs1, objArgs2, objArgs3);
		break;
	  case u00616:
		log.info("--------------USEAGE_TYPE.u00616--------");
		break;
	  case u00617:
		u00617(record, objArgs1, objArgs2);
		break;
	  case u00618:
		u00618(record, objArgs1);
		break;
	  case u00620:
		u00620(objArgs1, objArgs2);
		break;
	  case u00621:
		u00621(record, objArgs1, objArgs2);
		break;
	  case u00622:
		u00622(record, objArgs1, objArgs2);
		break;
	  case u00623:
		u00623(record, objArgs1);
		break;
	  case u00624:
		u00624(record, objArgs1);
		break;
	  case u00625:
		u00625(record, objArgs1, objArgs2);
		break;
	  case u00626:
		u00626(record, objArgs1, objArgs2);
		break;
	  case u00627:
		u00627(record, objArgs1);
		break;
	  case u00628:
		u00628(objArgs1);
		break;
	  case u00633:
		u00633(record, objArgs1, objArgs2, objArgs3);
		break;
	  case u00635:
		u00635(record, objArgs1, objArgs2);
		break;
	  case u00636:
		u00636(record, objArgs1, objArgs2);
		break;
	  case u00641:
		u00641(record, objArgs1);
		break;
	  case u00640:
		u00640(record, objArgs1, objArgs2, objArgs3);
		break;
	  case u00644:
		u00644(record, objArgs1, objArgs2);
		break;
	  case u00646:
		u00646(record, objArgs1, objArgs2);
		break;
	  case u00651:
		u00651(record, objArgs1);
		break;
	  case u00654:
		  u00654(record,objArgs1,objArgs2);
		  break;
	  case u00657:
		  u00657(record, objArgs1, objArgs2);
		  break;
	  case u00661:
		  u00661(record,objArgs1);
		  break;
	  case u00660:
		  u00660(record,objArgs1);
		  break;
	  case u00658:
		  u00658(record,objArgs1,objArgs2);
		  break;
	  case u00656:
		  u00656(record,objArgs1);
		  break;
	  case u00655:
		  u00655(record,objArgs1);
		  break;
	  default:
		break;
	}
  }
  
  private void u00603(Object objArgs3,Object objArgs1) {
	// TODO Auto-generated method stub
	  ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	  boolean visible=(Boolean) objArgs3;
	  Task task=(Task) objArgs1;
	  ProjectActionRecord record = new ProjectActionRecord();
	  record.setUserId(currentUser.getUserId());
	  record.setUserName(currentUser.getName());
	  record.setCreatedBy(currentUser.getUserId());
	  if(visible==true){
		  record.setRecord("将任务可见转化为所有人可见");
	  }else{
		  record.setRecord("将任务可见转化为参与者可见");
	  }
	  record.setTaskId(task.getTaskId());
	  record.setUseage(ActionRecord.TASK_CHANGE_VISIBLE.getCode());
	  record.setProjectId(task.getProjectId());
	  record.setShowByMoudle(1);
	  projectActionRecordService.save(record);
	  
	  
	  
	  
}

private void u00655(ProjectActionRecord record, Object objArgs1) {
	// TODO Auto-generated method stub
	  TaskLine line=(TaskLine) objArgs1;
	  TaskLine oldline=taskService.findTaskLineById(line.getTaskLineId());
	  if(!line.getName().equals(oldline.getName())){
		 record.setRecord("更新了任务列 "+oldline.getName()+"的名称 "+line.getName());
	  }
	  record.setProjectId(oldline.getTaskBoard().getProject().getProjectId());
	  record.setUseage(ActionRecord.TASK_LINE_UPDATE.getCode());
	  record.setShowByMoudle(2);
	  projectActionRecordService.save(record);
	  
}

private void u00656(ProjectActionRecord record, Object objArgs1) {
	// TODO Auto-generated method stub
	  int taskLineId=(Integer) objArgs1;
	  TaskLine taskline=taskService.findTaskLineById(taskLineId);
	  String linename=taskline.getName();
	  System.out.println(linename+"111111111111111111111111111111111111111111111");
	  record.setProjectId(taskline.getTaskBoard().getProject().getProjectId());
	  record.setShowByMoudle(2);
	  record.setUseage(ActionRecord.TASK_LINE_DELETE.getCode());
	  record.setRecord("删除了任务列 "+linename);
	  projectActionRecordService.save(record);
}

private void u00658(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	  int boardId =Integer.parseInt(String.valueOf(objArgs1));
	  int boardAdmin=Integer.parseInt(String.valueOf(objArgs2));
	  TaskBoard oldtaskboard=taskService.findTaskBoardById(boardId);
	  User user=loginService.findUser(boardAdmin);
	  record.setProjectId(oldtaskboard.getProject().getProjectId());
	  record.setUseage(ActionRecord.TASK_BOARD_ADMIN_UPDATE.getCode());
	  record.setShowByMoudle(2);
	  record.setRecord("将任务板 "+oldtaskboard.getName()+"管理员设置为 "+user.getName());
	  projectActionRecordService.save(record);
}

private void u00660(ProjectActionRecord record ,Object objArgs1) {
	  System.out.println("1111111111111111111111111111");
	  TaskBoard board=(TaskBoard) objArgs1;
	  TaskBoard oldtaskboard=taskService.findTaskBoardById(board.getTaskBoardId());
	  		System.out.println(board.getName());
		    System.out.println(oldtaskboard.getName());
		    if(!board.getName().equals(oldtaskboard.getName())){
		    	  System.out.println(oldtaskboard.getProject().getProjectId());
		    	  	ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		    		ProjectActionRecord NameRecord = new ProjectActionRecord();
		    		NameRecord.setUserId(currentUser.getUserId());
		    		NameRecord.setUserName(currentUser.getName());
		    		NameRecord.setUseage(ActionRecord.TASK_BOARD_NAME_UPDATE.getCode());
		    		NameRecord.setRecord("更新了任务板 " +oldtaskboard.getName()+"的名称 "+board.getName() );
		    		NameRecord.setShowByMoudle(2);
		    		NameRecord.setProjectId(oldtaskboard.getProject().getProjectId());
		    		projectActionRecordService.save(NameRecord);
		    }if(!board.getRemark().equals(oldtaskboard.getRemark())){
		    	ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	    		ProjectActionRecord RemarkRecord = new ProjectActionRecord();
	    		RemarkRecord.setUserId(currentUser.getUserId());
	    		RemarkRecord.setUserName(currentUser.getName());
	    		RemarkRecord.setUseage(ActionRecord.TASK_BOARD_REMARK_UPDATE.getCode());
	    		RemarkRecord.setRecord("更新了任务板 "+board.getName()+"的描述 "+board.getRemark());
	    		RemarkRecord.setShowByMoudle(2);
	    		RemarkRecord.setProjectId(oldtaskboard.getProject().getProjectId());
		    	projectActionRecordService.save(RemarkRecord);
		    }
		    	
	  
	  
	
}

private void u00661(ProjectActionRecord record, Object objArgs1) {
	  int boardId=Integer.parseInt(String.valueOf(objArgs1));
	  TaskBoard tboard=taskService.findTaskBoardById(boardId);
	  record.setUseage(ActionRecord.TASK_BOARD_DELETE.getCode());
	  record.setShowByMoudle(2);
	  record.setProjectId(tboard.getProject().getProjectId());
	  record.setRecord("删除了任务板 "+tboard.getName());
	  projectActionRecordService.save(record);
}

private void u00654(ProjectActionRecord record,Object objArgs1,Object objArgs2 ){
		int targetLineId=(Integer) objArgs1;
		String lineName=(String) objArgs2;
		TaskLine taskline=taskService.findTaskLineById(targetLineId);
		record.setProjectId(taskline.getTaskBoard().getProject().getProjectId());
		record.setShowByMoudle(2);
		record.setUseage(ActionRecord.TASK_LINE_CREATE.getCode());
		record.setRecord("创建了任务列 "+lineName);
	   	projectActionRecordService.save(record);
  }
  
  
  
  
  
  private void u00657(ProjectActionRecord record,Object objArgs1,Object objArgs2){
	  TaskBoard board=(TaskBoard) objArgs2;
	  int projectId=Integer.parseInt(String.valueOf(objArgs1));
	  record.setUseage(ActionRecord.TASK_BOARD_CREATE.getCode());
	  record.setProjectId(projectId);
	  record.setShowByMoudle(2);
	  record.setRecord("创建了任务板 " + board.getName());
	  projectActionRecordService.save(record);
  }
  
private void u00600(ProjectActionRecord record, Object objArgs1) {
	Task t = (Task)objArgs1;
	//boolean visible=(Boolean) objArgs2;
	record.setTaskId(t.getTaskId());
	record.setUseage(ActionRecord.TASK_CREATE.getCode());
	record.setProjectId(t.getProjectId());
	record.setShowByMoudle(3);
	if(t.getContent() != null && t.getContent().length() > 150){
		t.setContent(t.getContent().substring(0, 150)+"...");
	}
	record.setRecord("创建了任务 " + t.getContent());
//	// 所有成员可见
//	if(visible=true){
//		record.setUseage(ActionRecord.TASK_CHANGE_VISIBLE.getCode());
//		record.setShowByMoudle(1);
//		record.setProjectId(t.getProjectId());
//		record.setRecord("将任务可见转化为所有人可见");
//	}
//	//仅参与者可见
//	else {
//		record.setUseage(ActionRecord.TASK_CHANGE_VISIBLE.getCode());
//		record.setShowByMoudle(1);
//		record.setProjectId(t.getProjectId());
//		record.setRecord("将任务可见转化为仅参与者可见");
//	}
		projectActionRecordService.save(record);
  }






  private void u00601(ProjectActionRecord record, Object objArgs3, Object objArgs2) {
	Task task = taskService.findTaskById(Integer.parseInt(String.valueOf(objArgs3)));
	if(task != null) {
	  TaskLine targetLine = taskService
		  .findTaskLineById(Integer.parseInt(String.valueOf(objArgs2)));
	  record.setTaskId(task.getTaskId());
	  record.setUseage(ActionRecord.TASK_MOVE_TO_LINE.getCode());
	  record.setProjectId(task.getProjectId());
	  record.setShowByMoudle(1);
	  record.setOldValue(task.getTaskLine().getName());
	  record.setRecord("将任务从 " + task.getTaskLine().getName() + "任务列 移动到了 " + targetLine.getName()
		  + "任务列");
	  projectActionRecordService.save(record);
	}
  }

  private void u00602(ProjectActionRecord record, Object objArgs3, Object objArgs2) {
	Task task = taskService.findTaskById(Integer.parseInt(String.valueOf(objArgs3)));
	if(task != null) {
	  TaskBoard targetBoard = taskService.findTaskBoardById(Integer.parseInt(String
		  .valueOf(objArgs2)));
	  record.setTaskId(task.getTaskId());
	  record.setUseage(ActionRecord.TASK_MOVE_TO_BOARD.getCode());
	  record.setProjectId(task.getProjectId());
	  record.setShowByMoudle(1);
	  String boardName = task.getTaskLine().getTaskBoard().getName();
	  record.setOldValue(boardName);
	  record.setRecord(" 将任务从 " + boardName + " 任务板 移动到了 " + targetBoard.getName() + "任务板");
	  projectActionRecordService.save(record);
	}
  }

  private void u00604(ProjectActionRecord record, Object objArgs1) {
	int taskId = Integer.parseInt(String.valueOf(objArgs1));
	Task t = taskService.listParentAndChildTask(taskId);
	if(t != null && !t.getChildren().isEmpty())
	  return;
	Task task = taskService.findTaskById(taskId);
	if(task != null) {
	  record.setTaskId(task.getTaskId());
	  record.setUseage(ActionRecord.TASK_DELETE.getCode());
	  record.setProjectId(task.getProjectId());
	  record.setShowByMoudle(3);
	  record.setRecord(" 删除了任务 " + task.getContent());
	  projectActionRecordService.save(record);
	  if(t != null && t.getParentTask() != null) {
		u00616(task, t.getParentTask());
	  }
	}
  }

  private void u00605(ProjectActionRecord record, Object objArgs1, Object objArgs2, Object objArgs3) {
	int taskId = Integer.parseInt(String.valueOf(objArgs1));
	Task task = taskService.findTaskById(taskId);
	String useage = "", oldValue = "", recordStr = "", columnName = String.valueOf(objArgs3), content = String.valueOf(objArgs2);
	if(task != null) {
	  record.setTaskId(task.getTaskId());
	  record.setProjectId(task.getProjectId());
	  record.setShowByMoudle(1);
	  if(content != null && content.length() > 150){
		  content = content.substring(0,150)+"...";
	  }
	  if("content".equals(columnName)) {
		useage = ActionRecord.TASK_CONTENT_UPDATE.getCode();
		oldValue = task.getContent();
		recordStr = " 更新了任务的内容 " + content;
	  } else if("remark".equals(columnName)) {
		useage = ActionRecord.TASK_REMARK_UPDATE.getCode();
		oldValue = task.getRemark();
		recordStr = " 更新了任务的备注 " + content;
	  } else if("due_time".equals(columnName)) {
		useage = ActionRecord.TASK_DATE_UPDATE.getCode();
		oldValue = task.getDueTime() != null ? (new DateTime(task.getDueTime()))
			.toString("yyyy-MM-dd") : null;
		recordStr = content == null ? " 清空了任务的截止日期 " : " 更新了任务的截止日期 " + content;
	  } else if("priority".equals(columnName)) {
		useage = ActionRecord.TASK_PRIORITY_UPDATE.getCode();
		oldValue = task.getPriority().equals(BusinessConstant.TaskConstant.TASK_PRIORITY) ? "一般"
			: "紧急";
		recordStr = " 更新了任务的优先级 "
			+ (content.equals(BusinessConstant.TaskConstant.TASK_PRIORITY) ? "一般" : "紧急");
	  }else if("visible_status".equals(columnName)){
		  useage=ActionRecord.TASK_CHANGE_VISIBLE.getCode();
		  oldValue=task.getVisibleStatus().equals(BusinessConstant.TaskConstant.TASK_VISIBLE_STATUS_ALL)?"所有人可见":"仅参与者可见";
		  recordStr=" 更新了任务 "+task.getContent()+" 的可见性动态  "+(content.equals(BusinessConstant.TaskConstant.TASK_VISIBLE_STATUS_ALL)?"所有人可见":"仅参与者可见");
	  }
	  
	  if(oldValue != null && oldValue.length() > 80){
		  oldValue = oldValue.substring(0,80)+"...";
	  }
	  record.setUseage(useage);
	  record.setOldValue(oldValue);
	  record.setRecord(recordStr);
	  projectActionRecordService.save(record);
	}
  }

  private void u00609(ProjectActionRecord record, Object result, Object objArgs1, Object objArgs2) {
	int taskId = Integer.parseInt(String.valueOf(objArgs1));
	int executor = Integer.parseInt(String.valueOf(objArgs2));
	int oldExecutor = Integer.parseInt(String.valueOf(result));
	Task task = taskService.findTaskById(taskId);
	if(task != null) {
	  record.setTaskId(task.getTaskId());
	  record.setUseage(ActionRecord.TASK_EXECUTOR_UPDATE.getCode());
	  record.setProjectId(task.getProjectId());
	  if(oldExecutor > 0) {
		User executorUser = loginService.findUser(oldExecutor);
		record.setOldValue(executorUser.getName());
	  } else {
		record.setOldValue(null);
	  }
	  record.setShowByMoudle(1);
	  if(executor > 0) {
		User executorUser = loginService.findUser(executor);
		record.setRecord(" 更新了任务的执行人 " + executorUser.getName());
	  } else {
		record.setRecord(" 更新了任务的执行人 待认领");
	  }
	  projectActionRecordService.save(record);
	}
  }

  @SuppressWarnings("unchecked")
  private void u00610(ProjectActionRecord record, Object objArgs1, Object objArgs2, Object objArgs3) {
	int taskId = Integer.parseInt(String.valueOf(objArgs1));
	List<Integer> partner = (List<Integer>)objArgs2;
	boolean isSelected = (Boolean)objArgs3;
	if(!partner.isEmpty()) {
	  Task task = taskService.findTaskById(taskId);
	  if(task != null) {
		record.setTaskId(task.getTaskId());
		record.setProjectId(task.getProjectId());
		record.setShowByMoudle(1);
		String userIds = StringUtil.convertCollectionInteger(partner);
		List<User> users = loginService.findUserByIds(userIds);
		StringBuilder sb = new StringBuilder();
		for(User user : users) {
		  sb.append(user.getName()).append(",");
		}
		if(sb.length() > 0)
		  sb.deleteCharAt(sb.length() - 1);
		if(isSelected) {
		  record.setUseage(ActionRecord.TASK_MEMBER_CREATE.getCode());
		  record.setRecord(" 添加了任务的参与者 " + sb);
		} else {
		  record.setUseage(ActionRecord.TASK_MEMBER_DELETE.getCode());
		  record.setRecord(" 移除了任务的参与者 " + sb);
		}
		projectActionRecordService.save(record);
	  }
	}
  }

  private void u00612(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	int taskId = Integer.parseInt(String.valueOf(objArgs1));
	Task task = taskService.findTaskById(taskId);
	if(task != null) {
	  record.setTaskId(task.getTaskId());
	  record.setUseage(ActionRecord.TASK_CHECKLIST_CREATE.getCode());
	  record.setProjectId(task.getProjectId());
	  record.setShowByMoudle(1);
	  record.setRecord(" 添加了任务步骤 " + objArgs2);
	  projectActionRecordService.save(record);
	}
  }

  private void u00613(ProjectActionRecord record, Object objArgs1) {
	int checkListId = Integer.parseInt(String.valueOf(objArgs1));
	TaskCheckList checkList = taskService.findCheckListById(checkListId);
	if(checkList != null) {
	  Task task = checkList.getTask();
	  record.setTaskId(task.getTaskId());
	  record.setUseage(ActionRecord.TASK_CHECKLIST_DELETE.getCode());
	  record.setProjectId(task.getProjectId());
	  record.setShowByMoudle(1);
	  record.setRecord(" 删除了任务步骤 " + checkList.getListContent());
	  projectActionRecordService.save(record);
	}
  }

  private void u00614(ProjectActionRecord record, Object objArgs1, Object objArgs2, Object objArgs3) {
	int checkListId = Integer.parseInt(String.valueOf(objArgs1));
	if("list_content".equals(objArgs3)) {
	  TaskCheckList checkList = taskService.findCheckListById(checkListId);
	  if(checkList != null) {
		Task task = checkList.getTask();
		String oldListContent =checkList.getListContent();
		record.setTaskId(task.getTaskId());
		record.setUseage(ActionRecord.TASK_CHECKLIST_UPDATE.getCode());
		record.setProjectId(task.getProjectId());
		record.setShowByMoudle(1);
		record.setOldValue(oldListContent);
		record.setRecord(" 更新了任务步骤 "+ oldListContent+"的信息 "+ objArgs2);
		projectActionRecordService.save(record);
	  }
	}
  }

  private void u00616(Task task, Task parentTask) {
	ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	ProjectActionRecord record = new ProjectActionRecord();
	record.setUserId(currentUser.getUserId());
	record.setUserName(currentUser.getName());
	record.setCreatedBy(currentUser.getUserId());
	record.setTaskId(parentTask.getTaskId());
	record.setUseage(ActionRecord.TASK_CHILDREN_DELETE.getCode());
	record.setProjectId(task.getProjectId());
	record.setShowByMoudle(1);
	record.setRecord(" 删除了任务的子任务 " + task.getContent());
	projectActionRecordService.save(record);
  }

  private void u00615(int taskId, Object objArgs1) {
	ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	ProjectActionRecord record = new ProjectActionRecord();
	record.setUserId(currentUser.getUserId());
	record.setUserName(currentUser.getName());
	record.setCreatedBy(currentUser.getUserId());
	Task t = (Task)objArgs1;
	record.setTaskId(taskId);
	record.setUseage(ActionRecord.TASK_CHILDREN_CREATE.getCode());
	record.setProjectId(t.getProjectId());
	record.setShowByMoudle(1);
	record.setRecord(" 创建了任务的子任务 " + t.getContent());
	projectActionRecordService.save(record);
  }

  // 文件上传，包括任务的附件，文件库文件，会议附件
  private void u00617(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	String type = (String)objArgs2;
	ArgumentsForFile file = (ArgumentsForFile)objArgs1;
	if(!type.equals(BusinessConstant.FileType.FILESAVETYPE_MEETING)
		&& !type.equals(BusinessConstant.FileType.FILESAVETYPE_TASK)
		&& !type.equals(BusinessConstant.FileType.FILESAVETYPE_FILELIBRARY))
	  return;
	// 会议文件
	if(type.equals(BusinessConstant.FileType.FILESAVETYPE_MEETING)) {
	  record.setUseage(ActionRecord.MEETING_FILE_UPLOAD.getCode());
	  record.setProjectId(file.getProjectId());
	  record.setShowByMoudle(2);
	  record.setRecord("上传了会议的附件 " + file.getFileName() + " " + getFileKB(file.getFilesize()));
	}// 任务的附件
	else if(type.equals(BusinessConstant.FileType.FILESAVETYPE_TASK)) {
	  record.setUseage(ActionRecord.TASK_ATTACHMENT_UPLOAD.getCode());
	  record.setProjectId(file.getProjectId());
	  record.setTaskId(file.getTaskId());
	  record.setShowByMoudle(1);
	  record.setRecord("上传了任务的附件 " + file.getFileName() + " " + getFileKB(file.getFilesize()));
	}// 文件库文件
	else if(type.equals(BusinessConstant.FileType.FILESAVETYPE_FILELIBRARY)) {
	  record.setUseage(ActionRecord.FLIE_LIB_UPLOAD.getCode());
	  record.setProjectId(file.getProjectId());
	  record.setShowByMoudle(2);
	  record.setRecord("上传了文件库的文件 " + file.getFileName() + " " + getFileKB(file.getFilesize()));
	}
	projectActionRecordService.save(record);
  }

  public static final String OPERATE_TYPE_00618 = "operate_type_00618";

  public static final String OPERATE_TYPE_00643 = "operate_type_00643";

  // 文件删除
  private void u00618(ProjectActionRecord record, Object objArgs1) {
	ArgumentsForFile file = (ArgumentsForFile)objArgs1;
	// 任务附件
	if(file.getOperateType().equals(OPERATE_TYPE_00618)) {
	  record.setUseage(ActionRecord.TASK_ATTACHMENT_DELETE.getCode());
	  record.setProjectId(file.getProjectId());
	  record.setShowByMoudle(1);
	  record.setTaskId(file.getTaskId());
	  record.setRecord("删除了任务的附件 " + file.getFileName());
	}// 会议附件
	else if(file.getOperateType().equals(OPERATE_TYPE_00643)) {
	  record.setUseage(ActionRecord.MEETING_FILE_DELETE.getCode());
	  record.setProjectId(file.getProjectId());
	  record.setShowByMoudle(2);
	  record.setRecord("删除了会议的附件 " + file.getFileName());
	}
	projectActionRecordService.save(record);
  }

  private String getFileKB(long byteFile) {
	if(byteFile == 0)
	  return "0KB";
	long kb = 1024;
	return "" + byteFile / kb + "KB";
  }

  @SuppressWarnings("unchecked")
  private void u00620(Object objArgs1, Object objArgs2) {
	Attachment attachment = (Attachment)objArgs1;
	if(objArgs2 != null) {
	  List<Attachment> nodeList = (List<Attachment>)objArgs2;
	  for(Attachment at : nodeList) {
		saveU00620(at);
	  }
	} else {
	  saveU00620(attachment);
	}
  }

  private void saveU00620(Attachment attachment) {
	ProjectActionRecord record = new ProjectActionRecord();
	ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	record.setUserId(currentUser.getUserId());
	record.setUserName(currentUser.getName());
	record.setCreatedBy(currentUser.getUserId());
	record.setUseage(ActionRecord.FLIE_LIB_DELETE.getCode());
	record.setOldValue(attachment.getFileName());
	record.setProjectId(attachment.getRefId());
	record.setShowByMoudle(2);
	String str = "删除了文件 ";
	if(attachment.isFolder()) {
	  str = "删除了文件夹 ";
	}
	record.setRecord(str + attachment.getFileName());
	projectActionRecordService.save(record);
  }

  private void u00621(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	String oldFilename = (String)objArgs2;
	Attachment attachment = (Attachment)objArgs1;
	record.setUseage(ActionRecord.FLIE_LIB_UPDATE.getCode());
	record.setOldValue(oldFilename);
	record.setProjectId(attachment.getRefId());
	record.setShowByMoudle(2);
	String str = "更新了文件名称 ";
	if(attachment.isFolder()) {
	  str = "更新了文件夹名称 ";
	}
	record.setRecord(str + attachment.getFileName());
	projectActionRecordService.save(record);
  }

  private void u00622(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	Attachment attachment = (Attachment)objArgs1;

	Attachment destinationAttachment = new Attachment();
	destinationAttachment.setFileName("根目录");
	if(objArgs2 != null) {
	  destinationAttachment = (Attachment)objArgs2;
	}
	String parentAttachmentName = "根目录";
	if(attachment.getParentAttachment() != null) {
	  parentAttachmentName = attachment.getParentAttachment().getFileName();
	}
	record.setUseage(ActionRecord.FLIE_LIB_MOVE.getCode());
	record.setProjectId(attachment.getRefId());
	record.setShowByMoudle(2);

	record.setOldValue(parentAttachmentName);
	record.setRecord("将文件 " + attachment.getFileName() + " 从 " + parentAttachmentName + " 移动到了 "
		+ destinationAttachment.getFileName());
	projectActionRecordService.save(record);
  }

  private void u00623(ProjectActionRecord record, Object objArgs) {
	Meeting m = (Meeting)objArgs;
	record.setUseage(ActionRecord.METTING_CREATE.getCode());
	record.setProjectId(m.getProject().getProjectId());
	record.setShowByMoudle(2);
	record.setRecord("创建了项目会议 " + m.getTitle());
	projectActionRecordService.save(record);
  }

  private void u00624(ProjectActionRecord record, Object objArgs) {
	Meeting m = (Meeting)objArgs;
	record.setUseage(ActionRecord.METTING_DELETE.getCode());
	record.setProjectId(m.getProject().getProjectId());
	record.setShowByMoudle(2);
	record.setRecord("删除了项目会议 " + m.getTitle());
	projectActionRecordService.save(record);
  }

  private void u00625(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	UserWeekReport uwReport = (UserWeekReport)objArgs1;
	boolean isFilledIn = (Boolean)objArgs2;
	if(!isFilledIn) {
	  String monday = new SimpleDateFormat("MM-dd").format(uwReport.getMonday());
	  String sunday = new SimpleDateFormat("MM-dd").format(uwReport.getSunday());
	  record.setUseage(ActionRecord.PERSONAL_REPORT_CREATE.getCode());
	  record.setProjectId(uwReport.getPro().getProjectId());
	  record.setShowByMoudle(2);
	  record.setRecord("填写了 " + uwReport.getPro().getName() + " 的个人周报[周报周期" + monday + "~" + sunday
		  + "] ");
	  projectActionRecordService.save(record);
	}
  }

  private void u00626(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	if(objArgs1 != null) {
	  ProjectWeeklyReport pwr = (ProjectWeeklyReport)objArgs1;
	  boolean isFilledIn = (Boolean)objArgs2;
	  if(!isFilledIn) {
		String monday = new SimpleDateFormat("MM-dd").format(pwr.getBeginTime());
		String sunday = new SimpleDateFormat("MM-dd").format(pwr.getEndTime());
		record.setProjectId(pwr.getProject().getProjectId());
		record.setShowByMoudle(2);
		record.setUseage(ActionRecord.PRJ_REPORT_CREATE.getCode());
		record.setRecord("填写了 " + pwr.getProject().getName() + " 的项目周报[周报周期" + monday + "~"
			+ sunday + "] ");
		projectActionRecordService.save(record);
	  }
	}
  }

  private void u00627(ProjectActionRecord record, Object objArgs) {
	if(objArgs != null) {
	  Project project = (Project)objArgs;
	  record.setUseage(ActionRecord.PRJ_CREATE.getCode());
	  record.setProjectId(project.getProjectId());
	  record.setShowByMoudle(2);
	  record.setRecord("创建了项目 " + project.getName());
	  projectActionRecordService.save(record);
	}
  }

  private void u00628(Object objArgs1) {
	if(objArgs1 != null) {
	  Project project = (Project)objArgs1;
	  Project oldProject = projectService.findProjectBysql(project.getProjectId());
	  if(oldProject != null) {
		if(!project.getName().equals(oldProject.getName())) {
		  saveU00628(ActionRecord.PRJ_NAME_UPDATE.getCode(), oldProject.getName(),
			  project.getProjectId(), "更新了项目的名称 " + project.getName());
		}
		DateTime beginTime = new DateTime(project.getBeginTime());
		DateTime oldProject_beginTime = new DateTime(oldProject.getBeginTime());

		if(!beginTime.isEqual(oldProject_beginTime)) {
		  saveU00628(ActionRecord.PRJ_BEGIN_DATE_UPDATE.getCode(),
			  oldProject_beginTime.toString("yyyy-MM-dd"), project.getProjectId(), "更新了项目的开始时间 "
				  + beginTime.toString("yyyy-MM-dd"));
		}
		DateTime endTime = new DateTime(project.getEndTime());
		DateTime oldProject_endTime = new DateTime(oldProject.getEndTime());
		if(!endTime.isEqual(oldProject_endTime)) {
		  saveU00628(ActionRecord.PRJ_END_DATE_UPDATE.getCode(),
			  oldProject_endTime.toString("yyyy-MM-dd"), project.getProjectId(), "更新了项目的结束时间 "
				  + endTime.toString("yyyy-MM-dd"));
		}
		if(!project.getDescription().equals(oldProject.getDescription())) {
		  saveU00628(ActionRecord.PRJ_DETAIL_UPDATE.getCode(), null, project.getProjectId(),
			  "更新了项目的简介 ");
		}
		if(!PictureUtil.changeToBASE64EncoderStr(project.getLogo()).equals(oldProject.getLogoStr())) {
		  saveU00628(ActionRecord.PRJ_LOGO_UPDATE.getCode(), null, project.getProjectId(),
			  "更新了项目的图标");
		}
		if(project.isInvitationEnabled() && !oldProject.isInvitationEnabled()) {
		  saveU00628(ActionRecord.PRJ_URL_OPEN.getCode(), "关闭", project.getProjectId(),
			  "打开了项目的邀请成员链接");
		}
		if(!project.isInvitationEnabled() && oldProject.isInvitationEnabled()) {
		  saveU00628(ActionRecord.PRJ_URL_CLOSE.getCode(), "打开", project.getProjectId(),
			  "关闭了项目的邀请成员链接");
		}
		if(!project.getStatus().equals(oldProject.getStatus())) {
		  updateU00628(project, oldProject);
		}
	  }
	}
  }

  private void saveU00628(String usage, String oldValue, int projectId, String recordStr) {
	ProjectActionRecord record = new ProjectActionRecord();
	ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	record.setUserId(currentUser.getUserId());
	record.setUserName(currentUser.getName());
	record.setCreatedBy(currentUser.getUserId());
	record.setUseage(usage);
	record.setOldValue(oldValue);
	record.setProjectId(projectId);
	record.setShowByMoudle(2);
	record.setRecord(recordStr);
	projectActionRecordService.save(record);
  }

  private void updateU00628(Project project, Project oldproject) {
	ProjectActionRecord record = new ProjectActionRecord();
	ShiroUser currentUser = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	record.setUserId(currentUser.getUserId());
	record.setUserName(currentUser.getName());
	record.setCreatedBy(currentUser.getUserId());
	record.setProjectId(project.getProjectId());
	record.setShowByMoudle(2);
	if(project.getStatus().equals(ProjectSatus.DOING.getCode())) {
	  record.setUseage(ActionRecord.PRJ_STATUS_RESTART.getCode());
	  record.setRecord("重启了项目 " + project.getName());
	  record.setOldValue(oldproject.getStatus());
	} else if(project.getStatus().equals(ProjectSatus.DELETE.getCode())) {
	  record.setUseage(ActionRecord.PRJ_DELETE.getCode());
	  record.setRecord("删除了项目 " + project.getName());//
	  record.setOldValue(oldproject.getStatus());
	} else if(project.getStatus().equals(ProjectSatus.COMPLETE.getCode())) {
	  record.setUseage(ActionRecord.PRJ_STATUS_COMPLETE.getCode());
	  record.setRecord("完成了项目 " + project.getName());
	  record.setOldValue(oldproject.getStatus());
	} else if(project.getStatus().equals(ProjectSatus.STOP.getCode())) {
	  record.setUseage(ActionRecord.PRJ_STATUS_SUSPEND.getCode());
	  record.setRecord("暂停了项目 " + project.getName());
	  record.setOldValue(oldproject.getStatus());
	}

	projectActionRecordService.save(record);
  }

  private void u00633(ProjectActionRecord record, Object objArgs1, Object objArgs2, Object objArgs3) {
	int prjId = (Integer)objArgs2;
	User user = (User)objArgs1;
	String addFrom = (String)objArgs3;

	record.setProjectId(prjId);
	record.setShowByMoudle(2);
	if(addFrom.equals(AddProjectUserFrom.FROM_PROJECT_ADMIN)) {
	  record.setUseage(ActionRecord.PRJ_MEMBER_CREATE.getCode());
	  record.setRecord("添加了项目的成员 " + user.getName());
	  projectActionRecordService.save(record);
	} else if(addFrom.equals(AddProjectUserFrom.FROM_URL)) {
	  record.setUseage(ActionRecord.PRJ_URL_JOIN.getCode());
	  record.setRecord(" 通过项目链接加入项目");
	  projectActionRecordService.save(record);
	}

  }

  private void u00635(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	Team team = (Team)objArgs1;
	int projectId = (Integer)objArgs2;
	record.setProjectId(projectId);
	record.setShowByMoudle(2);
	record.setUseage(ActionRecord.TEAM_CREATE.getCode());
	record.setRecord("添加了项目的团队 " + team.getName());
	projectActionRecordService.save(record);
  }

  private void u00636(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	Team team = (Team)objArgs1;
	int projectId = (Integer)objArgs2;
	record.setProjectId(projectId);
	record.setShowByMoudle(2);
	record.setUseage(ActionRecord.TEAM_DELETE.getCode());
	record.setRecord("删除了项目的团队 " + team.getName());
	projectActionRecordService.save(record);
  }

  private void u00641(ProjectActionRecord record, Object objArgs1) {
	Project project = (Project)objArgs1;
	record.setProjectId(project.getProjectId());
	record.setShowByMoudle(2);
	record.setUseage(ActionRecord.PRJ_DELETE.getCode());
	record.setRecord("删除了项目 " + project.getName());
	projectActionRecordService.save(record);
  }

  private void u00640(ProjectActionRecord record, Object objArgs1, Object objArgs2, Object objArgs3) {
	User user = (User)objArgs1;
	Project project = (Project)objArgs2;
	int type = (Integer)objArgs3;
	if(type == 1) {
	  record.setUseage(ActionRecord.PRJ_QUIT.getCode());
	  record.setRecord("退出了项目 " + project.getName());
	} else {
	  record.setUseage(ActionRecord.PRJ_MEMBER_DELETE.getCode());
	  record.setRecord("删除了项目的成员 " + user.getName());
	}
	record.setProjectId(project.getProjectId());
	record.setShowByMoudle(2);

	projectActionRecordService.save(record);
  }

  private void u00644(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	User user = (User)objArgs1;
	int projectId = (Integer)objArgs2;
	record.setProjectId(projectId);
	record.setUseage(ActionRecord.PRJ_ADMIN_CREATE.getCode());
	record.setRecord("将项目成员 " + user.getName() + " 设置为管理员");
	record.setShowByMoudle(2);
	projectActionRecordService.save(record);
  }

  private void u00646(ProjectActionRecord record, Object objArgs1, Object objArgs2) {
	User user = (User)objArgs1;
	int projectId = (Integer)objArgs2;
	record.setProjectId(projectId);
	record.setUseage(ActionRecord.PRJ_SUPERVISER_CREATE.getCode());
	record.setRecord("将项目成员 " + user.getName() + " 设置为监督员");
	record.setShowByMoudle(2);
	projectActionRecordService.save(record);
  }

  private void u00651(ProjectActionRecord record, Object objArgs) {
	if(objArgs != null) {
	  Task task = (Task)objArgs;
	  Task oldTask = taskService.findTaskById(task.getTaskId());
	  record.setShowByMoudle(1);
	  record.setProjectId(oldTask.getProjectId());
	  record.setTaskId(oldTask.getTaskId());
	  if(task.getStatus().equals(TaskStatus.NO_PASS.getCode())) {
		record.setUseage(ActionRecord.TASK_NO_PASS.getCode());
		record.setRecord("不通过审核任务 " + oldTask.getContent());
		record.setOldValue(oldTask.getStatus());
	  } else if(task.getStatus().equals(TaskStatus.TO_CHECK.getCode())) {
		record.setRecord("将任务 " + oldTask.getContent() + "提交审核");
		record.setUseage(ActionRecord.TASK_TO_CHECK.getCode());
		record.setOldValue(oldTask.getStatus());
	  } else if(task.getStatus().equals(TaskStatus.COMPLETE.getCode())) {
		record.setRecord("审核通过了任务 " + oldTask.getContent());
		record.setUseage(ActionRecord.TASK_PASS.getCode());
		record.setOldValue(oldTask.getStatus());
	  }
	  projectActionRecordService.save(record);
	}
  }

}
