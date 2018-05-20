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
import com.netlink.nyuwa.dao.ImageRepository;
import com.netlink.nyuwa.entity.ImageEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * ImageEventSource.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-05-06 14:06 fubencheng.
 */
@Component
public class ImageEventSource implements InitializingBean {

    private EventSender eventSender;

    @Resource
    private EPServiceProvider epServiceProvider;

    @Resource
    private ImageRepository imageDao;

    public void sendImageEvent(){
        List<ImageEntity> imageEntityList = imageDao.findAll();
        for (ImageEntity imageEntity : imageEntityList){
            ImageEvent imageEvent = new ImageEvent();
            BeanUtils.copyProperties(imageEntity, imageEvent);
            eventSender.sendEvent(imageEvent);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EPAdministrator epAdministrator = epServiceProvider.getEPAdministrator();
        epAdministrator.getConfiguration().addEventType(ImageEvent.class);
        String epl = "select title, imageUrl, keywords "
                + "from ImageEvent(title like '%头条女神%' and title not like '%爆乳%').win:length(3) "
                + "where title like '%性感女神%' ";
        EPStatement epStatement = epAdministrator.createEPL(epl);
        epStatement.addListener(new ImageEventListener());
        this.eventSender = epServiceProvider.getEPRuntime().getEventSender("ImageEvent");
    }
}
