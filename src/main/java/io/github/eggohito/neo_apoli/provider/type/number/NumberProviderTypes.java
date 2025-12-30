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

	public static final NumberProviderType<AbsoluteNumberProvider> ABSOLUTE = registerInternal("absolute", AbsoluteNumberProvider.CODEC, AbsoluteNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<AddNumberProvider> ADD = registerInternal("add", AddNumberProvider.CODEC, AddNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BinomialNumberProvider> BINOMIAL = registerInternal("binomial", BinomialNumberProvider.CODEC, BinomialNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ChoiceNumberProvider> CHOICE = registerInternal("choice", ChoiceNumberProvider.CODEC, ChoiceNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ClampedNumberProvider> CLAMPED = registerInternal("clamped", ClampedNumberProvider.CODEC, ClampedNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ConditionalNumberProvider> CONDITIONAL = registerInternal("conditional", ConditionalNumberProvider.CODEC, ConditionalNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ConstantNumberProvider> CONSTANT = registerInternal("constant", ConstantNumberProvider.CODEC, ConstantNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<DivideNumberProvider> DIVIDE = registerInternal("divide", DivideNumberProvider.CODEC, DivideNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ExponentialNumberProvider> EXPONENTIAL = registerInternal("exponential", ExponentialNumberProvider.CODEC, ExponentialNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<LinearInterpolatedNumberProvider> LINEAR_INTERPOLATED = registerInternal("linear_interpolated", LinearInterpolatedNumberProvider.CODEC, LinearInterpolatedNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<MaxNumberProvider> MAX = registerInternal("max", MaxNumberProvider.CODEC, MaxNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<MinNumberProvider> MIN = registerInternal("min", MinNumberProvider.CODEC, MinNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<MultiplyNumberProvider> MULTIPLY = registerInternal("multiply", MultiplyNumberProvider.CODEC, MultiplyNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<NbtNumberProvider> NBT = registerInternal("nbt", NbtNumberProvider.CODEC, NbtNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<SubtractNumberProvider> SUBTRACT = registerInternal("subtract", SubtractNumberProvider.CODEC, SubtractNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<UniformNumberProvider> UNIFORM = registerInternal("uniform", UniformNumberProvider.CODEC, UniformNumberProvider.STREAM_CODEC);

	public static final NumberProviderType<AdjacentBlocksNumberProvider> ADJACENT_BLOCKS_NUMBER_PROVIDER = registerInternal("adjacent_blocks", AdjacentBlocksNumberProvider.CODEC, AdjacentBlocksNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<AttributeNumberProvider> ATTRIBUTE = registerInternal("attribute", AttributeNumberProvider.CODEC, AttributeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BlocksCollidingBoxNumberProvider> BLOCKS_COLLIDING_BOX = registerInternal("blocks_colliding_box", BlocksCollidingBoxNumberProvider.CODEC, BlocksCollidingBoxNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BlocksInRadiusNumberProvider> BLOCKS_IN_RADIUS = registerInternal("blocks_in_radius", BlocksInRadiusNumberProvider.CODEC, BlocksInRadiusNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BlocksIntersectingBoxNumberProvider> BLOCKS_INTERSECTING_BOX = registerInternal("blocks_intersecting_box", BlocksIntersectingBoxNumberProvider.CODEC, BlocksIntersectingBoxNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BoxComponentNumberProvider> BOX_COMPONENT = registerInternal("box/component", BoxComponentNumberProvider.CODEC, BoxComponentNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<BoxSizeNumberProvider> BOX_SIZE = registerInternal("box/size", BoxSizeNumberProvider.CODEC, BoxSizeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<CommandResultNumberProvider> COMMAND_RESULT = registerInternal("command_result", CommandResultNumberProvider.CODEC, CommandResultNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ContextKeyNumberProvider> CONTEXT_KEY = registerInternal("context_key", ContextKeyNumberProvider.CODEC, ContextKeyNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<CooldownProgressNumberProvider> COOLDOWN_PROGRESS = registerInternal("cooldown/progress", CooldownProgressNumberProvider.CODEC, CooldownProgressNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<CooldownRemainingTicksNumberProvider> COOLDOWN_REMAINING_TICKS = registerInternal("cooldown/remaining_ticks", CooldownRemainingTicksNumberProvider.CODEC, CooldownRemainingTicksNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<DamageAmountNumberProvider> DAMAGE_AMOUNT = registerInternal("damage_amount", DamageAmountNumberProvider.CODEC, DamageAmountNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<DistanceBetweenPositionsNumberProvider> DISTANCE_BETWEEN_POSITIONS = registerInternal("distance_between_positions", DistanceBetweenPositionsNumberProvider.CODEC, DistanceBetweenPositionsNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<EntitiesInRadiusFromEntityNumberProvider> ENTITIES_IN_RADIUS_FROM_ENTITY = registerInternal("entities_in_radius/from_entity", EntitiesInRadiusFromEntityNumberProvider.CODEC, EntitiesInRadiusFromEntityNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<EntitiesInRadiusFromPositionNumberProvider> ENTITIES_IN_RADIUS_FROM_POSITION = registerInternal("entities_in_radius/from_position", EntitiesInRadiusFromPositionNumberProvider.CODEC, EntitiesInRadiusFromPositionNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<EquippedEnchantmentLevelNumberProvider> EQUIPPED_ENCHANTMENT_LEVEL = registerInternal("equipped_enchantment_level", EquippedEnchantmentLevelNumberProvider.CODEC, EquippedEnchantmentLevelNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<FluidHeightFromEntityNumberProvider> FLUID_HEIGHT_FROM_ENTITY = registerInternal("fluid_height/from_entity", FluidHeightFromEntityNumberProvider.CODEC, FluidHeightFromEntityNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<FuelAmountNumberProvider> FUEL_AMOUNT = registerInternal("fuel_amount", FuelAmountNumberProvider.CODEC, FuelAmountNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ItemCountMaxNumberProvider> ITEM_COUNT_MAX = registerInternal("item_count/max", ItemCountMaxNumberProvider.CODEC, ItemCountMaxNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<ItemCountNumberProvider> ITEM_COUNT = registerInternal("item_count", ItemCountNumberProvider.CODEC, ItemCountNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<KeyPressedTicksNumberProvider> KEY_PRESSED_TICKS = registerInternal("key_pressed/ticks", KeyPressedTicksNumberProvider.CODEC, KeyPressedTicksNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<KeyPressedTimeNumberProvider> KEY_PRESSED_TIME = registerInternal("key_pressed/time", KeyPressedTimeNumberProvider.CODEC, KeyPressedTimeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<LightLevelNumberProvider> LIGHT_LEVEL = registerInternal("light_level", LightLevelNumberProvider.CODEC, LightLevelNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<TimeNumberProvider> TIME = registerInternal("time", TimeNumberProvider.CODEC, TimeNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<VectorComponentNumberProvider> VECTOR_COMPONENT = registerInternal("vector/component", VectorComponentNumberProvider.CODEC, VectorComponentNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<VelocityComponentNumberProvider> VELOCITY_COMPONENT = registerInternal("velocity/component", VelocityComponentNumberProvider.CODEC, VelocityComponentNumberProvider.STREAM_CODEC);
	public static final NumberProviderType<VelocityMagnitudeNumberProvider> VELOCITY_MAGNITUDE = registerInternal("velocity/magnitude", VelocityMagnitudeNumberProvider.CODEC, VelocityMagnitudeNumberProvider.STREAM_CODEC);

	public static void registerAll() {

	}

	private static <P extends NumberProvider> NumberProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends NumberProvider> NumberProviderType<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, id, new NumberProviderType<>(mapCodec, packetCodec));
	}

}
