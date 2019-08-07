<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp"%>
  <div class="wrap " id = "userWeekRep">    
      <div class=" weekly weekly-top weekly-personal vertical-scroll" id="userWeeklyDisplay"   style="display: none">
              <div class="title">
   				 	
   				 	 <div class="layer-group open-layer layer-inblock" id="selectWeek_div" >
                     <button class="btn" id="selectWeek_btn"></button>
                    <ul id="selectWeek" class="boardBackground  space-item2  floatLayer vertical-scroll-min" style='display:none'>
                    </ul>
                 </div>
                    <div class="layer-group open-layer layer-inblock" id="selectProject_div">
                    <button data-projectid="0" id="selectProject_btn" class="btn"><a><spring:message code="xiangmu"  />选择<i class=" icon-down-open-big"></i></a> </button>
                    <ul id="selectProject" class="boardBackground   space-item2  floatLayer vertical-scroll-min" style='display:none'>
                    </ul>
                 </div>
   				 
              </div>
              <div id = "uwReportInfo">
               			
              </div>
               			
          </div>
  </div>
<script type="text/javaScript" src="${context }/static/script/weeklyReport/userWeeklyReport.js"></script>