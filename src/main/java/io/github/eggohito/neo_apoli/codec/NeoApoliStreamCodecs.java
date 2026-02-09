package io.github.eggohito.neo_apoli.codec;

import com.google.gson.internal.LazilyParsedNumber;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionManager;
import io.github.eggohito.neo_apoli.context.parameter.ContextParameter;
import io.github.eggohito.neo_apoli.mixin.access.TagEntryAccessor;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.AttributedModifier;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RecipeUtil;
import io.github.eggohito.neo_apoli.util.StreamCodecUtil;
import io.github.eggohito.neo_apoli.util.tag.LazyTagLike;
import io.github.eggohito.neo_apoli.util.tag.TagLike;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LightLayer;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class NeoApoliStreamCodecs {

	public static final StreamCodec<ByteBuf, Set<ResourceLocation>> MUTABLE_IDENTIFIER_SET = ByteBufCodecs.collection(ObjectOpenHashSet::new, ResourceLocation.STREAM_CODEC);

	public static final StreamCodec<ByteBuf, Set<ResourceLocation>> MUTABLE_NON_EMPTY_IDENTIFIER_SET = StreamCodecUtil.nonEmptyCollection(MUTABLE_IDENTIFIER_SET);

	public static final StreamCodec<ByteBuf, InteractionHand> HAND = StreamCodecUtil.enumType(InteractionHand.class);

	public static final StreamCodec<ByteBuf, List<InteractionHand>> HANDS = ByteBufCodecs.collection(ObjectArrayList::new, HAND);

	public static final StreamCodec<ByteBuf, EnumSet<InteractionHand>> HAND_SET = HANDS.map(EnumSet::copyOf, ObjectArrayList::new);

	public static final StreamCodec<FriendlyByteBuf, Number> NUMBER = new StreamCodec<>() {

		@Override
		public Number decode(FriendlyByteBuf buf) {
			byte type = buf.readByte();
			return switch (type) {
				case 0 ->
					buf.readByte();
				case 1 ->
					buf.readDouble();
				case 2 ->
					buf.readFloat();
				case 3 ->
					buf.readInt();
				case 4 ->
					buf.readLong();
				case 5 ->
					buf.readShort();
				case 6 ->
					new LazilyParsedNumber(buf.readUtf());
				default ->
					throw new IllegalArgumentException("Unsupported number type: " + type);
			};
		}

		@Override
		public void encode(FriendlyByteBuf buf, Number value) {
			switch (value) {
				case Byte b -> {
					buf.writeByte(0);
					buf.writeByte(b);
				}
				case Double d -> {
					buf.writeByte(1);
					buf.writeDouble(d);
				}
				case Float f -> {
					buf.writeByte(2);
					buf.writeFloat(f);
				}
				case Integer i -> {
					buf.writeByte(3);
					buf.writeInt(i);
				}
				case Long l -> {
					buf.writeByte(4);
					buf.writeLong(l);
				}
				case Short s -> {
					buf.writeByte(5);
					buf.writeShort(s);
				}
				default -> {
					buf.writeByte(6);
					buf.writeUtf(value.toString());
				}
			}
		}

	};

	public static final StreamCodec<ByteBuf, NbtPathArgument.NbtPath> NBT_PATH = ByteBufCodecs.fromCodecTrusted(NbtPathArgument.NbtPath.CODEC);

	public static final StreamCodec<ByteBuf, LightLayer> LIGHT_TYPE = StreamCodecUtil.enumType(LightLayer.class);

	public static final StreamCodec<ByteBuf, List<Direction>> DIRECTIONS = ByteBufCodecs.collection(ObjectArrayList::new, Direction.STREAM_CODEC);

	public static final StreamCodec<ByteBuf, EnumSet<Direction>> DIRECTION_SET = DIRECTIONS.map(EnumSet::copyOf, ObjectArrayList::new);

	public static final StreamCodec<ByteBuf, Direction.Axis> AXIS = StreamCodecUtil.enumType(Direction.Axis.class);

	public static final StreamCodec<ByteBuf, Explosion.BlockInteraction> DESTRUCTION_TYPE = StreamCodecUtil.enumType(Explosion.BlockInteraction.class);

	public static final StreamCodec<FriendlyByteBuf, InteractionResult> ACTION_RESULT = StreamCodecUtil.mapped(MiscUtil.ACTION_RESULTS);

	public static final StreamCodec<RegistryFriendlyByteBuf, BlockInput> BLOCK_INPUT = ByteBufCodecs.fromCodecWithRegistriesTrusted(NeoApoliCodecs.STRINGIFIED_BLOCK_INPUT);

	public static final StreamCodec<ByteBuf, TagKey<EntityType<?>>> ENTITY_TYPE_TAG = TagKey.streamCodec(Registries.ENTITY_TYPE);

	public static final StreamCodec<ByteBuf, Set<TagKey<EntityType<?>>>> ENTITY_TYPE_TAG_SET = ByteBufCodecs.collection(ObjectOpenHashSet::new, ENTITY_TYPE_TAG);

	public static final StreamCodec<RegistryFriendlyByteBuf, List<AttributedModifier>> ATTRIBUTE_MODIFIERS = ByteBufCodecs.collection(ObjectArrayList::new, AttributedModifier.STREAM_CODEC);

	public static final StreamCodec<ByteBuf, Dynamic<?>> PASSTHROUGH = ByteBufCodecs.fromCodec(Codec.PASSTHROUGH);

	public static final StreamCodec<RegistryFriendlyByteBuf, Dynamic<?>> REGISTRY_PASSTHROUGH = ByteBufCodecs.fromCodecWithRegistriesTrusted(Codec.PASSTHROUGH);

	public static final StreamCodec<RegistryFriendlyByteBuf, CraftingRecipe> CRAFTING_RECIPE = Recipe.STREAM_CODEC.map(
		recipe -> RecipeUtil.validateCraftingRecipe(recipe).getOrThrow(),
		Function.identity()
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, RecipeHolder<CraftingRecipe>> CRAFTING_RECIPE_ENTRY = RecipeHolder.STREAM_CODEC.map(
		recipeEntry -> new RecipeHolder<>(recipeEntry.id(), RecipeUtil.validateCraftingRecipe(recipeEntry.value()).getOrThrow()),
		Function.identity()
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, ContextParameter<Number>> NUMBER_CONTEXT_KEY = ContextParameter.streamCodec("number", Number.class);

	public static final StreamCodec<RegistryFriendlyByteBuf, ContextParameter<Entity>> ENTITY_CONTEXT_KEY = ContextParameter.streamCodec("entity", Entity.class);

	public static final StreamCodec<RegistryFriendlyByteBuf, ClipContext.Block> BLOCK_CLIP_CONTEXT = StreamCodecUtil.enumType(ClipContext.Block.class);

	public static final StreamCodec<RegistryFriendlyByteBuf, ClipContext.Fluid> FLUID_CLIP_CONTEXT = StreamCodecUtil.enumType(ClipContext.Fluid.class);

	public static final StreamCodec<RegistryFriendlyByteBuf, EntityAnchorArgument.Anchor> ENTITY_ANCHOR = StreamCodecUtil.enumType(EntityAnchorArgument.Anchor.class);

	public static final StreamCodec<RegistryFriendlyByteBuf, Difficulty> DIFFICULTY = StreamCodecUtil.enumType(Difficulty.class);

	public static final StreamCodec<ByteBuf, ExtraCodecs.TagOrElementLocation> TAG_OR_ELEMENT_ID = ByteBufCodecs.STRING_UTF8.map(
		str -> str.startsWith("#")
			? ResourceLocation.read(str.substring(1)).map(id -> new ExtraCodecs.TagOrElementLocation(id, true)).getOrThrow()
			: ResourceLocation.read(str).map(id -> new ExtraCodecs.TagOrElementLocation(id, false)).getOrThrow(),
		ExtraCodecs.TagOrElementLocation::toString
	);

	public static final StreamCodec<ByteBuf, TagEntry> TAG_ENTRY = StreamCodec.composite(
		TAG_OR_ELEMENT_ID, tagEntry -> ((TagEntryAccessor) tagEntry).callElementOrTag(),
		ByteBufCodecs.BOOL, tagEntry -> ((TagEntryAccessor) tagEntry).isRequired(),
		TagEntryAccessor::createTagEntry
	);

	public static final StreamCodec<ByteBuf, List<TagEntry>> TAG_ENTRIES = ByteBufCodecs.collection(ObjectArrayList::new, TAG_ENTRY);

	public static final StreamCodec<ByteBuf, TagLike<EntityType<?>>> ENTITY_TYPE_TAG_LIKE = TagLike.createStreamCodec(BuiltInRegistries.ENTITY_TYPE);

	public static final StreamCodec<ByteBuf, TagLike<PowerEntry<?>>> POWER_TAG_LIKE = TagLike.createStreamCodec(PowerManager.TAG_LOOKUP);

	public static final StreamCodec<ByteBuf, TagLike<Action>> ACTION_TAG_LIKE = TagLike.createStreamCodec(ActionManager.TAG_LOOKUP);

	public static final StreamCodec<ByteBuf, LazyTagLike<EntityType<?>>> LAZY_ENTITY_TYPE_TAG_LIKE = LazyTagLike.createLazyStreamCodec(BuiltInRegistries.ENTITY_TYPE);

	public static final StreamCodec<ByteBuf, LazyTagLike<PowerEntry<?>>> LAZY_POWER_TAG_LIKE = LazyTagLike.createLazyStreamCodec(PowerManager.TAG_LOOKUP);

	public static final StreamCodec<ByteBuf, LazyTagLike<Action>> LAZY_ACTION_TAG_LIKE = LazyTagLike.createLazyStreamCodec(ActionManager.TAG_LOOKUP);

}
