package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.particle.type.NeoApoliParticleTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3d.Vec3dProvider;
import io.github.eggohito.neo_apoli.util.EntityTarget;
import io.github.eggohito.neo_apoli.util.context.*;
import lombok.AllArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;

import java.util.Optional;
import java.util.Set;

public interface ExplodeMetaAction extends MetaAction {

	BiEntityCondition damageableBiEntityCondition();

	BlockCondition destructibleBlockCondition();

	Vec3dProvider position();

	EntityTarget actor();

	Property property();

	Display display();

	@Override
	default void execute(Context context) {

		if (!(context.getWorld() instanceof ServerWorld serverWorld)) {
			return;
		}

		Context positionContext = context.makeChild(".position");
		Vec3d position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return;
		}

		Context powerContext = context.makeChild(".power");
		float power = property().power().nextFloat(powerContext);

		if (powerContext.hasErrors()) {
			return;
		}

		Context createFireContext = context.makeChild(".create_fire");
		boolean createFire = property().createFire().next(createFireContext);

		if (createFireContext.hasErrors()) {
			return;
		}

		Entity actor = context.nullable(actor().getParameter());
		Behavior behavior = new Behavior(this, context);

		ExplosionImpl explosion = new ExplosionImpl(serverWorld, actor, Explosion.createDamageSource(serverWorld, actor), behavior, position, power, createFire, property().destructionType());
		explosion.explode();

		ParticleEffect particle = display().getParticleOrDefault(explosion);

		for (var player : serverWorld.getPlayers()) {

			if (player.squaredDistanceTo(position) >= 4096.0) {
				continue;
			}

			Optional<Vec3d> knockback = Optional.ofNullable(explosion.getKnockbackByPlayer().get(player));
			player.networkHandler.sendPacket(new ExplosionS2CPacket(position, knockback, particle, display().sound()));

		}

	}

	@Override
	default Set<ContextParameter<?>> getRequiredParameters() {
		return Set.of(actor().getParameter());
	}

	@Override
	default void validate(ErrorReporter reporter) {

		MetaAction.super.validate(reporter);

		damageableBiEntityCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BIENTITY))
			.makeChild(".damageable_bientity_condition"));
		destructibleBlockCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BLOCK))
			.makeChild(".destructible_block_condition"));

		property().validate(reporter);

	}

	@AllArgsConstructor
	class Behavior extends ExplosionBehavior {

		private final ExplodeMetaAction action;
		private final Context context;

		@Override
		public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {

			Context blockContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BLOCK))
				.add(ContextParameters.BLOCK_POS, pos)
				.add(ContextParameters.BLOCK_STATE, state)
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos)));

			return action.destructibleBlockCondition().test(blockContext.makeChild(".destructible_block_condition"));

		}

		@Override
		public boolean shouldDamage(Explosion explosion, Entity entity) {

			Context biEntityContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BIENTITY))
				.addNullable(ContextParameters.ACTOR, context.nullable(action.actor().getParameter()))
				.addNullable(ContextParameters.TARGET, entity));

			return action.damageableBiEntityCondition().test(biEntityContext.makeChild(".damageable_bientity_condition"));

		}

		@Override
		public float getKnockbackModifier(Entity entity) {

			Context knockbackModifierContext = ContextImpl.of(context, builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BIENTITY))
				.addNullable(ContextParameters.ACTOR, context.nullable(action.actor().getParameter()))
				.addNullable(ContextParameters.TARGET, entity));

			return action.property().knockbackMultiplier().nextFloat(knockbackModifierContext.makeChild(".knockback_multiplier"));

		}

	}

	record Property(Explosion.DestructionType destructionType, NumberProvider power, NumberProvider knockbackMultiplier, BooleanProvider createFire) implements ContextAware {

		public static final MapCodec<Property> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			NeoApoliCodecs.DESTRUCTION_TYPE.fieldOf("destruction_type").forGetter(Property::destructionType),
			NumberProvider.CODEC.fieldOf("power").forGetter(Property::power),
			NumberProvider.CODEC.optionalFieldOf("knockback_multiplier", new ConstantNumberProvider(1.0)).forGetter(Property::knockbackMultiplier),
			BooleanProvider.CODEC.optionalFieldOf("create_fire", new ConstantBooleanProvider(true)).forGetter(Property::createFire)
		).apply(instance, Property::new));

		public static final PacketCodec<RegistryByteBuf, Property> PACKET_CODEC = PacketCodec.tuple(
			NeoApoliPacketCodecs.DESTRUCTION_TYPE, Property::destructionType,
			NumberProvider.PACKET_CODEC, Property::power,
			NumberProvider.PACKET_CODEC, Property::knockbackMultiplier,
			BooleanProvider.PACKET_CODEC, Property::createFire,
			Property::new
		);

		@Override
		public void validate(ErrorReporter reporter) {

			ContextAware.super.validate(reporter);

			power().validate(reporter.makeChild(".power"));
			knockbackMultiplier().validate(reporter.makeChild(".knockback_multiplier"));
			createFire().validate(reporter.makeChild(".create_fire"));

		}

	}

	record Display(RegistryEntry<SoundEvent> sound, Optional<ParticleEffect> particle) {

		public static final MapCodec<Display> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SoundEvent.ENTRY_CODEC.optionalFieldOf("sound", SoundEvents.ENTITY_GENERIC_EXPLODE).forGetter(Display::sound),
			NeoApoliParticleTypes.EFFECT_CODEC.optionalFieldOf("particle").forGetter(Display::particle)
		).apply(instance, Display::new));

		public static final PacketCodec<RegistryByteBuf, Display> PACKET_CODEC = PacketCodec.tuple(
			SoundEvent.ENTRY_PACKET_CODEC, Display::sound,
			PacketCodecs.optional(NeoApoliParticleTypes.EFFECT_PACKET_CODEC), Display::particle,
			Display::new
		);

		public ParticleEffect getParticleOrDefault(ExplosionImpl explosion) {

			if (particle().isPresent()) {
				return particle().get();
			}

			else if (explosion.isSmall()) {
				return ParticleTypes.EXPLOSION;
			}

			else {
				return ParticleTypes.EXPLOSION_EMITTER;
			}

		}

	}

}
