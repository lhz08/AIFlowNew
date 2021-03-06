package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.vo.UserInfoVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface UserMapper {

    /**
     * @Author Jin Lingming
     * 根据用户名检索用户
     * @param username 用户名
     * @return
     */
    User selectUserByName(@Param("username") String username);


    /**
     * @Author Jin Lingming
     * 插入一条新的用户记录
     * @param user 用户
     */
    void insertUser(User user);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    int insert(User record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    User selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    List<User> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user
     *
     * @mbg.generated Sun Aug 30 16:18:08 CST 2020
     */
    int updateByPrimaryKey(User record);

    /**
     * 获取所有用户
     * @param
     * @return List<User>
     */
    List<UserInfoVO> getAllUsers(Integer userId);

    /**
     * 根据用户名和密码检索用户
     * @param username
     * @param password
     * @return
     */
    User selectUserByNameAndPassword(@Param("username") String username, @Param("password")  String password);

    int update(Map<String, Object> map);

}