package basics.beans.annotationconfig.utility;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationListener implements InitializingBean, DisposableBean, BeanPostProcessor {

    @EventListener
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("Envoi d’un email à : " + event.getEmail());
        // Logique d’envoi d’email ici
    }

	@Override
	public void destroy() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
}