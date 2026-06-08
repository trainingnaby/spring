package basics.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration // Cette annotation indique que cette classe est une classe de configuration Spring
@ComponentScan("basics.beans.annotationconfig") // Cette annotation indique à Spring de scanner le package 
//"basics.beans.annotationconfig" pour détecter les composants (beans) annotés avec @Component
public class AnnotationConfig {

}
