package com.ks0100.wp.audit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditServiceAnnotation {

  /**
   * 用例类型枚举，参考tbl_sys_status 006类型的编码
   * @author chen haifeng
   *
   */
	//+++++++++++++++++++++++++
  public enum USEAGE_TYPE {
	u00600, u00601, u00602, u00603, u00604, u00605, u00606, u00607, u00608, u00609, u00610, u00611, u00612, u00613, u00614, u00615, u00616, u00617, u00618, u00619, u00620, u00621, u00622, u00623, u00624, u00625, u00626, u00627, u00628, u00629, u00630, u00631, u00632, u00633, u00634, u00635, u00636, u00637, u00638, u00639, u00640, u00641, u00644, u00646,
	u00651, u00654,u00655,u00656,u00657,u00658,u00659,u00660,u00661,u00662,
  };
//++++++++++++++++++++++++++++++++++++++++++
  public USEAGE_TYPE useage();

  // 切面通知类型, 指定标识注解的方法使用的切面通知类型,AFTER一般用在添加，BEFORE用在删除和修改
  public enum ASPECTJ_TYPE {
	BEFORE, AFTER
  };

  public ASPECTJ_TYPE aspectJ_type() default ASPECTJ_TYPE.AFTER;

  //接收的第一个参数类型。用在接收的参数类型和其他参数类型不重复的情况下
  public String argType1() default "";

  //接收第一个参数，排在第几位，从1开始。当接收的参数类型和其他参数类型重复，比如String类型，那么指定位置比较好。
  public int argIndex1() default 0;

  //接收的第二个参数类型
  public String argType2() default "";

  public String argType3() default "";

  //接收第二个参数，排在第几位。
  public int argIndex2() default 0;

  public int argIndex3() default 0;
}
