package ru.betterend.blocks.basis;

import java.util.Collections;
import java.util.List;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;

public class BlockTrapdoor extends TrapdoorBlock {
	public BlockTrapdoor(Block source) {
		super(FabricBlockSettings.copyOf(source).nonOpaque());
	}

	@Override
	public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
		return Collections.singletonList(new ItemStack(this));
	}
}