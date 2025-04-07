package basics.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration // indique que cette classe va produire des beans
@ComponentScan("basics.beans.annotationconfig") // indique le package à scanner pour trouver les beans	
public class AnnotationConfig {

}
