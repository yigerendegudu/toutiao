package com.nowcoder.dao;

import com.nowcoder.model.News;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NewsDAO {
    String TABLE_NAME = "news";
    String INSERT_FIELDS = " title, link, image, like_count, comment_count, created_date, user_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;
    //insert into news () values()
    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{link},#{image},#{likeCount},#{commentCount},#{createdDate},#{userId})"})
    int addNews(News news);

    List<News> selectByUserIdAndOffset(@Param("userId") int userId, @Param("offset") int offset,
                                       @Param("limit") int limit);


    @Select({"select " , SELECT_FIELDS , "from " , TABLE_NAME , " where id =  #{newId}"  })
    News getById(int newsId);
    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id =  #{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);
}
