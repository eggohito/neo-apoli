package io.github.eggohito.neo_apoli.provider.type.number;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.*;
import io.github.eggohito.neo_apoli.provider.meta.number.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class NumberProviderTypes {

	public static final NumberProviderType<AbsoluteNumberProvider> ABSOLUTE = registerInternal("absolute", AbsoluteNumberProvider.CODEC, AbsoluteNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<AddNumberProvider> ADD = registerInternal("add", AddNumberProvider.CODEC, AddNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<BinomialNumberProvider> BINOMIAL = registerInternal("binomial", BinomialNumberProvider.CODEC, BinomialNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ClampedNumberProvider> CLAMPED = registerInternal("clamped", ClampedNumberProvider.CODEC, ClampedNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ConstantNumberProvider> CONSTANT = registerInternal("constant", ConstantNumberProvider.CODEC, ConstantNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<DivideNumberProvider> DIVIDE = registerInternal("divide", DivideNumberProvider.CODEC, DivideNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<IfElseListNumberProvider> IF_ELSE_LIST = registerInternal("if_else_list", IfElseListNumberProvider.CODEC, IfElseListNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<IfElseNumberProvider> IF_ELSE = registerInternal("if_else", IfElseNumberProvider.CODEC, IfElseNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<LinearInterpolationNumberProvider> LINEAR_INTERPOLATION = registerInternal("linear_interpolation", LinearInterpolationNumberProvider.CODEC, LinearInterpolationNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<MaxNumberProvider> MAX = registerInternal("max", MaxNumberProvider.CODEC, MaxNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<MinNumberProvider> MIN = registerInternal("min", MinNumberProvider.CODEC, MinNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<MultiplyNumberProvider> MULTIPLY = registerInternal("multiply", MultiplyNumberProvider.CODEC, MultiplyNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<NbtNumberProvider> NBT = registerInternal("nbt", NbtNumberProvider.CODEC, NbtNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<SubtractNumberProvider> SUBTRACT = registerInternal("subtract", SubtractNumberProvider.CODEC, SubtractNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<UniformNumberProvider> UNIFORM = registerInternal("uniform", UniformNumberProvider.CODEC, UniformNumberProvider.PACKET_CODEC);

	public static final NumberProviderType<AdjacentBlocksNumberProvider> ADJACENT_BLOCKS = registerInternal("adjacent_blocks", AdjacentBlocksNumberProvider.CODEC, AdjacentBlocksNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<BlocksInRadiusNumberProvider> BLOCKS_IN_RADIUS = registerInternal("blocks_in_radius", BlocksInRadiusNumberProvider.CODEC, BlocksInRadiusNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<BlocksIntersectingBoxNumberProvider> BLOCKS_INTERSECTING_BOX = registerInternal("blocks_intersecting_box", BlocksIntersectingBoxNumberProvider.CODEC, BlocksIntersectingBoxNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<CollidedBlocksNumberProvider> COLLIDED_BLOCKS = registerInternal("collided_blocks", CollidedBlocksNumberProvider.CODEC, CollidedBlocksNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<AttributeNumberProvider> ATTRIBUTE = registerInternal("attribute", AttributeNumberProvider.CODEC, AttributeNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<BiEntitiesInRadiusNumberProvider> BIENTITIES_IN_RADIUS = registerInternal("bientities_in_radius", BiEntitiesInRadiusNumberProvider.CODEC, BiEntitiesInRadiusNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<CommandResultNumberProvider> COMMAND_RESULT = registerInternal("command_result", CommandResultNumberProvider.CODEC, CommandResultNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<DamageAmountNumberProvider> DAMAGE_AMOUNT = registerInternal("damage_amount", DamageAmountNumberProvider.CODEC, DamageAmountNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<DistanceBetweenBiEntitiesNumberProvider> DISTANCE_BETWEEN_BIENTITIES = registerInternal("distance_between_bientities", DistanceBetweenBiEntitiesNumberProvider.CODEC, DistanceBetweenBiEntitiesNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<DistanceBetweenEntityAndBlockNumberProvider> DISTANCE_BETWEEN_ENTITY_AND_BLOCK = registerInternal("distance_between_entity_and_block", DistanceBetweenEntityAndBlockNumberProvider.CODEC, DistanceBetweenEntityAndBlockNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<EntitiesInRadiusNumberProvider> ENTITIES_IN_RADIUS = registerInternal("entities_in_radius", EntitiesInRadiusNumberProvider.CODEC, EntitiesInRadiusNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<FuelAmountNumberProvider> FUEL_AMOUNT = registerInternal("fuel_amount", FuelAmountNumberProvider.CODEC, FuelAmountNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ItemCountNumberProvider> ITEM_COUNT = registerInternal("item_count", ItemCountNumberProvider.CODEC, ItemCountNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<ItemMaxCountNumberProvider> ITEM_MAX_COUNT = registerInternal("item_max_count", ItemMaxCountNumberProvider.CODEC, ItemMaxCountNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<LightLevelNumberProvider> LIGHT_LEVEL = registerInternal("light_level", LightLevelNumberProvider.CODEC, LightLevelNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<PositionNumberProvider> POSITION = registerInternal("position", PositionNumberProvider.CODEC, PositionNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<TimeNumberProvider> TIME = registerInternal("time", TimeNumberProvider.CODEC, TimeNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<VelocityMagnitudeNumberProvider> VELOCITY_MAGNITUDE = registerInternal("velocity/magnitude", VelocityMagnitudeNumberProvider.CODEC, VelocityMagnitudeNumberProvider.PACKET_CODEC);
	public static final NumberProviderType<VelocityNumberProvider> VELOCITY = registerInternal("velocity", VelocityNumberProvider.CODEC, VelocityNumberProvider.PACKET_CODEC);

	public static void registerAll() {

	}

	private static <P extends NumberProvider> NumberProviderType<P> registerInternal(String path, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <P extends NumberProvider> NumberProviderType<P> register(Identifier id, MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {
		return Registry.register(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, id, new NumberProviderType<>(mapCodec, packetCodec));
	}

}
