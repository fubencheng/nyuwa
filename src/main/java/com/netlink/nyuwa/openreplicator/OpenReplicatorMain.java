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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * OpenReplicatorMain.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-27 16:06 fubencheng.
 */
@Slf4j
public class OpenReplicatorMain {

    public static void main(String[] args) throws Exception{
        OpenReplicator openReplicator = new OpenReplicator();
        openReplicator.setHost("127.0.0.1");
        openReplicator.setPort(3306);
        openReplicator.setUser("root");
        openReplicator.setPassword("root");

        String binlogFileName = null;
        Long binlogPosition = null;

        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", "root", "root");
        log.info("connect to master, host={}", "127.0.0.1");

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("show master status");
        while (resultSet.next()){
            binlogFileName = resultSet.getString("File");
            binlogPosition =resultSet.getLong("Position");
        }

        openReplicator.setBinlogFileName(binlogFileName);
        openReplicator.setBinlogPosition(binlogPosition);
        openReplicator.setBinlogEventListener(binlogEventV4 -> {

        });
    }

}
