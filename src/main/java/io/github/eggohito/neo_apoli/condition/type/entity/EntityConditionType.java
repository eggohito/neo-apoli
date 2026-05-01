package io.github.eggohito.neo_apoli.condition.type.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.kind.custom.EntityConditionKind;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record EntityConditionType<C extends EntityCondition>(MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) implements ConditionType<C> {

	public static final FixedRegistryAlias<EntityConditionType<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.ENTITY_CONDITION_TYPE, ConditionType.ALIASES);

	public static final Codec<EntityConditionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ENTITY_CONDITION_TYPE);

	@Override
	public EntityConditionKind kind() {
		return EntityConditionKind.INSTANCE;
	}

}
