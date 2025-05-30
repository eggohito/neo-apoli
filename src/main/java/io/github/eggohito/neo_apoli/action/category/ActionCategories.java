package io.github.eggohito.neo_apoli.action.category;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.BiEntityAction;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class ActionCategories {

	public static final ActionCategory<BiEntityAction> BIENTITY_ACTION = register("Bi-entity action", NeoApoliRegistryKeys.BIENTITY_ACTION, BiEntityAction.CODEC, BiEntityAction.PACKET_CODEC);
	public static final ActionCategory<BlockAction> BLOCK_ACTION = register("Block action", NeoApoliRegistryKeys.BLOCK_ACTION, BlockAction.CODEC, BlockAction.PACKET_CODEC);
	public static final ActionCategory<EntityAction> ENTITY_ACTION = register("Entity action", NeoApoliRegistryKeys.ENTITY_ACTION, EntityAction.CODEC, EntityAction.PACKET_CODEC);

	public static void registerAll() {

	}

	public static <A extends Action<?>> ActionCategory<A> register(String name, RegistryKey<? extends Registry<A>> registryRef, Codec<A> baseCodec, PacketCodec<RegistryByteBuf, A> basePacketCodec) {
		return register(registryRef.getValue(), new ActionCategory<>() {

			@Override
			public RegistryKey<? extends Registry<A>> registryRef() {
				return registryRef;
			}

			@Override
			public Codec<A> baseCodec() {
				return baseCodec;
			}

			@Override
			public PacketCodec<RegistryByteBuf, A> basePacketCodec() {
				return basePacketCodec;
			}

			@Override
			public String toString() {
				return name;
			}

		});
	}

	public static <A extends Action<?>> ActionCategory<A> register(Identifier id, ActionCategory<A> category) {
		return Registry.register(NeoApoliRegistries.ACTION_CATEGORY, id, category);
	}

}
