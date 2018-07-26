package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.utils.ToutiaoUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @RequestMapping(path =  {"/msg/list"}, method = RequestMethod.GET)
    public String conversationDetail(Model model){
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0,10);
            for (Message msg : conversationList){
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int  targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user",user);
                vo.set("headUrl", user.getHeadUrl());
                vo.set("unread", messageService.getConversationUnread(localUserId, msg.getConversationId()));
                 conversations.add(vo);
            }
            model.addAttribute("conversations",conversations);
            return  "letter";
        }catch (Exception e){
            logger.error("站内信失败" + e.getMessage());
        }
        return  "letter";
    }

    @RequestMapping(path =  {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public  String addMessage(@RequestParam("fromId") int fromId,
                              @RequestParam("toId") int toId,
                              @RequestParam("content") String content) {
        try {
            Message msg = new Message();
            msg.setContent(content);
            msg.setFromId(fromId);
            msg.setCreatedDate(new Date());
            msg.setToId(toId);
            msg.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(msg);
            return ToutiaoUtil.getJSONString(msg.getId());
        } catch (Exception e) {
            logger.error("确认过眼神就是增加消息失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发送消息失败");
        }


    }
    @RequestMapping(path = {"/msg/detail"},method = RequestMethod.GET)
    public String conversationDetail(Model model, @Param("conversationId") String conversationId){
        try {

            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0,10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message message : conversationList){
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                User user = userService.getUser(message.getFromId());
                if (user == null){
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());

                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        }catch (Exception e){
            logger.error("detail failed" + e.getMessage());
        }
        return  "letterDetail";
    }
}
