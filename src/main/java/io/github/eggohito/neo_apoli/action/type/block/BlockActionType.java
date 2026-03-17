package io.github.eggohito.neo_apoli.action.type.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.block.BlockAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record BlockActionType<A extends BlockAction>(MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> streamCodec) implements ActionType<A> {

	public static final String PREFIX = "block/";

	public static final FixedRegistryAlias<BlockActionType<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.BLOCK_ACTION_TYPE, ActionType.ALIASES);

	public static final Codec<BlockActionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockActionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.BLOCK_ACTION_TYPE);

}
