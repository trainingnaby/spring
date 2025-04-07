package basics.klee2.hierarchical.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import basics.beans.annotationconfig.utility.UserService;

@Configuration
public class ChildConfig {

    @Bean
    public UtilsService utilsService(CommonService commonService) {
        return new UtilsService(commonService);
    }
}
