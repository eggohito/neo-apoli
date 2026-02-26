package io.github.eggohito.neo_apoli.provider.type.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.number.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NumberProviderTypes {

	public static final NumberProviderType<AbsoluteNumberProvider> ABSOLUTE = registerInternal("absolute", AbsoluteNumberProvider.MAP_CODEC, AbsoluteNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<AddNumberProvider> ADD = registerInternal("add", AddNumberProvider.MAP_CODEC, AddNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BinomialNumberProvider> BINOMIAL = registerInternal("binomial", BinomialNumberProvider.MAP_CODEC, BinomialNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ClampedNumberProvider> CLAMPED = registerInternal("clamped", ClampedNumberProvider.MAP_CODEC, ClampedNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ConditionalNumberProvider> CONDITIONAL = registerInternal("conditional", ConditionalNumberProvider.MAP_CODEC, ConditionalNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ConstantNumberProvider> CONSTANT = registerInternal("constant", ConstantNumberProvider.MAP_CODEC, ConstantNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<DivideNumberProvider> DIVIDE = registerInternal("divide", DivideNumberProvider.MAP_CODEC, DivideNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ExponentialNumberProvider> EXPONENTIAL = registerInternal("exponential", ExponentialNumberProvider.MAP_CODEC, ExponentialNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<LinearInterpolatedNumberProvider> LINEAR_INTERPOLATED = registerInternal("linear_interpolated", LinearInterpolatedNumberProvider.MAP_CODEC, LinearInterpolatedNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<MaxNumberProvider> MAX = registerInternal("max", MaxNumberProvider.MAP_CODEC, MaxNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<MinNumberProvider> MIN = registerInternal("min", MinNumberProvider.MAP_CODEC, MinNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<MultiplyNumberProvider> MULTIPLY = registerInternal("multiply", MultiplyNumberProvider.MAP_CODEC, MultiplyNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<NbtNumberProvider> NBT = registerInternal("nbt", NbtNumberProvider.MAP_CODEC, NbtNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<SubtractNumberProvider> SUBTRACT = registerInternal("subtract", SubtractNumberProvider.MAP_CODEC, SubtractNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<SwitchNumberProvider> SWITCH = registerInternal("switch", SwitchNumberProvider.MAP_CODEC, SwitchNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<UniformNumberProvider> UNIFORM = registerInternal("uniform", UniformNumberProvider.MAP_CODEC, UniformNumberProvider.STREAM_CODEC);

	public static final NumberProviderType<AdjacentBlocksNumberProvider> ADJACENT_BLOCKS = registerInternal("adjacent_blocks", AdjacentBlocksNumberProvider.MAP_CODEC, AdjacentBlocksNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<AttributeNumberProvider> ATTRIBUTE = registerInternal("attribute", AttributeNumberProvider.MAP_CODEC, AttributeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BlocksCollidingBoxNumberProvider> BLOCKS_COLLIDING_BOX = registerInternal("blocks_colliding_box", BlocksCollidingBoxNumberProvider.MAP_CODEC, BlocksCollidingBoxNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BlocksInRadiusNumberProvider> BLOCKS_IN_RADIUS = registerInternal("blocks_in_radius", BlocksInRadiusNumberProvider.MAP_CODEC, BlocksInRadiusNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BlocksIntersectingBoxNumberProvider> BLOCKS_INTERSECTING_BOX = registerInternal("blocks_intersecting_box", BlocksIntersectingBoxNumberProvider.MAP_CODEC, BlocksIntersectingBoxNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BoxComponentNumberProvider> BOX_COMPONENT = registerInternal("box/component", BoxComponentNumberProvider.MAP_CODEC, BoxComponentNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BoxSizeNumberProvider> BOX_SIZE = registerInternal("box/size", BoxSizeNumberProvider.MAP_CODEC, BoxSizeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BrightnessNumberProvider> BRIGHTNESS = registerInternal("brightness", BrightnessNumberProvider.MAP_CODEC, BrightnessNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<CommandResultNumberProvider> COMMAND_RESULT = registerInternal("command_result", CommandResultNumberProvider.MAP_CODEC, CommandResultNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ContextNumberProvider> CONTEXT = registerInternal("context", ContextNumberProvider.MAP_CODEC, ContextNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<DistanceBetweenPositionsNumberProvider> DISTANCE_BETWEEN_POSITIONS = registerInternal("distance_between_positions", DistanceBetweenPositionsNumberProvider.MAP_CODEC, DistanceBetweenPositionsNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<EntitiesInRadiusFromEntityNumberProvider> ENTITIES_IN_RADIUS_FROM_ENTITY = registerInternal("entities_in_radius/from_entity", EntitiesInRadiusFromEntityNumberProvider.MAP_CODEC, EntitiesInRadiusFromEntityNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<EntitiesInRadiusFromPositionNumberProvider> ENTITIES_IN_RADIUS_FROM_POSITION = registerInternal("entities_in_radius/from_position", EntitiesInRadiusFromPositionNumberProvider.MAP_CODEC, EntitiesInRadiusFromPositionNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<EquippedEnchantmentLevelNumberProvider> EQUIPPED_ENCHANTMENT_LEVEL = registerInternal("equipped_enchantment_level", EquippedEnchantmentLevelNumberProvider.MAP_CODEC, EquippedEnchantmentLevelNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<FluidHeightFromEntityNumberProvider> FLUID_HEIGHT_FROM_ENTITY = registerInternal("fluid_height/from_entity", FluidHeightFromEntityNumberProvider.MAP_CODEC, FluidHeightFromEntityNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<FoodLevelNumberProvider> FOOD_LEVEL = registerInternal("food_level", FoodLevelNumberProvider.MAP_CODEC, FoodLevelNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<FuelAmountNumberProvider> FUEL_AMOUNT = registerInternal("fuel_amount", FuelAmountNumberProvider.MAP_CODEC, FuelAmountNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ItemAttributeNumberProvider> ITEM_ATTRIBUTE = registerInternal("item/attribute", ItemAttributeNumberProvider.MAP_CODEC, ItemAttributeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ItemCountMaxNumberProvider> ITEM_COUNT_MAX = registerInternal("item_count/max", ItemCountMaxNumberProvider.MAP_CODEC, ItemCountMaxNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ItemCountNumberProvider> ITEM_COUNT = registerInternal("item_count", ItemCountNumberProvider.MAP_CODEC, ItemCountNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<KeyPressedTicksNumberProvider> KEY_PRESSED_TICKS = registerInternal("key_pressed/ticks", KeyPressedTicksNumberProvider.MAP_CODEC, KeyPressedTicksNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<KeyPressedTimeNumberProvider> KEY_PRESSED_TIME = registerInternal("key_pressed/time", KeyPressedTimeNumberProvider.MAP_CODEC, KeyPressedTimeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<LightLevelNumberProvider> LIGHT_LEVEL = registerInternal("light_level", LightLevelNumberProvider.MAP_CODEC, LightLevelNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<PowerCooldownProgressNumberProvider> POWER_COOLDOWN_PROGRESS = registerInternal("power/cooldown/progress", PowerCooldownProgressNumberProvider.MAP_CODEC, PowerCooldownProgressNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<PowerCooldownRemainingTicksNumberProvider> POWER_COOLDOWN_REMAINING_TICKS = registerInternal("power/cooldown/remaining_ticks", PowerCooldownRemainingTicksNumberProvider.MAP_CODEC, PowerCooldownRemainingTicksNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<RoundNumberProvider> ROUND = registerInternal("round", RoundNumberProvider.MAP_CODEC, RoundNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<TimeNumberProvider> TIME = registerInternal("time", TimeNumberProvider.MAP_CODEC, TimeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<VectorComponentNumberProvider> VECTOR_COMPONENT = registerInternal("vector/component", VectorComponentNumberProvider.MAP_CODEC, VectorComponentNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<VectorLengthNumberProvider> VECTOR_LENGTH = registerInternal("vector/length", VectorLengthNumberProvider.MAP_CODEC, VectorLengthNumberProvider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends NumberProvider> NumberProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends NumberProvider> NumberProviderType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, id, new NumberProviderType<>(mapCodec, packetCodec));
	}

}
