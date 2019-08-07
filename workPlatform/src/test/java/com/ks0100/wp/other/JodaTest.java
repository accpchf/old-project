package com.ks0100.wp.other;
  
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
  
public class JodaTest {  
  
    public static void main(String[] args) {  
        //初始化时间  
                /*DateTime dateTime=new DateTime(2012, 12, 13, 18, 23,55);  
                  
                // 年,月,日,时,分,秒,毫秒    
                DateTime dt3 = new DateTime(2011, 2, 13, 10, 30, 50, 333);// 2010年2月13日10点30分50秒333毫秒  
                  
                //下面就是按照一点的格式输出时间  
                String str2 = dateTime.toString("MM/dd/yyyy hh:mm:ss.SSSa");  
                String str3 = dateTime.toString("dd-MM-yyyy HH:mm:ss");  
                String str4 = dateTime.toString("EEEE dd MMMM, yyyy HH:mm:ssa");  
                String str5 = dateTime.toString("MM/dd/yyyy HH:mm ZZZZ");  
                String str6 = dateTime.toString("MM/dd/yyyy HH:mm Z");  
                  
                DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");  
                //时间解析    
                DateTime dateTime2 = DateTime.parse("2012-12-21 23:22:45", format);    
                    
                //时间格式化，输出==> 2012/12/21 23:22:45 Fri    
                String string_u = dateTime2.toString("yyyy/MM/dd HH:mm:ss EE");    
                System.out.println(string_u);    
                    
                //格式化带Locale，输出==> 2012年12月21日 23:22:45 星期五    
                String string_c = dateTime2.toString("yyyy年MM月dd日 HH:mm:ss EE",Locale.CHINESE);    
                System.out.println(string_c);  
                  
                DateTime now = new DateTime();// 取得当前时间  
                
                System.out.println("now:----"+now.getMillis());  
                System.out.println("今天是本周的第"+now.getDayOfWeek()+"天");
                System.out.println("本周一是:"+now.dayOfWeek().withMinimumValue().toString("yyyy-MM-dd"));
                System.out.println("本周日是:"+now.dayOfWeek().withMaximumValue().toString("yyyy-MM-dd"));
                System.out.println("上个月的日期是"+now.minusMonths(1).toString("yyyy-MM-01"));*/
                
                DateTimeFormatter format2 = DateTimeFormat .forPattern("yyyy-MM-dd");  
                DateTime a= DateTime.parse("2015-07-09", format2).plusDays(1);
                if(!a.isAfterNow()){
                	 System.out.println("测试时间1:"+a.toString("yyyy-MM-dd")+"大于今天");
                }else if(a.isEqualNow()){
                	System.out.println("测试时间1:"+a.toString("yyyy-MM-dd")+"等于今天");
                }
               /* if(Days.daysBetween(a,now).getDays()==0){
                	System.out.println("测试时间:"+a.toString("yyyy-MM-dd")+"等于今天");
                }
                
                // 根据指定格式,将时间字符串转换成DateTime对象,这里的格式和上面的输出格式是一样的    
                DateTime dt2 = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").parseDateTime("2012-12-26 03:27:39");  
                  
                //计算两个日期间隔的天数  
                LocalDate start=new LocalDate(2012, 12,14);    
                LocalDate end=new LocalDate(2013, 01, 15);    
                int days = Days.daysBetween(start, end).getDays();  
                  
                //计算两个日期间隔的小时数,分钟数,秒数  
                  
                //增加日期  
                DateTime dateTime1 = DateTime.parse("2012-12-03");  
                dateTime1 = dateTime1.plusDays(30);  
                dateTime1 = dateTime1.plusHours(3);  
                dateTime1 = dateTime1.plusMinutes(3);  
                dateTime1 = dateTime1.plusMonths(2);  
                dateTime1 = dateTime1.plusSeconds(4);  
                dateTime1 = dateTime1.plusWeeks(5);  
                dateTime1 = dateTime1.plusYears(3);  
                  
                // Joda-time 各种操作.....    
                dateTime = dateTime.plusDays(1) // 增加天    
                                    .plusYears(1)// 增加年    
                                    .plusMonths(1)// 增加月    
                                    .plusWeeks(1)// 增加星期    
                                    .minusMillis(1)// 减分钟    
                                    .minusHours(1)// 减小时    
                                    .minusSeconds(1);// 减秒数  
                  
                //判断是否闰月    
                DateTime dt4 = new DateTime();    
                org.joda.time.DateTime.Property month = dt4.monthOfYear();    
                System.out.println("是否闰月:" + month.isLeap());  
                  
                //取得 3秒前的时间    
                DateTime dt5 = dateTime1.secondOfMinute().addToCopy(-3);    
                dateTime1.getSecondOfMinute();// 得到整分钟后，过的秒钟数    
                dateTime1.getSecondOfDay();// 得到整天后，过的秒钟数    
                dateTime1.secondOfMinute();// 得到分钟对象,例如做闰年判断等使用  
                  
                // DateTime与java.util.Date对象,当前系统TimeMillis转换    
                DateTime dt6 = new DateTime(new Date());    
                Date date = dateTime1.toDate();    
                DateTime dt7 = new DateTime(System.currentTimeMillis());    
                dateTime1.getMillis();   
                  
                Calendar calendar = Calendar.getInstance();    
                dateTime = new DateTime(calendar);  
                
                System.out.println(":" + month.isLeap());*/
                
                
    }  
}