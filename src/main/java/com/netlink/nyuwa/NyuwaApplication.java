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

//	@Bean
//	public EPServiceProvider epServiceProvider(){
//		Configuration configuration = new Configuration();
//		configuration.addEventType(ImageEvent.class);
//		return EPServiceProviderManager.getDefaultProvider(configuration);
//	}
//
//	@Bean
//	public EPAdministrator epAdministrator(){
//		return epServiceProvider().getEPAdministrator();
//	}
//
//	@Bean
//	public EPStatement epStatement(){
//		String epl = "select title, imageUrl, keywords "
//				+ "from ImageEvent(title like '%头条女神%' and title not like '%爆乳%').win:length(3) "
//				+ "where title like '%性感女神%' ";
//		EPStatement epStatement = epAdministrator().createEPL(epl);
//		epStatement.addListener(new ImageEventListener());
//		return epStatement;
//	}
}
