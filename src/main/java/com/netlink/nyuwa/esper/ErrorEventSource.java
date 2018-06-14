/*  
 * Licensed under the Apache License, Version 2.0 (the "License");  
 *  you may not use this file except in compliance with the License.  
 *  You may obtain a copy of the License at  
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0  
 *  
 *  Unless required by applicable law or agreed to in writing, software  
 *  distributed under the License is distributed on an "AS IS" BASIS,  
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  
 *  See the License for the specific language governing permissions and  
 *  limitations under the License.  
 */
package com.netlink.nyuwa.esper;

import com.espertech.esper.client.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * ErrorEventSource.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-20 20:10 fubencheng.
 */
@Component
public class ErrorEventSource implements InitializingBean {

    private EPRuntime epRuntime;

    @Resource
    private EPServiceProvider epServiceProvider;

    public void sendLogEvent(){
        Map<String, Object> logEvent = new HashMap<>(18);
        logEvent.put("appName", "nyuwa");
        logEvent.put("ip", "127.0.0.1");
        logEvent.put("level", "ERROR");
        logEvent.put("source", "microLog");
        logEvent.put("message", "2018-06-08 14:57:44,682 [Thread-0] ERROR [XXX] [gitlab.nyuwa.com/stargate/nyuwa/controllers.(*QA_Controller).Hello:20] [trace=,span=,parent=,name=,app=,begintime=,endtime=] - Error:code=1, desc=结构体日志测试, reason=<nil>");
        epRuntime.sendEvent(logEvent, "error_event");

        logEvent.put("appName", "nyuwa");
        logEvent.put("ip", "127.0.0.1");
        logEvent.put("level", "ERROR");
        logEvent.put("source", "microLog");
        logEvent.put("message", "2018-06-08 14:57:44,682 [Thread-0] ERROR [XXX] [gitlab.nyuwa.com/stargate/nyuwa/controllers.(*QA_Controller).Hello:20] [trace=,span=,parent=,name=,app=,begintime=,endtime=] - Error:code=1, desc=结构体日志测试, reason=<nil>");
        epRuntime.sendEvent(logEvent, "error_event");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EPAdministrator epAdministrator = this.epServiceProvider.getEPAdministrator();
        Map<String, Object> eventType = new HashMap<>(16);
        eventType.put("level", String.class);
        eventType.put("appName", String.class);
        eventType.put("source", String.class);
        eventType.put("time", String.class);
        eventType.put("message", String.class);
        eventType.put("class", String.class);
        eventType.put("ip", String.class);
        eventType.put("hostname", String.class);
        epAdministrator.getConfiguration().addEventType("error_event", eventType);
        String epl = "select ip,appName,source,message "
                + "from error_event.win:time_batch(30 sec) "
                + "where level = 'ERROR' "
                + "group by level "
                + "having count(*)>=1 ";
        EPStatement epStatement = epAdministrator.createEPL(epl);
        epStatement.addListener(new ErrorEventListener());
        this.epRuntime = epServiceProvider.getEPRuntime();
    }

}
