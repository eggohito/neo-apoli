package io.github.eggohito.neo_apoli.condition.custom.block;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.condition.custom.meta.ReferenceMetaCondition;
import io.github.eggohito.neo_apoli.registry.condition.NeoApoliBlockConditionTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record ReferenceBlockCondition(ResourceLocation value) implements BlockCondition, ReferenceMetaCondition<BlockCondition> {

	public static final MapCodec<ReferenceBlockCondition> MAP_CODEC = ReferenceMetaCondition.mapCodec(ReferenceBlockCondition::new);
	public static final StreamCodec<RegistryFriendlyByteBuf, ReferenceBlockCondition> STREAM_CODEC = ReferenceMetaCondition.streamCodec(ReferenceBlockCondition::new);

	@Override
	public BlockCondition.Kind targetKind() {
		return BlockCondition.Kind.INSTANCE;
	}

	@Override
	public BlockCondition.Type<?> getType() {
		return NeoApoliBlockConditionTypes.REFERENCE;
	}

}
