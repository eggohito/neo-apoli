package io.github.eggohito.neo_apoli.action.category;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.BlockAction;
import io.github.eggohito.neo_apoli.action.EntityAction;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.category.Category;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Locale;

public final class ActionCategories {

	public static final ActionCategory<BlockAction> BLOCK_ACTION = register("Block action", BlockAction.CODEC, BlockAction.PACKET_CODEC);
	public static final ActionCategory<EntityAction> ENTITY_ACTION = register("Entity action", EntityAction.CODEC, EntityAction.PACKET_CODEC);

	public static void registerAll() {

	}

	public static <A extends Action<?>> ActionCategory<A> register(String name, Codec<A> codec, PacketCodec<RegistryByteBuf, A> packetCodec) {

		String transformedName = Category.NAME_PATTERN.matcher(name.toLowerCase(Locale.ROOT))
			.replaceAll("")
			.replace(' ', '_');

		return register(NeoApoli.id(transformedName), new ActionCategory<>() {

			@Override
			public String directory() {
				return transformedName;
			}

			@Override
			public Codec<A> codec() {
				return codec;
			}

			@Override
			public PacketCodec<RegistryByteBuf, A> packetCodec() {
				return packetCodec;
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
