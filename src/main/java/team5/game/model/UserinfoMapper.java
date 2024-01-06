package team5.game.model;

import java.util.ArrayList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserinfoMapper {

  @Select("SELECT * FROM userinfo WHERE username = #{username}")
  Userinfo selectUserinfo(String username);

  @Select("SELECT * FROM userinfo")
  ArrayList<Userinfo> selectAlluserinfo();

  @Select("SELECT username FROM userinfo WHERE username != #{username} AND role = '人狼'")
  String selectJinro(String username);

  @Select("SELECT * FROM userinfo WHERE username != #{username}")
  ArrayList<Userinfo> selectTarget(String username);

  @Select("SELECT role FROM userinfo")
  ArrayList<String> selectWolf();

  @Select("SELECT username FROM userinfo WHERE role = '人狼'")
  ArrayList<String> select_Jinro();

  @Select("SELECT username FROM userinfo WHERE role != '人狼'")
  ArrayList<String> selectNotJinro();

  @Select("SELECT username FROM userinfo WHERE role is not null")
  ArrayList<String> selectNotnullUses();

  @Select("SELECT username FROM userinfo WHERE selected = true")
  ArrayList<String> selectTrueSelected();

  @Update("UPDATE userinfo SET selected = true WHERE username = #{username}")
  void updateSelectedTrue(String username);

  @Update("UPDATE userinfo SET selected = false WHERE username = #{username}")
  void updateSelectedFalse(String username);

  @Update("UPDATE userinfo SET role = #{role} WHERE username = #{username}")
  void updateUserInfo(String role, String username);

  @Update("UPDATE userinfo SET role = null WHERE username = #{username}")
  void updateUserInfoNull(String username);

}
