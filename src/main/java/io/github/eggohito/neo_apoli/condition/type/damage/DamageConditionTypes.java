package io.github.eggohito.neo_apoli.condition.type.damage;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.damage.DamageCondition;
import io.github.eggohito.neo_apoli.condition.custom.damage.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class DamageConditionTypes extends ConditionTypes {

	public static final DamageConditionType<AllOfDamageCondition> ALL_OF = registerInternal("all_of", AllOfDamageCondition.CODEC, AllOfDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<AnyOfDamageCondition> ANY_OF = registerInternal("any_of", AnyOfDamageCondition.CODEC, AnyOfDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<CompareDamageCondition> COMPARE = registerInternal("compare", CompareDamageCondition.CODEC, CompareDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<CompareToRangeDamageCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeDamageCondition.CODEC, CompareToRangeDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<ConstantDamageCondition> CONSTANT = registerInternal("constant", ConstantDamageCondition.CODEC, ConstantDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<InvertedDamageCondition> INVERTED = registerInternal("inverted", InvertedDamageCondition.CODEC, InvertedDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<ReferenceDamageCondition> REFERENCE = registerInternal("reference", ReferenceDamageCondition.CODEC, ReferenceDamageCondition.PACKET_CODEC);

	public static final DamageConditionType<IsInTagDamageCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagDamageCondition.CODEC, IsInTagDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<IsOfDamageCondition> IS_OF = registerInternal("is_of", IsOfDamageCondition.CODEC, IsOfDamageCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends DamageCondition> DamageConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends DamageCondition> DamageConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(id.withPrefixedPath(DamageConditionType.PREFIX), Registry.register(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, id, new DamageConditionType<>(mapCodec, packetCodec)));
	}

}
