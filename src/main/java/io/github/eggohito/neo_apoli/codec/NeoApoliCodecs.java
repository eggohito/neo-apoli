package io.github.eggohito.neo_apoli.codec;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.mixin.access.TagParserAccessor;
import io.github.eggohito.neo_apoli.util.AttributedAttributeModifier;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.HandProperty;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class NeoApoliCodecs {

	public static final Codec<Set<ResourceLocation>> MUTABLE_NON_EMPTY_IDENTIFIER_SET = ResourceLocation.CODEC.listOf(1, Integer.MAX_VALUE).xmap(ObjectOpenHashSet::new, ObjectArrayList::new);

	public static final Codec<InteractionHand> HAND = HandProperty.CODEC.xmap(HandProperty::get, HandProperty::fromHand);

	public static final Codec<List<InteractionHand>> HANDS = ExtraCodecs.nonEmptyList(HAND.listOf());

	public static final Codec<EnumSet<InteractionHand>> HAND_SET = HANDS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final Codec<Number> NUMBER = new Codec<>() {

		@Override
		public <I> DataResult<Pair<Number, I>> decode(DynamicOps<I> ops, I input) {
			return ops.getNumberValue(input).map(number -> Pair.of(number, input));
		}

		@Override
		public <I> DataResult<I> encode(Number number, DynamicOps<I> ops, I prefix) {
			return DataResult.success(ops.createNumeric(number));
		}

	};

	public static final Codec<LightLayer> LIGHT_TYPE = CodecUtil.enumType(LightLayer.class);

	public static final Codec<Explosion.BlockInteraction> DESTRUCTION_TYPE = CodecUtil.enumType(Explosion.BlockInteraction.class);

	public static final Codec<List<Direction>> DIRECTIONS = Direction.CODEC.listOf();

	public static final Codec<EnumSet<Direction>> DIRECTION_SET = DIRECTIONS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final Codec<InteractionResult> ACTION_RESULT = CodecUtil.mapped(MiscUtil.ACTION_RESULTS);

	public static final Codec<Tag> STRINGIFIED_NBT = Codec.STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(TagParserAccessor.getDefaultReader().parseFully(str));
			}

			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Error parsing string NBT: " + e.getMessage());
			}

		},
		nbtElement -> {

			StringTagVisitor nbtWriter = new StringTagVisitor();
			nbtElement.accept(nbtWriter);

			return nbtWriter.build();

		}
	);

	public static final Codec<Tag> NBT_ELEMENT = Codec.PASSTHROUGH.xmap(
		dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(),
		nbtElement -> new Dynamic<>(NbtOps.INSTANCE, nbtElement)
	);

	public static final Codec<Tag> REGULAR_OR_STRINGIFIED_NBT_ELEMENT = new MultiAlternativeCodec<>(NBT_ELEMENT, STRINGIFIED_NBT);

	public static final Codec<BlockState> STRINGIFIED_BLOCK_STATE = Codec.STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK, str, true).blockState());
			}

			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Couldn't parse string as block state: " + e.getMessage());
			}

		},
		BlockStateParser::serialize
	);

	public static final Codec<BlockState> REGULAR_OR_STRINGIFIED_BLOCK_STATE = Codec.withAlternative(BlockState.CODEC, STRINGIFIED_BLOCK_STATE);

	public static final Codec<List<AttributedAttributeModifier>> NONEMPTY_ATTRIBUTE_MODIFIERS = ExtraCodecs.nonEmptyList(AttributedAttributeModifier.CODEC.listOf());

	public static final Codec<Vec3> VECTOR_3_DOUBLE = new MultiAlternativeCodec<>(Vec3.CODEC, NeoApoliMapCodecs.VECTOR_3_DOUBLE.codec());

	public static final Codec<TypedContextKey<Number>> NUMBER_CONTEXT_KEY = CodecUtil.createContextKeyCodec("number", Number.class);

	public static final Codec<TypedContextKey<Entity>> ENTITY_CONTEXT_KEY = CodecUtil.createContextKeyCodec("entity", Entity.class);

	public static final Codec<ClipContext.Block> BLOCK_CLIP_CONTEXT = CodecUtil.enumType(ClipContext.Block.class);

	public static final Codec<ClipContext.Fluid> FLUID_CLIP_CONTEXT = CodecUtil.enumType(ClipContext.Fluid.class);

	public static final Codec<EntityAnchorArgument.Anchor> ENTITY_ANCHOR = CodecUtil.enumType(EntityAnchorArgument.Anchor.class);

}
