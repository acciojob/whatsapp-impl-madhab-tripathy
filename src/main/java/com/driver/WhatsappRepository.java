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
        userHashMap.put(mobile,new User(name,mobile));
        return "SUCCESS";
    }
    // 2. Create group
    public Group createGroup(List<User> users){
        // 1. If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
        Group group = null;
        if(users.size() == 2){
            String groupName = users.get(1).getName(); // get last user in the list
            group = new Group(groupName,users.size());
        }
        // 2. If there are 2+ users, the name of group should be "Group count".
        // For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
        else if(users.size() > 2){
            this.customGroupCount += 1;
            String groupName = "Group "+this.customGroupCount;
            group = new Group(groupName,users.size()); // group name with number of participant
        }
        adminMap.put(group,users.get(0)); // store group admin
        groupUserMap.put(group,users);
        return group;
    }
    public int createMessage(String content){
        this.messageId += 1;
        Message message = new Message(this.messageId,content);
        return this.messageId;
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception {
        // 1. Throw "Group does not exist" if the mentioned group does not exist
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        // 2. Throw "You are not allowed to send message" if the sender is not a member of the group
        List<User> userList = groupUserMap.get(group);
        if(!userList.contains(sender)){
            throw new Exception("You are not allowed to send message");
        }
        // 3. Send message, If the message is sent successfully, return the final number of messages in that group.
        List<Message> messageList = groupMessageMap.getOrDefault(group,new ArrayList<>());
        messageList.add(message);
        groupMessageMap.put(group,messageList);
        senderMap.put(message,sender);
        return messageList.size();
    }
    public void changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        User admin = adminMap.get(group);
        if(!approver.equals(admin)){
            throw new Exception("Approver does not have rights");
        }
        //Throw "User is not a participant" if the user is not a part of the group
        List<User> participant = groupUserMap.get(group);
        if(!participant.contains(user)){
            throw new Exception("User is not a participant");
        }
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin
        // and the admin rights are transferred from approver to user.
        adminMap.put(group,user);
    }
    public int removeUser(User user) throws Exception{
        // This is a bonus problem and does not contains any marks
        // A user belongs to exactly one group
        // If user is not found in any group, throw "User not found" exception
        for(List<User> list : groupUserMap.values()){
            if(!list.contains(user)){
                throw new Exception("User not found");
            }
        }
        // If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        for (User admin : adminMap.values()){
            if(user.equals(admin)){
                throw new Exception("Cannot remove admin");
            }
        }
        // If user is not the admin, remove the user from the group, remove all its messages from all the databases,
        // and update relevant attributes accordingly.
        Group userInGroup = null;
        int afterRemoveUsersInGroup = 0;
        int messageCountInGroup = 0;
        boolean isUserPresentInGroup = false;
        // update number of users in group
        for (Group group : groupUserMap.keySet()){
            List<User> userList = groupUserMap.get(group);
            if(userList.contains(user)){
                userInGroup = group;
                userList.remove(user);
                group.setNumberOfParticipants(userList.size()-1);
                groupUserMap.put(group,userList);
                afterRemoveUsersInGroup = group.getNumberOfParticipants();
                isUserPresentInGroup = true;
            }
        }
        //  updated number of messages in group
        if(isUserPresentInGroup){
            List<Message> messages = groupMessageMap.get(userInGroup);
            for (Message message : senderMap.keySet()){
                User currentUser = senderMap.get(message);
                if(currentUser.equals(user)){
                    messages.remove(message); // remove message from current messages list
                    groupMessageMap.put(userInGroup,messages);
                    senderMap.remove(message);
                    messageCountInGroup = messages.size();
                }
            }
        }
        // the updated number of overall messages
        return afterRemoveUsersInGroup + messageCountInGroup;
    }
}
