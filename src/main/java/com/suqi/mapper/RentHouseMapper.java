package com.suqi.mapper;


import com.suqi.pojo.RentHouse;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RentHouseMapper {
    //查询所有
    @Select("select * from rent_house")
    List<RentHouse> getAll();

    @Select("select * from rent_house where owner_address=#{ownerAddress} or tenant_address=#{ownerAddress}")
    List<RentHouse> getRentHouseByAddress(String ownerAddress);

    @Insert("insert into rent_house(id,house_id,owner_address,tenant_address,start_time,update_time)" +
            "values(#{id},#{houseId},#{ownerAddress},#{tenantAddress},now(),now())")
    Integer set(RentHouse house);

    @Update("update rent_house set house_id=#{houseId},owner_address=#{ownerAddress}," +
            "tenant_address=#{tenantAddress},update_time=now()" +
            "where id=#{id}")
    Integer update(RentHouse house);

    @Delete("delete from rent_house where id=#{id} and house_id=#{houseId}")
    Integer delete(Integer id, Integer houseId);

}
