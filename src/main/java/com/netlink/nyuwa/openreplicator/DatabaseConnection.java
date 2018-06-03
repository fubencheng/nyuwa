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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DatabaseConnection.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-06-02 16:56 fubencheng.
 */
@Slf4j
@Component
public class DatabaseConnection implements InitializingBean {

    private Connection connection;

    @Override
    public void afterPropertiesSet() throws Exception {
        initConnection();
    }

    private void initConnection(){
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", "root", "root");
                log.info("connect to master, host={}", "127.0.0.1");
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    public Connection getConnection(){
        try{
            if (connection == null || connection.isClosed()){
                initConnection();
            }
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return connection;
    }

    public Map<String, List<ColumnInfo>> getColumns(){
        Map<String, List<ColumnInfo>> columns = new HashMap<>(16);

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();
            while(resultSet.next()){
                String databaseName = resultSet.getString("TABLE_CAT");
                ResultSet result = metaData.getTables(databaseName, null, null, new String[]{"TABLE"});
                while(result.next()){
                    String tableName = result.getString("TABLE_NAME");
                    String key = databaseName +"."+tableName;
                    ResultSet columnSet = metaData.getColumns(databaseName, null, tableName, null);
                    columns.put(key, new ArrayList<>());
                    while(columnSet.next()){
                        ColumnInfo columnInfo = new ColumnInfo(columnSet.getString("COLUMN_NAME"), columnSet.getString("TYPE_NAME"));
                        columns.get(key).add(columnInfo);
                    }
                    if (columnSet != null) {
                        columnSet.close();
                    }
                }
                if (result != null) {
                    result.close();
                }
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return columns;
    }

    public List<BinlogInfo> getBinlogs(){
        List<BinlogInfo> binlogList = new ArrayList<>();

        Statement statement;
        ResultSet resultSet;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("show binary logs");
            while(resultSet.next()){
                BinlogInfo binlogInfo = new BinlogInfo(resultSet.getString("Log_name"),resultSet.getLong("File_size"));
                binlogList.add(binlogInfo);
            }
            if(resultSet != null) {
                resultSet.close();
            }
            if(statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return binlogList;
    }

    public BinlogMasterStatus getBinlogMasterStatus(){
        BinlogMasterStatus binlogMasterStatus = new BinlogMasterStatus();

        Statement statement;
        ResultSet resultSet;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("show master status");
            while(resultSet.next()){
                binlogMasterStatus.setBinlogName(resultSet.getString("File"));
                binlogMasterStatus.setPosition(resultSet.getLong("Position"));
            }
            if(resultSet != null) {
                resultSet.close();
            }
            if(statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return binlogMasterStatus;
    }

    public int getServerId(){

        int serverId = -1;
        Statement statement;
        ResultSet resultSet;

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("show variables like 'server_id'");
            while(resultSet.next()){
                serverId = resultSet.getInt("Value");
            }
            if(resultSet != null) {
                resultSet.close();
            }
            if(statement != null) {
                statement.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return serverId;
    }
}
