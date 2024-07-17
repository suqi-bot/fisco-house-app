package com.suqi.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentHouse {
    private Integer id;
    private Integer houseId;
    private String ownerAddress;
    private String tenantAddress;
}
