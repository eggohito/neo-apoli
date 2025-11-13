package io.github.eggohito.neo_apoli.condition.type.meta;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.*;
import io.github.eggohito.neo_apoli.condition.custom.meta.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public class MetaConditionTypes extends ConditionTypes {

	public static final MetaConditionType<AllOfCondition> ALL_OF = registerInternal("all_of", AllOfCondition.CODEC, AllOfCondition.PACKET_CODEC);
	public static final MetaConditionType<AnyOfCondition> ANY_OF = registerInternal("any_of", AnyOfCondition.CODEC, AnyOfCondition.PACKET_CODEC);
	public static final MetaConditionType<CompareCondition> COMPARE = registerInternal("compare", CompareCondition.CODEC, CompareCondition.PACKET_CODEC);
	public static final MetaConditionType<CompareToRangeCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeCondition.CODEC, CompareToRangeCondition.PACKET_CODEC);
	public static final MetaConditionType<ConstantCondition> CONSTANT = registerInternal("constant", ConstantCondition.CODEC, ConstantCondition.PACKET_CODEC);
	public static final MetaConditionType<InvertedCondition> INVERTED = registerInternal("inverted", InvertedCondition.CODEC, InvertedCondition.PACKET_CODEC);
	public static final MetaConditionType<ReferenceCondition> REFERENCE = registerInternal("reference", ReferenceCondition.CODEC, ReferenceCondition.PACKET_CODEC);
	public static final MetaConditionType<TestEntityCondition> TEST_ENTITY = registerInternal("test_entity", TestEntityCondition.CODEC, TestEntityCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends MetaCondition> MetaConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends MetaCondition> MetaConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(id, new MetaConditionType<>(mapCodec, packetCodec));
	}

}
