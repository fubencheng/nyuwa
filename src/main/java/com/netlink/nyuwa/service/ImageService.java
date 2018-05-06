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

import com.netlink.nyuwa.core.Mapper;
import com.netlink.nyuwa.core.TxtWriter;
import com.netlink.nyuwa.dao.ImageRepository;
import com.netlink.nyuwa.dto.ImageDTO;
import com.netlink.nyuwa.entity.ImageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * ImageService.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-04-22 13:27 fubencheng.
 */
@Slf4j
@Service
public class ImageService {

    private ImageRepository imageDao;

    @Autowired
    public ImageService(ImageRepository imageDao){
        this.imageDao = imageDao;
    }

    public List<ImageEntity> findByPredicate(Predicate<ImageEntity> imagePredicate){
        // 数据量大会耗尽内存
        List<ImageEntity> imageList = imageDao.findAll();
        List<ImageEntity> result = new ArrayList<>();
        for (ImageEntity imageEntity : imageList) {
            if (imagePredicate.test(imageEntity)){
                result.add(imageEntity);
            }
        }
        return result;
    }

    public List<ImageDTO> findAndMapped(Mapper<ImageEntity, ImageDTO> mapper){
        List<ImageEntity> imageList = imageDao.findAll();
        List<ImageDTO> result = new ArrayList<>();
        for (ImageEntity imageEntity : imageList){
            ImageDTO imageDTO = new ImageDTO();
            mapper.map(imageEntity, imageDTO);
            result.add(imageDTO);
        }
        return result;
    }

    public void findAndWrite(Predicate<ImageEntity> predicate, TxtWriter<ImageEntity> writer){
        List<ImageEntity> imageList = imageDao.findAll();
        for (ImageEntity imageEntity : imageList){
            if (predicate.test(imageEntity)){
                writer.write(imageEntity);
            }
        }
    }
}
