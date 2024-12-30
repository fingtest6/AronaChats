package org.fingtest6;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ReloadCommand implements CommandExecutor {

    private final aronachat plugin;

    // 构造函数，用于接收主插件实例
    public ReloadCommand(aronachat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("chat") && args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            // 重新加载配置文件
            plugin.reloadConfig();
            // 通知发送者配置文件已成功重载
            sender.sendMessage("[AronaChat] 配置文件已成功重载");
            return true;
        }
        return false; // 表示命令未被正确处理
    }
}