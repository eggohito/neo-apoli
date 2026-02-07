package io.github.eggohito.neo_apoli.action.custom.meta;

import com.mojang.datafixers.util.Function7;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.condition.custom.bientity.BiEntityCondition;
import io.github.eggohito.neo_apoli.condition.custom.bientity.ConstantBiEntityCondition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.bool.BooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.bool.ConstantBooleanProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.ConstantVec3Provider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.registry.NeoApoliParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public interface SpawnParticlesMetaAction extends MetaAction {

	ContextKeySet DEFAULT_CONDITION_CONTEXT = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.ACTOR_ENTITY)
		.build();

	ParticleOptions particle();
	BiEntityCondition biEntityCondition();

	Vec3Provider position();
	Vec3Provider spread();

	NumberProvider speed();
	NumberProvider count();

	BooleanProvider force();

	@Override
	default void execute(Context context) {

		if (!context.hasAllParameters(this.getRequiredParameters()) || !(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		Vec3 position = position().next(context.forChild(".position"));
		Vec3 spread = spread().next(context.forChild(".spread"));

		boolean force = force().next(context.forChild(".force"));
		float speed = speed().nextFloat(context.forChild(".speed"));
		int count = count().nextInt(context.forChild(".count"));

		for (var viewer : serverLevel.getServer().getPlayerList().getPlayers()) {

			Context biEntityContext = this.biEntityContext(context, viewer).build(serverLevel);

			if (biEntityCondition().test(biEntityContext)) {
				this.sendParticlesTo(biEntityContext, serverLevel, viewer, force, position, count, spread, speed);
			}

		}

	}

	@Override
	default void validate(Context.Validator validator) {

		MetaAction.super.validate(validator);

		biEntityCondition().validate(this.biEntityValidator(validator).forChild(".bientity_condition"));
		position().validate(validator.forChild(".position"));
		spread().validate(validator.forChild(".spread"));
		speed().validate(validator.forChild(".speed"));
		count().validate(validator.forChild(".count"));
		force().validate(validator.forChild(".force"));

	}

	default Context.Builder biEntityContext(Context context, ServerPlayer viewer) {
		return new Context.Builder(context).withRequired(NeoApoliContextParams.ACTOR_ENTITY, viewer);
	}

	default Context.Validator biEntityValidator(Context.Validator validator) {
		return validator.withAdditionalKeysFromSets(DEFAULT_CONDITION_CONTEXT);
	}

	default void sendParticlesTo(Context context, ServerLevel serverLevel, ServerPlayer viewer, boolean force, Vec3 pos, int count, Vec3 spread, float speed) {
		serverLevel.sendParticles(viewer, particle(), force, particle().getType().getOverrideLimiter(), pos.x(), pos.y(), pos.z(), count, spread.x(), spread.y(), spread.z(), speed);
	}

	static <M extends SpawnParticlesMetaAction, P extends Vec3Provider> MapCodec<M> createDefaultedCodec(@Nullable Supplier<@NotNull P> defaultPosition, Function7<ParticleOptions, BiEntityCondition, Vec3Provider, Vec3Provider, NumberProvider, NumberProvider, BooleanProvider, M> constructor) {

		Function<String, MapCodec<Vec3Provider>> positionField = name -> defaultPosition == null
			? Vec3Provider.CODEC.fieldOf(name)
			: Vec3Provider.CODEC.optionalFieldOf(name, defaultPosition.get());

		return RecordCodecBuilder.mapCodec(instance -> instance.group(
			NeoApoliCodecs.PARTICLE_OPTIONS.fieldOf("particle").forGetter(SpawnParticlesMetaAction::particle),
			BiEntityCondition.CODEC.optionalFieldOf("bientity_condition", new ConstantBiEntityCondition(true)).forGetter(SpawnParticlesMetaAction::biEntityCondition),
			positionField.apply("position").forGetter(SpawnParticlesMetaAction::position),
			Vec3Provider.CODEC.optionalFieldOf("spread", new ConstantVec3Provider(0.5, 0.5, 0.5)).forGetter(SpawnParticlesMetaAction::spread),
			NumberProvider.CODEC.optionalFieldOf("speed", new ConstantNumberProvider(0.0F)).forGetter(SpawnParticlesMetaAction::speed),
			NumberProvider.clamped(0, Integer.MAX_VALUE).optionalFieldOf("count", new ConstantNumberProvider(1)).forGetter(SpawnParticlesMetaAction::count),
			BooleanProvider.CODEC.optionalFieldOf("force", new ConstantBooleanProvider(false)).forGetter(SpawnParticlesMetaAction::force)
		).apply(instance, constructor));

	}

	static <M extends SpawnParticlesMetaAction> MapCodec<M> mapCodec(Function7<ParticleOptions, BiEntityCondition, Vec3Provider, Vec3Provider, NumberProvider, NumberProvider, BooleanProvider, M> constructor) {
		return createDefaultedCodec(null, constructor);
	}

	static <M extends SpawnParticlesMetaAction> StreamCodec<RegistryFriendlyByteBuf, M> streamCodec(Function7<ParticleOptions, BiEntityCondition, Vec3Provider, Vec3Provider, NumberProvider, NumberProvider, BooleanProvider, M> constructor) {
		return StreamCodec.composite(
			NeoApoliParticleTypes.OPTIONS_STREAM_CODEC, SpawnParticlesMetaAction::particle,
			BiEntityCondition.STREAM_CODEC, SpawnParticlesMetaAction::biEntityCondition,
			Vec3Provider.STREAM_CODEC, SpawnParticlesMetaAction::position,
			Vec3Provider.STREAM_CODEC, SpawnParticlesMetaAction::spread,
			NumberProvider.STREAM_CODEC, SpawnParticlesMetaAction::speed,
			NumberProvider.STREAM_CODEC, SpawnParticlesMetaAction::count,
			BooleanProvider.STREAM_CODEC, SpawnParticlesMetaAction::force,
			constructor
		);
	}

}
