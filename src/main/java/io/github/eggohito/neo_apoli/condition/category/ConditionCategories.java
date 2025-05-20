package io.github.eggohito.neo_apoli.condition.category;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Locale;

public final class ConditionCategories {

	public static final ConditionCategory<BiEntityCondition> BIENTITY_CONDITION = register("Bi-entity condition", BiEntityCondition.CODEC, BiEntityCondition.PACKET_CODEC);
	public static final ConditionCategory<BlockCondition> BLOCK_CONDITION = register("Block condition", BlockCondition.CODEC, BlockCondition.PACKET_CODEC);
	public static final ConditionCategory<EntityCondition> ENTITY_CONDITION = register("Entity condition", EntityCondition.CODEC, EntityCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	public static <C extends Condition<?>> ConditionCategory<C> register(String name, Codec<C> codec, PacketCodec<RegistryByteBuf, C> packetCodec) {

		String transformedName = Category.NAME_PATTERN.matcher(name.toLowerCase(Locale.ROOT))
			.replaceAll("")
			.replace(' ', '_');

		return register(NeoApoli.id(transformedName), new ConditionCategory<>() {

			@Override
			public String directory() {
				return transformedName;
			}

			@Override
			public Codec<C> codec() {
				return codec;
			}

			@Override
			public PacketCodec<RegistryByteBuf, C> packetCodec() {
				return packetCodec;
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
