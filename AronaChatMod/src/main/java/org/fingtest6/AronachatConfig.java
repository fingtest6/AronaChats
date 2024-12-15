package org.fingtest6;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class AronachatConfig {
    public static final AronachatConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    // 定义配置项
    public final ModConfigSpec.ConfigValue<String> Token;
    public final ModConfigSpec.ConfigValue<String> ssistantid;
    public final ModConfigSpec.ConfigValue<String> prefix;

    AronachatConfig(ModConfigSpec.Builder builder) {

        // 定义字符串类型的配置项
        Token = builder.define("Token", "");
        ssistantid = builder.define("ssistant_id", "");
        prefix = builder.define("prefix", "阿罗娜");

    }

    static {
        Pair<AronachatConfig, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(AronachatConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
