package com.sami.autils;

import com.sami.autils.commands.AutoBoop;
import com.sami.autils.commands.AutoParty;
import com.sami.autils.commands.AutoUtilsCommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = autil.MODID, name = autil.NAME, version = autil.VERSION, clientSideOnly = true)
public class autil {
    public static final String MODID = "autils";
    public static final String NAME = "Auto Utils Mod";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new AutoBoop());
        ClientCommandHandler.instance.registerCommand(new AutoParty());
        ClientCommandHandler.instance.registerCommand(new autils_cmd());
        System.out.println(NAME + " loaded.");
    }
}
