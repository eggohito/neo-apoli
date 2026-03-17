package io.github.eggohito.neo_apoli.action.type.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ItemActionType<A extends ItemAction>(MapCodec<A> mapCodec, StreamCodec<RegistryFriendlyByteBuf, A> streamCodec) implements ActionType<A> {

	public static final String PREFIX = "item/";

	public static final FixedRegistryAlias<ItemActionType<?>> ALIASES = FixedRegistryAlias.extended(NeoApoliRegistries.ITEM_ACTION_TYPE, ActionType.ALIASES);

	public static final Codec<ItemActionType<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);

	public static final StreamCodec<RegistryFriendlyByteBuf, ItemActionType<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.ITEM_ACTION_TYPE);

}
