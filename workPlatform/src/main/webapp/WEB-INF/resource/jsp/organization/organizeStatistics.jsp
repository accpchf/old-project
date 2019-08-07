<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
	String context = request.getContextPath();
	request.setAttribute("context", context);
%>
<!-- <div id="staffStatistics" class="layout-5  boardBackground">
	<div class="h-title">
		<h4><spring:message code="chengyuan"  />统计<input style="font-size: 14px; margin-left: 20px; cursor: pointer;" type="button" id="cyTjExport" value="导出为EXCEL" /></h4>
		<div  class="layer-group open-layer layer-inblock2 pull-right">
			<a><span>最近一个月</span><i class=" icon-down-dir"></i></a>
			<ul style="display:none" class="boardBackground   space-item2  floatLayer vertical-scroll-min">
				<li><a>最近一周</a><i class="icon-ok" style="display:none"></i></li>
				<li><a>最近一个月</a><i class="icon-ok" style="display:none"></i></li>
				<li><a>所有</a><i class="icon-ok" style="display:none"></i></li>
			</ul>
		</div>
	</div>
	
</div>
<div id="projectsStatistics"  class="layout-5  boardBackground">
	<div class="h-title">
		<h4><spring:message code="xiangmu"  />统计<input style="font-size: 14px; margin-left: 20px; cursor: pointer;" type="button" id="xmTjExport" value="导出为EXCEL" /></h4>
	</div>
</div> 
<div id="teamStatistics"  class="layout-5  boardBackground">
	<div class="h-title">
		<h4><spring:message code="tuandui"  /></h4>
	</div>
	
</div>-->
<div class="wrap organization-common">   
         <div class="layout-5 ">
         	<h4>按工时<button id="xmTjExportByTime" class="btn outer-borer">导出为EXCEL</button></h4>
         	<div class="h-title">        	 	
				<div id='staffWorkStatisticsMenu' class="layer-group open-layer layer-inblock2 pull-right">
					<a><span>最近一个月</span><i class=" icon-down-dir"></i></a>
					<ul style="display:none" class="boardBackground   space-item2  floatLayer vertical-scroll-min">
						<li><a>最近一周</a><i class="icon-ok" style="display:none"></i></li>
						<li><a>最近一个月</a><i class="icon-ok" style="display:none"></i></li>
						<li><a>所有</a><i class="icon-ok" style="display:none"></i></li>
					</ul>
				</div>              
        	</div> 
         	<div class="layouts boardBackground" id="wTimeStatistics">
            	<ul class="nav nav-tabs nav-justified">
			       <li data-type="user" class="active"><a href="#">成员</a></li>
			       <li data-type="project"><a href="#">项目</a></li>
			       <li data-type="team"><a href="#">团队</a></li>
			    </ul>
			    <div id="staffWTimeStatisticsChart"  class="layout-5  boardBackground"></div>
			    <div id="projectWTimeStatisticsChart" class="layout-5  boardBackground" style="display:none;"></div>
			    <div id="teamWTimeStatisticsChart" class="layout-5  boardBackground" style="display:none;"></div>
         	</div>

         </div>  
         <div class="layout-5 ">
         	<h4>按任务<button id="xmTjExport" class="btn outer-borer">导出为EXCEL</button></h4>
         	<div class="h-title">        	 	
                 <div id='staffStatisticsMenu' class="layer-group open-layer layer-inblock2 pull-right">
					<a><span>最近一个月</span><i class=" icon-down-dir"></i></a>
					<ul style="display:none" class="boardBackground   space-item2  floatLayer vertical-scroll-min">
						<li><a>最近一周</a><i class="icon-ok" style="display:none"></i></li>
						<li><a>最近一个月</a><i class="icon-ok" style="display:none"></i></li>
						<li><a>所有</a><i class="icon-ok" style="display:none"></i></li>
					</ul>
				</div>            
        	</div> 
         	<div class="layouts boardBackground" id="userStatistics">
         		<ul class="nav nav-tabs nav-justified">
			       <li data-type="user" class="active"><a href="#">成员</a></li>
			       <li data-type="project"><a href="#">项目</a></li>
			       <li data-type="team"><a href="#">团队</a></li>
			    </ul>
			    <div id="staffTaskStatisticsChart"  class="layout-5  boardBackground"></div>
			    <div id="projectTaskStatisticsChart" class="layout-5  boardBackground" style="display:none;"></div>
			    <div id="teamTaskStatisticsChart" class="layout-5  boardBackground" style="display:none;"></div>
            </div>
            
         </div>     
    </div>
</div>
<script src="${context }/static/script/organize/organizeStatistics.js"></script>
