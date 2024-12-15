package org.fingtest6;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.JSONArray;

public class MessageHandle {
    public static String execute(String token, String assistantId, String message) {
        String url = "https://open.hunyuan.tencent.com/openapi/v1/agent/chat/completions";
        HttpURLConnection connection = null;
        try {
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("X-Source", "openapi");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + token);
            connection.setDoOutput(true);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("assistant_id", assistantId);
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

            OutputStream os = connection.getOutputStream();
            os.write(jsonBody.toString().getBytes(StandardCharsets.UTF_8));
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("choices") && !jsonResponse.getJSONArray("choices").isEmpty()) {
                return jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                return "No response received.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "发送请求失败了老师,请检测配置文件是否正确config/aronachat-common.toml";
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}