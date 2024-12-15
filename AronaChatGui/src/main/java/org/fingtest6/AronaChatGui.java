package org.fingtest6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONArray;

public class AronaChatGui {

    public static void main(String[] args) {
        // 创建 JFrame 实例，作为主窗口
        JFrame frame = new JFrame("AronaChat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        // 创建一个面板，用于放置输入框、按钮
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Token:"));
        JTextField tokenField = new JTextField(20);
        tokenField.setText(""); // 默认值，实际使用时应替换为您的token
        inputPanel.add(tokenField);

        inputPanel.add(new JLabel("消息:"));
        JTextField messageField = new JTextField(20);
        inputPanel.add(messageField);

        JButton submitButton = new JButton("发送");
        inputPanel.add(submitButton);

        // 创建一个文本区域，用于显示日志
        JTextArea logArea = new JTextArea(10, 30);
        logArea.setEditable(false); // 设置为不可编辑
        JScrollPane scrollPane = new JScrollPane(logArea);

        // 将输入面板和日志区域添加到主窗口
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        // 为按钮添加事件监听器
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String token = tokenField.getText();
                String message = messageField.getText();
                sendPostRequest(token, message, logArea);
                messageField.setText(""); // 清空输入框
            }
        });

        // 显示窗口
        frame.setVisible(true);
    }

    private static void sendPostRequest(String token, String message, JTextArea logArea) {
        String url = "https://open.hunyuan.tencent.com/openapi/v1/agent/chat/completions";
        HttpURLConnection connection = null;
        try {
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();

            // 添加请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-Source", "openapi");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // 发送 POST 请求必须设置如下两行
            connection.setDoOutput(true);

            // 定义请求体
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("assistant_id", "hQkO6Ugq4NQN");
            jsonBody.put("user_id", "username");
            jsonBody.put("stream", false);

            JSONArray messages = new JSONArray();
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            JSONArray messageContent = new JSONArray();
            JSONObject textContent = new JSONObject();
            textContent.put("type", "text");
            textContent.put("text", message);
            messageContent.put(textContent);
            messageObj.put("content", messageContent);
            messages.put(messageObj);
            jsonBody.put("messages", messages);

            // 将请求体转换为 JSON 格式的字符串
            OutputStream os = connection.getOutputStream();
            os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
            os.close();

            // 发送请求并获取响应
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 解析响应内容
                JSONObject jsonResponse = new JSONObject(response.toString());

                // 检查 "choices" 键是否存在
                if (jsonResponse.has("choices") && !jsonResponse.getJSONArray("choices").isEmpty()) {
                    JSONArray choicesArray = jsonResponse.getJSONArray("choices");
                    JSONObject choice = choicesArray.getJSONObject(0);

                    // 检查 "message" 键是否存在
                    if (choice.has("message") && choice.getJSONObject("message").has("content")) {
                        String contentValue = choice.getJSONObject("message").getString("content");
                        logArea.append("阿罗娜: " + contentValue + "\n");
                    }
                }
            } else {
                logArea.append("POST request not worked.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}