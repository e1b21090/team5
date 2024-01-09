package team5.game.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.ArrayList;

@Mapper
public interface GamelogMapper {
  @Insert("INSERT INTO gamelog (name1, role1, name2, role2, name3, role3, name4, role4, result) VALUES (#{name1}, #{role1}, #{name2}, #{role2}, #{name3}, #{role3}, #{name4}, #{role4}, #{result})")
  void insertGamelog(Gamelog gamelog);

  @Select("SELECT * FROM gamelog ORDER BY id DESC LIMIT 5")
  ArrayList<Gamelog> selectGamelog();

}
