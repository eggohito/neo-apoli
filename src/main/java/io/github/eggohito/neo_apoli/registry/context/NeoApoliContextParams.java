package io.github.eggohito.neo_apoli.registry.context;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.*;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.CachedBlock;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

import static net.minecraft.commands.Commands.literal;

public class NeoApoliContextParams {

	public static final FixedRegistryAlias<Context.Parameter<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.CONTEXT_PARAMETER);

	public static final Codec<Context.Parameter<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);
	public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.CONTEXT_PARAMETER);

	public static final Context.Parameter<Entity> ACTOR_ENTITY = registerInternal("actor_entity", EntityContextParameter::new);
	public static final Context.Parameter<Entity> DAMAGING_ENTITY = registerInternal("damaging_entity", EntityContextParameter::new);
	public static final Context.Parameter<Entity> DIRECT_DAMAGING_ENTITY = registerInternal("direct_damaging_entity", EntityContextParameter::new);
	public static final Context.Parameter<Entity> PROJECTILE_ENTITY = registerInternal("projectile_entity", EntityContextParameter::new);
	public static final Context.Parameter<Entity> TARGET_ENTITY = registerInternal("target_entity", EntityContextParameter::new);
	public static final Context.Parameter<Entity> THIS_ENTITY = registerInternal("this_entity", EntityContextParameter::new);

	public static final Context.Parameter<CachedBlock> BROKEN_BLOCK = registerInternal("broken_block", BlockContextParameter::new);
	public static final Context.Parameter<Direction> BROKEN_SIDE = registerInternal("broken_side", id -> new EnumContextParameter<>(id, Direction.class));

	public static final Context.Parameter<CachedBlock> MINING_BLOCK = registerInternal("mining_block", BlockContextParameter::new);
	public static final Context.Parameter<CachedBlock> SELECTED_BLOCK = registerInternal("selected_block", BlockContextParameter::new);

	public static final Context.Parameter<Float> DEALT_DAMAGE_AMOUNT = registerSimpleInternal("dealt_damage/amount", Float.class);
	public static final Context.Parameter<Float> TAKEN_DAMAGE_AMOUNT = registerSimpleInternal("taken_damage/amount", Float.class);
	public static final Context.Parameter<DamageSource> DEALT_DAMAGE_SOURCE = registerSimpleInternal("dealt_damage/source", DamageSource.class);
	public static final Context.Parameter<DamageSource> TAKEN_DAMAGE_SOURCE = registerSimpleInternal("taken_damage/source", DamageSource.class);

	public static final Context.Parameter<MobEffectInstance> APPLIED_EFFECT = registerSimpleInternal("applied_effect", MobEffectInstance.class);

	public static final Context.Parameter<ItemStack> ITEM_IN_CONTAINER = registerInternal("item_in_container", ItemContextParameter::new);
	public static final Context.Parameter<SlotAccess> ITEM_IN_CONTAINER_SLOT = registerInternal("item_in_container_slot", SlotAccessContextParameter::new);
	public static final Context.Parameter<ItemStack> USED_ITEM = registerInternal("used_item", ItemContextParameter::new);
	public static final Context.Parameter<SlotAccess> USED_ITEM_SLOT = registerInternal("used_item_slot", SlotAccessContextParameter::new);

	public static void registerAll() {
		ALIASES.addPathAlias("actor", ACTOR_ENTITY);
		ALIASES.addPathAlias("target", TARGET_ENTITY);
		ALIASES.addPathAlias("projectile", PROJECTILE_ENTITY);
		ALIASES.addPathAlias("this", THIS_ENTITY);
	}


	public static void addAsArguments(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> addendNode) {

		for (var parameter : NeoApoliRegistries.CONTEXT_PARAMETER) {

			String id = parameter.name().toString();
			var parameterNode = literal(id).build();

			parameter.addAsArgument(buildContext, baseNode, parameterNode);

			if (!parameterNode.getChildren().isEmpty()) {
				addendNode.addChild(parameterNode);
			}

		}

	}

	public static <T, C extends Context.Parameter<T>> C register(ResourceLocation id, Function<ResourceLocation, C> constructor) {
		return Registry.register(NeoApoliRegistries.CONTEXT_PARAMETER, id, constructor.apply(id));
	}

	@ApiStatus.Internal
	public static <T, C extends Context.Parameter<T>> C registerInternal(String path, Function<ResourceLocation, C> constructor) {
		return register(NeoApoli.id(path), constructor);
	}

	public static <T> Context.Parameter<T> registerSimple(ResourceLocation id, Class<T> typeClass) {
		return register(id, _id -> Context.simpleParameter(_id, typeClass));
	}

	@ApiStatus.Internal
	public static <T> Context.Parameter<T> registerSimpleInternal(String path, Class<T> typeClass) {
		return registerSimple(NeoApoli.id(path), typeClass);
	}

	public static final class Codecs {

		public static final Codec<Context.Parameter<CachedBlock>> BLOCK = Context.parameterCodec("block", CachedBlock.class);
		public static final Codec<Context.Parameter<DamageSource>> DAMAGE_SOURCE = Context.parameterCodec("damage source", DamageSource.class);
		public static final Codec<Context.Parameter<Direction>> DIRECTION = Context.parameterCodec("direction", Direction.class);
		public static final Codec<Context.Parameter<Entity>> ENTITY = Context.parameterCodec("entity", Entity.class);
		public static final Codec<Context.Parameter<ItemStack>> ITEM = Context.parameterCodec("item", ItemStack.class);
		public static final Codec<Context.Parameter<MobEffectInstance>> EFFECT = Context.parameterCodec("effect", MobEffectInstance.class);
		public static final Codec<Context.Parameter<Number>> NUMBER = Context.parameterCodec("number", Number.class);
		public static final Codec<Context.Parameter<SlotAccess>> SLOT = Context.parameterCodec("slot", SlotAccess.class);

	}

	public static final class StreamCodecs {

		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<CachedBlock>> BLOCK = Context.parameterStreamCodec("block", CachedBlock.class);
		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<DamageSource>> DAMAGE_SOURCE = Context.parameterStreamCodec("damage source", DamageSource.class);
		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<Direction>> DIRECTION = Context.parameterStreamCodec("direction", Direction.class);
		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<Entity>> ENTITY = Context.parameterStreamCodec("entity", Entity.class);
		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<ItemStack>> ITEM = Context.parameterStreamCodec("item", ItemStack.class);
		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<MobEffectInstance>> EFFECT = Context.parameterStreamCodec("effect", MobEffectInstance.class);
		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<Number>> NUMBER = Context.parameterStreamCodec("number", Number.class);
		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<SlotAccess>> SLOT = Context.parameterStreamCodec("slot", SlotAccess.class);

	}

}
