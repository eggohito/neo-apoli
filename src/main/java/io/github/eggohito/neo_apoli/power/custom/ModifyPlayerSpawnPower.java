package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@EqualsAndHashCode
@Getter
public class ModifyPlayerSpawnPower extends Power implements Prioritized<ModifyPlayerSpawnPower> {

	public static final MapCodec<ModifyPlayerSpawnPower> CODEC = RecordCodecBuilder.mapCodec(instance -> addActiveConditionField(instance)
		.and(Level.RESOURCE_KEY_CODEC.fieldOf("dimension").forGetter(ModifyPlayerSpawnPower::getDimension))
		.and(TagKey.hashedCodec(Registries.BIOME).optionalFieldOf("biome_tag").forGetter(ModifyPlayerSpawnPower::getBiomeTag))
		.and(TagKey.hashedCodec(Registries.STRUCTURE).optionalFieldOf("structure_tag").forGetter(ModifyPlayerSpawnPower::getStructureTag))
		.and(Codec.INT.optionalFieldOf("priority", 0).forGetter(ModifyPlayerSpawnPower::getPriority))
		.apply(instance, ModifyPlayerSpawnPower::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, ModifyPlayerSpawnPower> STREAM_CODEC = StreamCodec.composite(
		ByteBufCodecs.optional(Condition.STREAM_CODEC), Power::getActiveCondition,
		ResourceKey.streamCodec(Registries.DIMENSION), ModifyPlayerSpawnPower::getDimension,
		ByteBufCodecs.optional(TagKey.streamCodec(Registries.BIOME)), ModifyPlayerSpawnPower::getBiomeTag,
		ByteBufCodecs.optional(TagKey.streamCodec(Registries.STRUCTURE)), ModifyPlayerSpawnPower::getStructureTag,
		ByteBufCodecs.INT, ModifyPlayerSpawnPower::getPriority,
		ModifyPlayerSpawnPower::new
	);

	private final ResourceKey<Level> dimension;
	private final Optional<TagKey<Biome>> biomeTag;
	private final Optional<TagKey<Structure>> structureTag;
	private final int priority;

	public ModifyPlayerSpawnPower(Optional<Condition> activeCondition, ResourceKey<Level> dimension, Optional<TagKey<Biome>> biomeTag, Optional<TagKey<Structure>> structureTag, int priority) {
		super(activeCondition);
		this.dimension = dimension;
		this.biomeTag = biomeTag;
		this.structureTag = structureTag;
		this.priority = priority;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MODIFY_PLAYER_SPAWN;
	}

	@Override
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance(holder, this);
	}

	@Override
	public void validate(Context.Validator validator) {
		super.validate(validator);
		RegistryUtil.validateKey(validator.forChild(".dimension"), this.getDimension());
		this.getBiomeTag().ifPresent(biomeTag -> RegistryUtil.validateTag(validator.forChild(".biome_tag"), biomeTag));
		this.getStructureTag().ifPresent(structureTag -> RegistryUtil.validateTag(validator.forChild(".structure_tag"), structureTag));
	}

	public static class Instance extends Power.Instance<ModifyPlayerSpawnPower> {

		private static final MapCodec<Optional<ServerPlayer.RespawnConfig>> DATA_CODEC = ExtraCodecs.optionalEmptyMap(ServerPlayer.RespawnConfig.CODEC).fieldOf("respawn");

		@Getter
		private Optional<ServerPlayer.RespawnConfig> respawnConfig;
		@Getter
		private Optional<TeleportTransition> respawnTeleport;

		@Getter
		private boolean completed;
		@Getter
		private boolean initialized;

		protected Instance(@NotNull Entity holder, @NotNull ModifyPlayerSpawnPower power) {
			super(holder, power);
			this.respawnConfig = Optional.empty();
			this.respawnTeleport = Optional.empty();
		}

		@Override
		public void onTick() {

			if (holder instanceof ServerPlayer serverPlayer) {

				MinecraftServer server = serverPlayer.server;
				CompletableFuture<Optional<ServerPlayer.RespawnConfig>> respawnFuture = CompletableFuture.supplyAsync(() -> this.findRespawn(server));

				respawnFuture.thenAccept(this::setRespawn);

			}

			this.initialized = true;

		}

		@Override
		public boolean shouldTick() {
			return !initialized;
		}

		@Override
		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, MapLike<I> mapInput) {
			return DATA_CODEC.decode(ops, mapInput)
				.ifSuccess(this::setRespawn)
				.map(ignored -> Unit.INSTANCE);
		}

		@Override
		public <O> RecordBuilder<O> encodeData(RegistryOps<O> ops, RecordBuilder<O> prefix) {
			return DATA_CODEC.encode(this.getRespawnConfig(), ops, prefix);
		}

		private void setRespawn(Optional<ServerPlayer.RespawnConfig> config) {

			this.respawnConfig = config;
			this.completed = true;

			if (holder.level() instanceof ServerLevel serverLevel) {

				MinecraftServer server = serverLevel.getServer();
				ServerLevel actualDimension = Objects.requireNonNull(server.getLevel(power.getDimension()));

				this.respawnTeleport = config.map(inner -> new TeleportTransition(actualDimension, inner.pos().getBottomCenter(), Vec3.ZERO, 0.0F, 0.0F, TeleportTransition.DO_NOTHING));

			}

		}

		private Optional<ServerPlayer.RespawnConfig> findRespawn(MinecraftServer server) {

			//	TODO: Add config flags for these properties
			int horizontalSteps = 64;
			int verticalSteps = 64;
			int radius = 6400;
			int range = 64;

			ServerLevel dimension = Objects.requireNonNull(server.getLevel(power.getDimension()), "Target dimension cannot be null!");
			BlockPos dimensionSpawnPos = dimension.getSharedSpawnPos();

			BlockPos biomePos = this.getBiomePos(dimension, dimensionSpawnPos, horizontalSteps, verticalSteps, radius);
			BlockPos structurePos = this.getStructurePos(dimension, biomePos, radius);

			return this.getSpawnPos(dimension, structurePos, range).map(pos -> new ServerPlayer.RespawnConfig(power.getDimension(), BlockPos.containing(pos), 0.0F, true));

		}

		private BlockPos getBiomePos(ServerLevel dimension, BlockPos pos, int horizontalSteps, int verticalSteps, int radius) {

			if (power.getBiomeTag().isEmpty()) {
				return pos;
			}

			var foundBiome = dimension.findClosestBiome3d(
				biomeHolder -> biomeHolder.is(power.getBiomeTag().get()),
				pos,
				radius,
				horizontalSteps,
				verticalSteps
			);

			if (foundBiome != null) {
				return foundBiome.getFirst();
			}

			else {
				return pos;
			}

		}

		private BlockPos getStructurePos(ServerLevel dimension, BlockPos pos, int radius) {

			if (power.getStructureTag().isEmpty()) {
				return pos;
			}

			Registry<Structure> registry = dimension.registryAccess().lookupOrThrow(Registries.STRUCTURE);
			HolderSet.Named<Structure> structures = registry.getOrThrow(power.getStructureTag().get());

			var foundStructure = dimension.getChunkSource().getGenerator().findNearestMapStructure(
				dimension,
				structures,
				pos,
				radius,
				false
			);

			if (foundStructure != null) {
				return foundStructure.getFirst();
			}

			else {
				return pos;
			}

		}

		//	TODO: Improve the searching algorithm and let the spawn radius game rule affect the result position
		private Optional<Vec3> getSpawnPos(ServerLevel dimension, BlockPos startPos, int range) {

			int dx = 1;
			int dz = 0;

			int segmentLength = 1;
			int center = startPos.getY();

			Vec3 spawnPos;
			BlockPos.MutableBlockPos mutableStartPos = startPos.mutable();

			int x = startPos.getX();
			int z = startPos.getZ();

			int segmentProgress = 0;
			int[] offsets = {0, 0};

			int minY = dimension.dimensionType().minY();
			int maxY = dimension.dimensionType().logicalHeight();

			while (offsets[1] < maxY || offsets[0] > minY) {

				for (int steps = 0; steps < range; steps++) {

					x += dx;
					z += dz;

					mutableStartPos.setX(x);
					mutableStartPos.setZ(z);

					++segmentProgress;

					for (var offset : offsets) {

						mutableStartPos.setY(center + offset);
						spawnPos = DismountHelper.findSafeDismountLocation(holder.getType(), dimension, mutableStartPos, true);

						if (spawnPos != null) {
							return Optional.of(spawnPos);
						}

					}

					if (segmentProgress != segmentLength) {
						continue;
					}

					segmentProgress = 0;

					int bdx = dx;
					dx = -dz;
					dz = bdx;

					if (dz == 0) {
						++segmentProgress;
					}

				}

				offsets[0] = offsets[0] - 1;
				offsets[1] = offsets[1] + 1;

			}

			return Optional.empty();

		}

	}

}
