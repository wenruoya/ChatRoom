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
import java.util.Map;

public class Login {
    private JPanel panel1;
    private JTextField textFieldmobile;
    private JTextField textFieldName;
    private JButton ButtonLogin;

    private JFrame frame;
    private static final String LOGIN_URL = "http://chatroom.codingpython.cn/login?mobile={0}&name={1}";
    public Login() {
        ButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String mobile = textFieldmobile.getText();
                String name = textFieldName.getText();
                if(mobile == null || mobile.trim().equals("")){
                    JOptionPane.showMessageDialog(null, "手机号码不能为空");
                    return;
                }
                if(name == null || name.trim().equals("")){
                    JOptionPane.showMessageDialog(null, "姓名不能为空");
                    return;
                }
                OkHttpClient httpClient = new OkHttpClient();
                Request request = new Request.Builder().url(MessageFormat.format(LOGIN_URL, mobile, name)).build();
                try (Response response = httpClient.newCall(request).execute()) {
                    String content = response.body().string();
                    System.out.println(content);

                    Gson gson = new Gson();
                    Map<String, Object> responseJson = gson.fromJson(content, Map.class);
                    Object detail = responseJson.get("detail");
                    if(detail !=null){
                        JOptionPane.showMessageDialog(null,detail);
                        return;
                    }
                    Object token = responseJson.get("token");
                    if(token != null){
                        frame.dispose();  //消除窗口

                        String tokenString = token.toString();
                        // 跳转
                        Lobby lobby = new Lobby();
                        lobby.run(tokenString);

                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
    }
    //测试账户 15657682222  Mr金
    public void run(){
        frame = new JFrame("Login");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Login().run();
    }

}
