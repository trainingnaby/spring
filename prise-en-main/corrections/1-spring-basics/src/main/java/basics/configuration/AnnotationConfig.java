package basics.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration // Cette annotation indique que cette classe est une classe de configuration Spring
@ComponentScan("basics.beans.annotationconfig") // Cette annotation indique à Spring de scanner le package 
//"basics.beans.annotationconfig" pour détecter les composants (beans) annotés avec @Component7
@PropertySource(value = { "classpath:application.properties" }) // Cette annotation indique à Spring de charger les propriétés à partir du fichier "application.properties" situé dans le classpath
public class AnnotationConfig {

}
