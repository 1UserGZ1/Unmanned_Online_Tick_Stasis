package com.dyxiaojiazi.unmannedonlinetickstasis;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.server.ServerLifecycleHooks;

public class EventHandler {
    private long lastPlayerOfflineTime = 0;
    private boolean wasPaused = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != TickEvent.Phase.START) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        // 获取当前在线玩家数量
        int onlineCount = server.getPlayerList().getPlayerCount();

        // 如果启用暂停
        if (Config.ENABLE_PAUSE.get()) {
            if (onlineCount == 0) {
                // 没有玩家在线
                if (lastPlayerOfflineTime == 0) {
                    lastPlayerOfflineTime = System.currentTimeMillis();
                }

                long delayMillis = Config.DELAY_SECONDS.get() * 1000L;
                boolean shouldPause = System.currentTimeMillis() - lastPlayerOfflineTime >= delayMillis;

                if (shouldPause && !UnmannedOnlineTickStasis.isPaused()) {
                    // 进入暂停状态
                    UnmannedOnlineTickStasis.setPaused(true);
                    sendPauseMessage(server, true);
                } else if (!shouldPause && UnmannedOnlineTickStasis.isPaused()) {
                    // 延迟未到但意外暂停，恢复（防止延迟减少时误判）
                    UnmannedOnlineTickStasis.setPaused(false);
                    sendPauseMessage(server, false);
                }
            } else {
                // 有玩家在线，确保未暂停
                lastPlayerOfflineTime = 0;
                if (UnmannedOnlineTickStasis.isPaused()) {
                    UnmannedOnlineTickStasis.setPaused(false);
                    sendPauseMessage(server, false);
                }
            }
        } else {
            // 功能关闭，确保未暂停
            if (UnmannedOnlineTickStasis.isPaused()) {
                UnmannedOnlineTickStasis.setPaused(false);
                sendPauseMessage(server, false);
            }
        }
    }

    // 玩家登录/登出时重置离线计时器
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            lastPlayerOfflineTime = 0;
            // 如果服务器暂停，立即恢复
            if (UnmannedOnlineTickStasis.isPaused()) {
                UnmannedOnlineTickStasis.setPaused(false);
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                if (server != null) {
                    sendPauseMessage(server, false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                int onlineCount = server.getPlayerList().getPlayerCount();
                if (onlineCount == 0) {
                    lastPlayerOfflineTime = System.currentTimeMillis();
                }
            }
        }
    }

    // 命令白名单拦截
    @SubscribeEvent
    public void onCommand(CommandEvent event) {
        if (!UnmannedOnlineTickStasis.isPaused()) return;

        CommandSourceStack source = event.getParseResults().getContext().getSource();
        // 仅当命令源为控制台（服务器）且不是玩家时拦截
        if (!(source.getEntity() instanceof ServerPlayer) && source.getServer() != null) {
            String commandInput = event.getParseResults().getReader().getString();
            // 提取命令名称（第一个空格前的部分）
            String commandName = commandInput.split(" ")[0].toLowerCase();

            if (!Config.isCommandWhitelisted(commandName)) {
                event.setCanceled(true);
                source.sendFailure(Component.translatable("message." + UnmannedOnlineTickStasis.MODID + ".command_blocked"));
                UnmannedOnlineTickStasis.LOGGER.warn("Blocked console command '{}' while server is paused.", commandInput);
            }
        }
    }

    private void sendPauseMessage(MinecraftServer server, boolean paused) {
        // 控制台日志
        if (paused) {
            UnmannedOnlineTickStasis.LOGGER.info(Component.translatable("message." + UnmannedOnlineTickStasis.MODID + ".paused_console").getString());
        } else {
            UnmannedOnlineTickStasis.LOGGER.info(Component.translatable("message." + UnmannedOnlineTickStasis.MODID + ".resumed_console").getString());
        }

        // 向所有在线玩家广播（如果有）
        Component msg = Component.translatable(paused ? "message." + UnmannedOnlineTickStasis.MODID + ".paused_chat" : "message." + UnmannedOnlineTickStasis.MODID + ".resumed_chat")
                .withStyle(paused ? ChatFormatting.YELLOW : ChatFormatting.GREEN);
        server.getPlayerList().broadcastSystemMessage(msg, false);
    }
}