package io.github.eggohito.neo_apoli.util.context;

import com.mojang.serialization.Codec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistryKeys;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.alias.RegistryFixedAlias;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextParameter;
import io.github.eggohito.neo_apoli.util.context.parameter.Vec3dContextParameter;
import io.github.eggohito.neo_apoli.util.context.parameter.block.BlockEntityContextParameter;
import io.github.eggohito.neo_apoli.util.context.parameter.block.BlockPosContextParameter;
import io.github.eggohito.neo_apoli.util.context.parameter.block.BlockStateContextParameter;
import io.github.eggohito.neo_apoli.util.context.parameter.entity.EntityContextParameter;
import io.github.eggohito.neo_apoli.util.context.parameter.item.ItemStackContextParameter;
import io.github.eggohito.neo_apoli.util.context.parameter.item.StackReferenceContextParameter;
import io.github.eggohito.neo_apoli.util.context.parameter.number.FloatContextParameter;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.function.Function;

public final class NeoApoliContextParameters {

	public static final RegistryFixedAlias<TypedContextParameter<?>> ALIASES = RegistryFixedAlias.of(NeoApoliRegistries.TYPED_CONTEXT_PARAMETER);
	public static final Codec<TypedContextParameter<?>> CODEC = RegistryUtil.createAliasedCodec(ALIASES);
	public static final PacketCodec<RegistryByteBuf, TypedContextParameter<?>> PACKET_CODEC = PacketCodecs.registryValue(NeoApoliRegistryKeys.TYPED_CONTEXT_PARAMETER);

	//	Usually used in bi-entity contexts
	public static final TypedContextParameter<Entity> ACTOR = registerInternal("actor", EntityContextParameter::new);
	public static final TypedContextParameter<Entity> TARGET = registerInternal("target", EntityContextParameter::new);

	//	Usually used in block contexts
	public static final TypedContextParameter<BlockPos> BLOCK_POS = registerInternal("block_pos", BlockPosContextParameter::new);
	public static final TypedContextParameter<BlockState> BLOCK_STATE = registerInternal("block_state", BlockStateContextParameter::new);
	public static final TypedContextParameter<BlockEntity> BLOCK_ENTITY = registerInternal("block_entity", BlockEntityContextParameter::new);
	public static final TypedContextParameter<Direction> DIRECTION = registerInternal("direction", id -> new TypedContextParameter<>(id, Direction.class));

	//	Usually used in damage contexts
	public static final TypedContextParameter<DamageSource> DAMAGE_SOURCE = registerInternal("damage_source", id -> new TypedContextParameter<>(id, DamageSource.class));
	public static final TypedContextParameter<Float> DAMAGE_AMOUNT = registerInternal("damage_amount", FloatContextParameter::new);
	public static final TypedContextParameter<Entity> DAMAGING_ENTITY = registerInternal("damaging_entity", EntityContextParameter::new);
	public static final TypedContextParameter<Entity> DIRECT_DAMAGING_ENTITY = registerInternal("direct_damaging_entity", EntityContextParameter::new);

	//	Usually used in entity contexts
	public static final TypedContextParameter<Entity> THIS_ENTITY = registerInternal("this_entity", EntityContextParameter::new);
	public static final TypedContextParameter<Vec3d> ENTITY_POS = registerInternal("entity_pos", Vec3dContextParameter::new);

	//	Usually used in item contexts
	public static final TypedContextParameter<StackReference> STACK_REFERENCE = registerInternal("stack_reference", StackReferenceContextParameter::new);
	public static final TypedContextParameter<ItemStack> ITEM_STACK = registerInternal("item_stack", ItemStackContextParameter::new);

	//	Can be used generally
	public static final TypedContextParameter<PowerReference> POWER_REFERENCE = registerInternal("power_reference", id -> new TypedContextParameter<>(id, PowerReference.class));
	public static final TypedContextParameter<Hand> HAND = registerInternal("hand", id -> new TypedContextParameter<>(id, Hand.class));

	public static void init() {

	}

	private static <T, P extends TypedContextParameter<T>> TypedContextParameter<T> registerInternal(String path, Function<Identifier, P> parameter) {
		return register(parameter.apply(NeoApoli.id(path)));
	}

	public static <T, P extends TypedContextParameter<T>> TypedContextParameter<T> register(P parameter) {
		return Registry.register(NeoApoliRegistries.TYPED_CONTEXT_PARAMETER, parameter.getId(), parameter);
	}

}
