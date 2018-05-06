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
 * ImageEventListener.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-06 13:51 fubencheng.
 */
public class ImageEventListener implements UpdateListener {

    @Override
    public void update(EventBean[] newEvent, EventBean[] oldEvent) {
        System.out.println(String.format("new event size : %s", newEvent.length));
        String title = (String) newEvent[0].get("title");
        String imageUrl = (String) newEvent[0].get("imageUrl");
        String keywords = (String ) newEvent[0].get("keywords");
        System.out.println("--->" + title + "===>" + imageUrl + "***>" + keywords);
        if (oldEvent != null){
            String oldTitle = (String) newEvent[0].get("title");
            String oldImageUrl = (String) newEvent[0].get("imageUrl");
            String oldKeywords = (String ) newEvent[0].get("keywords");
            System.out.println(">>>" + oldTitle + ">>>" + oldImageUrl + ">>>" + oldKeywords);
        }
    }
}
