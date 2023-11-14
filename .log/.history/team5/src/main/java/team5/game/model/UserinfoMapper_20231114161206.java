package team5.game.model;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserinfoMapper {

  @Select("SELECT * FROM userinfo WHERE username = #{username}")
  Userinfo selectUserinfo(String username);

  @Select("SELECT username FROM userinfo WHERE username != #{username} AND role = '人狼'")
  String selectJinro(String username);

  @Select("SELECT * FROM userinfo WHERE username != #{username}")
  ArrayList<Userinfo> selectTarget(String username);

  @Select("SELECT username FROM userinfo WHERE role != null")
  ArrayList<String> selectNotnullUses();

  @Update("UPDATE userinfo SET role = #{role} WHERE username = #{username}")
  void updateUserInfo(String role, String username);

}
