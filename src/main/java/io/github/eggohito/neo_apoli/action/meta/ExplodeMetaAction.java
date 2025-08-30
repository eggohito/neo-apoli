package io.github.eggohito.neo_apoli.action.meta;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliPacketCodecs;
import io.github.eggohito.neo_apoli.condition.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.BlockCondition;
import io.github.eggohito.neo_apoli.condition.meta.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.block.ConstantBlockCondition;
import io.github.eggohito.neo_apoli.particle.type.NeoApoliParticleTypes;
import io.github.eggohito.neo_apoli.provider.NumberProvider;
import io.github.eggohito.neo_apoli.provider.meta.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.util.context.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.explosion.ExplosionImpl;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.Set;

public interface ExplodeMetaAction {

	BiEntityCondition damageableBiEntityCondition();

	BlockCondition destructibleBlockCondition();

	ExplosionProperty property();

	ExplosionDisplay display();

	@ApiStatus.Internal
	default void internalImpl(Context context) {

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
			context.nullable(ContextParameters.ENTITY),
			null,
			behavior,
			position,
			property.power().nextFloat(context.makeChild(".power")),
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

	default Set<ContextParameter<?>> getAllowedParameters() {
		return Set.of(ContextParameters.POSITION);
	}

	default void validate(ContextAware.ErrorReporter reporter) {

		damageableBiEntityCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BIENTITY))
			.makeChild(".damageable_bientity_condition"));
		destructibleBlockCondition().validate(reporter
			.withContextType(ContextTypeUtil.merge(reporter.getContextType(), ContextTypes.BLOCK))
			.makeChild(".destructible_block_condition"));

		property().validate(reporter);

	}

	static <M extends ExplodeMetaAction> MapCodec<M> codec(Function4<BiEntityCondition, BlockCondition, ExplosionProperty, ExplosionDisplay, M> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			BiEntityCondition.CODEC.optionalFieldOf("damageable_bientity_condition", new ConstantBiEntityCondition(true)).forGetter(ExplodeMetaAction::damageableBiEntityCondition),
			BlockCondition.CODEC.optionalFieldOf("destructible_block_condition", new ConstantBlockCondition(true)).forGetter(ExplodeMetaAction::destructibleBlockCondition),
			ExplosionProperty.CODEC.forGetter(ExplodeMetaAction::property),
			ExplosionDisplay.CODEC.forGetter(ExplodeMetaAction::display)
		).apply(instance, constructor));
	}

	static <M extends ExplodeMetaAction> PacketCodec<RegistryByteBuf, M> packetCodec(Function4<BiEntityCondition, BlockCondition, ExplosionProperty, ExplosionDisplay, M> constructor) {
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
		public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos blockPos, BlockState blockState, float power) {

			Context blockContext = context.copy(builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BLOCK))
				.add(ContextParameters.BLOCK_POS, blockPos)
				.add(ContextParameters.BLOCK_STATE, blockState)
				.addNullable(ContextParameters.BLOCK_ENTITY, world.getBlockEntity(blockPos)));

			return destructibleBlockCondition.test(blockContext.makeChild(".destructible_block_condition"));

		}

		@Override
		public boolean shouldDamage(Explosion explosion, Entity target) {

			Context biEntityContext = context.copy(builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BIENTITY))
				.addNullable(ContextParameters.ACTOR, context.nullable(ContextParameters.ENTITY))
				.addNullable(ContextParameters.TARGET, target));

			return damageableBiEntityCondition.test(biEntityContext.makeChild(".damageable_bientity_condition"));

		}

		@Override
		public float getKnockbackModifier(Entity target) {

			Context biEntityContext = context.copy(builder -> builder
				.withContextType(ContextTypeUtil.merge(context.getType(), ContextTypes.BIENTITY))
				.addNullable(ContextParameters.ACTOR, context.nullable(ContextParameters.ENTITY))
				.addNullable(ContextParameters.TARGET, target));

			return this.property.knockbackMultiplier().nextFloat(biEntityContext.makeChild(".knockback_multiplayer"));

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

		public void validate(ContextAware.ErrorReporter reporter) {
			power().validate(reporter.makeChild(".power"));
			knockbackMultiplier().validate(reporter.makeChild(".knockback_multiplier"));
		}

	}

	record ExplosionDisplay(RegistryEntry<SoundEvent> soundEvent, ParticleEffect smallParticle, ParticleEffect largeParticle) {

		public static final MapCodec<ExplosionDisplay> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			SoundEvent.ENTRY_CODEC.optionalFieldOf("sound", SoundEvents.ENTITY_GENERIC_EXPLODE).forGetter(ExplosionDisplay::soundEvent),
			NeoApoliParticleTypes.EFFECT_CODEC.optionalFieldOf("small_particle", ParticleTypes.EXPLOSION).forGetter(ExplosionDisplay::smallParticle),
			NeoApoliParticleTypes.EFFECT_CODEC.optionalFieldOf("large_particle", ParticleTypes.EXPLOSION_EMITTER).forGetter(ExplosionDisplay::largeParticle)
		).apply(instance, ExplosionDisplay::new));

		public static final PacketCodec<RegistryByteBuf, ExplosionDisplay> PACKET_CODEC = PacketCodec.tuple(
			SoundEvent.ENTRY_PACKET_CODEC, ExplosionDisplay::soundEvent,
			NeoApoliParticleTypes.EFFECT_PACKET_CODEC, ExplosionDisplay::smallParticle,
			NeoApoliParticleTypes.EFFECT_PACKET_CODEC, ExplosionDisplay::largeParticle,
			ExplosionDisplay::new
		);

		public ParticleEffect getParticle(ExplosionImpl explosion) {
			return explosion.isSmall()
				? this.smallParticle()
				: this.largeParticle();
		}

	}

}
