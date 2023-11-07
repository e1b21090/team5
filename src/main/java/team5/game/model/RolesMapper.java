package team5.game.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RolesMapper {
  @Select("SELECT * FROM roles WHERE id = #{id}")
  Roles selectRoles(int id);

  @Update("UPDATE roles SET use = #{true} WHERE id = #{id}")
  void updateUserInfo(int id);

}
