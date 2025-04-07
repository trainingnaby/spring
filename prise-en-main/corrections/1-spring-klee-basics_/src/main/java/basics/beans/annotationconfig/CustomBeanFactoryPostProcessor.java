//package basics.beans.annotationconfig;
//
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.MutablePropertyValues;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//        for (String name : beanFactory.getBeanDefinitionNames()) {
//            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
//
//            if (beanDefinition.getBeanClassName() != null &&
//                beanDefinition.getBeanClassName().endsWith("Service")) {
//                
//                // Ex: ajouter une propriété à ce bean
//                MutablePropertyValues propertyValues = beanDefinition.getPropertyValues();
//                propertyValues.add("prefix", "Prod-");
//                
//            }
//        }
//    }
//}