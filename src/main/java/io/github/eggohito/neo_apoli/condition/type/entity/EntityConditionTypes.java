package io.github.eggohito.neo_apoli.condition.type.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.entity.EntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.entity.*;
import io.github.eggohito.neo_apoli.condition.type.ConditionType;
import io.github.eggohito.neo_apoli.condition.type.ConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class EntityConditionTypes extends ConditionTypes {

	public static final EntityConditionType<AllOfEntityCondition> ALL_OF = registerInternal("all_of", AllOfEntityCondition.CODEC, AllOfEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<AnyOfEntityCondition> ANY_OF = registerInternal("any_of", AnyOfEntityCondition.CODEC, AnyOfEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<CompareEntityCondition> COMPARE = registerInternal("compare", CompareEntityCondition.CODEC, CompareEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<CompareToRangeEntityCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeEntityCondition.CODEC, CompareToRangeEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<ConstantEntityCondition> CONSTANT = registerInternal("constant", ConstantEntityCondition.CODEC, ConstantEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<InvertedEntityCondition> INVERTED = registerInternal("inverted", InvertedEntityCondition.CODEC, InvertedEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<OffsetEntityCondition> OFFSET = registerInternal("offset", OffsetEntityCondition.CODEC, OffsetEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<ReferenceEntityCondition> REFERENCE = registerInternal("reference", ReferenceEntityCondition.CODEC, ReferenceEntityCondition.PACKET_CODEC);

	public static final EntityConditionType<HasCollidedHorizontallyEntityCondition> HAS_COLLIDED_HORIZONTALLY = registerInternal("has_collided_horizontally", HasCollidedHorizontallyEntityCondition.CODEC, HasCollidedHorizontallyEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<HasEquippedItemEntityCondition> HAS_EQUIPPED_ITEM = registerInternal("has_equipped_item", HasEquippedItemEntityCondition.CODEC, HasEquippedItemEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsClimbingEntityCondition> IS_CLIMBING = registerInternal("is_climbing", IsClimbingEntityCondition.CODEC, IsClimbingEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsInBlockEntityCondition> IS_IN_BLOCK = registerInternal("is_in_block", IsInBlockEntityCondition.CODEC, IsInBlockEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsInTagEntityCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagEntityCondition.CODEC, IsInTagEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsInvisibleEntityCondition> IS_INVISIBLE = registerInternal("is_invisible", IsInvisibleEntityCondition.CODEC, IsInvisibleEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsOfEntityCondition> IS_OF = registerInternal("is_of", IsOfEntityCondition.CODEC, IsOfEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsOnBlockEntityCondition> IS_ON_BLOCK = registerInternal("is_on_block", IsOnBlockEntityCondition.CODEC, IsOnBlockEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsPowerActiveEntityCondition> IS_POWER_ACTIVE = registerInternal("is_power_active", IsPowerActiveEntityCondition.CODEC, IsPowerActiveEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsSneakingEntityCondition> IS_SNEAKING = registerInternal("is_sneaking", IsSneakingEntityCondition.CODEC, IsSneakingEntityCondition.PACKET_CODEC);
	public static final EntityConditionType<IsSprintingEntityCondition> IS_SPRINTING = registerInternal("is_sprinting", IsSprintingEntityCondition.CODEC, IsSprintingEntityCondition.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <C extends EntityCondition> EntityConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends EntityCondition> EntityConditionType<C> register(Identifier id, MapCodec<C> mapCodec, PacketCodec<RegistryByteBuf, C> packetCodec) {
		return ConditionTypes.register(id.withPrefixedPath(EntityConditionType.PREFIX), Registry.register(NeoApoliRegistries.ENTITY_CONDITION_TYPE, id, new EntityConditionType<>(mapCodec, packetCodec)));
	}

}
