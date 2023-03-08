package com.driver;

import java.sql.Timestamp;
import java.util.*;

import ch.qos.logback.classic.Logger;
import org.apache.tomcat.util.digester.ArrayStack;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.swing.plaf.PanelUI;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashMap<String,User> userHashMap;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userHashMap = new HashMap<String,User>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public Logger logger = (Logger) LoggerFactory.getLogger(WhatsappRepository.class);
    // 1. create user with key - number
    public String createUser(String name ,String mobile) throws Exception {
        if(userHashMap.containsKey(mobile)){
            throw new Exception("User already exists");
        }
        else {
            userHashMap.put(mobile,new User(name,mobile));
        }
        return "SUCCESS";
    }
    // 2. Create group
    public Group createGroup(List<User> users){
        // 1. If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
        Group group = null;
        if(users.size() == 2){
            int last_index = users.size()-1;
            String groupName = users.get(last_index).getName();
            group = new Group(groupName,users.size());
        }
        // 2. If there are 2+ users, the name of group should be "Group count".
        // For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
        else if(users.size() > 2){
            this.customGroupCount += 1;
            String groupName = "Group "+this.customGroupCount;
            group = new Group(groupName,users.size());
        }
        groupUserMap.put(group,users);
        return group;
    }
    public int createMessage(String content){
        this.messageId += 1;
        Message message = new Message(content);
        message.setId(this.messageId);
        return this.messageId;
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception {
        // 1. Throw "Group does not exist" if the mentioned group does not exist
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        // 2. Throw "You are not allowed to send message" if the sender is not a member of the group
        List<User> userList = groupUserMap.get(group);
        for(User user : userList){
            if(sender != user){
                throw new Exception("You are not allowed to send message");
            }
        }
        // 3. Send message, If the message is sent successfully, return the final number of messages in that group.
        List<Message> messageList = groupMessageMap.getOrDefault(group,new ArrayList<>());
        messageList.add(message);
        groupMessageMap.put(group,messageList);
        senderMap.put(message,sender);
        return messageList.size();
    }
}
