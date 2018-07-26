package com.nowcoder;

import com.nowcoder.dao.*;
import com.nowcoder.model.*;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class InitDataBaseTests {
	@Autowired
	UserDAO userDAO;
	@Autowired
	NewsDAO newsDAO;

	@Autowired
	LoginTicketDAO loginTicketDAO;

	@Autowired
	CommentDAO commentDAO;

	@Autowired
	MessageDAO messageDAO;
	@Test
	public void initData() {
		Random random = new Random();
		for (int i = 0; i < 10; i++) {
			User user = new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
			user.setName(String.format("USER%d", i));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);

			News news = new News();
			news.setCommentCount(i);
			Date date = new Date();
			date.setTime(date.getTime() + 1000*3600*5*i);
			news.setCreatedDate(date);
			news.setImage(String.format("http://images.nowcoder.com/head/%dm.png", random.nextInt(1000)));
			news.setLikeCount(i+1);
			news.setUserId(i+1);
			news.setTitle(String.format("TITLE{%d}", i));
			news.setLink(String.format("http://www.nowcoder.com/%d.html", i));
			newsDAO.addNews(news);

			user.setPassword("newpassword");
			userDAO.updatePassword(user);


			LoginTicket loginTicket  = new LoginTicket();
			loginTicket.setStatus(0);
			loginTicket.setUserId(i + 1);
			loginTicket.setExpired(date);
			loginTicket.setTicket(String .format("TICKET%d",i));
			loginTicketDAO.addTicket(loginTicket);

			//Assert.assertEquals("mypass",userDAO.selectById(1).getPassword());

			Comment comment = new Comment();
			comment.setContent(i + "xxx");
			comment.setCreatedDate(new Date());
			comment.setEntityId(i);
			comment.setEntityType(1);
			comment.setId(i);
			comment.setUserId(i + 1);
			comment.setStatus(1);
			commentDAO.addComment(comment);

			Message message = new Message();
			message.setContent("111");
			message.setCreatedDate(new Date());

		}
	}

}
