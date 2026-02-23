package com.dyxiaojiazi.unmannedonlinetickstasis;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SERVER_CONFIG;

    public static final ForgeConfigSpec.BooleanValue ENABLE_PAUSE;
    public static final ForgeConfigSpec.IntValue DELAY_SECONDS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> COMMAND_WHITELIST;

    private static List<Pattern> whitelistPatterns = new ArrayList<>();

    static {
        BUILDER.push("Pause Settings");
        ENABLE_PAUSE = BUILDER
                .comment("Enable the pause functionality when no players are online. (Default: true)")
                .define("enablePause", true);

        DELAY_SECONDS = BUILDER
                .comment("Delay in seconds after the last player leaves before the server pauses. (Default: 0)")
                .defineInRange("delaySeconds", 0, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Command Whitelist");
        COMMAND_WHITELIST = BUILDER
                .comment("List of commands that can be executed from console while the server is paused. Supports wildcard '*' at the end. Example: [\"say\", \"list\", \"stop\"]")
                .defineList("commandWhitelist", List.of("say", "list", "stop"), Config::validateCommandEntry);
        BUILDER.pop();

        SERVER_CONFIG = BUILDER.build();

        // 编译通配符为正则表达式
        updatePatterns();
    }

    private static boolean validateCommandEntry(Object obj) {
        return obj instanceof String && !((String) obj).isEmpty();
    }

    public static void updatePatterns() {
        whitelistPatterns.clear();
        List<? extends String> list = COMMAND_WHITELIST.get();
        for (String entry : list) {
            if (entry.endsWith("*")) {
                String prefix = Pattern.quote(entry.substring(0, entry.length() - 1));
                whitelistPatterns.add(Pattern.compile("^" + prefix + ".*"));
            } else {
                whitelistPatterns.add(Pattern.compile("^" + Pattern.quote(entry) + "$"));
            }
        }
    }

    public static boolean isCommandWhitelisted(String command) {
        if (whitelistPatterns.isEmpty()) return false;
        for (Pattern pattern : whitelistPatterns) {
            if (pattern.matcher(command).matches()) {
                return true;
            }
        }
        return false;
    }
}