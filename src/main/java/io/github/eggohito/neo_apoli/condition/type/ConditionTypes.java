package io.github.eggohito.neo_apoli.condition.type;

import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.custom.*;
import io.github.eggohito.neo_apoli.condition.type.bientity.BiEntityConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.block.BlockConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.damage.DamageConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.effect.EffectConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.entity.EntityConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.fluid.FluidConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.item.ItemConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.key.KeyConditionTypes;
import io.github.eggohito.neo_apoli.condition.type.world.WorldConditionTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public final class ConditionTypes {

	public static final ConditionType<AllOfCondition> ALL_OF = registerInternal("all_of", AllOfCondition.MAP_CODEC, AllOfCondition.STREAM_CODEC);
	public static final ConditionType<AnyOfCondition> ANY_OF = registerInternal("any_of", AnyOfCondition.MAP_CODEC, AnyOfCondition.STREAM_CODEC);
	public static final ConditionType<CompareCondition> COMPARE = registerInternal("compare", CompareCondition.MAP_CODEC, CompareCondition.STREAM_CODEC);
	public static final ConditionType<CompareToRangeCondition> COMPARE_TO_RANGE = registerInternal("compare_to_range", CompareToRangeCondition.MAP_CODEC, CompareToRangeCondition.STREAM_CODEC);
	public static final ConditionType<ConstantCondition> CONSTANT = registerInternal("constant", ConstantCondition.MAP_CODEC, ConstantCondition.STREAM_CODEC);
	public static final ConditionType<DynamicCondition> DYNAMIC = registerInternal("dynamic", DynamicCondition.MAP_CODEC, DynamicCondition.STREAM_CODEC);
	public static final ConditionType<InvertedCondition> INVERTED = registerInternal("inverted", InvertedCondition.MAP_CODEC, InvertedCondition.STREAM_CODEC);
	public static final ConditionType<ReferenceCondition> REFERENCE = registerInternal("reference", ReferenceCondition.MAP_CODEC, ReferenceCondition.STREAM_CODEC);
	public static final ConditionType<TestEntityCondition> TEST_ENTITY = registerInternal("test_entity", TestEntityCondition.MAP_CODEC, TestEntityCondition.STREAM_CODEC);
	public static final ConditionType<TestWorldCondition> TEST_WORLD = registerInternal("test_world", TestWorldCondition.MAP_CODEC, TestWorldCondition.STREAM_CODEC);

	public static void registerAll() {
		BiEntityConditionTypes.registerAll();
		BlockConditionTypes.registerAll();
		DamageConditionTypes.registerAll();
		EffectConditionTypes.registerAll();
		EntityConditionTypes.registerAll();
		FluidConditionTypes.registerAll();
		ItemConditionTypes.registerAll();
		KeyConditionTypes.registerAll();
		WorldConditionTypes.registerAll();
	}

	private static <C extends Condition> ConditionType<C> registerInternal(String path, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> packetCodec) {
		return register(NeoApoli.id(path), mapCodec, packetCodec);
	}

	public static <C extends Condition> ConditionType<C> register(ResourceLocation id, MapCodec<C> mapCodec, StreamCodec<RegistryFriendlyByteBuf, C> streamCodec) {
		return register(id, new ConditionType<>() {

			@Override
			public MapCodec<C> mapCodec() {
				return mapCodec;
			}

			@Override
			public StreamCodec<RegistryFriendlyByteBuf, C> streamCodec() {
				return streamCodec;
			}

		});
	}

	public static <C extends Condition, T extends ConditionType<C>> T register(ResourceLocation id, T type) {
		return Registry.register(NeoApoliRegistries.CONDITION_TYPE, id, type);
	}

}
