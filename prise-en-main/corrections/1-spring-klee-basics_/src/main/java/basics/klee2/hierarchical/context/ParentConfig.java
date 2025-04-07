package basics.klee2.hierarchical.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParentConfig {

    @Bean
    public CommonService commonService() {
        return new CommonService();
    }
}
