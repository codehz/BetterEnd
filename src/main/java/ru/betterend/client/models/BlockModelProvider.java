package ru.betterend.client.models;

import java.util.Map;
import java.util.Optional;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import ru.betterend.BetterEnd;

import static net.minecraft.client.resources.model.ModelBakery.MISSING_MODEL_LOCATION;

public interface BlockModelProvider extends ModelProvider {
	default @Nullable BlockModel getBlockModel(ResourceLocation resourceLocation, BlockState blockState) {
		Optional<String> pattern = Patterns.createBlockSimple(resourceLocation.getPath());
		return ModelsHelper.fromPattern(pattern);
	}

	default UnbakedModel getModelVariant(ResourceLocation resourceLocation, BlockState blockState, Map<ResourceLocation, UnbakedModel> modelCache) {
		ResourceLocation modelId = new ResourceLocation(resourceLocation.getNamespace(),
				"block/" + resourceLocation.getPath());
		registerBlockModel(resourceLocation, modelId, blockState, modelCache);
		return ModelsHelper.createBlockSimple(modelId);
	}

	default void registerBlockModel(ResourceLocation stateId, ResourceLocation modelId, BlockState blockState, Map<ResourceLocation, UnbakedModel> modelCache) {
		if (!modelCache.containsKey(modelId)) {
			UnbakedModel model = getBlockModel(stateId, blockState);
			if (model != null) {
				modelCache.put(modelId, model);
			} else {
				BetterEnd.LOGGER.warning("Error loading model: {}", modelId);
				modelCache.put(modelId, modelCache.get(MISSING_MODEL_LOCATION));
			}
		}
	}
}