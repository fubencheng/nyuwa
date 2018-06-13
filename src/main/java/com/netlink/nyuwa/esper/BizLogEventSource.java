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
 * BizLogEventSource.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-20 20:10 fubencheng.
 */
@Component
public class BizLogEventSource implements InitializingBean {

    private EPRuntime epRuntime;

    @Resource
    private EPServiceProvider epServiceProvider;

    public void sendLogEvent(){
        Map<String, Object> bizlogEvent = new HashMap<>(16);
        bizlogEvent.put("appName", "northernlight");
        bizlogEvent.put("source", "biz");
        bizlogEvent.put("creditApplyNo", "36012018061317080196");
        bizlogEvent.put("packageName", "com.zhongan.northernlight.service.impl.YZFTCBTServiceImpl");
        bizlogEvent.put("costTime", 10096);
        bizlogEvent.put("success", Boolean.TRUE.toString());
        bizlogEvent.put("time", "2018-06-13T10:14:17.017+08:00");
        bizlogEvent.put("apiType", "HSF-CAI");
        bizlogEvent.put("methodName", "apply");
        bizlogEvent.put("ip", "10.253.17.236");
        bizlogEvent.put("hostName", "iZ23jrnx628Z");
        this.epRuntime.sendEvent(bizlogEvent, "bizlog_event");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EPAdministrator epAdministrator = this.epServiceProvider.getEPAdministrator();
        Map<String, Object> bizlogEvent = new HashMap<>(16);
        bizlogEvent.put("appName", String.class);
        bizlogEvent.put("source", String.class);
        bizlogEvent.put("methodName", String.class);
        bizlogEvent.put("packageName", String.class);
        bizlogEvent.put("costTime", Long.class);
        bizlogEvent.put("success", String.class);
        bizlogEvent.put("creditApplyNo", String.class);
        bizlogEvent.put("time", String.class);
        bizlogEvent.put("apiType", String.class);
        bizlogEvent.put("ip", String.class);
        bizlogEvent.put("hostName", String.class);
        epAdministrator.getConfiguration().addEventType("bizlog_event", bizlogEvent);
        String epl = "select max(costTime),appName,ip,packageName,hostName,creditApplyNo,apiType,source,time,methodName "
                + "from bizlog_event.win:time_batch(60 sec) "
                + "where packageName like  '%YZFTCBTServiceImpl%' "
                + "having max(costTime)>10000";

//        String epl = "select max(costTime),appName,ip,packageName,hostName,creditApplyNo,apiType,source,time,methodName "
//                + "from bizlog_event.win:length_batch(1) "
//                + "where packageName like  '%YZFTCBTServiceImpl%' "
//                + "having costTime > 10000";
        EPStatement epStatement = epAdministrator.createEPL(epl);
        epStatement.addListener(new BizLogEventListener());
        this.epRuntime = epServiceProvider.getEPRuntime();
    }

}
