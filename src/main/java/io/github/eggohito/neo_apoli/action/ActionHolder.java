package io.github.eggohito.neo_apoli.action;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ActionHolder<A extends Action>(ResourceLocation id, A value) {

	public static final MapCodec<ActionHolder<?>> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		ResourceLocation.CODEC.fieldOf("id").forGetter(ActionHolder::id),
		Action.MAP_CODEC.forGetter(ActionHolder::value)
	).apply(instance, ActionHolder::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ActionHolder<?>> STREAM_CODEC = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, ActionHolder::id,
		Action.STREAM_CODEC, ActionHolder::value,
		ActionHolder::new
	);

	public Action valueGeneric() {
		return value();
	}

}
