package team5.game.model;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GamelogMapper {
    @Insert("INSERT INTO gamelog (name1, role1, name2, role2, name3, role3, name4, role4, result) VALUES (#{name1}, #{role1}, #{name2}, #{role2}, #{name3}, #{role3}, #{name4}, #{role4}, #{result})")
    void insertGamelog(Gamelog gamelog);
}
