package depavlo.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * The Class SpringApplicationContext.
 * 
 * @author Pavlo Degtyaryev
 */
public class SpringApplicationContext implements ApplicationContextAware {

	/** The ApplicationContext. */
	private static ApplicationContext CONTEXT;

	/**
	 * Sets the application context.
	 *
	 * @param applicationContext the new application context
	 * @throws BeansException the beans exception
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		CONTEXT = applicationContext;
	}

	/**
	 * Gets the bean by name.
	 *
	 * @param beanName the bean name
	 * @return the bean
	 */
	public static Object getBean(String beanName) {
		return CONTEXT.getBean(beanName);
	}

}
