package com.suqi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class House {
    private Integer id;
    private String houseName;
    private String houseContext;
    private String state;
    private String houseImg;
    private Integer housePrice;
    private String ownerAddress;
    private String ownerName;
}
