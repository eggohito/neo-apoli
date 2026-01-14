package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.misc.Prioritized;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
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

		private static final MapCodec<Optional<ServerPlayer.RespawnConfig>> LOCATION_MAP_CODEC = ExtraCodecs.optionalEmptyMap(ServerPlayer.RespawnConfig.CODEC).fieldOf("location");

		@NotNull
		@Getter
		private Optional<ServerPlayer.RespawnConfig> spawnLocation = Optional.empty();
		@NotNull
		@Getter
		private Optional<TeleportTransition> spawnTeleport = Optional.empty();

		protected Instance(@NotNull Entity holder, @NotNull ModifyPlayerSpawnPower power) {
			super(holder, power);
		}

		@Override
		public void onGranted() {
			super.onGranted();
			this.findSpawnLocation();
		}

		@Override
		public void onRespawned() {
			super.onRespawned();
			this.findSpawnLocation();
		}

		@Override
		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, MapLike<I> mapInput) {
			return LOCATION_MAP_CODEC.decode(ops, mapInput)
				.ifSuccess(this::setSpawnLocation)
				.map(ignored -> Unit.INSTANCE);
		}

		@Override
		public <O> RecordBuilder<O> encodeData(RegistryOps<O> ops, RecordBuilder<O> prefix) {
			return LOCATION_MAP_CODEC.encode(this.getSpawnLocation(), ops, prefix);
		}

		protected void findSpawnLocation() {

			if (!(holder instanceof ServerPlayer serverPlayer)) {
				return;
			}

			MinecraftServer server = serverPlayer.server;
			CompletableFuture<Optional<ServerPlayer.RespawnConfig>> spawnLookup = CompletableFuture.supplyAsync(() -> this.findSpawnLocation(server));

			spawnLookup.thenAccept(this::setSpawnLocation);

		}

		protected void setSpawnLocation(Optional<ServerPlayer.RespawnConfig> location) {

			if (holder.level() instanceof ServerLevel serverLevel) {

				MinecraftServer server = serverLevel.getServer();
				ServerLevel dimension = Objects.requireNonNull(server.getLevel(power.getDimension()));

				this.spawnTeleport = location.map(config -> new TeleportTransition(dimension, config.pos().getBottomCenter(), Vec3.ZERO, 0.0F, 0.0F, TeleportTransition.DO_NOTHING));

			}

			this.spawnLocation = location;

		}

		private Optional<ServerPlayer.RespawnConfig> findSpawnLocation(MinecraftServer server) {

			if (!(holder instanceof ServerPlayer serverPlayer) || server.getLevel(power.getDimension()) == null) {
				return Optional.empty();
			}

			int horizontalStep = NeoApoli.getConfig().modifyPlayerSpawn.horizontalStep;
			int verticalStep = NeoApoli.getConfig().modifyPlayerSpawn.verticalStep;
			int radius = NeoApoli.getConfig().modifyPlayerSpawn.radius;

			ServerLevel dimension = Objects.requireNonNull(server.getLevel(power.getDimension()));
			BlockPos spawnPos = dimension.getSharedSpawnPos();

			spawnPos = this.findBiomeLocation(dimension, spawnPos, horizontalStep, verticalStep, radius);
			spawnPos = this.findStructureLocation(dimension, spawnPos, radius);

			return Optional.of(new ServerPlayer.RespawnConfig(
				power.getDimension(),
				MiscUtil.adjustSpawnLocationSafely(serverPlayer, dimension, spawnPos),
				0.0F,
				true
			));

		}

		private BlockPos findBiomeLocation(ServerLevel dimension, BlockPos pos, int horizontalSteps, int verticalSteps, int radius) {

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

		private BlockPos findStructureLocation(ServerLevel dimension, BlockPos pos, int radius) {

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
				return foundStructure.getFirst().mutable().setY(pos.getY());
			}

			else {
				return pos;
			}

		}

	}

}
