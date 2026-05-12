package io.github.eggohito.neo_apoli.registry.condition;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.custom.entity.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliEntityConditionTypes {

	public static final EntityCondition.Type<AllOfEntityCondition> ALL_OF = registerInternal("all_of", AllOfEntityCondition.MAP_CODEC, AllOfEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<AnyOfEntityCondition> ANY_OF = registerInternal("any_of", AnyOfEntityCondition.MAP_CODEC, AnyOfEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<CompareEntityCondition> COMPARE = registerInternal("compare", CompareEntityCondition.MAP_CODEC, CompareEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<CompareToRangeEntityCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeEntityCondition.MAP_CODEC, CompareToRangeEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<ConstantEntityCondition> CONSTANT = registerInternal("constant", ConstantEntityCondition.MAP_CODEC, ConstantEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<DynamicEntityCondition> DYNAMIC = registerInternal("dynamic", DynamicEntityCondition.MAP_CODEC, DynamicEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<InvertedEntityCondition> INVERTED = registerInternal("inverted", InvertedEntityCondition.MAP_CODEC, InvertedEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<OffsetEntityCondition> OFFSET = registerInternal("offset", OffsetEntityCondition.MAP_CODEC, OffsetEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<ReferenceEntityCondition> REFERENCE = registerInternal("reference", ReferenceEntityCondition.MAP_CODEC, ReferenceEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<TestWorldEntityCondition> TEST_WORLD = registerInternal("test_world", TestWorldEntityCondition.MAP_CODEC, TestWorldEntityCondition.STREAM_CODEC);

	public static final EntityCondition.Type<ExistsEntityCondition> EXISTS = registerInternal("exists", ExistsEntityCondition.MAP_CODEC, ExistsEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<HasCollidedHorizontallyEntityCondition> HAS_COLLIDED_HORIZONTALLY = registerInternal("has_collided_horizontally", HasCollidedHorizontallyEntityCondition.MAP_CODEC, HasCollidedHorizontallyEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<HasEquippedItemEntityCondition> HAS_EQUIPPED_ITEM = registerInternal("has_equipped_item", HasEquippedItemEntityCondition.MAP_CODEC, HasEquippedItemEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<HasPowerEntityCondition> HAS_POWER = registerInternal("has_power", HasPowerEntityCondition.MAP_CODEC, HasPowerEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<HasPressedKeysSimultaneouslyEntityCondition> HAS_PRESSED_KEYS_SIMULTANEOUSLY = registerInternal("has_pressed_keys_simultaneously", HasPressedKeysSimultaneouslyEntityCondition.MAP_CODEC, HasPressedKeysSimultaneouslyEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsClimbingEntityCondition> IS_CLIMBING = registerInternal("is_climbing", IsClimbingEntityCondition.MAP_CODEC, IsClimbingEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsFallFlyingEntityCondition> IS_FALL_FLYING = registerInternal("is_fall_flying", IsFallFlyingEntityCondition.MAP_CODEC, IsFallFlyingEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsInvisibleEntityCondition> IS_INVISIBLE = registerInternal("is_invisible", IsInvisibleEntityCondition.MAP_CODEC, IsInvisibleEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsInBlockEntityCondition> IS_IN_BLOCK = registerInternal("is_in_block", IsInBlockEntityCondition.MAP_CODEC, IsInBlockEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsInTagEntityCondition> IS_IN_TAG = registerInternal("is_in_tag", IsInTagEntityCondition.MAP_CODEC, IsInTagEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsOfEntityCondition> IS_OF = registerInternal("is_of", IsOfEntityCondition.MAP_CODEC, IsOfEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsOnBlockEntityCondition> IS_ON_BLOCK = registerInternal("is_on_block", IsOnBlockEntityCondition.MAP_CODEC, IsOnBlockEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsOnFireEntityCondition> IS_ON_FIRE = registerInternal("is_on_fire", IsOnFireEntityCondition.MAP_CODEC, IsOnFireEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsPowerActiveEntityCondition> IS_POWER_ACTIVE = registerInternal("is_power_active", IsPowerActiveEntityCondition.MAP_CODEC, IsPowerActiveEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsSneakingEntityCondition> IS_SNEAKING = registerInternal("is_sneaking", IsSneakingEntityCondition.MAP_CODEC, IsSneakingEntityCondition.STREAM_CODEC);
	public static final EntityCondition.Type<IsSprintingEntityCondition> IS_SPRINTING = registerInternal("is_sprinting", IsSprintingEntityCondition.MAP_CODEC, IsSprintingEntityCondition.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <C extends EntityCondition> EntityCondition.Type<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <C extends EntityCondition> EntityCondition.Type<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return Registry.register(NeoApoliRegistries.ENTITY_CONDITION_TYPE, id, new EntityCondition.Type<>(mapCodec, streamCodec));
	}

}
