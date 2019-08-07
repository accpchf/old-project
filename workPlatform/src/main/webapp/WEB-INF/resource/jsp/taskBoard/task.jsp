<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<!--右边浮出层 -->
<div id='updateTaskRightLayer' class="layer-right  boardBackground clear-radius">
  <div class="panel-content  vertical-scroll" style="bottom:0px;">
    <div id="taskTitleBar" class="layer-top task-infor">
      <h5>
	     <span><i id='moveOtherTaskgroup' class="icon-th-large" title="任务板"></i></span>
	     <span><i  id='moveOtherStage' class="icon-th-list" title="任务列"></i></span>
	     <span id="waitAudit" class="check-box"><i class="icon-ok" style='display:none'></i></span><span class="waitAudit">提交审核</span>
	     <span id="pass" class="check-box"><i class="icon-ok" style='display:none'></i></span><span class="pass">通过</span>
	     <span id="notPass" class="check-box"><i class="icon-ok" style='display:none'></i></span><span class="nopass">不通过</span>
      </h5>
      <a id="taskMenu">更多 <i class=" icon-down-open-big"></i></a>
    </div>
    <div class="task-infor">
      <div id="updateTaskContent" class="block task-content">
        <!-- <span class="check-box pull-left"><i class="icon-ok" style='display:none'></i></span> -->
        <textarea class="form-control"></textarea>
      </div>
      <div id="updateRemark" class="add-message">
        <a><i class="icon-plus-circle"></i>添加备注</a>
        <span style="display:none;">任务备注</span>
        <p title="点击编辑"></p>
      </div>
    </div>
    <!-- <div class="task-infor task-detail little-portrait">
      <ul>
      	<li id="updateCreator">
          <label>创建者</label><br />
          <span><a><img src="${context }/static/images/pic-shelters.png" class="img-circle"></a>
          </span>
        </li>
        <li id="updateCreateDate">
          <label>创建日期</label><br />
          <a><i class="icon-calendar"></i>
            <input readonly class="taskBoardDate"/>
          </a>
        </li>
      </ul>
    </div> -->
    <div class="task-infor task-detail little-portrait">
      <ul>
      	<li id="admin">
          <label>管理员 </label><br />
           <span><a><img data-id="0" src="${context }/static/images/pic-shelters.png" class="img-circle">待认领</a>
          </span>
        </li>
        <li id="updateExecutor">
          <label>执行者</label><br />
          <span><a><img data-id="0" src="${context }/static/images/pic-shelters.png" class="img-circle">待认领</a>
          </span>
        </li>
        <li id="updateLevel">
          <label>优先级</label>
          <div class="layer-group open-layer">
            <a id="changeLevel"><i class=" icon-record gray"></i>一般</a>
            <ul class="add-message boardBackground inerLayer space-item  floatLayer"  style="display:none">
              <li>
                <a><i class=" icon-record gray"></i>一般</a>
                <i class="icon-ok"></i>
              </li>
              <li>
                <a><i class=" icon-record green"></i>紧急</a>
                <i class="icon-ok" style="display:none"></i>
              </li>
            </ul>
          </div>
        </li>
        <li id="status">
          <label>状态</label><br />
          <a>进行中</a>
        </li>
      </ul>
    </div>
    <div class="task-infor task-detail little-portrait">
      <ul>
         <li id="createdTime">
          <label>创建时间</label><br />
          <a><i class="icon-calendar"></i></a>
        </li>
        <li id="updateEndDate">
          <label>截止日期</label><br />
          <a><i class="icon-calendar"></i>
            <input placeholder="选择截止日期" readonly class="taskBoardDate"/>
          </a>
        </li>
        <li id="planTime" style="display:none">
          <label>计划工时</label><br />
          <a>
            1天
          </a>
        </li>
        <li id="realityTime" style="display:none">
          <label>实际工时</label><br />
          <a>
            1天
          </a>
        </li>
        <li id="visibilityUser">
          <label>可见性</label>
          <div class="layer-group open-layer">
            <a id="changeVisibility">仅参与者可见</a>
            <ul class="add-message boardBackground inerLayer space-item  floatLayer"  style="display:none">
              <li>
                <a></i>仅参与者可见</a>
                <i class="icon-ok"></i>
              </li>
              <li>
                <a>所有成员可见</a>
                <i class="icon-ok" style="display:none"></i>
              </li>
            </ul>
          </div>
        </li>
      </ul>
    </div>
    <div id="updateCheckItem" class="task-infor add-message">
      <a><i class="icon-plus-circle"></i>步骤</a>
      <div id="progressbar">
        <div class="progress-label"></div>
      </div>
      <ul class="check-item">
      </ul>
    </div>
    <div  id="updateChildTask" class="task-infor add-message newInfor">
      <label>父任务</label><br />
      <div id="parentTask" ><ul class="check-item little-portrait"></ul></div>
      <label>子任务</label><a id="addChildTask" title='添加子任务'><i class="icon-plus-circle"></i></a><br />
      <div id="childTask" ><ul class="check-item little-portrait"></ul></div>
      <div id="addChildTaskForm" class="task"></div>
    </div>
     <div  id="updatePartner" class="task-infor add-message little-portrait">
        <label>参与者</label><br />
        <ul>
          <li id="selectPartner"><i class="icon-plus-circle icon-big"></i></li>
        </ul>
      </div>
      <div id="tabContent" class="task-infor task-activity-tab">
      		<ul id="switchTab" class="nav nav-tabs nav-justified">
      		   <!-- <li class="active"><a href="#">留言</a></li> -->
               <li class="active"><a href="#">动态 </a></li>
               <li><a href="#">附件</a></li>
               <li><a href="#">不通过原因（<span >2</span>次）</a>
            </ul>
            <!-- <div class="task-message little-portrait">
            	<ul id="taskMessage">
                </ul>
            </div> -->
            <div class="activity-detail little-portrait ">
            	<ul id="activityContent">
                </ul>
            </div>
            <div class="nopassReason little-portrait log-list corner-6" style="display:none">
            	<ul></ul>
            </div>
            <div class="tab-accessory little-portrait" style="display:none">
            	<h5>
            		<form id="uploadFileForm" action="${context }/task/uploadTaskFile.htm" method="post" enctype="multipart/form-data">
            	    	<input id="uploadFileInput" type="file" name="taskFiles" style="display:none" multiple="multiple">
            	    	<input id="fileTaskId" name="taskId" type="text"  style="display:none">
            	    	<input id="uploadProjectId" name="projectId" type="text"  style="display:none">
    				</form>
    				<form id="downLoadFileForm" class="tohide" action="${context }/filelibrary/downloadfile.htm" method="post">
				    	<input type="hidden" name="remove_cache"/>
				    	<input type="hidden" name="fileUrl"/>
			   		 </form>
                	<a id="uploadFileBtn" > <i class="icon-attach-2"></i>添加附件 </a>
                </h5>    	
                <!-- 下载附件表单 -->
                <ul id="taskFileContend">
                </ul>
            </div>
            
      </div>
    <!-- <div id="discussContent" class="comment">
      <div class="flex-text-wrap">
        <textarea  placeholder="说点什么" class="form-control"></textarea>
      </div>
      <div class="comment-handler">
        <button class="btn btn-sure">回复</button>
      </div>
    </div> -->

  </div>
  <script type="text/javaScript" src="${context }/static/script/taskBoard/task.js"></script>