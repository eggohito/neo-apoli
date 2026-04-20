package io.github.eggohito.neo_apoli.codec;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.mixin.access.BlockInputAccessor;
import io.github.eggohito.neo_apoli.mixin.access.TagParserAccessor;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.registry.NeoApoliParticleTypes;
import io.github.eggohito.neo_apoli.util.AttributedModifier;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.tag.LazyTagLike;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class NeoApoliCodecs {

	public static final Codec<Set<ResourceLocation>> IDENTIFIER_SET = ResourceLocation.CODEC.listOf().xmap(Set::copyOf, List::copyOf);

	public static final Codec<Set<ResourceLocation>> NON_EMPTY_IDENTIFIER_SET = CodecUtil.nonEmptySet(IDENTIFIER_SET);

	public static final Codec<InteractionHand> HAND = CodecUtil.enumType(InteractionHand.class, ImmutableMap.<String, InteractionHand>builder()
		.put("mainhand", InteractionHand.MAIN_HAND)
		.put("offhand", InteractionHand.OFF_HAND)
		.build());

	public static final Codec<List<InteractionHand>> HANDS = ExtraCodecs.nonEmptyList(HAND.listOf());

	public static final Codec<EnumSet<InteractionHand>> HAND_SET = HANDS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final Codec<LightLayer> LIGHT_TYPE = CodecUtil.enumType(LightLayer.class);

	public static final Codec<Explosion.BlockInteraction> DESTRUCTION_TYPE = CodecUtil.enumType(Explosion.BlockInteraction.class);

	public static final Codec<List<Direction>> DIRECTIONS = Direction.CODEC.listOf();

	public static final Codec<EnumSet<Direction>> DIRECTION_SET = DIRECTIONS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final Codec<InteractionResult> INTERACTION_RESULT = CodecUtil.mapped(MiscUtil.INTERACTION_RESULTS);

	public static final Codec<Tag> STRINGIFIED_TAG = Codec.STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(TagParserAccessor.getDefaultReader().parseFully(str));
			}

			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Error parsing string NBT: " + e.getMessage());
			}

		},
		Tag::toString
	);

	public static final Codec<Tag> TAG = Codec.PASSTHROUGH.xmap(
		dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(),
		nbtElement -> new Dynamic<>(NbtOps.INSTANCE, nbtElement)
	);

	public static final Codec<CompoundTag> STRINGIFIED_COMPOUND_TAG = Codec.STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(TagParser.parseCompoundFully(str));
			}

			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Error parsing string NBT: " + e.getMessage());
			}

		},
		CompoundTag::toString
	);

	public static final Codec<CompoundTag> COMPOUND_TAG = TAG.comapFlatMap(MiscUtil::asCompoundTag, Function.identity());

	public static final Codec<Tag> REGULAR_OR_STRINGIFIED_TAG = new MultiAlternativeCodec<>(TAG, STRINGIFIED_TAG);

	public static final Codec<CompoundTag> REGULAR_OR_STRINGIFIED_COMPOUND_TAG = new MultiAlternativeCodec<>(COMPOUND_TAG, STRINGIFIED_COMPOUND_TAG);

	public static final Codec<BlockInput> BLOCK_INPUT = NeoApoliMapCodecs.BLOCK_INPUT.codec();

	public static final Codec<BlockInput> STRINGIFIED_BLOCK_INPUT = Codec.STRING.comapFlatMap(
		input -> {

			try {

				BlockStateParser.BlockResult blockResult = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, input, true);
				BlockInput blockInput = new BlockInput(blockResult.blockState(), blockResult.properties().keySet(), blockResult.nbt());

				return DataResult.success(blockInput);

			}

			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Couldn't parse string as block input: " + e.getMessage());
			}

		},
		blockInput -> {

			String blockStateString = BlockStateParser.serialize(blockInput.getState());
			String tagString = Optional.ofNullable(((BlockInputAccessor) blockInput).getTag())
				.map(CompoundTag::toString)
				.orElse("");

			return blockStateString + tagString;

		}
	);

	public static final Codec<BlockInput> REGULAR_OR_STRINGIFIED_BLOCK_INPUT = new MultiAlternativeCodec<>(BLOCK_INPUT, STRINGIFIED_BLOCK_INPUT);

	public static final Codec<List<AttributedModifier>> NONEMPTY_ATTRIBUTE_MODIFIERS = ExtraCodecs.nonEmptyList(AttributedModifier.CODEC.listOf());

	public static final Codec<Vec3> VECTOR_3_DOUBLE = new MultiAlternativeCodec<>(Vec3.CODEC, NeoApoliMapCodecs.VECTOR_3_DOUBLE.codec());

	public static final Codec<ClipContext.Block> BLOCK_CLIP_CONTEXT = CodecUtil.enumType(ClipContext.Block.class);

	public static final Codec<ClipContext.Fluid> FLUID_CLIP_CONTEXT = CodecUtil.enumType(ClipContext.Fluid.class);

	public static final Codec<EntityAnchorArgument.Anchor> ENTITY_ANCHOR = CodecUtil.enumType(EntityAnchorArgument.Anchor.class);

	public static final Codec<Difficulty> DIFFICULTY = CodecUtil.enumType(Difficulty.class);

	public static final Codec<LazyTagLike<EntityType<?>>> LAZY_ENTITY_TYPE_TAG_LIKE = LazyTagLike.createLazyCodec(BuiltInRegistries.ENTITY_TYPE);

	public static final Codec<LazyTagLike<PowerEntry<?>>> LAZY_POWER_TAG_LIKE = LazyTagLike.createLazyCodec(PowerManager.TAG_LOOKUP);

	public static final Codec<SimpleParticleType> SIMPLE_PARTICLE = NeoApoliParticleTypes.CODEC.comapFlatMap(
		particleType -> particleType instanceof SimpleParticleType simpleParticleType
			? DataResult.success(simpleParticleType)
			: DataResult.error(() -> "Particle \"" + RegistryUtil.getId(BuiltInRegistries.PARTICLE_TYPE, particleType) + "\" requires parameters!"),
		Function.identity()
	);

	public static final Codec<ParticleOptions> PARTICLE_OPTIONS = Codec.withAlternative(NeoApoliParticleTypes.OPTIONS_CODEC, SIMPLE_PARTICLE);

	public static final Codec<RoundingMode> ROUNDING_MODE = CodecUtil.enumType(RoundingMode.class);

	public static final Codec<Map<Pattern, String>> REPLACEMENT_MAP = ExtraCodecs.strictUnboundedMap(ExtraCodecs.PATTERN, Codec.STRING);

}
