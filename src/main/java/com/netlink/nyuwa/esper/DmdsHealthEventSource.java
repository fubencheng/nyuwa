package com.netlink.nyuwa.esper;

import com.alibaba.fastjson.JSONObject;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fubencheng
 */
@Component
public class DmdsHealthEventSource implements InitializingBean {

    private EPRuntime epRuntime;

    @Resource
    private EPServiceProvider epServiceProvider;

    public void sendDmdsHealthEvent(){
        String eventJson1 = "{\"source\":\"DEV\",\"appName\":\"dmds_health\",\"timestamp\":1544760467677,\"endpoint\":\"10.253.4.154\",\"port\":\"8006\",\"ip\":\"10.253.4.154\",\"dmdsname\":\"openplatform-dev\",\"dmds_health\":2}";
        String eventJson2 = "{\"source\":\"DEV\",\"appName\":\"dmds_health\",\"timestamp\":1544760467677,\"endpoint\":\"10.253.4.154\",\"port\":\"8007\",\"ip\":\"10.253.4.154\",\"dmdsname\":\"openplatform-dev\",\"dmds_health\":1}";
        String eventJson3 = "{\"source\":\"DEV\",\"appName\":\"dmds_health\",\"timestamp\":1544760467677,\"endpoint\":\"10.253.4.154\",\"port\":\"8008\",\"ip\":\"10.253.4.154\",\"dmdsname\":\"openplatform-dev\",\"dmds_health\":2}";
        String eventJson4 = "{\"source\":\"DEV\",\"appName\":\"dmds_health\",\"timestamp\":1544760467677,\"endpoint\":\"10.253.4.154\",\"port\":\"8009\",\"ip\":\"10.253.4.154\",\"dmdsname\":\"openplatform-dev\",\"dmds_health\":1}";
        String eventJson5 = "{\"source\":\"DEV\",\"appName\":\"dmds_health\",\"timestamp\":1544760467677,\"endpoint\":\"10.253.4.154\",\"port\":\"8009\",\"ip\":\"10.253.4.154\",\"dmdsname\":\"openplatform-dev\",\"dmds_health\":2}";

        epRuntime.sendEvent(JSONObject.parseObject(eventJson1, HashMap.class), "DEV_dmds_health");
        epRuntime.sendEvent(JSONObject.parseObject(eventJson2, HashMap.class), "DEV_dmds_health");
        epRuntime.sendEvent(JSONObject.parseObject(eventJson3, HashMap.class), "DEV_dmds_health");
        epRuntime.sendEvent(JSONObject.parseObject(eventJson4, HashMap.class), "DEV_dmds_health");
        epRuntime.sendEvent(JSONObject.parseObject(eventJson5, HashMap.class), "DEV_dmds_health");
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        EPAdministrator epAdministrator = this.epServiceProvider.getEPAdministrator();
        Map<String, Object> dmdsHealthType = new HashMap<>(16);
        dmdsHealthType.put("dmds_health", Integer.class);
        dmdsHealthType.put("endpoint", String.class);
        dmdsHealthType.put("appName", String.class);
        dmdsHealthType.put("ip", String.class);
        dmdsHealthType.put("source", String.class);
        dmdsHealthType.put("timestamp", String.class);
        dmdsHealthType.put("dmdsname", String.class);
        dmdsHealthType.put("port", String.class);

        epAdministrator.getConfiguration().addEventType("DEV_dmds_health", dmdsHealthType);
        String epl = "select count(*),dmds_health,appName,ip,source,timestamp,dmdsname,port  from DEV_dmds_health.win:length_batch(2)  where dmds_health = 1   group by ip,port ";
        EPStatement epStatement = epAdministrator.createEPL(epl);
        epStatement.addListener(new DmdsHealthEventListener());
        this.epRuntime = epServiceProvider.getEPRuntime();
    }
}