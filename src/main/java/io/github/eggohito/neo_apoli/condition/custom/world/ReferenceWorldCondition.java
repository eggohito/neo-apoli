package io.github.eggohito.neo_apoli.condition.custom.world;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliWorldConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceWorldCondition(ResourceLocation value) implements WorldCondition, ReferenceMetaCondition<WorldCondition> {

	public static final MapCodec<ReferenceWorldCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceWorldCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceWorldCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceWorldCondition::new);

	@Override
	public WorldCondition.Kind targetKind() {
		return WorldCondition.Kind.INSTANCE;
	}

	@Override
	public WorldCondition.Type<?> getType() {
		return NeoApoliWorldConditionTypes.REFERENCE;
	}

}
