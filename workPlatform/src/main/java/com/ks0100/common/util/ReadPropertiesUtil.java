package com.ks0100.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 
 * 在spring配置文件里面（比如 applicationContext.xml）配置，用getContextProperty(String name)就能拿到属性的值
 * <bean id="propertyConfigurer" class="com.ks0100.common.util.ReadPropertiesUtil">
		<property name="ignoreResourceNotFound" value="true" />
		<property name="locations">
			<list>
				<value>/WEB-INF/config/mail.properties</value>
				<value>/WEB-INF/config/wp.properties</value>
				<value>/WEB-INF/config/jdbc.properties</value>
			</list>
		</property>
	</bean>
 * @author chen haifeng
 * 
 */
public class ReadPropertiesUtil extends PropertyPlaceholderConfigurer {
  private static Map<String, Object> propertiesMap;

  @Override
  protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
	  Properties props) throws BeansException {
	super.processProperties(beanFactoryToProcess, props);
	propertiesMap = new HashMap<String, Object>();
	for(Object key : props.keySet()) {
	  String keyStr = key.toString();
	  String value = props.getProperty(keyStr);
	  propertiesMap.put(keyStr, value);
	}
  }

  public static Object getContextProperty(String name) {
	return propertiesMap.get(name);
  }

  public static String getStringContextProperty(String name) {
	return (String)propertiesMap.get(name);
  }
}
