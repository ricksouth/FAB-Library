package com.natamus.findablocklibrary.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ConfigHandler {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final General GENERAL = new General(BUILDER);
	public static final ForgeConfigSpec spec = BUILDER.build();

	public static class General {
		public final ForgeConfigSpec.ConfigValue<Integer> blockCheckAroundEntitiesDelayMs;

		public General(ForgeConfigSpec.Builder builder) {
			builder.push("General");
			blockCheckAroundEntitiesDelayMs = builder
					.comment("The delay of the is-there-a-block-around-check around entities in ms. Increasing this number can increase TPS if needed.")
					.defineInRange("blockCheckAroundEntitiesDelayMs", 30000, 0, 3600000);
			
			builder.pop();
		}
	}
}