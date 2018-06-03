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
package com.netlink.nyuwa.openreplicator;

import com.google.code.or.OpenReplicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * OpenReplicatorMain.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-27 16:06 fubencheng.
 */
@Slf4j
@Component
public class OpenReplicatorMain implements InitializingBean {

    @Resource
    private DatabaseConnection databaseConnection;

    @Resource
    private BinlogEventV4Listener binlogEventV4Listener;

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    protected void start() {
        OpenReplicator openReplicator = new OpenReplicator();
        openReplicator.setHost("127.0.0.1");
        openReplicator.setPort(3306);
        openReplicator.setUser("root");
        openReplicator.setPassword("root");

        // TODO 应该是从上一次消费的fileName和position开始继续拉取的
        String binlogFileName = databaseConnection.getBinlogMasterStatus().getBinlogName();
        Long binlogPosition = databaseConnection.getBinlogMasterStatus().getPosition();
        openReplicator.setBinlogFileName(binlogFileName);
        openReplicator.setBinlogPosition(binlogPosition);
        openReplicator.setBinlogEventListener(binlogEventV4Listener);

        try {
            openReplicator.start();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
