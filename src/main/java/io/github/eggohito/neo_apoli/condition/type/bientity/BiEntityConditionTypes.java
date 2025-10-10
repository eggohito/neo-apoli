package io.github.eggohito.neo_apoli.condition.type.bientity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.EqualsBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.IsOwnerBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.SwapEntityContextBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.TestEntityConditionBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class BiEntityConditionTypes {

	public static final BiEntityConditionType<AllOfBiEntityCondition> ALL_OF = registerInternal("all_of", AllOfBiEntityCondition.CODEC, AllOfBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<AnyOfBiEntityCondition> ANY_OF = registerInternal("any_of", AnyOfBiEntityCondition.CODEC, AnyOfBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<CompareBiEntityCondition> COMPARE = registerInternal("compare", CompareBiEntityCondition.CODEC, CompareBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<CompareToRangeBiEntityCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeBiEntityCondition.CODEC, CompareToRangeBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<ConstantBiEntityCondition> CONSTANT = registerInternal("constant", ConstantBiEntityCondition.CODEC, ConstantBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<InvertedBiEntityCondition> INVERTED = registerInternal("inverted", InvertedBiEntityCondition.CODEC, InvertedBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<ReferenceBiEntityCondition> REFERENCE = registerInternal("reference", ReferenceBiEntityCondition.CODEC, ReferenceBiEntityCondition.PACKET_CODEC);

	public static final BiEntityConditionType<EqualsBiEntityCondition> EQUALS = registerInternal("equals", EqualsBiEntityCondition.CODEC, EqualsBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<IsOwnerBiEntityCondition> IS_OWNER = registerInternal("is_owner", IsOwnerBiEntityCondition.CODEC, IsOwnerBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<SwapEntityContextBiEntityCondition> SWAP_ENTITY_CONTEXT = registerInternal("swap_entity_context", SwapEntityContextBiEntityCondition.CODEC, SwapEntityContextBiEntityCondition.PACKET_CODEC);
	public static final BiEntityConditionType<TestEntityConditionBiEntityCondition> TEST_ENTITY_CONDITION = registerInternal("test_entity_condition", TestEntityConditionBiEntityCondition.CODEC, TestEntityConditionBiEntityCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends BiEntityCondition> BiEntityConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends BiEntityCondition> BiEntityConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.BIENTITY_CONDITION_TYPE, id, new BiEntityConditionType<>(mapCodec, packetCodec));
	}

}
