package io.github.eggohito.neo_apoli.provider.type.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.number.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class NumberProviderTypes {

	public static final NumberProviderType<AbsoluteNumberProvider> ABSOLUTE = registerInternal("absolute", AbsoluteNumberProvider.CODEC, AbsoluteNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<AddNumberProvider> ADD = registerInternal("add", AddNumberProvider.CODEC, AddNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<BinomialNumberProvider> BINOMIAL = registerInternal("binomial", BinomialNumberProvider.CODEC, BinomialNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ChoiceNumberProvider> CHOICE = registerInternal("choice", ChoiceNumberProvider.CODEC, ChoiceNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ClampedNumberProvider> CLAMPED = registerInternal("clamped", ClampedNumberProvider.CODEC, ClampedNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ConditionalNumberProvider> CONDITIONAL = registerInternal("conditional", ConditionalNumberProvider.CODEC, ConditionalNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ConstantNumberProvider> CONSTANT = registerInternal("constant", ConstantNumberProvider.CODEC, ConstantNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<DivideNumberProvider> DIVIDE = registerInternal("divide", DivideNumberProvider.CODEC, DivideNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<LinearInterpolatedNumberProvider> LINEAR_INTERPOLATED = registerInternal("linear_interpolated", LinearInterpolatedNumberProvider.CODEC, LinearInterpolatedNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<MaxNumberProvider> MAX = registerInternal("max", MaxNumberProvider.CODEC, MaxNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<MinNumberProvider> MIN = registerInternal("min", MinNumberProvider.CODEC, MinNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<MultiplyNumberProvider> MULTIPLY = registerInternal("multiply", MultiplyNumberProvider.CODEC, MultiplyNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<NbtNumberProvider> NBT = registerInternal("nbt", NbtNumberProvider.CODEC, NbtNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<SubtractNumberProvider> SUBTRACT = registerInternal("subtract", SubtractNumberProvider.CODEC, SubtractNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<UniformNumberProvider> UNIFORM = registerInternal("uniform", UniformNumberProvider.CODEC, UniformNumberProvider.PACKET_CODEC);

	public static final NumberProviderType<AdjacentBlocksNumberProvider> ADJACENT_BLOCKS_NUMBER_PROVIDER = registerInternal("adjacent_blocks", AdjacentBlocksNumberProvider.CODEC, AdjacentBlocksNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<AttributeNumberProvider> ATTRIBUTE = registerInternal("attribute", AttributeNumberProvider.CODEC, AttributeNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<BlocksCollidingBoxNumberProvider> BLOCKS_COLLIDING_BOX = registerInternal("blocks_colliding_box", BlocksCollidingBoxNumberProvider.CODEC, BlocksCollidingBoxNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<BlocksInRadiusNumberProvider> BLOCKS_IN_RADIUS = registerInternal("blocks_in_radius", BlocksInRadiusNumberProvider.CODEC, BlocksInRadiusNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<BlocksIntersectingBoxNumberProvider> BLOCKS_INTERSECTING_BOX = registerInternal("blocks_intersecting_box", BlocksIntersectingBoxNumberProvider.CODEC, BlocksIntersectingBoxNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<CommandResultNumberProvider> COMMAND_RESULT = registerInternal("command_result", CommandResultNumberProvider.CODEC, CommandResultNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<DamageAmountNumberProvider> DAMAGE_AMOUNT = registerInternal("damage_amount", DamageAmountNumberProvider.CODEC, DamageAmountNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<DistanceBetweenPositionsNumberProvider> DISTANCE_BETWEEN_POSITIONS = registerInternal("distance_between_positions", DistanceBetweenPositionsNumberProvider.CODEC, DistanceBetweenPositionsNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<EntitiesInRadiusFromEntityNumberProvider> ENTITIES_IN_RADIUS_FROM_ENTITY = registerInternal("entities_in_radius/from_entity", EntitiesInRadiusFromEntityNumberProvider.CODEC, EntitiesInRadiusFromEntityNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<EntitiesInRadiusFromPositionNumberProvider> ENTITIES_IN_RADIUS_FROM_POSITION = registerInternal("entities_in_radius/from_position", EntitiesInRadiusFromPositionNumberProvider.CODEC, EntitiesInRadiusFromPositionNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<FuelAmountNumberProvider> FUEL_AMOUNT = registerInternal("fuel_amount", FuelAmountNumberProvider.CODEC, FuelAmountNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ItemCountMaxNumberProvider> ITEM_COUNT_MAX = registerInternal("item_count/max", ItemCountMaxNumberProvider.CODEC, ItemCountMaxNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ItemCountNumberProvider> ITEM_COUNT = registerInternal("item_count", ItemCountNumberProvider.CODEC, ItemCountNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<KeyPressedTicksNumberProvider> KEY_PRESSED_TICKS = registerInternal("key_pressed/ticks", KeyPressedTicksNumberProvider.CODEC, KeyPressedTicksNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<KeyPressedTimeNumberProvider> KEY_PRESSED_TIME = registerInternal("key_pressed/time", KeyPressedTimeNumberProvider.CODEC, KeyPressedTimeNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<LightLevelNumberProvider> LIGHT_LEVEL = registerInternal("light_level", LightLevelNumberProvider.CODEC, LightLevelNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ParameterNumberProvider> PARAMETER = registerInternal("parameter", ParameterNumberProvider.CODEC, ParameterNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<PositionComponentNumberProvider> POSITION_COMPONENT = registerInternal("position/component", PositionComponentNumberProvider.CODEC, PositionComponentNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<TimeNumberProvider> TIME = registerInternal("time", TimeNumberProvider.CODEC, TimeNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<VelocityComponentNumberProvider> VELOCITY_COMPONENT = registerInternal("velocity/component", VelocityComponentNumberProvider.CODEC, VelocityComponentNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<VelocityMagnitudeNumberProvider> VELOCITY_MAGNITUDE = registerInternal("velocity/magnitude", VelocityMagnitudeNumberProvider.CODEC, VelocityMagnitudeNumberProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends NumberProvider> NumberProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends NumberProvider> NumberProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, id, new NumberProviderType<>(mapCodec, packetCodec));
	}

}
