package com.suqi.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaseContract {
    private Long hourseId;

    private String landlord;
    private String tenant;
    private Long rentAmount;
    private Long depositAmount;
    private Long rentDueDate;
    private Long leaseStartDate;
    private Long leaseEndDate;
    private Long agreementId;
    private Long sign;

    private Long paidAmount;
    private Long spendedToken;
    private Long alreadyAmount;


}
