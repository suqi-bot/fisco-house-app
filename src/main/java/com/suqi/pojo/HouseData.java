package com.suqi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseData {
    private Integer hourseId;
    private String description;
    private Integer price;
    private String appoveAddress;

}
