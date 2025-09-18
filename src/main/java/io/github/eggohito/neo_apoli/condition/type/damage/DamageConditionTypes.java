package io.github.eggohito.neo_apoli.condition.type.damage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.DamageCondition;
import io.github.eggohito.neo_apoli.condition.custom.damage.IsInTagDamageCondition;
import io.github.eggohito.neo_apoli.condition.custom.damage.IsOfDamageCondition;
import io.github.eggohito.neo_apoli.condition.meta.damage.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.IdentifierAlias;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class DamageConditionTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final Codec<DamageConditionType<?>> CODEC = RegistryUtil.createAliasedCodec(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, ALIASES);
	public static final PacketCodec<RegistryByteBuf, DamageConditionType<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.DAMAGE_CONDITION_TYPE);

	public static final DamageConditionType<AllOfDamageCondition> ALL_OF = registerInternal("all_of", AllOfDamageCondition.CODEC, AllOfDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<AnyOfDamageCondition> ANY_OF = registerInternal("any_of", AnyOfDamageCondition.CODEC, AnyOfDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<CompareDamageCondition> COMPARE = registerInternal("compare", CompareDamageCondition.CODEC, CompareDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<CompareToRangeDamageCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeDamageCondition.CODEC, CompareToRangeDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<ConstantDamageCondition> CONSTANT = registerInternal("constant", ConstantDamageCondition.CODEC, ConstantDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<InvertedDamageCondition> INVERTED = registerInternal("inverted", InvertedDamageCondition.CODEC, InvertedDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<ReferenceDamageCondition> REFERENCE = registerInternal("reference", ReferenceDamageCondition.CODEC, ReferenceDamageCondition.PACKET_CODEC);

	public static final DamageConditionType<IsOfDamageCondition> IS_OF = registerInternal("is_of", IsOfDamageCondition.CODEC, IsOfDamageCondition.PACKET_CODEC);
	public static final DamageConditionType<IsInTagDamageCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagDamageCondition.CODEC, IsInTagDamageCondition.PACKET_CODEC);

	public static void registerAll() {
		ALIASES.addPathAlias("and", getId(ALL_OF).getPath());
		ALIASES.addPathAlias("or", getId(ANY_OF).getPath());
	}

	private static <C extends DamageCondition> DamageConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends DamageCondition> DamageConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, id, new DamageConditionType<>(mapCodec, packetCodec));
	}

	public static Identifier getId(DamageConditionType<?> type) {
		return RegistryUtil.getId(NeoApoliRegistries.DAMAGE_CONDITION_TYPE, type);
	}

}
