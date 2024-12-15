// aronachat.java
package org.fingtest6;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import net.minecraft.commands.Commands;
import net.neoforged.fml.ModLoadingContext;
import org.fingtest6.AronachatConfig;

import java.nio.file.Path;

import static com.mojang.brigadier.arguments.StringArgumentType.string;

@Mod("aronachat")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class aronachat {
    // 添加一个静态字段来存储 aronamessage
    private static String aronamessage = "";

    public aronachat(ModContainer container) {
        // Register the config
        container.registerConfig(ModConfig.Type.COMMON, AronachatConfig.CONFIG_SPEC);
    }

    public static void setAronaMessage(String message) {
        aronamessage = message;
    }

    public static String getAronaMessage() {
        return aronamessage;
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("arona")
                .then(Commands.argument("message", string())
                        .executes(context -> {
                            // 获取玩家输入的消息
                            String message = context.getArgument("message", String.class);

                            // 读取配置文件中的值
                            String token = AronachatConfig.CONFIG.Token.get();
                            String assistantId = AronachatConfig.CONFIG.ssistantid.get();
                            String prefix = AronachatConfig.CONFIG.prefix.get();

                            // 使用 MessageHandle 发送消息并获取回复
                            String reply = MessageHandle.execute(token, assistantId, message);
                            setAronaMessage(reply);

                            // 调用 ChatProcedure，将消息广播到所有玩家
                            Level world = context.getSource().getLevel();
                            ChatProcedure.execute(world, prefix);

                            return 0;
                        })));
    }
}
