package com.suqi.mapper;

import com.suqi.pojo.House;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HouseMapper {
    //查询所有
    @Select("select * from house")
    List<House> getAll();

    @Select("select * from house where id = #{id}")
    House getHouseById(Integer id);

    @Insert("insert into house(id,house_id,house_name,house_context,state,house_img,house_price,owner_address,owner_name)" +
            "values(#{id},#{houseId},#{houseName},#{houseContext},#{state},#{houseImg},#{housePrice},#{ownerAddress},#{ownerName})")
    Integer set(House house);
    @Update("update house set house_name=#{houseName},house_context=#{houseContext},state=#{state},house_img=#{houseImg}," +
            "house_price=#{housePrice} where id=#{id} and owner_address=#{ownerAddress}")
    Integer update(House house);
    @Delete("delete from house where id=#{id} and owner_address=#{ownerAddress}")
    Integer delete(Integer houseId ,String ownerAddress);
    @Select("select * from house where owner_address = #{ownerAddress}")
    List<House> getHouseByUserAddress(String ownerAddress);

    @Select("select * from house where id=#{houseId} and owner_address = #{ownerAddress}")
    List<House> getRentHouse(Integer houseId, String ownerAddress);
}
