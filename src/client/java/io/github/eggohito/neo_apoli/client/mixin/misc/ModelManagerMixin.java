package io.github.eggohito.neo_apoli.client.mixin.misc;

import com.google.common.collect.ImmutableMap;
import io.github.eggohito.neo_apoli.client.event.TextureAtlasRegistrationEvents;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

	@Mutable
	@Shadow
	@Final
	private static Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;

	static {

		ImmutableMap.Builder<ResourceLocation, ResourceLocation> builder = new ImmutableMap.Builder<>();
		builder.putAll(VANILLA_ATLASES);

		TextureAtlasRegistrationEvents.SIMPLE.invoker().register(atlasId -> builder.put(atlasId.sheet(), atlasId.name()));

		VANILLA_ATLASES = builder.build();

	}

}
