package com.bdilab.aiflow.mapper;

import com.bdilab.aiflow.model.User;
import com.bdilab.aiflow.model.application.Application;
import com.bdilab.aiflow.model.menu.TransferAmenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UUMSUserMapper {

    List<String> selectByUsernameList(@Param("list") List<String> list);

    int updateByUsername(User user);

    Long insert(User user);

    @Select({"select id from user where user_name = #{username} and status in(0,1)"})
    Long getIdByName(@Param("username") String username);

    void deleteUserApplicationByUserId(@Param("userId") Long userId);

    Long getAppIdByCodingId(@Param("codingId") String codingId);

    void insertUserApplication(@Param("appId") Long appId, @Param("userId") Long userId);
    List<Application> getUumsUserApplication(@Param("userId") Long userId);


    List<String> selectByCodingIdList(@Param("list") List<String> list);


    void updateApplicationByCodingId(Application application);

    void insertApplication(Application application);

    List<TransferAmenu> selectParentMenus();

    /**
     * @param code
     * @return
     */
    List<TransferAmenu> selectMenusByParentcode(@Param("code") String code);
}
