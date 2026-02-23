package com.dyxiaojiazi.unmannedonlinetickstasis;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(UnmannedOnlineTickStasis.MODID)
public class UnmannedOnlineTickStasis {
    public static final String MODID = "unmannedonlinetickstasis";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static boolean paused = false;

    @SuppressWarnings("deprecation") // 忽略过时API警告（FMLJavaModLoadingContext.get 和 ModLoadingContext.get）
    public UnmannedOnlineTickStasis() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册配置（ModLoadingContext.get() 已过时，但1.20.1中仍可用）
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        // 注册事件监听
        MinecraftForge.EVENT_BUS.register(new EventHandler());

        LOGGER.info("Unmanned Online Tick Stasis initialized.");
    }

    public static boolean isPaused() {
        return paused;
    }

    public static void setPaused(boolean p) {
        paused = p;
    }
}