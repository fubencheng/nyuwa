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

import com.google.code.or.binlog.BinlogEventListener;
import com.google.code.or.binlog.BinlogEventV4;
import com.google.code.or.binlog.impl.event.*;
import com.google.code.or.common.glossary.Column;
import com.google.code.or.common.glossary.Pair;
import com.google.code.or.common.glossary.Row;
import com.google.code.or.common.util.MySQLConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BinlogEventV4Listener.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-06-03 10:43 fubencheng.
 */
@Slf4j
@Component
public class BinlogEventV4Listener implements BinlogEventListener {

    @Resource
    private TableInfoHolder tableInfoHolder;

    @Override
    public void onEvents(BinlogEventV4 binlogEventV4) {
        int eventType = binlogEventV4.getHeader().getEventType();
        System.out.println("event type : " + eventType);
        TableInfo tableInfo;
        String databaseName;
        String tableName;
        switch (eventType){
            case MySQLConstants.FORMAT_DESCRIPTION_EVENT :
                // 启动监听binlog前伴随一个FORMAT_DESCRIPTION_EVENT
                System.out.println("format description event");
                break;

            case MySQLConstants.TABLE_MAP_EVENT :
                // 每次ROW_EVENT前都伴随一个TABLE_MAP_EVENT事件，保存一些表信息，如tableId, tableName, databaseName, 而ROW_EVENT只有tableId
                TableMapEvent tableMapEvent = (TableMapEvent) binlogEventV4;
                tableInfoHolder.saveTableIdMap(tableMapEvent);
                System.out.println("table map event, tableId = " + tableMapEvent.getTableId());
                break;

            case MySQLConstants.DELETE_ROWS_EVENT_V2 :
                DeleteRowsEventV2 deleteRowsEvent = (DeleteRowsEventV2) binlogEventV4;
                System.out.println("delete row event, tableId = " + deleteRowsEvent.getTableId());

                tableInfo = tableInfoHolder.getTableInfo(deleteRowsEvent.getTableId());
                databaseName = tableInfo.getDatabaseName();
                tableName = tableInfo.getTableName();

                List<Row> deleteRows = deleteRowsEvent.getRows();
                for(Row row : deleteRows){
                    List<Column> before = row.getColumns();
                    Map<String, String> beforeMap = getColumnMap(before, databaseName, tableName);
                    if(beforeMap != null && !beforeMap.isEmpty()){
                        DataEvent dataEvent = new DataEvent(deleteRowsEvent, databaseName, tableName);
                        dataEvent.setBefore(beforeMap);
                        DataEventManager.QUEUE.addLast(dataEvent);
                    }
                }
                break;

            case MySQLConstants.UPDATE_ROWS_EVENT_V2 :
                UpdateRowsEventV2 updateRowsEvent = (UpdateRowsEventV2) binlogEventV4;
                System.out.println("update row event, tableId = " + updateRowsEvent.getTableId());

                tableInfo = tableInfoHolder.getTableInfo(updateRowsEvent.getTableId());
                databaseName = tableInfo.getDatabaseName();
                tableName = tableInfo.getTableName();

                List<Pair<Row>> updateRows = updateRowsEvent.getRows();
                for(Pair<Row> pair : updateRows){
                    List<Column> columnsBefore = pair.getBefore().getColumns();
                    List<Column> columnsAfter = pair.getAfter().getColumns();

                    Map<String, String> beforeMap = getColumnMap(columnsBefore, databaseName, tableName);
                    Map<String, String> afterMap = getColumnMap(columnsAfter, databaseName, tableName);
                    if(beforeMap != null && afterMap != null && !beforeMap.isEmpty() && !afterMap.isEmpty()){
                        DataEvent dataEvent = new DataEvent(updateRowsEvent, databaseName, tableName);
                        dataEvent.setBefore(beforeMap);
                        dataEvent.setAfter(afterMap);
                        DataEventManager.QUEUE.addLast(dataEvent);
                    }
                }
                break;

            case MySQLConstants.WRITE_ROWS_EVENT_V2 :
                WriteRowsEventV2 writeRowsEvent = (WriteRowsEventV2) binlogEventV4;
                System.out.println("write row event, tableId = " + writeRowsEvent.getTableId());

                tableInfo = tableInfoHolder.getTableInfo(writeRowsEvent.getTableId());
                databaseName = tableInfo.getDatabaseName();
                tableName = tableInfo.getTableName();

                List<Row> writeRows = writeRowsEvent.getRows();
                for(Row row : writeRows){
                    List<Column> after = row.getColumns();
                    Map<String, String> afterMap = getColumnMap(after, databaseName, tableName);
                    if(afterMap != null && !afterMap.isEmpty()){
                        DataEvent dataEvent = new DataEvent(writeRowsEvent, databaseName, tableName);
                        dataEvent.setAfter(afterMap);
                        DataEventManager.QUEUE.addLast(dataEvent);
                    }
                }
                break;

            case MySQLConstants.QUERY_EVENT :
                QueryEvent queryEvent = (QueryEvent) binlogEventV4;
                tableInfo = getTableInfo(queryEvent);
                if (tableInfo == null) {
                    // 过滤掉事务begin等事件
                    break;
                }
                databaseName = tableInfo.getDatabaseName();
                tableName = tableInfo.getTableName();
                System.out.println("query event, databaseName = " + databaseName + ", tableName = " + tableName);

                DataEvent dataEvent = new DataEvent(queryEvent, databaseName, tableName);
                dataEvent.setIsDdl(true);
                dataEvent.setSql(queryEvent.getSql().toString());
                DataEventManager.QUEUE.addLast(dataEvent);
                break;

            case MySQLConstants.XID_EVENT :
                XidEvent xidEvent = (XidEvent) binlogEventV4;
                System.out.println("xid event, xid = " + xidEvent.getXid());
                break;

            default:
                System.out.println("unknown event, eventType = " + eventType);
                break;
        }
    }

    /**
     * ROW_EVENT中没有Column名称信息，需要读取列名信息，然后跟取回的List<Column>进行映射
     */
    private Map<String, String> getColumnMap(List<Column> columns, String databaseName, String tableName){
        Map<String, String> columnMap = new HashMap<>(16);
        if(columns != null && !columns.isEmpty()) {
            String fullName = databaseName + "." + tableName;
            List<ColumnInfo> columnInfoList = tableInfoHolder.getColumns(fullName);
            if (columnInfoList != null && !columnInfoList.isEmpty()){
                if (columnInfoList.size() == columns.size()) {
                    for (int i = 0; i < columnInfoList.size(); i++) {
                        if (columns.get(i).getValue() == null) {
                            columnMap.put(columnInfoList.get(i).getName(), "");
                        } else {
                            columnMap.put(columnInfoList.get(i).getName(), columns.get(i).toString());
                        }
                    }
                } else {
                    log.warn("cached column size does not match event column size! refresh column info cache.");
                    tableInfoHolder.refreshColumnsMap();
                    // TODO
                    return getColumnMap(columns, databaseName, tableName);
                }
            } else {
                log.warn("cached column info is empty! refresh column info cache.");
                tableInfoHolder.refreshColumnsMap();
                // TODO
                return getColumnMap(columns, databaseName, tableName);
            }
        }

        return columnMap;
    }

    /**
     * 从sql中提取Table信息，因为QUERY_EVENT是对应DATABASE这一级别的，不像ROW_EVENT是对应TABLE这一级别的，
     * 所以需要通过从sql中提取TABLE信息封装到TableInfo对象中
     */
    private TableInfo getTableInfo(QueryEvent queryEvent){
        String sql = queryEvent.getSql().toString().toLowerCase();

        TableInfo tableInfo = null;
        String databaseName = queryEvent.getDatabaseName().toString();
        String tableName;
        if(hasTableFlag(sql)){
            // 过滤掉事务begin之类的sql
            tableName = getTableName(sql);
            tableInfo = new TableInfo();
            tableInfo.setDatabaseName(databaseName);
            tableInfo.setTableName(tableName);
            tableInfo.setFullName(databaseName + "." + tableName);
        }

        return tableInfo;
    }

    private boolean hasTableFlag(String sql){
        String[] sqlArray = sql.split(" ");
        for(String item : sqlArray){
            if(item.equals("table")){
                return true;
            }
        }

        return false;
    }

    private String getTableName(String sql){
        String[] sqlArray = sql.split("\\.");
        String tableName = null;
        if (sqlArray.length > 1) {
            // alter table databaseName.tableName ...
            tableName = sqlArray[1].split(" ")[0];
        } else {
            // alter table tableName ...
            String[] sqlItemArray = sql.split(" ");
            boolean start = false;
            for (String item : sqlItemArray) {
                if (item.trim().equals("table")) {
                    start = true;
                    // 跳过 table 项
                    continue;
                }
                if (start && !item.trim().isEmpty()) {
                    tableName = item.trim();
                    break;
                }
            }
        }

        tableName.replaceAll("`", "").replaceAll(";", "");

        // create table person(...
        if(tableName.contains("(")){
            tableName = tableName.substring(0, tableName.indexOf("(")).trim();
        }

        return tableName;
    }

}
