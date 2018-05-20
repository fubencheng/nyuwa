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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * LogEventListener.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-20 20:08 fubencheng.
 */
public class LogEventListener implements UpdateListener {

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        String appName = newEvents[0].get("appName").toString();
        String level = newEvents[0].get("level").toString();
        String ip = newEvents[0].get("ip").toString();
        String message = newEvents[0].get("message").toString();
        System.out.println(String.format("appName=%s, level=%s, ip=%s, message=%s", appName, level, ip, message));
    }

}
