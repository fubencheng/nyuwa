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

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventSender;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * ErrorEventSource.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-20 20:10 fubencheng.
 */
@Component
public class ErrorEventSource implements InitializingBean {

    private EventSender eventSender;

    @Resource
    private EPServiceProvider epServiceProvider;

    public void sendLogEvent(){
        LogEvent logEvent = new LogEvent();
        logEvent.setAppName(" caasplatform");
        logEvent.setIp("10.253.5.249");
        logEvent.setLevel("ERROR");
        logEvent.setSource("microLog");
        logEvent.setMessage("2018-06-08 14:57:44,682 [Thread-0] ERROR [XXX] [gitlab.zhonganinfo.com/zis_stargate/caas-platform/controllers.(*QA_Controller).Hello:20] [trace=,span=,parent=,name=,app=,begintime=,endtime=] - Error:code=1, desc=结构体日志测试, reason=<nil>");
        eventSender.sendEvent(logEvent);

        logEvent.setAppName(" caasplatform");
        logEvent.setIp("10.253.5.249");
        logEvent.setLevel("ERROR");
        logEvent.setSource("microLog");
        logEvent.setMessage("2018-06-08 14:57:44,682 [Thread-0] ERROR [XXX] [gitlab.zhonganinfo.com/zis_stargate/caas-platform/controllers.(*QA_Controller).Hello:20] [trace=,span=,parent=,name=,app=,begintime=,endtime=] - Error:code=1, desc=结构体日志测试, reason=<nil>");
        eventSender.sendEvent(logEvent);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EPAdministrator epAdministrator = this.epServiceProvider.getEPAdministrator();
        epAdministrator.getConfiguration().addEventType(LogEvent.class);
        String epl = "select count(*),ip,appName,source,message  "
                + "from LogEvent.win:time_batch(30 sec)  "
                + "where level = 'ERROR'   "
//                + "group by level  "
                + "having count(*)=1  ";
        EPStatement epStatement = epAdministrator.createEPL(epl);
        epStatement.addListener(new LogEventListener());
        this.eventSender = epServiceProvider.getEPRuntime().getEventSender("LogEvent");
    }

}
