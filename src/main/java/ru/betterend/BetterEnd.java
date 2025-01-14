package ru.betterend;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import ru.bclib.util.Logger;
import ru.betterend.api.BetterEndPlugin;
import ru.betterend.config.Configs;
import ru.betterend.effects.EndEnchantments;
import ru.betterend.effects.EndPotions;
import ru.betterend.events.PlayerAdvancementsCallback;
import ru.betterend.integration.Integrations;
import ru.betterend.item.GuideBookItem;
import ru.betterend.recipe.AlloyingRecipes;
import ru.betterend.recipe.AnvilRecipes;
import ru.betterend.recipe.CraftingRecipes;
import ru.betterend.recipe.FurnaceRecipes;
import ru.betterend.recipe.InfusionRecipes;
import ru.betterend.recipe.SmithingRecipes;
import ru.betterend.registry.EndBiomes;
import ru.betterend.registry.EndBlockEntities;
import ru.betterend.registry.EndBlocks;
import ru.betterend.registry.EndEntities;
import ru.betterend.registry.EndFeatures;
import ru.betterend.registry.EndItems;
import ru.betterend.registry.EndPortals;
import ru.betterend.registry.EndSounds;
import ru.betterend.registry.EndStructures;
import ru.betterend.registry.EndTags;
import ru.betterend.util.BonemealPlants;
import ru.betterend.util.DataFixerUtil;
import ru.betterend.util.LootTableUtil;
import ru.betterend.world.generator.BetterEndBiomeSource;
import ru.betterend.world.generator.GeneratorOptions;
import ru.betterend.world.surface.SurfaceBuilders;

public class BetterEnd implements ModInitializer {
	public static final String MOD_ID = "betterend";
	public static final Logger LOGGER = new Logger(MOD_ID);
	private static boolean hasHydrogen;
	
	@Override
	public void onInitialize() {
		EndPortals.loadPortals();
		EndSounds.register();
		EndBlockEntities.register();
		EndFeatures.register();
		EndEntities.register();
		SurfaceBuilders.register();
		EndBiomes.register();
		BetterEndBiomeSource.register();
		EndTags.register();
		EndEnchantments.register();
		EndPotions.register();
		CraftingRecipes.register();
		FurnaceRecipes.register();
		AlloyingRecipes.register();
		AnvilRecipes.register();
		SmithingRecipes.register();
		InfusionRecipes.register();
		EndStructures.register();
		Integrations.register();
		BonemealPlants.init();
		GeneratorOptions.init();
		DataFixerUtil.init();
		LootTableUtil.init();
		
		if (hasGuideBook()) {
			GuideBookItem.register();
		}
		hasHydrogen = FabricLoader.getInstance().isModLoaded("hydrogen");
		
		FabricLoader.getInstance().getEntrypoints("betterend", BetterEndPlugin.class).forEach(BetterEndPlugin::register);
		Configs.saveConfigs();
		
		if (hasGuideBook()) {
			PlayerAdvancementsCallback.PLAYER_ADVANCEMENT_COMPLETE.register((player, advancement, criterionName) -> {
				ResourceLocation advId = new ResourceLocation("minecraft:end/enter_end_gateway");
				if (advId.equals(advancement.getId())) {
					player.addItem(new ItemStack(GuideBookItem.GUIDE_BOOK));
				}
			});
		}
	}
	
	public static boolean hasGuideBook() {
		return FabricLoader.getInstance().isModLoaded("patchouli");
	}
	
	public static boolean hasHydrogen() {
		return hasHydrogen;
	}
	
	public static ResourceLocation makeID(String path) {
		return new ResourceLocation(MOD_ID, path);
	}
	
	public static String getStringId(String id) {
		return String.format("%s:%s", MOD_ID, id);
	}

	public static boolean isModId(ResourceLocation id) {
		return id.getNamespace().equals(MOD_ID);
	}
	
	public static boolean isDevEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}
	
	public static boolean isClient() {
		return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
	}
}
