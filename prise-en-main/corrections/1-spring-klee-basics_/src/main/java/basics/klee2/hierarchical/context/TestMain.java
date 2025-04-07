package basics.klee2.hierarchical.context;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TestMain {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext parentContext = new AnnotationConfigApplicationContext(ParentConfig.class);

		AnnotationConfigApplicationContext childContext = new AnnotationConfigApplicationContext();
		childContext.setParent(parentContext);
		childContext.register(ChildConfig.class);
		childContext.refresh();
		
//		String[] beanDefinitionNames = parentContext.getBeanDefinitionNames();
//		System.out.println("list beans parent: ");
//		for (String beanName : beanDefinitionNames) {
//			System.out.println(beanName);
//		}
		System.out.println("is utilsService exists on parent context ? "+parentContext.containsBean("utilsService"));
		System.out.println("is commonService exists on child context ? "+parentContext.containsBean("commonService"));

//		String[] beanDefinitionNamesfils = childContext.getBeanDefinitionNames();
//		System.out.println("list beans fils: ");
//		for (String beanName : beanDefinitionNamesfils) {
//			System.out.println(beanName);
//		}
		System.out.println("is utilsService exists on child context ? "+childContext.containsBean("utilsService"));
		System.out.println("is commonService exists on child context ? "+childContext.containsBean("commonService"));


	}

}
