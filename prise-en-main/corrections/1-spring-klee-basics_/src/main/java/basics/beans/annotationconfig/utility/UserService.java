package basics.beans.annotationconfig.utility;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;

@Service
//@Scope(value = "prototype")
public class UserService implements DisposableBean{

	private final ApplicationEventPublisher publisher;

	public UserService(ApplicationEventPublisher publisher) {
		this.publisher = publisher;
	}
	public void createUser(String email) {
		System.out.println("depuis UserService ...");
		// logique de création utilisateur...
		publisher.publishEvent(new UserCreatedEvent(email));
	}
	
	@PreDestroy
	public void testDestroy() {
		System.out.println("wwwwwwwwwwwwwww");
	}
	@Override
	public void destroy() throws Exception {
		System.out.println("xxxxxxxxxxxx");
	}
	
	private String prefix;

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}