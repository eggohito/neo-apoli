package io.github.eggohito.neo_apoli.action.custom.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.action.custom.bientity.BiEntityAction;
import io.github.eggohito.neo_apoli.action.custom.bientity.NothingBiEntityAction;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionType;
import io.github.eggohito.neo_apoli.action.type.entity.EntityActionTypes;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.provider.custom.nbt.ConstantNbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.nbt.NbtProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.ConstantNumberProvider;
import io.github.eggohito.neo_apoli.provider.custom.number.NumberProvider;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.FloatSupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

public record ShootEntityAction(EntityType<?> entityType, BiEntityAction biEntityAction, NbtProvider tag, NumberProvider divergence, NumberProvider speed, NumberProvider count) implements EntityAction {

	private static final ContextKeySet CONDITION_CONTEXT = new ContextKeySet.Builder()
		.required(NeoApoliContextParams.ACTOR_ENTITY)
		.required(NeoApoliContextParams.TARGET_ENTITY)
		.required(NeoApoliContextParams.PROJECTILE_ENTITY)
		.build();

	public static final MapCodec<ShootEntityAction> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		EntityType.CODEC.fieldOf("entity_type").forGetter(ShootEntityAction::entityType),
		BiEntityAction.CODEC.optionalFieldOf("bientity_action", NothingBiEntityAction.INSTANCE).forGetter(ShootEntityAction::biEntityAction),
		NbtProvider.CODEC.optionalFieldOf("tag", new ConstantNbtProvider(new CompoundTag())).forGetter(ShootEntityAction::tag),
		NumberProvider.CODEC.optionalFieldOf("divergence", new ConstantNumberProvider(1.0F)).forGetter(ShootEntityAction::divergence),
		NumberProvider.CODEC.optionalFieldOf("speed", new ConstantNumberProvider(1.0F)).forGetter(ShootEntityAction::speed),
		NumberProvider.CODEC.optionalFieldOf("count", new ConstantNumberProvider(1)).forGetter(ShootEntityAction::count)
	).apply(instance, ShootEntityAction::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ShootEntityAction> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.registry(Registries.ENTITY_TYPE), ShootEntityAction::entityType,
		BiEntityAction.STREAM_CODEC, ShootEntityAction::biEntityAction,
		NbtProvider.STREAM_CODEC, ShootEntityAction::tag,
		NumberProvider.STREAM_CODEC, ShootEntityAction::divergence,
		NumberProvider.STREAM_CODEC, ShootEntityAction::speed,
		NumberProvider.STREAM_CODEC, ShootEntityAction::count,
		ShootEntityAction::new
	);

	@Override
	public EntityActionType<?> getType() {
		return EntityActionTypes.SHOOT;
	}

	@Override
	public void execute(Context context) {

		if (!(context.level() instanceof ServerLevel serverLevel) || !context.hasParameter(NeoApoliContextParams.THIS_ENTITY)) {
			return;
		}

		Entity shooter = context.getRequired(NeoApoliContextParams.THIS_ENTITY);
		Vec3 shooterMovement = shooter.getDeltaMovement();

		float pitch = shooter.getViewXRot(1.0F);
		float yaw = shooter.getViewYRot(1.0F);

		RandomSource random = serverLevel.getRandom();
		int count = count().nextInt(context.forChild(".count"));

		for (int i = 0; i < count; i++) {

			if (!(tag().nextTag(context.forChild(".tag")) instanceof CompoundTag entityTag)) {
				continue;
			}

			float divergence = divergence().nextFloat(context.forChild(".divergence"));
			float speed = speed().nextFloat(context.forChild(".speed"));

			CompoundTag entityDefinitionTag = entityTag.copy();
			entityDefinitionTag.put("id", EntityType.CODEC.encodeStart(serverLevel.registryAccess().createSerializationContext(NbtOps.INSTANCE), entityType()).getOrThrow());

			switch (EntityType.loadEntityRecursive(entityDefinitionTag, serverLevel, EntitySpawnReason.MOB_SUMMONED, e -> update(e, shooter.getEyePosition(1.0F)))) {
				case Projectile projectile -> {

					if (projectile instanceof AbstractHurtingProjectile hurtingProjectile) {
						hurtingProjectile.accelerationPower = speed;
					}

					projectile.shootFromRotation(shooter, pitch, yaw, 0.0F, speed, divergence);
					projectile.setOwner(shooter);

					this.postShoot(context, serverLevel, shooter, projectile, entityTag);

				}
				case Entity entity -> {

					float factor = 0.0172275F;
					float degrees = (float) Math.PI / 180F;

					float x = -Mth.sin(yaw * degrees) * Mth.cos(pitch * degrees);
					float y = -Mth.sin(pitch * degrees);
					float z = Mth.cos(yaw * degrees) * Mth.cos(pitch * degrees);

					FloatSupplier triangle = () -> random.triangle(0.0F, factor * divergence);
					Vec3 shootMovement = new Vec3(x, y, z)
						.normalize()
						.add(triangle.getAsFloat(), triangle.getAsFloat(), triangle.getAsFloat())
						.add(shooterMovement.x(), shooter.onGround() ? 0.0 : shooterMovement.y(), shooterMovement.z());

					entity.setDeltaMovement(shootMovement);
					this.postShoot(context, serverLevel, shooter, entity, entityTag);

				}
				case null -> {
					//	No-op
				}
			}

		}

	}

	private void postShoot(Context context, ServerLevel serverLevel, Entity shooter, Entity entity, CompoundTag entityTag) {

		CompoundTag mergedTag = entity.saveWithoutId(new CompoundTag());
		mergedTag.merge(entityTag);
		entity.load(mergedTag);

		serverLevel.tryAddFreshEntityWithPassengers(entity);

		Context biEntityContext = new Context.Builder(context)
			.withRequired(NeoApoliContextParams.ACTOR_ENTITY, shooter)
			.withRequired(NeoApoliContextParams.TARGET_ENTITY, entity)
			.withRequired(NeoApoliContextParams.PROJECTILE_ENTITY, entity)
			.build(serverLevel);

		biEntityAction().execute(biEntityContext.forChild(".bientity_action"));

	}

	private Entity update(Entity entity, Vec3 pos) {
		entity.snapTo(pos);
		return entity;
	}

	@Override
	public void validate(Context.Validator validator) {

		EntityAction.super.validate(validator);
		biEntityAction().validate(validator.withAdditionalKeysFromSets(CONDITION_CONTEXT).forChild(".bientity_action"));

		tag().validate(validator.forChild(".tag"));
		divergence().validate(validator.forChild(".divergence"));
		speed().validate(validator.forChild(".speed"));
		count().validate(validator.forChild(".count"));

	}

}
