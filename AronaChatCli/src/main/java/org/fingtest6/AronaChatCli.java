package org.fingtest6;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import org.json.JSONObject;
import org.json.JSONArray;


public class AronaChatCli {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        // 定义 API 的 URL
        String url = "https://open.hunyuan.tencent.com/openapi/v1/agent/chat/completions";

        // 定义请求头
        String token = getTokenFromConfig();

        // 创建 Scanner 对象用于获取用户输入，指定UTF-8编码
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());

        while (true) {
            // 获取用户输入的消息
            String userMessage = getUserMessage(scanner);

            HttpURLConnection connection = null;
            try {
                URL obj = new URL(url);
                connection = (HttpURLConnection) obj.openConnection();

                // 添加请求头
                connection.setRequestMethod("POST");
                connection.setRequestProperty("X-Source", "openapi");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Authorization", "Bearer " + token);

                // 发送 POST 请求必须设置如下两行
                connection.setDoOutput(true);

                // 定义请求体
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("assistant_id", "hQkO6Ugq4NQN");
                jsonBody.put("user_id", "username");
                jsonBody.put("stream", false);

                JSONArray messages = new JSONArray();
                JSONObject message = new JSONObject();
                message.put("role", "user");
                JSONArray messageContent = new JSONArray();
                JSONObject textContent = new JSONObject();
                textContent.put("type", "text");
                // 确保用户消息使用UTF-8编码
                textContent.put("text", new String(userMessage.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
                messageContent.put(textContent);
                message.put("content", messageContent);
                messages.put(message);
                jsonBody.put("messages", messages);

                // 将请求体转换为 JSON 格式的字符串
                String requestBody = jsonBody.toString(4); // 使用4个空格缩进美化JSON输出

                // 根据配置决定是否写入 Chat.log
                boolean chatLogEnabled = getChatLogConfig();
                if (chatLogEnabled) {
                    writeLog("Chat.log", "POST Request Body:\n" + requestBody);
                }

                // 将请求体写入输出流
                OutputStream os = connection.getOutputStream();
                os.write(requestBody.getBytes(StandardCharsets.UTF_8));
                os.close();

                // 发送请求并获取响应
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
                    for (int i = 0; i < choicesArray.length(); i++) {
                        JSONObject choice = choicesArray.getJSONObject(i);
                        // 检查 "message" 键是否存在
                        if (choice.has("message") && choice.getJSONObject("message").has("content")) {
                            // 获取消息内容
                            String contentValue = choice.getJSONObject("message").getString("content");
                            // 检查消息角色是否为助理
                            if (choice.getJSONObject("message").getString("role").equals("assistant")) {
                                System.out.println("阿罗娜: " + contentValue);
                            }
                        }
                    }
                } else {
                    System.out.println("'choices' key not found in the JSON response.");
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

    private static String getTokenFromConfig() {
        String configPath = "AronaChatConfig.json";
        try {
            if (Files.exists(Paths.get(configPath))) {
                String content = new String(Files.readAllBytes(Paths.get(configPath)), StandardCharsets.UTF_8);
                JSONObject config = new JSONObject(content);
                return config.getString("token");
            } else {
                Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8.name());
                System.out.print("输入你的腾讯元器Token: ");
                String token = scanner.nextLine();
                JSONObject config = new JSONObject();
                config.put("token", token);
                config.put("Chatlog", "false"); // 默认设置为false
                try (FileWriter file = new FileWriter(configPath)) {
                    file.write(config.toString(4));
                }
                return token;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean getChatLogConfig() {
        String configPath = "AronaChatConfig.json";
        try {
            String content = new String(Files.readAllBytes(Paths.get(configPath)), StandardCharsets.UTF_8);
            JSONObject config = new JSONObject(content);
            return config.getBoolean("Chatlog");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static String getUserMessage(Scanner scanner) {
        System.out.print("发送的消息: ");
        return scanner.nextLine();
    }

    private static void writeLog(String fileName, String message) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_8, Files.exists(Paths.get(fileName)) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}