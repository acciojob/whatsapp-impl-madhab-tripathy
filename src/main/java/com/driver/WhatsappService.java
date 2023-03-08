package com.driver;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
@Service
public class WhatsappService {
    WhatsappRepository whatsappRepository = new WhatsappRepository();
    public String createUser(String name, String mobile) throws Exception {
        try {
            return whatsappRepository.createUser(name,mobile);
        }
        catch (Exception e){
            throw new Exception();
        }
    }
    public Group createGroup(List<User> users){
        return whatsappRepository.createGroup(users);
    }
    public int createMessage(String content){
        return whatsappRepository.createMessage(content);
    }
    public int sendMessage(Message message, User user, Group group) throws Exception{
        try {
            return whatsappRepository.sendMessage(message,user,group);
        }catch (Exception e){
            throw new Exception();
        }
    }
}
