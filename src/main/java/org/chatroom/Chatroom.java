package org.chatroom;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Chatroom {
    private JPanel panel1;
    private JTextField textFieldmessage;
    private JButton buttonSend;
    private JList messageList;
    private String latestMessageTime="";
    private DefaultListModel messageListModel;
    private  static final String SEND_URL = "http://chatroom.codingpython.cn/chatroom/chat?token={0}&room={1}&message={2}";
    private String SEND_MESSSAGE_TO_LOBBY = "http://chatroom.codingpython.cn/chatroom/chat?token={0}&room=Lobby&message={1}";

    private String ROOM_URL = "http://chatroom.codingpython.cn/chatroom/messages?token={0}&room={1}";
    private String room="";
    private String token="";
    private boolean stopFlag=false;
    public Chatroom() {
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textFieldmessage.getText();
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(MessageFormat.format(SEND_URL, token, room,message)).build();
                try(Response response = okHttpClient.newCall(request).execute()){
                    List<Map> latest20Message = getList20Message(room,token);
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
                    messageList.ensureIndexIsVisible(messageListModel.size()-1);
                    textFieldmessage.setText("");
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = textFieldmessage.getText();
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(MessageFormat.format(SEND_MESSSAGE_TO_LOBBY, token, message)).build();
                try(Response response = okHttpClient.newCall(request).execute()){
                    List<Map> latest20Message = getList20Message(room,token);
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
                    messageList.ensureIndexIsVisible(messageListModel.size()-1);
                    textFieldmessage.setText("");
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
    }
    private List<Map> getList20Message(String room,String token){
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(MessageFormat.format(ROOM_URL,token, room)).build();
        List<Map> messages = null;
        try(Response response = httpClient.newCall(request).execute()){
            String  content = response.body().string();
            Gson gson = new Gson();
            Map responseJson = gson.fromJson(content,Map.class);
            messages = (List<Map>)responseJson.get("messages");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }
    public void run(String room, String token) {
        this.room = room;
        this.token = token;
        JFrame frame = new JFrame("Chatroom");
        frame.setContentPane(new Chatroom().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        messageListModel = new DefaultListModel();
        messageList.setModel(messageListModel);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                stopFlag = true;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Thread.currentThread().isInterrupted()

                while (!stopFlag){
                    try{
                        TimeUnit.SECONDS.sleep(1);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    List<Map> latest20Message = getList20Message(room,token);
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
                    messageList.ensureIndexIsVisible(messageListModel.size()-1);
                }
            }
        }).start();
    }


}
