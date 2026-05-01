package io.github.eggohito.neo_apoli.condition.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.kind.ConditionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface ConditionType<C extends Condition> {

	FixedRegistryAlias<ConditionType<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.CONDITION_TYPE);

	Codec<ConditionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	StreamCodec<RegistryFriendlyByteBuf, ConditionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.CONDITION_TYPE);

	ConditionKind<?> kind();

	MapCodec<C> mapCodec();

	StreamCodec<RegistryFriendlyByteBuf, C> streamCodec();

}
