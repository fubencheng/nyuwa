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

import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

/**
 * DatabaseConnectionTest.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-06-02 17:22 fubencheng.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DatabaseConnection databaseConnection;

    @Test
    public void testGetColumns(){
        Map<String, List<ColumnInfo>> columns = databaseConnection.getColumns();
        System.out.println(JSONObject.toJSONString(columns));
    }

    @Test
    public void testGetBinlogs(){
        List<BinlogInfo> binlogInfoList = databaseConnection.getBinlogs();
        System.out.println(JSONObject.toJSONString(binlogInfoList));
    }

    @Test
    public void testGetBinlogMasterStatus(){
        BinlogMasterStatus binlogMasterStatus = databaseConnection.getBinlogMasterStatus();
        System.out.println(JSONObject.toJSONString(binlogMasterStatus));
    }

    @Test
    public void testGetServerId(){
        int serverId = databaseConnection.getServerId();
        System.out.println(serverId);
    }

}
