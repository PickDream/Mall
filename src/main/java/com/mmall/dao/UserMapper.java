package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String userName);

    User selectLogin(@Param("username")String username
            ,@Param("password")String password);

    int checkEmail(String email);

    String selectQuestionByName(String username);

    int checkAnswer(@Param("userName")String username,
                    @Param("question")String question,
                    @Param("answer")String answer);

    int updatePasswordByUsername(@Param("username") String username,
                                 @Param("password") String password);
    int checkUserNameAndPassword(@Param("username")String username,
                                 @Param("oldPassword")String password);

    int checkEmailNotForUserId(@Param("email")String email,
                               @Param("id")int id);

    List<User> selectAllUser();
}