package com.netlink.nyuwa;

import com.espertech.esper.client.*;
import com.netlink.nyuwa.esper.ImageEvent;
import com.netlink.nyuwa.esper.ImageEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author fubencheng
 */
@SpringBootApplication
public class NyuwaApplication {

	public static void main(String[] args) {
		SpringApplication.run(NyuwaApplication.class, args);
	}

	@Bean
	public EPServiceProvider epServiceProvider(){
		return EPServiceProviderManager.getDefaultProvider();
	}

}
