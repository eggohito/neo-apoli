package io.github.eggohito.neo_apoli.codec;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import io.github.eggohito.neo_apoli.mixin.access.StringNbtReaderAccessor;
import io.github.eggohito.neo_apoli.util.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.visitor.StringNbtWriter;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.explosion.Explosion;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NeoApoliCodecs {

	public static final Codec<Set<Identifier>> MUTABLE_NON_EMPTY_IDENTIFIER_SET = Identifier.CODEC.listOf(1, Integer.MAX_VALUE).xmap(ObjectOpenHashSet::new, ObjectArrayList::new);

	public static final Codec<Hand> HAND = HandProperty.CODEC.xmap(HandProperty::get, HandProperty::fromHand);

	public static final Codec<List<Hand>> HANDS = HAND.listOf();

	public static final Codec<EnumSet<Hand>> HAND_SET = HANDS.xmap(EnumSet::copyOf, ObjectArrayList::new);

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

	public static final Codec<LightType> LIGHT_TYPE = CodecUtil.enumType(LightType.class);

	public static final Codec<Map<EntityParameter, EntityParameter>> ENTITY_PARAMETER_MAP = Codec.unboundedMap(EntityParameter.CODEC, EntityParameter.CODEC);

	public static final Codec<Explosion.DestructionType> DESTRUCTION_TYPE = CodecUtil.enumType(Explosion.DestructionType.class);

	public static final Codec<List<Direction>> DIRECTIONS = Direction.CODEC.listOf();

	public static final Codec<EnumSet<Direction>> DIRECTION_SET = DIRECTIONS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final Codec<ActionResult> ACTION_RESULT = CodecUtil.mapped(MiscUtil.ACTION_RESULTS);

	public static final Codec<NbtElement> STRINGIFIED_NBT = Codec.STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(StringNbtReaderAccessor.getDefaultReader().read(str));
			}

			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Error parsing string NBT: " + e.getMessage());
			}

		},
		nbtElement -> {

			StringNbtWriter nbtWriter = new StringNbtWriter();
			nbtElement.accept(nbtWriter);

			return nbtWriter.getString();

		}
	);

	public static final Codec<NbtElement> NBT_ELEMENT = Codec.PASSTHROUGH.xmap(
		dynamic -> dynamic.convert(NbtOps.INSTANCE).getValue(),
		nbtElement -> new Dynamic<>(NbtOps.INSTANCE, nbtElement)
	);

	public static final Codec<NbtElement> REGULAR_OR_STRINGIFIED_NBT_ELEMENT = new MultiAlternativeCodec<>(NBT_ELEMENT, STRINGIFIED_NBT);

	public static final Codec<BlockState> STRINGIFIED_BLOCK_STATE = Codec.STRING.comapFlatMap(
		str -> {

			try {
				return DataResult.success(BlockArgumentParser.block(Registries.BLOCK, str, true).blockState());
			}

			catch (CommandSyntaxException e) {
				return DataResult.error(() -> "Couldn't parse string as block state: " + e.getMessage());
			}

		},
		BlockArgumentParser::stringifyBlockState
	);

	public static final Codec<BlockState> REGULAR_OR_STRINGIFIED_BLOCK_STATE = Codec.withAlternative(BlockState.CODEC, STRINGIFIED_BLOCK_STATE);

	public static final Codec<TagKey<EntityType<?>>> UNPREFIXED_ENTITY_TYPE_TAG = TagKey.unprefixedCodec(RegistryKeys.ENTITY_TYPE);

	public static final Codec<List<AttributeModifier>> NONEMPTY_ATTRIBUTE_MODIFIERS = Codecs.nonEmptyList(AttributeModifier.CODEC.listOf());

}
