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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * LogEventSourceTest.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-06 14:18 fubencheng.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LogEventSourceTest {

    @Resource
    private LogEventSource logEventSource;

    @Test
    public void testSendLogEvent(){
        logEventSource.sendLogEvent();
    }

}
