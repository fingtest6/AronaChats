package org.fingtest6;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class AronaCommand implements CommandExecutor {

    private final aronachat plugin;

    // 构造函数，用于接收主插件实例
    public AronaCommand(aronachat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("使用方法: /arona [消息]");
            return true;
        }

        String message = String.join(" ", args); // 将所有参数组合成一个字符串作为消息内容

        // 从配置文件中获取前缀
        String prefix = plugin.getConfig().getString("prefix", "默认前缀");

        // 广播第一条消息到所有在线玩家
        String firstMessage;
        if (sender instanceof Player) {
            Player player = (Player) sender;
            firstMessage = "[AronaChat]" + player.getName() + ": \"" + message + "\"";
            player.getServer().broadcastMessage(firstMessage);
        } else {
            // 如果不是玩家（例如控制台），则输出不同消息
            firstMessage = "[Console]/arona: \"" + message + "\"";
            sender.sendMessage(firstMessage);
        }

        // 从配置文件中获取 assistant_id 和 Token
        String assistantId = plugin.getConfig().getString("assistant_id", "");
        String token = plugin.getConfig().getString("Token", "");

        // 使用异步任务来处理 HTTP 请求
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            // 调用 MessageHandle 获取服务器响应
            String serverResponse = MessageHandle.execute(token, assistantId, message);

            // 使用同步任务来广播第二条消息，因为不能直接在异步线程中操作游戏世界
            Bukkit.getScheduler().runTask(plugin, () -> {
                String secondMessage = "[AronaChat][" + prefix + "]:" + serverResponse;
                plugin.getServer().broadcastMessage(secondMessage);
            });
        });

        return true; // 表示命令已被正确处理
    }
}