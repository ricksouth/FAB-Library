package com.natamus.findablocklibrary;

import com.natamus.findablocklibrary.util.Reference;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class Main {
	public static Main instance;
	
    public Main() {
        instance = this;

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();  	
        modEventBus.addListener(this::loadComplete);
    }
	
    private void loadComplete(final FMLLoadCompleteEvent event) {

	}
}