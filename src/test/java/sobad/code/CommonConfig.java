package sobad.code;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ComponentScan("sobad.code")
@ActiveProfiles("dev")
public class CommonConfig {

}
