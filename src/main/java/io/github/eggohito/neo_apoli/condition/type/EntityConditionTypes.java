package io.github.eggohito.neo_apoli.condition.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.IsSneakingEntityConditionType;
import io.github.eggohito.neo_apoli.condition.custom.IsSprintingEntityConditionType;
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

	public static final Codec<EntityConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.ENTITY_CONDITION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, EntityConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.ENTITY_CONDITION_TYPE);

	public static final EntityConditionType<IsSneakingEntityConditionType> IS_SNEAKING = registerInternal("is_sneaking", IsSneakingEntityConditionType.CODEC, IsSneakingEntityConditionType.PACKET_CODEC);
	public static final EntityConditionType<IsSprintingEntityConditionType> IS_SPRINTING = registerInternal("is_sprinting", IsSprintingEntityConditionType.CODEC, IsSprintingEntityConditionType.PACKET_CODEC);

	public static void registerAll() {
		ALIASES.addPathAlias("sneaking", RegistryUtil.getIdPath(NeoApoliRegistries.ENTITY_CONDITION_TYPE, IS_SNEAKING));
		ALIASES.addPathAlias("sprinting", RegistryUtil.getIdPath(NeoApoliRegistries.ENTITY_CONDITION_TYPE, IS_SPRINTING));
	}

	private static <C extends EntityCondition> EntityConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends EntityCondition> EntityConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.ENTITY_CONDITION_TYPE, id, new EntityConditionType<>(mapCodec, packetCodec));
	}

}
