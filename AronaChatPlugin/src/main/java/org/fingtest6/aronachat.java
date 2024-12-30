package org.fingtest6;

import org.bukkit.plugin.java.JavaPlugin;

public final class aronachat extends JavaPlugin {

    @Override
    public void onEnable() {
        // 插件已启动
        saveDefaultConfig();
        getCommand("arona").setExecutor(new AronaCommand(this));
        getCommand("chat").setExecutor(new ReloadCommand(this));
        getLogger().info("插件已启动");
    }

    @Override
    public void onDisable() {
        getLogger().info("插件已关闭");
        // Plugin shutdown logic
    }
}
