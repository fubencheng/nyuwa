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
package com.netlink.nyuwa.service;

import com.netlink.nyuwa.dto.ImageDTO;
import com.netlink.nyuwa.entity.ImageEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

/**
 * ImageServiceTest.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-04-22 13:30 fubencheng.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ImageServiceTest {

    @Resource
    private ImageService imageService;

    @Test
    public void testFindById() {
        List<ImageEntity> imageList = imageService.findByPredicate((ImageEntity imageEntity) -> imageEntity.getId() == 401950L);
        System.out.println(imageList.size() + "-->" + imageList.get(0));
    }

    @Test
    public void testFindAndMapped() {
        List<ImageDTO> imageList = imageService.findAndMapped((ImageEntity entity, ImageDTO dto) -> BeanUtils.copyProperties(entity, dto));
        System.out.println(imageList.size() + "-->" + imageList.get(0));
    }

    @Test
    public void testFindAndWrite() {
        imageService.findAndWrite((ImageEntity imageEntity) -> imageEntity.getId() >50000, (ImageEntity imageEntity) -> {
            // 每次都会打开文件，写，关闭文件
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("ugirls.txt"))) {
                bufferedWriter.write(imageEntity.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

}
