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
package com.netlink.nyuwa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * ImageEntity.
 *
 * @author fubencheng.
 * @version 0.0.1 2018-04-22 13:14 fubencheng.
 */
@Entity
@Table(name = "t_image")
@Setter
@Getter
@ToString
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Length(max = 64)
    private String title;

    @NotNull
    @Length(max = 128)
    private String sourceUrl;

    @NotNull
    @Length(max = 32)
    private String sourceUrlHash;

    @NotNull
    @Length(max = 128)
    private String imageUrl;

    @NotNull
    @Length(max = 32)
    private String imageUrlHash;

    @Length(max = 64)
    private String keywords;

    @Length(max = 128)
    private String description;

    @NotNull
    private Date createTime;
}