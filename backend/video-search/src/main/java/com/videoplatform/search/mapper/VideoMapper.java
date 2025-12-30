package com.videoplatform.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.videoplatform.common.entity.Video;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {
    
    /**
     * 根据关键词搜索视频
     */
    @Select("SELECT * FROM video WHERE (title LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%')) AND status = 1 AND deleted = 0 ORDER BY view_count DESC, like_count DESC, published_at DESC")
    List<Video> searchVideos(@Param("keyword") String keyword);
    
    /**
     * 获取热门搜索关键词
     */
    @Select("SELECT tag.name FROM video_tag tag JOIN video_tag_relation relation ON tag.id = relation.tag_id GROUP BY tag.id, tag.name ORDER BY COUNT(relation.video_id) DESC LIMIT 10")
    List<String> getHotSearchKeywords();
}