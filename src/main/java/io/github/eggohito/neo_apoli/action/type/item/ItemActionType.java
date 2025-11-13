package io.github.eggohito.neo_apoli.action.type.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.action.custom.item.ItemAction;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record ItemActionType<A extends ItemAction>(MapCodec<A> mapCodec, PacketCodec<RegistryByteBuf, A> packetCodec) implements ActionType<A> {

	public static final String PREFIX = "item/";

	public static final RegistryFixedAlias<ItemActionType<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.ITEM_ACTION_TYPE, ActionType.ALIASES, PREFIX, "");

	public static final Codec<ItemActionType<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);

	public static final PacketCodec<RegistryByteBuf, ItemActionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ITEM_ACTION_TYPE);

}
