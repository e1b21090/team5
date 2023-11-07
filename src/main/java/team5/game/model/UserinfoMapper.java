package team5.game.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserinfoMapper {
  @Update("UPDATE userinfo SET role = #{role} WHERE username = #{username}")
  void updateUserInfo(String role, String username);

}
