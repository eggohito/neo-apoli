package io.github.eggohito.neo_apoli.client.mixin.impl.power.custom.crafting_recipe;

import io.github.eggohito.neo_apoli.duck.internal.PowerRecipeDisplayHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements PowerRecipeDisplayHolder {

	@Unique
	private final Int2ObjectOpenHashMap<PowerIdentifier> neo_apoli$powerIdsByIndex = new Int2ObjectOpenHashMap<>();

	@Override
	public Int2ObjectMap<PowerIdentifier> neo_apoli$getPowerIdsByIndex() {
		return new Int2ObjectOpenHashMap<>(neo_apoli$powerIdsByIndex);
	}

	@Override
	public void neo_apoli$setPowerIdsByIndex(Int2ObjectMap<PowerIdentifier> powerIdsByIndex) {

		this.neo_apoli$powerIdsByIndex.clear();

		this.neo_apoli$powerIdsByIndex.putAll(powerIdsByIndex);
		this.neo_apoli$powerIdsByIndex.trim();

	}

}
