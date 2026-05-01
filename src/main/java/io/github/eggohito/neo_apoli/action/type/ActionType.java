package io.github.eggohito.neo_apoli.action.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.kind.ActionKind;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface ActionType<A extends Action> {

	FixedRegistryAlias<ActionType<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.ACTION_TYPE);

	Codec<ActionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	StreamCodec<RegistryFriendlyByteBuf, ActionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ACTION_TYPE);

	ActionKind<?> kind();

	MapCodec<A> mapCodec();

	StreamCodec<RegistryFriendlyByteBuf, A> streamCodec();

}
