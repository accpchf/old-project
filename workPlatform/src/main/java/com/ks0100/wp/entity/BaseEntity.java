package com.ks0100.wp.entity;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 所有实体类的父类,实现Serializable接口,以便JVM可以序列化entity实例
 * 
 * @author xie linming
 * @date 2014-2-13
 * 
 */
public abstract class BaseEntity implements Serializable {

	private static final long serialVersionUID = 3310278150770927808L;
	
	/**
	 * 使用反射机制重写对象的toString()方法
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * 使用反射机制重写对象的hashCode()方法
	 * 如果子类实体类中有Set集合对象,并且配置了Hibernate的lazy="true",hashCode
	 * ()不能使用反射的方法,否则会抛LazyInitializationException,子类这时可使用下面的方法加以覆盖.
	 */
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	/*
	 * public int hashCode() { return new
	 * HashCodeBuilder(17,37).appendSuper(super
	 * .hashCode()).append(字段1).append(字段2).append(字段N).toHashCode(); }
	 */

	/**
	 * 使用反射机制重写对象的equals()方法
	 */
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	

}
