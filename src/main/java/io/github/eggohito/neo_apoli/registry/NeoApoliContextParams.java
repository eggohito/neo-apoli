package io.github.eggohito.neo_apoli.registry;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.util.alias.FixedRegistryAlias;
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

public class NeoApoliContextParams {

	public static final FixedRegistryAlias<Context.Parameter<?>> ALIASES = FixedRegistryAlias.of(NeoApoliRegistries.CONTEXT_PARAMETER);

	public static final Codec<Context.Parameter<?>> CODEC = ALIASES.createCodec(NeoApoli.MOD_NAMESPACE);
	public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<?>> STREAM_CODEC = ByteBufCodecs.registry(NeoApoliRegistryKeys.CONTEXT_PARAMETER);

	public static final Context.Parameter<Entity> ACTOR_ENTITY = registerInternal("actor_entity", Entity.class);
	public static final Context.Parameter<Entity> TARGET_ENTITY = registerInternal("target_entity", Entity.class);

	public static final Context.Parameter<BlockPos> BLOCK_POS = registerInternal("block_pos", BlockPos.class);
	public static final Context.Parameter<BlockState> BLOCK_STATE = registerInternal("block_state", BlockState.class);
	public static final Context.Parameter<BlockEntity> BLOCK_ENTITY = registerInternal("block_entity", BlockEntity.class);
	public static final Context.Parameter<Direction> DIRECTION = registerInternal("direction", Direction.class);

	public static final Context.Parameter<DamageSource> DAMAGE_SOURCE = registerInternal("damage_source", DamageSource.class);
	public static final Context.Parameter<Float> DAMAGE_AMOUNT = registerInternal("damage_amount", Float.class);
	public static final Context.Parameter<Entity> DAMAGING_ENTITY = registerInternal("damaging_entity", Entity.class);
	public static final Context.Parameter<Entity> DIRECT_DAMAGING_ENTITY = registerInternal("direct_damaging_entity", Entity.class);

	public static final Context.Parameter<Entity> PROJECTILE_ENTITY = registerInternal("projectile_entity", Entity.class);
	public static final Context.Parameter<Entity> THIS_ENTITY = registerInternal("this_entity", Entity.class);
	public static final Context.Parameter<Vec3> THIS_POS = registerInternal("this_pos", Vec3.class);

	public static final Context.Parameter<FluidState> FLUID_STATE = registerInternal("fluid_state", FluidState.class);

	public static final Context.Parameter<SlotAccess> SLOT_ACCESS = registerInternal("slot_access", SlotAccess.class);
	public static final Context.Parameter<ItemStack> ITEM_STACK = registerInternal("item_stack", ItemStack.class);

	public static final Context.Parameter<MobEffectInstance> EFFECT_INSTANCE = registerInternal("effect_instance", MobEffectInstance.class);

	public static final Context.Parameter<Double> CURRENT_VALUE = registerInternal("value/current", Double.class);
	public static final Context.Parameter<Double> MAX_VALUE = registerInternal("value/max", Double.class);
	public static final Context.Parameter<Double> MIN_VALUE = registerInternal("value/min", Double.class);

	public static void registerAll() {
		ALIASES.addPathAlias("this", THIS_ENTITY);
		ALIASES.addPathAlias("actor", ACTOR_ENTITY);
		ALIASES.addPathAlias("target", TARGET_ENTITY);
		ALIASES.addPathAlias("projectile", PROJECTILE_ENTITY);
	}

	private static <T> Context.Parameter<T> registerInternal(String path, Class<T> typeClass) {
		return register(NeoApoli.id(path), typeClass);
	}

	public static <T> Context.Parameter<T> register(ResourceLocation id, Class<T> typeClass) {
		return Registry.register(NeoApoliRegistries.CONTEXT_PARAMETER, id, Context.parameter(id, typeClass));
	}

	public static final class Codecs {

		public static final Codec<Context.Parameter<Number>> NUMBER = Context.parameterCodec("number", Number.class);
		public static final Codec<Context.Parameter<Entity>> ENTITY = Context.parameterCodec("entity", Entity.class);

	}

	public static final class StreamCodecs {

		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<Number>> NUMBER = Context.parameterStreamCodec("number", Number.class);
		public static final StreamCodec<RegistryFriendlyByteBuf, Context.Parameter<Entity>> ENTITY = Context.parameterStreamCodec("entity", Entity.class);

	}

}
