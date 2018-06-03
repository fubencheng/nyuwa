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

import com.google.code.or.binlog.BinlogEventV4Header;
import com.google.code.or.binlog.impl.event.AbstractBinlogEventV4;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * DataEvent.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-06-03 12:11 fubencheng.
 */
@Setter
@Getter
@ToString
public class DataEvent {

    private static AtomicLong UUID = new AtomicLong(0);

    /**
     * 事件唯一标识
     */
    private Long eventId;

    /**
     * 事件类型
     */
    private Integer eventType;

    private String databaseName;

    private String tableName;

    /**
     * 事件发生的时间戳[MySQL服务器的时间]
     */
    private Long timestamp;

    /**
     * Open-Replicator接收到的时间戳[事件执行的时间戳]
     */
    private Long timestampReceipt;

    private String binlogName;

    private Long position;

    private Long nextPostion;

    private Long serverId;

    private Map<String,String> before;

    private Map<String,String> after;

    private Boolean isDdl = Boolean.FALSE;

    private String sql;

    public DataEvent(final AbstractBinlogEventV4 binlogEventV4, String databaseName, String tableName){
        this.init(binlogEventV4);
        this.databaseName = databaseName;
        this.tableName = tableName;
    }

    private void init(final AbstractBinlogEventV4 binlogEventV4){
        this.eventId = UUID.getAndAdd(1);
        this.binlogName = binlogEventV4.getBinlogFilename();

        BinlogEventV4Header header = binlogEventV4.getHeader();
        this.timestamp = header.getTimestamp();
        this.eventType = header.getEventType();
        this.serverId = header.getServerId();
        this.timestampReceipt = header.getTimestampOfReceipt();
        this.position = header.getPosition();
        this.nextPostion = header.getNextPosition();
    }
}
