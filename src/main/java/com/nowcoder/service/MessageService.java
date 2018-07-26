package com.nowcoder.service;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;

    public int addMessage(Message message){
        return  messageDAO.addMessage(message);
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit){
        return  messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDAO.getConversationList(userId, offset, limit);

    }


    public  int getConversationUnread(int userId, String conversaiotnId){
        return  messageDAO.getConversationUnReadCount(userId, conversaiotnId);
    }
    //select * count(id) as cnt from (select * from table where fromid = or toid = order by desc ) oder
}
