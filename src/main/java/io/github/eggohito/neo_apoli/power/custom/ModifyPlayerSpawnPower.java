package io.github.eggohito.neo_apoli.power.custom;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import io.github.eggohito.neo_apoli.api.config.ConfigCategoryRegistrant;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.config.AbstractJsonCodecConfig;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.visitor.ClearableVisitor;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.custom.misc.PrioritizedPower;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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
import org.quiltmc.parsers.json.JsonFormat;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
@EqualsAndHashCode
@Getter
public class ModifyPlayerSpawnPower extends Power implements PrioritizedPower<ModifyPlayerSpawnPower> {

	public static final ClearableVisitor<Instance> VISITOR = ClearableVisitor.createThreadLocalized();

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
	public Type<?> getType() {
		return NeoApoliPowerTypes.MODIFY_PLAYER_SPAWN;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance(this);
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

		protected Instance(@NotNull ModifyPlayerSpawnPower power) {
			super(power);
		}

		@Override
		public void onGranted(Entity holder) {
			super.onGranted(holder);
			this.findSpawnLocation(holder);
		}

		@Override
		public void onRespawned(Entity holder) {
			super.onRespawned(holder);
			this.findSpawnLocation(holder);
		}

		@Override
		public <I> DataResult<Unit> decodeData(DynamicOps<I> ops, MapLike<I> mapInput) {
			return LOCATION_MAP_CODEC.decode(ops, mapInput)
				.ifSuccess(location -> this.spawnLocation = location)
				.map(ignored -> Unit.INSTANCE);
		}

		@Override
		public <O> RecordBuilder<O> encodeData(DynamicOps<O> ops, RecordBuilder<O> prefix) {
			return LOCATION_MAP_CODEC.encode(this.getSpawnLocation(), ops, prefix);
		}

		private CompletableFuture<Void> findSpawnLocation(Entity holder) {

			if (!(holder instanceof ServerPlayer serverPlayer)) {
				return CompletableFuture.completedFuture(null);
			}

			return CompletableFuture
				.supplyAsync(() -> this.getRespawnConfig(serverPlayer))
				.thenAccept(config -> this.setRespawnPoint(serverPlayer, config));

		}

		private void setRespawnPoint(ServerPlayer serverPlayer, Optional<ServerPlayer.RespawnConfig> config) {

			MinecraftServer server = serverPlayer.server;
			ServerLevel dimension = server.getLevel(power.getDimension());

			if (dimension != null) {
				this.spawnTeleport = config.map(inner -> new TeleportTransition(dimension, inner.pos().getBottomCenter(), Vec3.ZERO, 0.0F, 0.0F, TeleportTransition.DO_NOTHING));
			}

		}

		private Optional<ServerPlayer.RespawnConfig> getRespawnConfig(ServerPlayer serverPlayer) {

			MinecraftServer server = serverPlayer.server;
			ServerLevel dimension = server.getLevel(power.getDimension());

			if (dimension == null) {
				return Optional.empty();
			}

			BlockPos spawnPos = dimension.getSharedSpawnPos();

			int horizontalStep = Config.INSTANCE.horizontalStep.get();
			int verticalStep = Config.INSTANCE.verticalStep.get();
			int radius = Config.INSTANCE.radius.get();

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

	public static final class Config extends AbstractJsonCodecConfig<Config> implements ConfigCategoryRegistrant.Entry {

		public static final Config INSTANCE = new Config();
		public static final int VERSION = 1;

		public final ConfigEntry<Integer> horizontalStep = register("horizontal_step", 64, CodecUtil.nonNegativeInt());
		public final ConfigEntry<Integer> verticalStep = register("vertical_step", 64, CodecUtil.nonNegativeInt());
		public final ConfigEntry<Integer> radius = register("radius", 6400, CodecUtil.nonNegativeInt());

		public final ConfigEntry<Boolean> enabled = register("enabled", true, Codec.BOOL);
		public final ConfigEntry<Integer> version = register("version", VERSION, Codec.INT);

		Config() {
			super(FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/type/power/modify_player_spawn.json5"), JsonFormat.JSON5);
		}

		@Override
		public void addGroup(Consumer<OptionGroup> adder) {

			var horizontalStep = Option.<Integer>createBuilder()
				.name(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.option.horizontal_step.name"))
				.description(OptionDescription.of(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.option.horizontal_step.description")))
				.binding(this.horizontalStep.asBinding())
				.controller(option -> IntegerFieldControllerBuilder.create(option)
					.min(0));
			var verticalStep = Option.<Integer>createBuilder()
				.name(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.option.vertical_step.name"))
				.description(OptionDescription.of(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.option.vertical_step.description")))
				.binding(this.verticalStep.asBinding())
				.controller(option -> IntegerFieldControllerBuilder.create(option)
					.min(0));
			var radius = Option.<Integer>createBuilder()
				.name(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.option.radius.name"))
				.description(OptionDescription.of(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.option.radius.description")))
				.binding(this.radius.asBinding())
				.controller(option -> IntegerFieldControllerBuilder.create(option)
					.min(0));
			var enabled = Option.<Boolean>createBuilder()
				.name(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.option.enabled.name"))
				.description(OptionDescription.of(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.option.enabled.description")))
				.binding(this.enabled.asBinding())
				.controller(option -> BooleanControllerBuilder.create(option)
					.onOffFormatter()
					.coloured(true));

			adder.accept(OptionGroup.createBuilder()
				.name(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.name"))
				.description(OptionDescription.of(Component.translatable("config.neo-apoli.type.power.modify_player_spawn.description")))
				.option(horizontalStep.build())
				.option(verticalStep.build())
				.option(radius.build())
				.option(enabled.build())
				.build());

		}

		@Override
		public boolean load() {
			return this.loadFromFile();
		}

		@Override
		public void save() {
			this.saveToFile();
		}

	}

}
