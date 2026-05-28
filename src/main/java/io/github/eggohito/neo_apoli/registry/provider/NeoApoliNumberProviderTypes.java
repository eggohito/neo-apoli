package io.github.eggohito.neo_apoli.registry.provider;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.provider.custom.number.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class NeoApoliNumberProviderTypes {

	public static final NumberProvider.Type<AbsoluteNumberProvider> ABSOLUTE = registerInternal("absolute", AbsoluteNumberProvider.CODEC, AbsoluteNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<BinomialNumberProvider> BINOMIAL = registerInternal("binomial", BinomialNumberProvider.CODEC, BinomialNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ClampedNumberProvider> CLAMPED = registerInternal("clamped", ClampedNumberProvider.CODEC, ClampedNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ConditionalNumberProvider> CONDITIONAL = registerInternal("conditional", ConditionalNumberProvider.CODEC, ConditionalNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ConstantNumberProvider> CONSTANT = registerInternal("constant", ConstantNumberProvider.CODEC, ConstantNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ContextNumberProvider> CONTEXT = registerInternal("context", ContextNumberProvider.CODEC, ContextNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<DifferenceNumberProvider> DIFFERENCE = registerInternal("difference", DifferenceNumberProvider.CODEC, DifferenceNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<LinearInterpolatedNumberProvider> LINEAR_INTERPOLATED = registerInternal("linear_interpolated", LinearInterpolatedNumberProvider.CODEC, LinearInterpolatedNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<MaxNumberProvider> MAX = registerInternal("max", MaxNumberProvider.CODEC, MaxNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<MinNumberProvider> MIN = registerInternal("min", MinNumberProvider.CODEC, MinNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<NbtNumberProvider> NBT = registerInternal("nbt", NbtNumberProvider.CODEC, NbtNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<PowerNumberProvider> POWER = registerInternal("power", PowerNumberProvider.CODEC, PowerNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ProductNumberProvider> PRODUCT = registerInternal("product", ProductNumberProvider.CODEC, ProductNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<QuotientNumberProvider> QUOTIENT = registerInternal("quotient", QuotientNumberProvider.CODEC, QuotientNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<SumNumberProvider> SUM = registerInternal("sum", SumNumberProvider.CODEC, SumNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<SwitchNumberProvider> SWITCH = registerInternal("switch", SwitchNumberProvider.CODEC, SwitchNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<UniformNumberProvider> UNIFORM = registerInternal("uniform", UniformNumberProvider.CODEC, UniformNumberProvider.STREAM_CODEC);

	public static final NumberProvider.Type<AdjacentBlocksNumberProvider> ADJACENT_BLOCKS = registerInternal("adjacent_blocks", AdjacentBlocksNumberProvider.CODEC, AdjacentBlocksNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<BlocksCollidingBoxNumberProvider> BLOCKS_COLLIDING_BOX = registerInternal("blocks_colliding_box", BlocksCollidingBoxNumberProvider.CODEC, BlocksCollidingBoxNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<BlocksInRadiusNumberProvider> BLOCKS_IN_RADIUS = registerInternal("blocks_in_radius", BlocksInRadiusNumberProvider.CODEC, BlocksInRadiusNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<BlocksIntersectingBoxNumberProvider> BLOCKS_INTERSECTING_BOX = registerInternal("blocks_intersecting_box", BlocksIntersectingBoxNumberProvider.CODEC, BlocksIntersectingBoxNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<BoxComponentNumberProvider> BOX_COMPONENT = registerInternal("box/component", BoxComponentNumberProvider.CODEC, BoxComponentNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<BoxSizeNumberProvider> BOX_SIZE = registerInternal("box/size", BoxSizeNumberProvider.CODEC, BoxSizeNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<BrightnessNumberProvider> BRIGHTNESS = registerInternal("brightness", BrightnessNumberProvider.CODEC, BrightnessNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<CommandResultNumberProvider> COMMAND_RESULT = registerInternal("command_result", CommandResultNumberProvider.CODEC, CommandResultNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<DistanceBetweenPositionsNumberProvider> DISTANCE_BETWEEN_POSITIONS = registerInternal("distance_between_positions", DistanceBetweenPositionsNumberProvider.CODEC, DistanceBetweenPositionsNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<EffectAmplifierNumberProvider> EFFECT_AMPLIFIER = registerInternal("effect/amplifier", EffectAmplifierNumberProvider.CODEC, EffectAmplifierNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<EntitiesInRadiusNumberProvider> ENTITIES_IN_RADIUS = registerInternal("entities_in_radius", EntitiesInRadiusNumberProvider.CODEC, EntitiesInRadiusNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<EntityActiveEffectsNumberProvider> ENTITY_ACTIVE_EFFECTS = registerInternal("entity/active_effects", EntityActiveEffectsNumberProvider.CODEC, EntityActiveEffectsNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<EntityAttributeNumberProvider> ENTITY_ATTRIBUTE = registerInternal("entity/attribute", EntityAttributeNumberProvider.CODEC, EntityAttributeNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<EntityFluidHeightNumberProvider> ENTITY_FLUID_HEIGHT = registerInternal("entity/fluid_height", EntityFluidHeightNumberProvider.CODEC, EntityFluidHeightNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<EquippedEnchantmentLevelNumberProvider> EQUIPPED_ENCHANTMENT_LEVEL = registerInternal("equipped_enchantment_level", EquippedEnchantmentLevelNumberProvider.CODEC, EquippedEnchantmentLevelNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<FoodLevelNumberProvider> FOOD_LEVEL = registerInternal("food_level", FoodLevelNumberProvider.CODEC, FoodLevelNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ItemAttributeNumberProvider> ITEM_ATTRIBUTE = registerInternal("item/attribute", ItemAttributeNumberProvider.CODEC, ItemAttributeNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ItemCountMaxNumberProvider> ITEM_COUNT_MAX = registerInternal("item/max_count", ItemCountMaxNumberProvider.CODEC, ItemCountMaxNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ItemCountNumberProvider> ITEM_COUNT = registerInternal("item/count", ItemCountNumberProvider.CODEC, ItemCountNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<ItemFuelNumberProvider> ITEM_FUEL = registerInternal("item/fuel", ItemFuelNumberProvider.CODEC, ItemFuelNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<KeyPressedTicksNumberProvider> KEY_PRESSED_TICKS = registerInternal("key/pressed_ticks", KeyPressedTicksNumberProvider.CODEC, KeyPressedTicksNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<KeyPressedTimeNumberProvider> KEY_PRESSED_TIME = registerInternal("key/pressed_time", KeyPressedTimeNumberProvider.CODEC, KeyPressedTimeNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<LightLevelNumberProvider> LIGHT_LEVEL = registerInternal("light_level", LightLevelNumberProvider.CODEC, LightLevelNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<PowerCooldownProgressNumberProvider> POWER_COOLDOWN_PROGRESS = registerInternal("power/cooldown/progress", PowerCooldownProgressNumberProvider.CODEC, PowerCooldownProgressNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<PowerCooldownRemainingTicksNumberProvider> POWER_COOLDOWN_REMAINING_TICKS = registerInternal("power/cooldown/remaining_ticks", PowerCooldownRemainingTicksNumberProvider.CODEC, PowerCooldownRemainingTicksNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<RoundNumberProvider> ROUND = registerInternal("round", RoundNumberProvider.CODEC, RoundNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<SlotIdNumberProvider> SLOT_ID = registerInternal("slot_id", SlotIdNumberProvider.CODEC, SlotIdNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<TimeNumberProvider> TIME = registerInternal("time", TimeNumberProvider.CODEC, TimeNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<VectorComponentNumberProvider> VECTOR_COMPONENT = registerInternal("vector/component", VectorComponentNumberProvider.CODEC, VectorComponentNumberProvider.STREAM_CODEC);
	public static final NumberProvider.Type<VectorLengthNumberProvider> VECTOR_LENGTH = registerInternal("vector/length", VectorLengthNumberProvider.CODEC, VectorLengthNumberProvider.STREAM_CODEC);

	public static void registerAll() {
		NumberProvider.Type.ALIASES.addPathAlias("abs", ABSOLUTE);
		NumberProvider.Type.ALIASES.addPathAlias("add", SUM);
		NumberProvider.Type.ALIASES.addPathAlias("div", QUOTIENT);
		NumberProvider.Type.ALIASES.addPathAlias("divide", QUOTIENT);
		NumberProvider.Type.ALIASES.addPathAlias("exponential", POWER);
		NumberProvider.Type.ALIASES.addPathAlias("lerp", LINEAR_INTERPOLATED);
		NumberProvider.Type.ALIASES.addPathAlias("mul", PRODUCT);
		NumberProvider.Type.ALIASES.addPathAlias("multiply", PRODUCT);
		NumberProvider.Type.ALIASES.addPathAlias("pow", POWER);
		NumberProvider.Type.ALIASES.addPathAlias("sub", DIFFERENCE);
		NumberProvider.Type.ALIASES.addPathAlias("subtract", DIFFERENCE);
	}

	private static <P extends NumberProvider> NumberProvider.Type<P> registerInternal(String path, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
		return register(NeoApoli.id(path), mapCodec, streamCodec);
	}

	public static <P extends NumberProvider> NumberProvider.Type<P> register(ResourceLocation id, MapCodec<P> mapCodec, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec) {
		return Registry.register(NeoApoliRegistries.NUMBER_PROVIDER_TYPE, id, new NumberProvider.Type<>(mapCodec, streamCodec));
	}

}
