package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.particle.type.NeoApoliParticleTypes;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.util.context.*;
import io.github.eggohito.neo_apoli.util.context.parameter.TypedContextKey;
import lombok.AllArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Set;

public interface ExplodeMetaAction extends MetaAction {

	BiEntityCondition damageableBiEntityCondition();

	BlockCondition destructibleBlockCondition();

	Vec3Provider position();

	TypedContextKey<Entity> actor();

	Property property();

	Display display();

	@Override
	default void execute(Context context) {

		if (!(context.getLevel() instanceof ServerLevel serverWorld)) {
			return;
		}

		Context positionContext = context.forChild(".position");
		Vec3 position = position().next(positionContext);

		if (positionContext.hasErrors()) {
			return;
		}

		Context powerContext = context.forChild(".power");
		float power = property().power().nextFloat(powerContext);

		if (powerContext.hasErrors()) {
			return;
		}

		Context createFireContext = context.forChild(".create_fire");
		boolean createFire = property().createFire().next(createFireContext);

		if (createFireContext.hasErrors()) {
			return;
		}

		Entity actor = context.nullable(actor());
		DamageCalculator damageCalculator = new DamageCalculator(this, context);

		ServerExplosion explosion = new ServerExplosion(serverWorld, actor, Explosion.getDefaultDamageSource(serverWorld, actor), damageCalculator, position, power, createFire, property().destructionType());
		explosion.explode();

		ParticleOptions particle = display().getParticleOrDefault(explosion);

		for (var player : serverWorld.players()) {

			if (player.distanceToSqr(position) >= 4096.0) {
				continue;
			}

			Optional<Vec3> knockback = Optional.ofNullable(explosion.getHitPlayers().get(player));
			player.connection.send(new ClientboundExplodePacket(position, knockback, particle, display().sound()));

		}

	}

	@Override
	default Set<ContextKey<?>> getRequiredParameters() {
		return Set.of(actor());
	}

	@Override
	default void validate(ProblemReporter reporter) {

		MetaAction.super.validate(reporter);

		damageableBiEntityCondition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BIENTITY))
			.forChild(".damageable_bientity_condition"));
		destructibleBlockCondition().validate(reporter
			.withKeySet(ContextKeySetHelper.merge(reporter.getKeySet(), NeoApoliContextKeySets.BLOCK))
			.forChild(".destructible_block_condition"));

		property().validate(reporter);

	}

	@AllArgsConstructor
	class DamageCalculator extends ExplosionDamageCalculator {

		private final ExplodeMetaAction action;
		private final Context context;

		@Override
		public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {

			Context blockContext = new Context.Builder(context)
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BLOCK))
				.add(NeoApoliContextKeys.BLOCK_POS, pos)
				.add(NeoApoliContextKeys.BLOCK_STATE, state)
				.addNullable(NeoApoliContextKeys.BLOCK_ENTITY, world.getBlockEntity(pos))
				.build(context.getLevel());

			return action.destructibleBlockCondition().test(blockContext.forChild(".destructible_block_condition"));

		}

		@Override
		public boolean shouldDamageEntity(Explosion explosion, Entity entity) {

			Context biEntityContext = new Context.Builder(context)
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BIENTITY))
				.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, context.nullable(action.actor()))
				.addNullable(NeoApoliContextKeys.TARGET_ENTITY, entity)
				.build(context.getLevel());

			return action.damageableBiEntityCondition().test(biEntityContext.forChild(".damageable_bientity_condition"));

		}

		@Override
		public float getKnockbackMultiplier(Entity entity) {

			Context knockbackContext = new Context.Builder(context)
				.withKeySet(ContextKeySetHelper.merge(context.getKeySet(), NeoApoliContextKeySets.BIENTITY))
				.addNullable(NeoApoliContextKeys.ACTOR_ENTITY, context.nullable(action.actor()))
				.addNullable(NeoApoliContextKeys.TARGET_ENTITY, entity)
				.build(context.getLevel());

			return action.property().knockbackMultiplier().nextFloat(knockbackContext.forChild(".knockback_multiplier"));

		}

	}

	record Property(Explosion.BlockInteraction destructionType, NumberProvider power, NumberProvider knockbackMultiplier, BooleanProvider createFire) implements ContextAware {

		public static final MapCodec<Property> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			NeoApoliCodecs.DESTRUCTION_TYPE.fieldOf("destruction_type").forGetter(Property::destructionType),
			NumberProvider.CODEC.fieldOf("power").forGetter(Property::power),
			NumberProvider.CODEC.optionalFieldOf("knockback_multiplier", new ConstantNumberProvider(1.0)).forGetter(Property::knockbackMultiplier),
			BooleanProvider.CODEC.optionalFieldOf("create_fire", new ConstantBooleanProvider(true)).forGetter(Property::createFire)
		).apply(instance, Property::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Property> STREAM_CODEC = StreamCodec.composite(
			NeoApoliStreamCodecs.DESTRUCTION_TYPE, Property::destructionType,
			NumberProvider.STREAM_CODEC, Property::power,
			NumberProvider.STREAM_CODEC, Property::knockbackMultiplier,
			BooleanProvider.STREAM_CODEC, Property::createFire,
			Property::new
		);

		@Override
		public void validate(ProblemReporter reporter) {

			ContextAware.super.validate(reporter);

			power().validate(reporter.forChild(".power"));
			knockbackMultiplier().validate(reporter.forChild(".knockback_multiplier"));
			createFire().validate(reporter.forChild(".create_fire"));

		}

	}

	record Display(Holder<SoundEvent> sound, Optional<ParticleOptions> particle) {

		public static final MapCodec<Display> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SoundEvent.CODEC.optionalFieldOf("sound", SoundEvents.GENERIC_EXPLODE).forGetter(Display::sound),
			NeoApoliParticleTypes.EFFECT_CODEC.optionalFieldOf("particle").forGetter(Display::particle)
		).apply(instance, Display::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Display> STREAM_CODEC = StreamCodec.composite(
			SoundEvent.STREAM_CODEC, Display::sound,
			ByteBufCodecs.optional(NeoApoliParticleTypes.EFFECT_STREAM_CODEC), Display::particle,
			Display::new
		);

		public ParticleOptions getParticleOrDefault(ServerExplosion explosion) {

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
