package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.block.BlockCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliParticleTypes;
import io.github.eggohito.neo_apoli.registry.context.NeoApoliContextParams;
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
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.Set;

public interface ExplodeMetaAction extends Action {

	ContextKeySet DAMAGEABLE_PARAMS = new ContextKeySet.Builder()
		.optional(NeoApoliContextParams.ACTOR_ENTITY)
		.optional(NeoApoliContextParams.TARGET_ENTITY)
		.build();

	ContextKeySet DESTRUCTIBLE_PARAMS = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.BLOCK_POS)
		.required(NeoApoliContextParams.BLOCK_STATE)
		.optional(NeoApoliContextParams.BLOCK_ENTITY)
		.build();

	BiEntityCondition damageableBiEntityCondition();

	BlockCondition destructibleBlockCondition();

	Vec3Provider position();

	Context.Parameter<Entity> actor();

	Property property();

	Display display();

	@Override
	default void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		Context positionContext = context.forChild(".position");
		Vec3 position = position().nextVec3(positionContext);

		if (positionContext.hasErrors()) {
			return;
		}

		Context powerContext = context.forChild(".power");
		float power = property().power().nextFloat(powerContext);

		if (powerContext.hasErrors()) {
			return;
		}

		Context createFireContext = context.forChild(".create_fire");
		boolean createFire = property().createFire().nextBoolean(createFireContext);

		if (createFireContext.hasErrors()) {
			return;
		}

		Entity actor = context.getNullable(actor());
		DamageCalculator damageCalculator = new DamageCalculator(this, context);

		ServerExplosion explosion = new ServerExplosion(serverLevel, actor, Explosion.getDefaultDamageSource(serverLevel, actor), damageCalculator, position, power, createFire, property().blockInteraction());
		explosion.explode();

		ParticleOptions particle = display().getParticleOrDefault(explosion);

		for (var player : serverLevel.players()) {

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
	default void validate(Context.Validator validator) {

		Action.super.validate(validator);

		damageableBiEntityCondition().validate(validator
			.withAdditionalKeysFromSets(DAMAGEABLE_PARAMS)
			.forChild(".damageable_bientity_condition"));
		destructibleBlockCondition().validate(validator
			.withAdditionalKeysFromSets(DESTRUCTIBLE_PARAMS)
			.forChild(".destructible_block_condition"));

		property().validate(validator);

	}

	@AllArgsConstructor
	class DamageCalculator extends ExplosionDamageCalculator {

		private final ExplodeMetaAction action;
		private final Context context;

		@Override
		public boolean shouldBlockExplode(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, float power) {

			Context conditionContext = new Context.Builder(context)
				.withRequired(NeoApoliContextParams.BLOCK_POS, blockPos)
				.withRequired(NeoApoliContextParams.BLOCK_STATE, blockState)
				.withNullable(NeoApoliContextParams.BLOCK_ENTITY, blockGetter.getBlockEntity(blockPos))
				.build(context.level());

			return action.destructibleBlockCondition().test(conditionContext.forChild(".destructible_block_condition"));

		}

		@Override
		public boolean shouldDamageEntity(Explosion explosion, Entity entity) {

			Context biEntityContext = new Context.Builder(context)
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, context.getNullable(action.actor()))
				.withNullable(NeoApoliContextParams.TARGET_ENTITY, entity)
				.build(context.level());

			return action.damageableBiEntityCondition().test(biEntityContext.forChild(".damageable_bientity_condition"));

		}

		@Override
		public float getKnockbackMultiplier(Entity entity) {

			Context knockbackContext = new Context.Builder(context)
				.withNullable(NeoApoliContextParams.ACTOR_ENTITY, context.getNullable(action.actor()))
				.withNullable(NeoApoliContextParams.TARGET_ENTITY, entity)
				.build(context.level());

			return action.property().knockbackMultiplier().nextFloat(knockbackContext.forChild(".knockback_multiplier"));

		}

	}

	record Property(Explosion.BlockInteraction blockInteraction, NumberProvider power, NumberProvider knockbackMultiplier, BooleanProvider createFire) implements ContextUser {

		private static final ContextKeySet KNOCKBACK_PARAMS = new ContextKeySet.Builder()
			.optional(NeoApoliContextParams.ACTOR_ENTITY)
			.optional(NeoApoliContextParams.TARGET_ENTITY)
			.build();

		public static final MapCodec<Property> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			NeoApoliCodecs.DESTRUCTION_TYPE.fieldOf("block_interaction").forGetter(Property::blockInteraction),
			NumberProvider.CODEC.fieldOf("power").forGetter(Property::power),
			NumberProvider.CODEC.optionalFieldOf("knockback_multiplier", new ConstantNumberProvider(1.0)).forGetter(Property::knockbackMultiplier),
			BooleanProvider.CODEC.optionalFieldOf("create_fire", new ConstantBooleanProvider(true)).forGetter(Property::createFire)
		).apply(instance, Property::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Property> STREAM_CODEC = StreamCodec.composite(
			NeoApoliStreamCodecs.DESTRUCTION_TYPE, Property::blockInteraction,
			NumberProvider.STREAM_CODEC, Property::power,
			NumberProvider.STREAM_CODEC, Property::knockbackMultiplier,
			BooleanProvider.STREAM_CODEC, Property::createFire,
			Property::new
		);

		@Override
		public void validate(Context.Validator validator) {

			ContextUser.super.validate(validator);

			power().validate(validator.forChild(".power"));
			knockbackMultiplier().validate(validator.withAdditionalKeysFromSets(KNOCKBACK_PARAMS).forChild(".knockback_multiplier"));
			createFire().validate(validator.forChild(".create_fire"));

		}

	}

	record Display(Holder<SoundEvent> sound, Optional<ParticleOptions> particle) {

		public static final MapCodec<Display> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SoundEvent.CODEC.optionalFieldOf("sound", SoundEvents.GENERIC_EXPLODE).forGetter(Display::sound),
			NeoApoliParticleTypes.OPTIONS_CODEC.optionalFieldOf("particle").forGetter(Display::particle)
		).apply(instance, Display::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Display> STREAM_CODEC = StreamCodec.composite(
			SoundEvent.STREAM_CODEC, Display::sound,
			ByteBufCodecs.optional(NeoApoliParticleTypes.OPTIONS_STREAM_CODEC), Display::particle,
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
