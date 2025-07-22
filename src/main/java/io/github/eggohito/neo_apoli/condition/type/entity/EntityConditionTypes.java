package io.github.eggohito.neo_apoli.condition.type.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.*;
import io.github.eggohito.neo_apoli.condition.meta.entity.*;
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

	public static final EntityConditionType<AllOfEntityCondition> ALL_OF = registerInternal("all_of", AllOfEntityCondition.CODEC, AllOfEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<AnyOfEntityCondition> ANY_OF = registerInternal("any_of", AnyOfEntityCondition.CODEC, AnyOfEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<CompareEntityCondition> COMPARE = registerInternal("compare", CompareEntityCondition.CODEC, CompareEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<CompareToRangeEntityCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeEntityCondition.CODEC, CompareToRangeEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<ConstantEntityCondition> CONSTANT = registerInternal("constant", ConstantEntityCondition.CODEC, ConstantEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<InvertedEntityCondition> INVERTED = registerInternal("inverted", InvertedEntityCondition.CODEC, InvertedEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<ReferenceEntityCondition> REFERENCE = registerInternal("reference", ReferenceEntityCondition.CODEC, ReferenceEntityCondition.PACKET_CODEC);

	public static final EntityConditionType<EntityTypeEntityCondition> ENTITY_TYPE = registerInternal("entity_type", EntityTypeEntityCondition.CODEC, EntityTypeEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<EquippedItemEntityCondition> EQUIPPED_ITEM = registerInternal("equipped_item", EquippedItemEntityCondition.CODEC, EquippedItemEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsInBlockEntityCondition> IS_IN_BLOCK = registerInternal("is_in_block", IsInBlockEntityCondition.CODEC, IsInBlockEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsInTagEntityCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagEntityCondition.CODEC, IsInTagEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsOnBlockEntityCondition> IS_ON_BLOCK = registerInternal("is_on_block", IsOnBlockEntityCondition.CODEC, IsOnBlockEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsSneakingEntityCondition> IS_SNEAKING = registerInternal("is_sneaking", IsSneakingEntityCondition.CODEC, IsSneakingEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsSprintingEntityCondition> IS_SPRINTING = registerInternal("is_sprinting", IsSprintingEntityCondition.CODEC, IsSprintingEntityCondition.PACKET_CODEC);

	public static void registerAll() {

		ALIASES.addPathAlias("and", getId(ALL_OF).getPath());
		ALIASES.addPathAlias("or", getId(ANY_OF).getPath());

		ALIASES.addPathAlias("in_block", getId(IS_IN_BLOCK).getPath());
		ALIASES.addPathAlias("sneaking", getId(IS_SNEAKING).getPath());
		ALIASES.addPathAlias("sprinting", getId(IS_SPRINTING).getPath());

	}

	private static <C extends EntityCondition> EntityConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static Identifier getId(EntityConditionType<?> type) {
		return RegistryUtil.getId(NeoApoliRegistries.ENTITY_CONDITION_TYPE, type);
	}

	public static <C extends EntityCondition> EntityConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return Registry.register(NeoApoliRegistries.ENTITY_CONDITION_TYPE, id, new EntityConditionType<>(mapCodec, packetCodec));
	}

}
