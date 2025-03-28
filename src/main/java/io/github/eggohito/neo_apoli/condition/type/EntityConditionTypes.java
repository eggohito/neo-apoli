package io.github.eggohito.neo_apoli.condition.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.SneakingEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.SprintingEntityCondition;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class EntityConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<EntityConditionType<?>> CODEC = RegistryUtil.getAliasedCodec(NeoApoliRegistries.ENTITY_CONDITION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, EntityConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ENTITY_CONDITION_TYPE);

	public static final EntityConditionType<SneakingEntityCondition> SNEAKING = registerInternal("sneaking", SneakingEntityCondition.CODEC, SneakingEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<SprintingEntityCondition> SPRINTING = registerInternal("sprinting", SprintingEntityCondition.CODEC, SprintingEntityCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends EntityCondition> EntityConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends EntityCondition> EntityConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.ENTITY_CONDITION_TYPE, id, new EntityConditionType<>(mapCodec, packetCodec));
	}

}
