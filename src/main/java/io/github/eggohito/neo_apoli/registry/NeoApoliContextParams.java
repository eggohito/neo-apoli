package io.github.eggohito.neo_apoli.registry;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.parameter.*;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

import static net.minecraft.commands.Commands.literal;

public class NeoApoliContextParams {

	public static final RegistryFixedAlias<ContextParameter<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.CONTEXT_PARAMETER);

	public static final Codec<ContextParameter<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);
	public static final StreamCodec<RegistryFriendlyByteBuf, ContextParameter<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.CONTEXT_PARAMETER);

	public static final ContextParameter<Entity> ACTOR_ENTITY = registerInternal("actor_entity", EntityContextParameter::new);
	public static final ContextParameter<Entity> TARGET_ENTITY = registerInternal("target_entity", EntityContextParameter::new);

	public static final ContextParameter<BlockPos> BLOCK_POS = registerInternal("block_pos", BlockPosContextParameter::new);
	public static final ContextParameter<BlockState> BLOCK_STATE = registerInternal("block_state", BlockStateContextParameter::new);
	public static final ContextParameter<BlockEntity> BLOCK_ENTITY = registerInternal("block_entity", BlockEntityContextParameter::new);
	public static final ContextParameter<Direction> DIRECTION = registerInternal("direction", id -> new EnumContextParameter<>(id, Direction.class, Direction.CODEC, Direction::values));

	public static final ContextParameter<DamageSource> DAMAGE_SOURCE = registerInternal("damage_source", id -> Context.parameter(id, DamageSource.class));
	public static final ContextParameter<Float> DAMAGE_AMOUNT = registerInternal("damage_amount", id -> Context.parameter(id, Float.class));
	public static final ContextParameter<Entity> DAMAGING_ENTITY = registerInternal("damaging_entity", EntityContextParameter::new);
	public static final ContextParameter<Entity> DIRECT_DAMAGING_ENTITY = registerInternal("direct_damaging_entity", EntityContextParameter::new);

	public static final ContextParameter<Entity> PROJECTILE_ENTITY = registerInternal("projectile_entity", EntityContextParameter::new);
	public static final ContextParameter<Entity> THIS_ENTITY = registerInternal("this_entity", EntityContextParameter::new);
	public static final ContextParameter<Vec3> THIS_POS = registerInternal("this_pos", Vec3ContextParameter::new);

	public static final ContextParameter<FluidState> FLUID_STATE = registerInternal("fluid_state", FluidStateContextParameter::new);

	public static final ContextParameter<SlotAccess> SLOT_ACCESS = registerInternal("slot_access", SlotAccessContextParameter::new);
	public static final ContextParameter<ItemStack> ITEM_STACK = registerInternal("item_stack", ItemStackContextParameter::new);

	public static final ContextParameter<MobEffectInstance> EFFECT_INSTANCE = registerInternal("effect_instance", id -> Context.parameter(id, MobEffectInstance.class));

	public static final ContextParameter<Double> CURRENT_VALUE = registerInternal("value/current", id -> Context.parameter(id, Double.class));
	public static final ContextParameter<Double> MAX_VALUE = registerInternal("value/max", id -> Context.parameter(id, Double.class));
	public static final ContextParameter<Double> MIN_VALUE = registerInternal("value/min", id -> Context.parameter(id, Double.class));

	public static void registerAll() {
		ALIASES.addPathAlias("this", THIS_ENTITY);
		ALIASES.addPathAlias("actor", ACTOR_ENTITY);
		ALIASES.addPathAlias("target", TARGET_ENTITY);
		ALIASES.addPathAlias("projectile", PROJECTILE_ENTITY);
	}

	private static <T, K extends ContextParameter<T>> K registerInternal(String path, Function<ResourceLocation, K> constructor) {
		return register(NeoApoli.id(path), constructor);
	}

	public static <T, K extends ContextParameter<T>> K register(ResourceLocation id, Function<ResourceLocation, K> constructor) {
		return register(constructor.apply(id));
	}

	public static <T, K extends ContextParameter<T>> K register(K parameter) {
		return Registry.register(NeoApoliRegistries.CONTEXT_PARAMETER, parameter.name(), parameter);
	}

	public static void addAllAsArguments(CommandBuildContext buildContext, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> addendNode) {

		for (var parameter : NeoApoliRegistries.CONTEXT_PARAMETER) {

			String id = parameter.name().toString();
			var parameterNode = literal(id).build();

			parameter.addAsArgument(buildContext, baseNode, parameterNode);

			if (!parameterNode.getChildren().isEmpty()) {
				addendNode.addChild(parameterNode);
			}

		}

	}

}
