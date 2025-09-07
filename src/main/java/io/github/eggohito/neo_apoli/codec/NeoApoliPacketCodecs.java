package io.github.eggohito.neo_apoli.codec;

import com.google.gson.internal.LazilyParsedNumber;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import io.github.eggohito.neo_apoli.util.*;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.LightType;
import net.minecraft.world.explosion.Explosion;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class NeoApoliPacketCodecs {

	public static final PacketCodec<ByteBuf, Set<Identifier>> MUTABLE_IDENTIFIER_SET = PacketCodecs.collection(ObjectOpenHashSet::new, Identifier.PACKET_CODEC);

	public static final PacketCodec<ByteBuf, Set<Identifier>> MUTABLE_NON_EMPTY_IDENTIFIER_SET = PacketCodecUtil.nonEmptyCollection(MUTABLE_IDENTIFIER_SET);

	public static final PacketCodec<ByteBuf, Hand> HAND = HandProperty.PACKET_CODEC.xmap(HandProperty::get, HandProperty::fromHand);

	public static final PacketCodec<ByteBuf, List<Hand>> HANDS = PacketCodecs.collection(ObjectArrayList::new, HAND);

	public static final PacketCodec<ByteBuf, EnumSet<Hand>> HAND_SET = HANDS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final PacketCodec<PacketByteBuf, Number> NUMBER = new PacketCodec<>() {

		@Override
		public Number decode(PacketByteBuf buf) {
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
					new LazilyParsedNumber(buf.readString());
				default ->
					throw new IllegalArgumentException("Unsupported number type: " + type);
			};
		}

		@Override
		public void encode(PacketByteBuf buf, Number value) {
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
				case LazilyParsedNumber n -> {
					buf.writeByte(6);
					buf.writeString(n.toString());
				}
				default ->
					throw new IllegalArgumentException("Unsupported number: " + value);
			}
		}

	};

	public static final PacketCodec<ByteBuf, NbtPathArgumentType.NbtPath> NBT_PATH = PacketCodecs.unlimitedCodec(NbtPathArgumentType.NbtPath.CODEC);

	public static final PacketCodec<ByteBuf, LightType> LIGHT_TYPE = PacketCodecUtil.enumType(LightType.class);

	public static final PacketCodec<ByteBuf, List<Direction>> DIRECTIONS = PacketCodecs.collection(ObjectArrayList::new, Direction.PACKET_CODEC);

	public static final PacketCodec<ByteBuf, EnumSet<Direction>> DIRECTION_SET = DIRECTIONS.xmap(EnumSet::copyOf, ObjectArrayList::new);

	public static final PacketCodec<ByteBuf, Direction.Axis> AXIS = PacketCodecUtil.enumType(Direction.Axis.class);

	public static final PacketCodec<ByteBuf, Map<EntityParameter, EntityParameter>> ENTITY_PARAMETER_MAP = PacketCodecs.map(Object2ObjectOpenHashMap::new, EntityParameter.PACKET_CODEC, EntityParameter.PACKET_CODEC);

	public static final PacketCodec<ByteBuf, Explosion.DestructionType> DESTRUCTION_TYPE = PacketCodecUtil.enumType(Explosion.DestructionType.class);

	public static final PacketCodec<PacketByteBuf, ActionResult> ACTION_RESULT = PacketCodecUtil.mapped(MiscUtil.ACTION_RESULTS);

	public static final PacketCodec<RegistryByteBuf, BlockState> BLOCK_STATE = PacketCodecs.unlimitedRegistryCodec(BlockState.CODEC);

	public static final PacketCodec<ByteBuf, TagKey<EntityType<?>>> ENTITY_TYPE_TAG = TagKey.packetCodec(RegistryKeys.ENTITY_TYPE);

	public static final PacketCodec<ByteBuf, Set<TagKey<EntityType<?>>>> ENTITY_TYPE_TAG_SET = PacketCodecs.collection(ObjectOpenHashSet::new, ENTITY_TYPE_TAG);

	public static final PacketCodec<RegistryByteBuf, List<AttributeModifier>> ATTRIBUTE_MODIFIERS = PacketCodecs.collection(ObjectArrayList::new, AttributeModifier.PACKET_CODEC);

	public static final PacketCodec<ByteBuf, Dynamic<?>> PASSTHROUGH = PacketCodecs.codec(Codec.PASSTHROUGH);

	public static final PacketCodec<RegistryByteBuf, Dynamic<?>> REGISTRY_PASSTHROUGH = PacketCodecs.unlimitedRegistryCodec(Codec.PASSTHROUGH);

	public static final PacketCodec<RegistryByteBuf, CraftingRecipe> CRAFTING_RECIPE = Recipe.PACKET_CODEC.xmap(
		recipe -> RecipeUtil.validateCraftingRecipe(recipe).getOrThrow(),
		Function.identity()
	);

	public static final PacketCodec<RegistryByteBuf, RecipeEntry<CraftingRecipe>> CRAFTING_RECIPE_ENTRY = RecipeEntry.PACKET_CODEC.xmap(
		recipeEntry -> new RecipeEntry<>(recipeEntry.id(), RecipeUtil.validateCraftingRecipe(recipeEntry.value()).getOrThrow()),
		Function.identity()
	);

}
