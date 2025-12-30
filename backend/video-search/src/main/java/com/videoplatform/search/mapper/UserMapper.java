package com.videoplatform.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.videoplatform.common.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据关键词搜索用户
     */
    @Select("SELECT * FROM user WHERE (username LIKE CONCAT('%',#{keyword},'%') OR nickname LIKE CONCAT('%',#{keyword},'%')) AND status = 1 AND deleted = 0 ORDER BY follower_count DESC, video_count DESC")
    List<User> searchUsers(@Param("keyword") String keyword);
}