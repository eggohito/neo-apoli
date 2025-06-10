package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.type.ActionType;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.particle.NothingParticleEffect;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;

import java.util.Optional;
import java.util.Set;

public interface ExplodeMetaAction<T extends ActionType<?>> extends Action<T> {

	ContextType ENTITY_CONTEXT_TYPE = new ContextType.Builder()
		.require(ContextParameters.POSITION)
		.allow(ContextParameters.THIS_ENTITY)
		.allow(ContextParameters.ACTOR)
		.allow(ContextParameters.TARGET)
		.build();

	ContextType BLOCK_CONTEXT_TYPE = new ContextType.Builder()
		.require(ContextParameters.POSITION)
		.require(ContextParameters.BLOCK_STATE)
		.allow(ContextParameters.THIS_ENTITY)
		.allow(ContextParameters.BLOCK_ENTITY)
		.build();

	BiEntityCondition damageableBiEntityCondition();

	BlockCondition destructibleBlockCondition();

	ExplosionProperty property();

	ExplosionDisplay display();

	@Override
	default void execute(Context context) {

		World world = context.getWorld();
		Vec3d position = context.required(ContextParameters.POSITION);

		if (!(world instanceof ServerWorld serverWorld)) {
			return;
		}

		ExplosionProperty property = this.property();
		ExplosionDisplay display = this.display();

		CustomExplosionBehavior behavior = new CustomExplosionBehavior(this.damageableBiEntityCondition(), this.destructibleBlockCondition(), property, context);
		ExplosionImpl explosion = new ExplosionImpl(
			serverWorld,
			context.nullable(ContextParameters.THIS_ENTITY),
			null,
			behavior,
			position,
			property.power().floatValue(context.makeChild("power")),
			property.createFire(),
			property.destructionType()
		);

		if (context.hasErrors()) {
			return;
		}

		ParticleEffect particle = display.getParticle(explosion);
		explosion.explode();

		for (ServerPlayerEntity serverPlayer : serverWorld.getPlayers()) {

			if (serverPlayer.squaredDistanceTo(position) >= 4096.0) {
				continue;
			}

			Optional<Vec3d> playerKnockback = Optional.ofNullable(explosion.getKnockbackByPlayer().get(serverPlayer));
			serverPlayer.networkHandler.sendPacket(new ExplosionS2CPacket(position, playerKnockback, particle, display.soundEvent()));

		}

	}

	@Override
	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	@Override
	default void validate(ErrorReporter reporter) {

		Action.super.validate(reporter);

		damageableBiEntityCondition().validate(reporter.makeChild("damageable_bientity_condition"));
		destructibleBlockCondition().validate(reporter.makeChild("destructible_block_condition"));

	}

	static <M extends ExplodeMetaAction<?>> MapCodec<M> codec(Function4<BiEntityCondition, BlockCondition, ExplosionProperty, ExplosionDisplay, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			BiEntityCondition.CODEC.optionalFieldOf("damageable_bientity_condition", new ConstantBiEntityCondition(true)).forGetter(ExplodeMetaAction::damageableBiEntityCondition),
			BlockCondition.CODEC.optionalFieldOf("destructible_block_condition", new ConstantBlockCondition(true)).forGetter(ExplodeMetaAction::destructibleBlockCondition),
			ExplosionProperty.CODEC.forGetter(ExplodeMetaAction::property),
			ExplosionDisplay.CODEC.forGetter(ExplodeMetaAction::display)
		).apply(instance, constructor));
	}

	static <M extends ExplodeMetaAction<?>> PacketCodec<RegistryByteBuf, M> packetCodec(Function4<BiEntityCondition, BlockCondition, ExplosionProperty, ExplosionDisplay, M> constructor) {
		return PacketCodec.tuple(
			BiEntityCondition.PACKET_CODEC, ExplodeMetaAction::damageableBiEntityCondition,
			BlockCondition.PACKET_CODEC, ExplodeMetaAction::destructibleBlockCondition,
			ExplosionProperty.PACKET_CODEC, ExplodeMetaAction::property,
			ExplosionDisplay.PACKET_CODEC, ExplodeMetaAction::display,
			constructor
		);
	}

	class CustomExplosionBehavior extends ExplosionBehavior {

		private final BiEntityCondition damageableBiEntityCondition;
		private final BlockCondition destructibleBlockCondition;

		private final ExplosionProperty property;
		private final Context context;

		CustomExplosionBehavior(BiEntityCondition damageBiEntityCondition, BlockCondition destructibleBlockCondition, ExplosionProperty property, Context context) {
			this.damageableBiEntityCondition = damageBiEntityCondition;
			this.destructibleBlockCondition = destructibleBlockCondition;
			this.property = property;
			this.context = context;
		}

		@Override
		public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {

			Context blockContext = context.copy(builder -> builder
				.withContextType(BLOCK_CONTEXT_TYPE)
				.add(ContextParameters.POSITION, pos.toCenterPos())
				.add(ContextParameters.BLOCK_STATE, state)
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(pos)))
				.makeChild("destructible_block_condition");

			return destructibleBlockCondition.test(blockContext);

		}

		@Override
		public boolean shouldDamage(Explosion explosion, Entity target) {

			Context biEntityContext = context.copy(builder -> builder
				.withContextType(ENTITY_CONTEXT_TYPE)
				.addNullable(ContextParameters.ACTOR, context.nullable(ContextParameters.THIS_ENTITY))
				.addNullable(ContextParameters.TARGET, target));

			return damageableBiEntityCondition.test(biEntityContext);

		}

		@Override
		public float getKnockbackModifier(Entity target) {

			Context knockbackContext = context.copy(builder -> builder
				.withContextType(ENTITY_CONTEXT_TYPE)
				.addNullable(ContextParameters.ACTOR, context.nullable(ContextParameters.ACTOR))
				.addNullable(ContextParameters.TARGET, target));

			return this.property.knockbackMultiplier().floatValue(knockbackContext);

		}

	}

	record ExplosionProperty(Explosion.DestructionType destructionType, NumberProvider power, NumberProvider knockbackMultiplier, boolean createFire) {

		public static final MapCodec<ExplosionProperty> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			NeoApoliCodecs.DESTRUCTION_TYPE.fieldOf("destruction_type").forGetter(ExplosionProperty::destructionType),
			NumberProvider.CODEC.fieldOf("power").forGetter(ExplosionProperty::power),
			NumberProvider.CODEC.optionalFieldOf("knockback_multiplier", new ConstantNumberProvider(1.0)).forGetter(ExplosionProperty::knockbackMultiplier),
			Codec.BOOL.fieldOf("create_fire").forGetter(ExplosionProperty::createFire)
		).apply(instance, ExplosionProperty::new));

		public static final PacketCodec<RegistryByteBuf, ExplosionProperty> PACKET_CODEC = PacketCodec.tuple(
			NeoApoliPacketCodecs.DESTRUCTION_TYPE, ExplosionProperty::destructionType,
			NumberProvider.PACKET_CODEC, ExplosionProperty::power,
			NumberProvider.PACKET_CODEC, ExplosionProperty::knockbackMultiplier,
			PacketCodecs.BOOLEAN, ExplosionProperty::createFire,
			ExplosionProperty::new
		);

		public void validate(ErrorReporter reporter) {
			power().validate(reporter.makeChild("power"));
			knockbackMultiplier().validate(reporter.makeChild("knockback_multiplier"));
		}

	}

	record ExplosionDisplay(RegistryEntry<SoundEvent> soundEvent, ParticleEffect smallParticle, ParticleEffect largeParticle) {

		public static final MapCodec<ExplosionDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SoundEvent.ENTRY_CODEC.optionalFieldOf("sound", Registries.SOUND_EVENT.getEntry(SoundEvents.INTENTIONALLY_EMPTY)).forGetter(ExplosionDisplay::soundEvent),
			ParticleTypes.TYPE_CODEC.optionalFieldOf("small_particle", new NothingParticleEffect()).forGetter(ExplosionDisplay::smallParticle),
			ParticleTypes.TYPE_CODEC.optionalFieldOf("large_particle", new NothingParticleEffect()).forGetter(ExplosionDisplay::largeParticle)
		).apply(instance, ExplosionDisplay::new));

		public static final PacketCodec<RegistryByteBuf, ExplosionDisplay> PACKET_CODEC = PacketCodec.tuple(
			SoundEvent.ENTRY_PACKET_CODEC, ExplosionDisplay::soundEvent,
			ParticleTypes.PACKET_CODEC, ExplosionDisplay::smallParticle,
			ParticleTypes.PACKET_CODEC, ExplosionDisplay::largeParticle,
			ExplosionDisplay::new
		);

		public ParticleEffect getParticle(ExplosionImpl explosion) {
			return explosion.isSmall()
				? this.smallParticle()
				: this.largeParticle();
		}

	}

}
