package com.nowcoder.controller;


import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.QiniuService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class NewsController {
    public   static  final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    NewsService newsService;

    @Autowired
    UserService userService;

    @Autowired
    QiniuService qiniuService;

    @Autowired
    CommentService commentService;

    @RequestMapping(path = {"/user/addNews/"},method = {RequestMethod.POST})
    @ResponseBody
    public  String addNews(@RequestParam("image") String  image,
                           @RequestParam("title") String  title,
                           @RequestParam("link") String  link){
        try{
            News news = new News();
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setImage(image);
            news.setLink(link);
            if(hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            }else {
                news.setUserId(3);
            }
            newsService.addNews(news);
            return  ToutiaoUtil.getJSONString(0);
        }catch (Exception e){
            logger.error("添加咨询失败"  + e.getMessage());
            return  ToutiaoUtil.getJSONString(1,"添加失败");
        }

    }
    @RequestMapping(path = {"/news/{newsId}"},method = {RequestMethod.GET})
    public String getNews(@PathVariable("newsId") int newsId, Model model){
        News news = newsService.getById(newsId);
        try {
            if (news != null){
                List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<ViewObject>();
                for (Comment comment : comments){
                    ViewObject vo = new ViewObject();
                    vo.set("comment", comment);
                    vo.set("user", userService.getUser(comment.getUserId()));
                    commentVOs.add(vo);
                }
                model.addAttribute("comments", commentVOs);

            }
            model.addAttribute("news",news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
            return  "detail";
        }catch (Exception e){
          logger.error("获取咨询失败" + e.getMessage());
        }
        return  "detail";
    }
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int  newsId,@RequestParam("content") String content){
        try {
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setEntityId(newsId);
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setStatus(0);
            commentService.addComment(comment);

            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
        }catch (Exception e){
            logger.error("增加评论失败."+ e.getMessage());
        }
        return "redirect:/news/" + String.valueOf(newsId);
    }
    //{"/uploadImage/"}
    @RequestMapping(path = {"/uploadImage/"},method = {RequestMethod.POST})
    @ResponseBody
    public  String  uploadImage(@RequestParam("file") MultipartFile file){

        try {
            String  fileUrl = qiniuService.saveImage(file);

            if (fileUrl == null){
                return  ToutiaoUtil.getJSONString(1, "上传失败");
            }
            return  ToutiaoUtil.getJSONString(0, fileUrl);

        }catch (Exception e){
            logger.error("上传错误" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传出错");
        }

    }
    @RequestMapping(value = {"/image"}, method = {RequestMethod.GET})
    @ResponseBody
    public  void  getImage(@RequestParam("name") String  imagename,
                             HttpServletResponse response){

        try {


            response.setContentType("image/type");

            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imagename)),response.getOutputStream());
        }catch (Exception e){
            logger.error("读取图片错误",e.getMessage());
        }
    }

}
