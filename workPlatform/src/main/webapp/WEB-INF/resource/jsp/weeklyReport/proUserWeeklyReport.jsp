<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp"%>
<script type="text/javascript">
window.readOnly = true;
<status:permission status="${project.status }">
	window.readOnly = false;
</status:permission>
</script>
<body class="project vertical-scroll">
	<div class="main-content" id="proUserWeekRep">

		<div class="home-body-wrap">
			<div class="dynamic-content ">
				<div class="weekly-tab">
					<ul class="nav nav-tabs ">
						<li class="active" data-type="userWReport"><a href="#"><spring:message
									code="chengyuan" />周报</a>
						</li>
						<myshiro:hasPermissionByList name="hasPermissionByList"
							permitCodeAndIds="${permitCodeAndIds}">
							<li data-type="proWReport"><a href="#"><spring:message
										code="xiangmu" />周报</a>
							</li>
						</myshiro:hasPermissionByList>
					</ul>
				</div>
				<div id="userWeeklyReport">
					<div class="layout-left weekly-top" id="displayTime">
						<div class="time-bg" id="selectTimeList"
							style="position:fixed;width:250px">
							<div class="layer-group open-layer layer-inblock"
								id="selectWeek_div">
								<ul id="selectWeek"
									class="boardBackground   space-item2  floatLayer vertical-scroll-min"
									style='display:none'>
									<!-- <li><a>12.15-12.19</a> <i class=" icon-ok"></i>  </li>
                       <li><a>12.15-12.19</a></li>
                       <li><a>12.15-12.19</a></li> -->
								</ul>
							</div>
						</div>
						<ul style="margin-top:50px">
							<li data-userid="0"><span class="check-box" id="allUser"><i
									class="icon-ok"></i> </span> <img class="img-circle"
								src="${context }/static/images/head-portraits2.png" /><a>所有<spring:message
										code="chengyuan" />
							</a>
							</li>
							<div id="userList">
								<%-- <li><span class="check-box"><i class="icon-ok"></i></span>  <img class="img-circle" src="${context }/static/images/head-pic.jpg" /><a>moonstar</a></li>
                    <li><span class="check-box"><i class="icon-ok"></i></span>  <img class="img-circle" src="${context }/static/images/head-pic3.jpg" /><a>jack</a></li> --%>
							</div>
						</ul>

					</div>
					<div class="layout-right  weekly weekly-top vertical-scroll">
						<div class="dynamic-state weekly-report">
							<div class="title">
								<h4>&nbsp;</h4>
							</div>
							<div id="proUserWReport"></div>
						</div>
						<div id="isOrNotUserReport"></div>
					</div>
				</div>
				<div id="proWeeklyReport" class="tohide">
					<div class="layout-left weekly-top ">
						<ul class="weekly-item">
							<li class="select"><a><i class=" icon-calendar"></i> </a></li>
							<li><a><i class="icon-calendar"></i> </a></li>
							<li><a><i class="icon-calendar"></i> </a></li>
							<li><a><i class=" icon-calendar"></i> </a></li>
						</ul>

					</div>
					<div
						class="layout-right  weekly weekly-top weekly-program vertical-scroll"
						id="proWeekReport" data-pwrid>
						<div id="projectWeekReport_div">
							<div class="title">
								<h4></h4>
							</div>
							<div class="time-block corner-3">
								<div class="layer-top task-infor ">
									<h5>企业工作平台-协同办公</h5>
								</div>
								<div class="task-infor task-detail little-portrait">
									<ul>
										<li><label><spring:message code="xiangmu" />状态</label><br />
											<a class="status-width"></i><i class=" icon-record green"></i><input id="status" value=""
												class="input-width transparency projectWeekReport"
												readonly="true" placeholder="点击编辑"> </a></li>
										<li class="removepointer"><label>交付日期</label><br /> <a
											id="deliveryTime" class="mouse-shape"><i
												class="icon-calendar "></i> <!-- 2014-09-09 --> </a></li>
										<li><label><spring:message code="xiangmu" />阶段</label><br />
											<a class="status-width"></i><i class=" icon-record green"></i><input
												id="phase" value=""
												class="transparency projectWeekReport input-width"
												readonly="true" placeholder="点击编辑"> </a></li>
										<li><label><spring:message code="xiangmu" />进度（%）</label><br />
											<a class="width-change"><input id="progress" value=""
												class="transparency projectWeekReport input-width"
												readonly="true"  title="点击编辑" data-toggle="tooltip"> </a></li>
										<li><label>填写人</label><br /> <a class="mouse-shape"
											id="filledUser" popup-content></a></li>
										<li><label>填写日期</label><br /> <a id="createTime"
											class="mouse-shape"><i class="icon-calendar "></i>2014-10-12</a>
										</li>
									</ul>
								</div>
								<div class="task-infor add-message little-portrait">
									<label><spring:message code="xiangmu" /> <spring:message
											code="chengyuan" /> </label><br>
									<ul id="usersPro">
									</ul>
								</div>
								<div class="week-block">
									<h1>上周工作情况</h1>
									<div class="task-infor add-message  little-portrait">
										<label>风险或问题</label>
										<table class=" table" id="lastWeekRisk">
										</table>
									</div>
									<div class="task-infor add-message  little-portrait">
										<label>工作内容</label>
										<table class=" table" id="lastWeekWork">
										</table>
									</div>
								</div>
								<div class="week-block">
									<h1>本周工作情况</h1>
									<div class="task-infor add-message  little-portrait">
										<label>风险或问题</label>
										<status:permission status="${project.status }">
											<myshiro:hasPermission
												name="<%=PermitConstant.PRJ_ADMIN_ACCESS %>"
												objectId="${project.projectId}">
												<a class="green-link" id="addProblem"><i
													class="icon-plus-circle"></i>添加问题</a>
											</myshiro:hasPermission>
										</status:permission>

										<br>
										<table class=" table" id="weekRisk">
										</table>
									</div>
									<div class="task-infor add-message  little-portrait">
										<label>工作内容</label>

										<status:permission status="${project.status }">
											<myshiro:hasPermission
												name="<%=PermitConstant.PRJ_ADMIN_ACCESS %>"
												objectId="${project.projectId}">
												<a class="green-link" id="addWork"><i
													class="icon-plus-circle"></i>添加工作</a>
											</myshiro:hasPermission>
										</status:permission>
										<br>
										<table class=" table" id="weekWorks">
										</table>
									</div>
									<div
										class="task-infor add-message  little-portrait  task-content"
										data-pwrcid="0">
										<label>工作总结</label>
										<status:permission status="${project.status }">
											<myshiro:hasPermission
												name="<%=PermitConstant.PRJ_ADMIN_ACCESS %>"
												objectId="${project.projectId}">
												<i class=" icon-pencil" title="修改"></i>
											</myshiro:hasPermission>
										</status:permission>

										<p></p>
										<textarea class="form-control weekly-control"
											style="display: none"></textarea>
									</div>
								</div>
								<div class="week-block">
									<h1>下周工作情况</h1>
									<div class="task-infor add-message  little-portrait">
										<label>工作计划</label>
										<status:permission status="${project.status }">
											<myshiro:hasPermission
												name="<%=PermitConstant.PRJ_ADMIN_ACCESS %>"
												objectId="${project.projectId}">
												<a class="green-link" id="addPlan"><i
													class="icon-plus-circle"></i>添加计划</a>
											</myshiro:hasPermission>
										</status:permission>




										<br>
										<table class=" table" id="nextWeekPlan">
											<%-- <tr><th>编号</th><th>工作内容</th><th>责任人</th></tr>
                            <tr>
                                <td>1</td><td>评估技术方案</td><td>
                                <ul>
                                    <li><img src="${context }/static/images/head-pic.jpg" class="img-circle"><a class="remove-member-handler">×</a></li>
                                    <li><i class="icon-plus-circle icon-big"></i></li>
                                 </ul>
                                 </td>
                             </tr>
                               <tr>
                                <td>1</td><td>评估技术方案</td><td>
                                <ul>
                                    <li><img src="${context }/static/images/head-pic.jpg" class="img-circle"><a class="remove-member-handler">×</a></li>
                                    <li><i class="icon-plus-circle icon-big"></i></li>
                                 </ul>
                                 </td><i class="icon-trash-empty" title="删除"></i>
                             </tr>    --%>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>


			</div>
		</div>
	</div>
	<script>
		$(function () { $("[data-toggle='tooltip']").tooltip(); });
	</script>
	<script type="text/javaScript" src="${context }/static/script/weeklyReport/proUserWeeklyReport.js?_T=<%=System.currentTimeMillis()%>"></script>
</body>