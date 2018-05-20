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

import com.espertech.esper.client.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * LogEventSource.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-20 20:10 fubencheng.
 */
@Component
public class LogEventSource implements InitializingBean {

    private EventSender eventSender;

    @Resource
    private EPServiceProvider epServiceProvider;

    public void sendLogEvent(){
        LogEvent logEvent = new LogEvent();
        logEvent.setAppName("credit-core-cash-loan");
        logEvent.setIp("10.253.22.134");
        logEvent.setLevel("ERROR");
        logEvent.setSource("microLog");
        logEvent.setMessage("2018-05-17 03:01:23,567 [HSFBizProcessor-4-thread-39] ERROR [com.netlink.creditcore.cashloan.apiImpl.LoanInnerApiImpl] [LoanInnerApiImpl.java:72] [5d6d8057-85e5-4130-bbc8-069093b0ef94] - generatePdf, java.lang.NullPointerException: null");
        eventSender.sendEvent(logEvent);

        logEvent.setAppName("credit-core-cash-loan");
        logEvent.setIp("10.253.22.134");
        logEvent.setLevel("ERROR");
        logEvent.setSource("microLog");
        logEvent.setMessage("2018-05-17 03:01:23,567 [HSFBizProcessor-4-thread-39] ERROR [com.netlink.creditcore.cashloan.apiImpl.LoanInnerApiImpl] [LoanInnerApiImpl.java:72] [5d6d8057-85e5-4130-bbc8-069093b0ef94] - generatePdf, java.lang.NullPointerException: null");
        eventSender.sendEvent(logEvent);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EPAdministrator epAdministrator = this.epServiceProvider.getEPAdministrator();
        epAdministrator.getConfiguration().addEventType(LogEvent.class);
        String epl = "select count(*),ip,appName,source,level,message "
                + "from LogEvent.win:length_batch(1) "
                + "where level = 'ERROR'  and message not like  '%资金还款试算状态为已报案但报案金额为0%'  and message not like  '%批改异常。。。批减金额不能大于承保余额。。。%'  and message not like  '%查询到该保单状态非有效状态%'  and message not like  '%Lock release affected rows=0, userId=%'  and message not like  '%原始借款交易已并发锁定，请稍后重试%'  and message not like  '%查询资金回款计划返回：null%'  and message not like  '%setGeneratePdfDTO fundRepayPlanQuery failed, userId=%'  and message not like  '%Lock release failed, userId=%'  and message not like  '%getPayMode fail, payType not available, payType=%'  and message not like  '%还款计划更新资金平台返回立案号失败%'  and message not like  '%资金平台回款计划冻结异常：回款计划待报案%'  and message not like  '%marketRepayTrialResp is not success%'  and message not like  '%资金平台回款计划冻结异常：失败归集日校验失败，非实时分账冻结失败%'  and message not like  '%批改失败null%'  and message not like  '%repay repayDate not after withdrawDate, productCode=%'  and message not like  '%交易正在处理中%'  and message not like  '%generatePdf, java.lang.NullPointerException%' ";
        EPStatement epStatement = epAdministrator.createEPL(epl);
        epStatement.addListener(new LogEventListener());
        this.eventSender = epServiceProvider.getEPRuntime().getEventSender("LogEvent");
    }

}
