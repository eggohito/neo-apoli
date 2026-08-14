package io.github.eggohito.neo_apoli.action.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.entity.EntityProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.ConstantNbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.vec3.Vec3Provider;
import io.github.eggohito.neo_apoli.registry.NeoApoliActionTypes;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.function.UnaryOperator;

public record ShootEntityAction(EntityType<?> entityType, NbtProvider tag, Vec3Provider position, Vec3Provider direction, NumberProvider velocity, NumberProvider inaccuracy, NumberProvider count, Optional<EntityProvider> shooter) implements Action {

	public static final MapCodec<ShootEntityAction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityType.CODEC.fieldOf("entity_type").forGetter(ShootEntityAction::entityType),
		NbtProvider.CODEC.optionalFieldOf("tag", new ConstantNbtProvider(new CompoundTag())).forGetter(ShootEntityAction::tag),
		Vec3Provider.CODEC.fieldOf("position").forGetter(ShootEntityAction::position),
		Vec3Provider.CODEC.fieldOf("direction").forGetter(ShootEntityAction::direction),
		NumberProvider.CODEC.optionalFieldOf("velocity", new ConstantNumberProvider(1.0F)).forGetter(ShootEntityAction::velocity),
		NumberProvider.CODEC.optionalFieldOf("inaccuracy", new ConstantNumberProvider(1.0F)).forGetter(ShootEntityAction::inaccuracy),
		NumberProvider.CODEC.optionalFieldOf("count", new ConstantNumberProvider(1)).forGetter(ShootEntityAction::count),
		EntityProvider.CODEC.optionalFieldOf("shooter").forGetter(ShootEntityAction::shooter)
	).apply(instance, ShootEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ShootEntityAction> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.registry(Registries.ENTITY_TYPE), ShootEntityAction::entityType,
		NbtProvider.STREAM_CODEC, ShootEntityAction::tag,
		Vec3Provider.STREAM_CODEC, ShootEntityAction::position,
		Vec3Provider.STREAM_CODEC, ShootEntityAction::direction,
		NumberProvider.STREAM_CODEC, ShootEntityAction::velocity,
		NumberProvider.STREAM_CODEC, ShootEntityAction::inaccuracy,
		NumberProvider.STREAM_CODEC, ShootEntityAction::count,
		ByteBufCodecs.optional(EntityProvider.STREAM_CODEC), ShootEntityAction::shooter,
		ShootEntityAction::new
	);

	@Override
	public Type<?> getType() {
		return NeoApoliActionTypes.SHOOT_ENTITY;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		RandomSource random = serverLevel.getRandom();
		int count = count().getInt(context.forChild(".count"));

		for (int i = 0; i < count; i++) {

			Context positionContext = context.forChild(".position");
			Vec3 position = position().getVec3(positionContext);

			if (positionContext.hasErrors()) {
				continue;
			}

			Context directionContext = context.forChild(".direction");
			Vec3 direction = direction().getVec3(directionContext);

			if (directionContext.hasErrors()) {
				continue;
			}

			CompoundTag entityTag = tag().getTag(context.forChild(".tag"))
				.filter(CompoundTag.class::isInstance)
				.map(CompoundTag.class::cast)
				.orElse(null);

			if (entityTag == null) {
				continue;
			}

			float inaccuracy = inaccuracy().getFloat(context.forChild(".inaccuracy"));
			float velocity = velocity().getFloat(context.forChild(".velocity"));

			CompoundTag entityDefinitionTag = entityTag.copy();
			entityDefinitionTag.putString("id", RegistryUtil.getId(BuiltInRegistries.ENTITY_TYPE, entityType()).toString());

			switch (EntityType.loadEntityRecursive(entityDefinitionTag, serverLevel, EntitySpawnReason.COMMAND, snapTo(position))) {
				case Projectile projectile -> {

					projectile.shoot(direction.x(), direction.y(), direction.z(), inaccuracy, velocity);
					shooter().flatMap(p -> p.getEntity(context.forChild(".entity"))).ifPresent(projectile::setOwner);

					this.postShoot(serverLevel, projectile, entityTag);

				}
				case Entity entity -> {

					Vec3 movement = direction.normalize()
						.add(randomInaccuracy(random, inaccuracy), randomInaccuracy(random, inaccuracy), randomInaccuracy(random, inaccuracy))
						.scale(velocity);

					entity.setDeltaMovement(movement);

					entity.setXRot((float) Mth.atan2(movement.y(), movement.horizontalDistance()) * Mth.RAD_TO_DEG);
					entity.setYRot((float) Mth.atan2(movement.x(), movement.z()) * Mth.RAD_TO_DEG);

					entity.xRotO = entity.getXRot();
					entity.yRotO = entity.getYRot();

					this.postShoot(serverLevel, entity, entityTag);

				}
				case null -> {
					//  No-op
				}
			}

		}

	}

	@Override
	public void validate(Context.Validator validator) {
		Action.super.validate(validator);
		tag().validate(validator.forChild(".tag"));
		position().validate(validator.forChild(".position"));
		direction().validate(validator.forChild(".direction"));
		velocity().validate(validator.forChild(".velocity"));
		inaccuracy().validate(validator.forChild(".inaccuracy"));
		count().validate(validator.forChild(".count"));
		shooter().ifPresent(p -> p.validate(validator.forChild(".shooter")));
	}

	private void postShoot(ServerLevel serverLevel, Entity shotOutEntity, CompoundTag entityTag) {

		CompoundTag mergedTag = shotOutEntity.saveWithoutId(new CompoundTag());
		mergedTag.merge(entityTag);

		shotOutEntity.load(mergedTag);
		serverLevel.tryAddFreshEntityWithPassengers(shotOutEntity);

	}

	private static UnaryOperator<Entity> snapTo(Vec3 origin) {
		return entity -> {
			entity.snapTo(origin);
			return entity;
		};
	}

	private static float randomInaccuracy(RandomSource random, float factor) {
		return random.triangle(0.0F, 0.0172275F * factor);
	}

}
