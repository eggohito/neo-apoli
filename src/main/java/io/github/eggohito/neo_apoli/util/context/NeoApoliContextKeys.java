package io.github.eggohito.neo_apoli.util.context;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.Vec3ContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.block.BlockEntityContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.block.BlockPosContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.block.BlockStateContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.entity.EntityContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.fluid.FluidStateContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.item.ItemStackContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.item.StackReferenceContextKey;
import io.github.eggohito.neo_apoli.util.context.parameter.number.FloatContextKey;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
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

public final class NeoApoliContextKeys {

	public static final RegistryFixedAlias<TypedContextKey<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.TYPED_CONTEXT_KEY);

	public static final Codec<TypedContextKey<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);
	public static final StreamCodec<RegistryFriendlyByteBuf, TypedContextKey<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.TYPED_CONTEXT_KEY);

	//	Usually used in bi-entity contexts
	public static final TypedContextKey<Entity> ACTOR_ENTITY = registerInternal("actor_entity", EntityContextKey::new);
	public static final TypedContextKey<Entity> TARGET_ENTITY = registerInternal("target_entity", EntityContextKey::new);

	//	Usually used in block contexts
	public static final TypedContextKey<BlockPos> BLOCK_POS = registerInternal("block_pos", BlockPosContextKey::new);
	public static final TypedContextKey<BlockState> BLOCK_STATE = registerInternal("block_state", BlockStateContextKey::new);
	public static final TypedContextKey<BlockEntity> BLOCK_ENTITY = registerInternal("block_entity", BlockEntityContextKey::new);
	public static final TypedContextKey<Direction> DIRECTION = registerInternal("direction", id -> new TypedContextKey<>(id, Direction.class));

	//	Usually used in damage contexts
	public static final TypedContextKey<DamageSource> DAMAGE_SOURCE = registerInternal("damage_source", id -> new TypedContextKey<>(id, DamageSource.class));
	public static final TypedContextKey<Float> DAMAGE_AMOUNT = registerInternal("damage_amount", FloatContextKey::new);
	public static final TypedContextKey<Entity> DAMAGING_ENTITY = registerInternal("damaging_entity", EntityContextKey::new);
	public static final TypedContextKey<Entity> DIRECT_DAMAGING_ENTITY = registerInternal("direct_damaging_entity", EntityContextKey::new);

	//	Usually used in entity contexts
	public static final TypedContextKey<Entity> PROJECTILE_ENTITY = registerInternal("projectile_entity", id -> new TypedContextKey<>(id, Entity.class));
	public static final TypedContextKey<Entity> THIS_ENTITY = registerInternal("this_entity", EntityContextKey::new);
	public static final TypedContextKey<Vec3> THIS_POS = registerInternal("this_pos", Vec3ContextKey::new);

	//	Usually used in fluid contexts
	public static final TypedContextKey<FluidState> FLUID_STATE = registerInternal("fluid_state", FluidStateContextKey::new);

	//	Usually used in item contexts
	public static final TypedContextKey<SlotAccess> STACK_REFERENCE = registerInternal("stack_reference", StackReferenceContextKey::new);
	public static final TypedContextKey<ItemStack> ITEM_STACK = registerInternal("item_stack", ItemStackContextKey::new);

	//	Usually used in status effect contexts
	public static final TypedContextKey<MobEffectInstance> EFFECT_INSTANCE = registerInternal("mob_effect", id -> new TypedContextKey<>(id, MobEffectInstance.class));

	//	Usually used in HUD elements
	public static final TypedContextKey<Double> CURRENT_VALUE = registerInternal("value/current", id -> new TypedContextKey<>(id, Double.class));
	public static final TypedContextKey<Double> MAX_VALUE = registerInternal("value/max", id -> new TypedContextKey<>(id, Double.class));
	public static final TypedContextKey<Double> MIN_VALUE = registerInternal("value/min", id -> new TypedContextKey<>(id, Double.class));

	//	Can be used generally
	public static final TypedContextKey<InteractionHand> HAND = registerInternal("hand", id -> new TypedContextKey<>(id, InteractionHand.class));
	public static final TypedContextKey<PowerReference> POWER_REFERENCE = registerInternal("power_reference", id -> new TypedContextKey<>(id, PowerReference.class));

	public static void init() {
		ALIASES.addPathAlias("actor", ACTOR_ENTITY);
		ALIASES.addPathAlias("projectile", PROJECTILE_ENTITY);
		ALIASES.addPathAlias("target", TARGET_ENTITY);
		ALIASES.addPathAlias("this", THIS_ENTITY);
	}

	private static <T, P extends TypedContextKey<T>> TypedContextKey<T> registerInternal(String path, Function<ResourceLocation, P> parameter) {
		return register(parameter.apply(NeoApoli.id(path)));
	}

	public static <T, P extends TypedContextKey<T>> TypedContextKey<T> register(P parameter) {
		return Registry.register(NeoApoliRegistries.TYPED_CONTEXT_KEY, parameter.name(), parameter);
	}

    public static void addAsArguments(CommandBuildContext registryAccess, CommandNode<CommandSourceStack> baseNode, CommandNode<CommandSourceStack> withNode, CommandNode<CommandSourceStack> onNode) {

		for (var key : NeoApoliRegistries.TYPED_CONTEXT_KEY) {

            String id = key.name().toString();
            TypedContextKey.CommandBuilder commandBuilder = key.getCommandBuilder();

            if (commandBuilder == null) {
                continue;
            }

            CommandNode<CommandSourceStack> parameterNode = literal(id).build();
            commandBuilder.addArguments(registryAccess, baseNode, parameterNode);

            withNode.addChild(parameterNode);

        }

        baseNode.addChild(withNode);
        baseNode.addChild(onNode);

    }

}
