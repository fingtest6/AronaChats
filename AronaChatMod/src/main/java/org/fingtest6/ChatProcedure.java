package org.fingtest6;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.network.chat.Component;

public class ChatProcedure {
    public static void execute(LevelAccessor world, String prefix) {
        String message = aronachat.getAronaMessage();
        if (!world.isClientSide() && world.getServer() != null) {
            world.getServer().getPlayerList()
                    .broadcastSystemMessage(Component.literal(prefix + ": " + message), false);
        }
    }
}
