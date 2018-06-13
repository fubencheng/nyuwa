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
 * BizLogEventListener.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-20 20:08 fubencheng.
 */
public class BizLogEventListener implements UpdateListener {

    @Override
    public void update(EventBean[] newEvents, EventBean[] oldEvents) {
        if (newEvents != null) {
            String appName = newEvents[0].get("appName").toString();
            String methodName = newEvents[0].get("methodName").toString();
            String packageName = newEvents[0].get("packageName").toString();
            String time = newEvents[0].get("time").toString();
            System.out.println(String.format("--->appName=%s, methodName=%s, packageName=%s, time=%s", appName, methodName, packageName, time));
        } else {
            System.out.println("no new event!!!");
        }
    }

}
