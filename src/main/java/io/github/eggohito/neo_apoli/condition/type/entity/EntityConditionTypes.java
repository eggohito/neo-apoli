package io.github.eggohito.neo_apoli.condition.type.entity;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.entity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class EntityConditionTypes {

	public static final EntityConditionType<AllOfEntityCondition> ALL_OF = registerInternal("all_of", AllOfEntityCondition.MAP_CODEC, AllOfEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<AnyOfEntityCondition> ANY_OF = registerInternal("any_of", AnyOfEntityCondition.MAP_CODEC, AnyOfEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<CompareEntityCondition> COMPARE = registerInternal("compare", CompareEntityCondition.MAP_CODEC, CompareEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<CompareToRangeEntityCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeEntityCondition.MAP_CODEC, CompareToRangeEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<ConstantEntityCondition> CONSTANT = registerInternal("constant", ConstantEntityCondition.MAP_CODEC, ConstantEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<DynamicEntityCondition> DYNAMIC = registerInternal("dynamic", DynamicEntityCondition.MAP_CODEC, DynamicEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<InvertedEntityCondition> INVERTED = registerInternal("inverted", InvertedEntityCondition.MAP_CODEC, InvertedEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<OffsetEntityCondition> OFFSET = registerInternal("offset", OffsetEntityCondition.MAP_CODEC, OffsetEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<ReferenceEntityCondition> REFERENCE = registerInternal("reference", ReferenceEntityCondition.MAP_CODEC, ReferenceEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<TestWorldEntityCondition> TEST_WORLD = registerInternal("test_world", TestWorldEntityCondition.MAP_CODEC, TestWorldEntityCondition.STREAM_CODEC);

	public static final EntityConditionType<ExistsEntityCondition> EXISTS = registerInternal("exists", ExistsEntityCondition.MAP_CODEC, ExistsEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<HasCollidedHorizontallyEntityCondition> HAS_COLLIDED_HORIZONTALLY = registerInternal("has_collided_horizontally", HasCollidedHorizontallyEntityCondition.MAP_CODEC, HasCollidedHorizontallyEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<HasEquippedItemEntityCondition> HAS_EQUIPPED_ITEM = registerInternal("has_equipped_item", HasEquippedItemEntityCondition.MAP_CODEC, HasEquippedItemEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<HasPowerEntityCondition> HAS_POWER = registerInternal("has_power", HasPowerEntityCondition.MAP_CODEC, HasPowerEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<HasPressedKeysSimultaneouslyEntityCondition> HAS_PRESSED_KEYS_SIMULTANEOUSLY = registerInternal("has_pressed_keys_simultaneously", HasPressedKeysSimultaneouslyEntityCondition.MAP_CODEC, HasPressedKeysSimultaneouslyEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsClimbingEntityCondition> IS_CLIMBING = registerInternal("is_climbing", IsClimbingEntityCondition.MAP_CODEC, IsClimbingEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsFallFlyingEntityCondition> IS_FALL_FLYING = registerInternal("is_fall_flying", IsFallFlyingEntityCondition.MAP_CODEC, IsFallFlyingEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsInvisibleEntityCondition> IS_INVISIBLE = registerInternal("is_invisible", IsInvisibleEntityCondition.MAP_CODEC, IsInvisibleEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsInBlockEntityCondition> IS_IN_BLOCK = registerInternal("is_in_block", IsInBlockEntityCondition.MAP_CODEC, IsInBlockEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsInTagEntityCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagEntityCondition.MAP_CODEC, IsInTagEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsOfEntityCondition> IS_OF = registerInternal("is_of", IsOfEntityCondition.MAP_CODEC, IsOfEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsOnBlockEntityCondition> IS_ON_BLOCK = registerInternal("is_on_block", IsOnBlockEntityCondition.MAP_CODEC, IsOnBlockEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsOnFireEntityCondition> IS_ON_FIRE = registerInternal("is_on_fire", IsOnFireEntityCondition.MAP_CODEC, IsOnFireEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsPowerActiveEntityCondition> IS_POWER_ACTIVE = registerInternal("is_power_active", IsPowerActiveEntityCondition.MAP_CODEC, IsPowerActiveEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsSneakingEntityCondition> IS_SNEAKING = registerInternal("is_sneaking", IsSneakingEntityCondition.MAP_CODEC, IsSneakingEntityCondition.STREAM_CODEC);
	public static final EntityConditionType<IsSprintingEntityCondition> IS_SPRINTING = registerInternal("is_sprinting", IsSprintingEntityCondition.MAP_CODEC, IsSprintingEntityCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends EntityCondition> EntityConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends EntityCondition> EntityConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.ENTITY_CONDITION_TYPE, id, new EntityConditionType<>(mapCodec, streamCodec));
	}

}
