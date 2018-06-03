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

import com.google.code.or.binlog.impl.event.TableMapEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TableInfoHolder.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-06-03 11:04 fubencheng.
 */
@Slf4j
@Component
public class TableInfoHolder implements InitializingBean {

    private static Map<Long, TableInfo> tabledIdMap = new ConcurrentHashMap<>(16);
    private static Map<String, List<ColumnInfo>> columnsMap = new ConcurrentHashMap<>(16);


    @Resource
    private DatabaseConnection databaseConnection;


    @Override
    public void afterPropertiesSet() throws Exception {
        columnsMap = databaseConnection.getColumns();
    }

    public void saveTableIdMap(TableMapEvent tableMapEvent){
        long tableId = tableMapEvent.getTableId();
        tabledIdMap.remove(tableId);

        TableInfo table = new TableInfo();
        table.setDatabaseName(tableMapEvent.getDatabaseName().toString());
        table.setTableName(tableMapEvent.getTableName().toString());
        table.setFullName(tableMapEvent.getDatabaseName()+"."+tableMapEvent.getTableName());

        tabledIdMap.put(tableId, table);
    }

    public TableInfo getTableInfo(long tableId){
        return tabledIdMap.get(tableId);
    }

    public List<ColumnInfo> getColumns(String fullName){
        return columnsMap.get(fullName);
    }

    public synchronized void refreshColumnsMap(){
        Map<String, List<ColumnInfo>> colsMap = databaseConnection.getColumns();
        if(colsMap != null && !colsMap.isEmpty()) {
            columnsMap = colsMap;
        } else {
            log.error("refresh columnsMap failed!");
        }
    }
}
