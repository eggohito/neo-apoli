package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.condition.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public final class ConditionCategories {

	public static final ConditionCategory<BiEntityCondition> BIENTITY_CONDITION = register("Bi-entity condition", NeoApoliRegistryKeys.BIENTITY_CONDITION, BiEntityCondition.CODEC, BiEntityCondition.PACKET_CODEC);
	public static final ConditionCategory<BlockCondition> BLOCK_CONDITION = register("Block condition", NeoApoliRegistryKeys.BLOCK_CONDITION, BlockCondition.CODEC, BlockCondition.PACKET_CODEC);
	public static final ConditionCategory<EntityCondition> ENTITY_CONDITION = register("Entity condition", NeoApoliRegistryKeys.ENTITY_CONDITION, EntityCondition.CODEC, EntityCondition.PACKET_CODEC);
	public static final ConditionCategory<ItemCondition> ITEM_CONDITION = register("Item condition", NeoApoliRegistryKeys.ITEM_CONDITION, ItemCondition.CODEC, ItemCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	public static <C extends Condition<?>> ConditionCategory<C> register(String name, RegistryKey<? extends Registry<C>> registryRef, Codec<C> baseCodec, PacketCodec<RegistryByteBuf, C> basePacketCodec) {
		return register(registryRef.getValue(), new ConditionCategory<>() {

			@Override
			public RegistryKey<? extends Registry<C>> registryRef() {
				return registryRef;
			}

			@Override
			public Codec<C> baseCodec() {
				return baseCodec;
			}

			@Override
			public PacketCodec<RegistryByteBuf, C> basePacketCodec() {
				return basePacketCodec;
			}

			@Override
			public String toString() {
				return name;
			}

		});

	}

	public static <C extends Condition<?>> ConditionCategory<C> register(Identifier id, ConditionCategory<C> category) {
		return Registry.register(NeoApoliRegistries.CONDITION_CATEGORY, id, category);
	}

}
