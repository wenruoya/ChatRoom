package org.chatroom;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Lobby {
    private JPanel panel1;
    private JTextField messageField;
    private JButton SendButton;
    private JButton JavaButton;
    private JButton FoodButton;
    private JButton MasterButton;
    private JList MessageList;

    private DefaultListModel messageListModel;
    private String token;
    private String latestMessageTime;

    private String LOBBY_URL = "http://chatroom.codingpython.cn/chatroom/messages?token={0}&room=Lobby";
    private String SEND_MESSSAGE_TO_LOBBY = "http://chatroom.codingpython.cn/chatroom/chat?token={0}&room=Lobby&message={1}";

    public Lobby() {
        SendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(MessageFormat.format(SEND_MESSSAGE_TO_LOBBY, token, message)).build();
                try(Response response = okHttpClient.newCall(request).execute()){
                    List<Map> latest20Message = getList20Message();
                    String tempTime = null;
                    for (Map item:latest20Message){
                        if(((String)item.get("created_at")).compareTo(latestMessageTime) > 0){
                            messageListModel.addElement(MessageFormat.format("{0}({1}):{2}",item.get("name"),item.get("created_at"),item.get("message")));
                            tempTime = ((String)item.get("created_at"));
                        }
                    }
                    if(tempTime!=null){
                        latestMessageTime = tempTime;
                    }
                    MessageList.ensureIndexIsVisible(messageListModel.size()-1);
                    messageField.setText("");
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        JavaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Chatroom().run("Java",token);
            }
        });
        FoodButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Chatroom().run("台院美食",token);
            }
        });
        MasterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Chatroom().run("考研",token);
            }
        });
    }

    private List<Map> getList20Message(){
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(MessageFormat.format(LOBBY_URL, token)).build();
        try(Response response = httpClient.newCall(request).execute()){
            String  content = response.body().string();
            Gson gson = new Gson();
            Map responseJson = gson.fromJson(content,Map.class);
            List<Map> messages = (List<Map>)responseJson.get("messages");
            for (Map message:messages){
                //绑定数据
                return messages;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  void run(String token) {
        this.token = token;
        JFrame frame = new JFrame("Lobby");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        messageListModel = new DefaultListModel();
        MessageList.setModel(messageListModel);

        refreshMessageList(token);

        new Thread(new Runnable() {
            @Override
            public void run() {
                //Thread.currentThread().isInterrupted()
                while (true){
                    try{
                        TimeUnit.SECONDS.sleep(1);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                        List<Map> latest20Message = getList20Message();
                        String tempTime = null;
                        for (Map item:latest20Message){
                            if(((String)item.get("created_at")).compareTo(latestMessageTime) > 0){
                                messageListModel.addElement(MessageFormat.format("{0}({1}):{2}",item.get("name"),item.get("created_at"),item.get("message")));
                                tempTime = ((String)item.get("created_at"));
                            }
                        }
                        if(tempTime!=null){
                            latestMessageTime = tempTime;
                        }
                        MessageList.ensureIndexIsVisible(messageListModel.size()-1);
                }
            }
        }).start();
    }

    private void refreshMessageList(String token) {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(MessageFormat.format(LOBBY_URL, token)).build();
        try(Response response = httpClient.newCall(request).execute()){
            String  content = response.body().string();
            Gson gson = new Gson();
            Map responseJson = gson.fromJson(content,Map.class);
            List<Map> messages = (List<Map>)responseJson.get("messages");
            for (Map message:messages){
                //绑定数据
                messageListModel.addElement(MessageFormat.format("{0}({1}):{2}",message.get("created_at"),message.get("message")));
                latestMessageTime = (String)message.get("created_at");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

