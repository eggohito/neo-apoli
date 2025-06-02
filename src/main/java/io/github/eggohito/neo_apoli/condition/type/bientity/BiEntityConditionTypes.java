package io.github.eggohito.neo_apoli.condition.type.bientity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.EqualsBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.SwapEntityContextBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.TestEntityConditionBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BiEntityConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<BiEntityConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, BiEntityConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.BIENTITY_CONDITION_TYPE);

	public static final BiEntityConditionType<AllOfBiEntityCondition> ALL_OF = registerInternal("all_of", AllOfBiEntityCondition.CODEC, AllOfBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<AnyOfBiEntityCondition> ANY_OF = registerInternal("any_of", AnyOfBiEntityCondition.CODEC, AnyOfBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<CompareBiEntityCondition> COMPARE = registerInternal("compare", CompareBiEntityCondition.CODEC, CompareBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<ConstantBiEntityCondition> CONSTANT = registerInternal("constant", ConstantBiEntityCondition.CODEC, ConstantBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<InvertedBiEntityCondition> INVERTED = registerInternal("inverted", InvertedBiEntityCondition.CODEC, InvertedBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<ReferenceBiEntityCondition> REFERENCE = registerInternal("reference", ReferenceBiEntityCondition.CODEC, ReferenceBiEntityCondition.PACKET_CODEC);

	public static final BiEntityConditionType<EqualsBiEntityCondition> EQUALS = registerInternal("equals", EqualsBiEntityCondition.CODEC, EqualsBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<SwapEntityContextBiEntityCondition> SWAP_ENTITY_CONTEXT = registerInternal("swap_entity_context", SwapEntityContextBiEntityCondition.CODEC, SwapEntityContextBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<TestEntityConditionBiEntityCondition> TEST_ENTITY_CONDITION = registerInternal("test_entity_condition", TestEntityConditionBiEntityCondition.CODEC, TestEntityConditionBiEntityCondition.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.addPathAlias("and", RegistryUtil.getIdPath(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, ALL_OF));
		ALIASES.addPathAlias("or", RegistryUtil.getIdPath(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, ANY_OF));

	}

	private static <C extends BiEntityCondition> BiEntityConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends BiEntityCondition> BiEntityConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, id, new BiEntityConditionType<>(mapCodec, packetCodec));
	}

}
