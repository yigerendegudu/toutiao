package com.nowcoder.service;

import com.nowcoder.dao.NewsDAO;
import com.nowcoder.model.News;
import com.nowcoder.utils.ToutiaoUtil;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;

    public List<News> getLatestNews(int userId, int offset, int limit){
        return  newsDAO.selectByUserIdAndOffset(userId, offset, limit);
    }
    public int updateCommentCount(int id, int count){
        return  newsDAO.updateCommentCount(id, count);
    }

    public News getById(int newsId){
        return   newsDAO.getById(newsId);
    }

    public String saveImage(MultipartFile file) throws IOException {
        int doPos = file.getOriginalFilename().lastIndexOf(".");
        if (doPos < 0){
            return  null;
        }
        String fileExt = file.getOriginalFilename().substring(doPos + 1).toLowerCase();
        if (!ToutiaoUtil.isFileAllowed(fileExt)){
            return  null;
        }
        String  fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." +  fileExt;
        Files.copy(file.getInputStream(), new File(ToutiaoUtil.IMAGE_DIR + fileName).toPath());
        return  ToutiaoUtil.TOUTIAO_DOMAIN + "image？name=" + fileName;
    }

    public int addNews(News news){
        newsDAO.addNews(news);
        return  news.getId();
    }
}
