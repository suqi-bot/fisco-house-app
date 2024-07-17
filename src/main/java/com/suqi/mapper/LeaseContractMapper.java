package com.suqi.mapper;
import com.suqi.pojo.LeaseContract;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * @description pension_account
 * @author z
 * @date
 */
@Mapper
@Repository
public interface LeaseContractMapper {
    @Insert("insert into lease_contract(hourseId, landlord, tenant, monthly_rent, deposit_amount, rent_due_date, lease_start_date, lease_end_date) " +
            "values(#{leaseContract.hourseId}, #{leaseContract.landlord}, #{leaseContract.tenant}, #{leaseContract.rentAmount}, #{leaseContract.depositAmount},#{leaseContract.leaseStartDate},#{leaseContract.leaseStartDate},#{leaseContract.leaseEndDate})")
    public Integer insert(@Param("leaseContract") LeaseContract leaseContract);

    @Select("select id, landlord, tenant, monthly_rent, monthlyRent, deposit_amount, rent_due_date," +
            "lease_start_date, lease_end_date from lease_contract where id = #{id}")
    LeaseContract selectLeaseContractById(Integer id);

    @Select("select count(1) from lease_contract")
    Integer selectCount();
}