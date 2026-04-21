package io.github.eggohito.neo_apoli.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.mixin.access.RegistryOpsAccessor;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableServerRegistriesAccessor;
import io.github.eggohito.neo_apoli.mixin.access.ServerPlayerAccessor;
import io.github.eggohito.neo_apoli.resource.json.JsonFileToIdConverter;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.jetbrains.annotations.Nullable;
import org.quiltmc.parsers.json.JsonFormat;
import org.quiltmc.parsers.json.JsonReader;
import org.quiltmc.parsers.json.gson.GsonReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MiscUtil {

	public static final ImmutableBiMap<String, InteractionResult> INTERACTION_RESULTS = ImmutableBiMap.<String, InteractionResult>builder()
		.put("success", InteractionResult.SUCCESS)
		.put("success_server", InteractionResult.SUCCESS_SERVER)
		.put("consume", InteractionResult.CONSUME)
		.put("fail", InteractionResult.FAIL)
		.put("pass", InteractionResult.PASS)
		.build();

	public static final Gson GSON = new GsonBuilder()
		.disableHtmlEscaping()
		.setPrettyPrinting()
		.create();

	public static final String ERROR_PADDING = "\n\t";

	public static CommandSyntaxException createCommandException(Message message) {
		return new SimpleCommandExceptionType(message).create();
	}

	public static CommandSyntaxException createCommandExceptionWithContext(ImmutableStringReader reader, Message message) {
		return new SimpleCommandExceptionType(message).createWithContext(reader);
	}

	private static final MapCodec<Optional<ResourceCondition>> RESOURCE_CONDITION_MAP_CODEC = ResourceCondition.CONDITION_CODEC.optionalFieldOf(ResourceConditions.CONDITIONS_KEY);

	public static <I> boolean isResourceConditionFulfilled(ResourceLocation resourceId, I input, String directory, DynamicOps<I> ops) {
		return ops.getMap(input).mapOrElse(mapInput -> isResourceConditionFulfilled(resourceId, mapInput, directory, ops), error -> true);
	}

	public static <I> boolean isResourceConditionFulfilled(ResourceLocation resourceId, MapLike<I> mapInput, String directory, DynamicOps<I> ops) {

		RegistryOps.RegistryInfoLookup infoLookup = ops instanceof RegistryOps<I> registryOps
			? ((RegistryOpsAccessor) registryOps).getLookupProvider()
			: null;

		return RESOURCE_CONDITION_MAP_CODEC.decode(ops, mapInput)
			.ifError(error -> NeoApoli.LOGGER.error("Failed to parse resource conditions for file of type {} with ID '{}', skipping: {}", directory, resourceId, error.message()))
			.result()
			.flatMap(Function.identity())
			.map(condition -> condition.test(infoLookup))
			.orElse(true);

	}

	public static boolean isResultPass(InteractionResult result) {
		return (result instanceof InteractionResult.Success(InteractionResult.SwingSource swingSource, InteractionResult.ItemContext ignored) && swingSource == InteractionResult.SwingSource.NONE)
			|| result instanceof InteractionResult.TryEmptyHandInteraction
			|| result instanceof InteractionResult.Pass;
	}

	public static InteractionResult overrideResult(InteractionResult oldResult, InteractionResult newResult) {

		if (isResultPass(newResult)) {
			return oldResult;
		}

		else {
			return newResult;
		}

	}

	public static Set<ServerPlayer> getTrackingOrEmpty(Entity entity) {

		if (entity.level().isClientSide()) {
			return new ObjectOpenHashSet<>();
		}

		Set<ServerPlayer> trackers = new ObjectOpenHashSet<>(PlayerLookup.tracking(entity));

		if (entity instanceof ServerPlayer self) {
			trackers.add(self);
		}

		return trackers;

	}

	@Nullable
	public static CachedBlock getViewBlocking(Entity entity) {

		if (!(entity instanceof LivingEntity livingEntity)) {
			return null;
		}

		Level level = entity.level();
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (int i = 0; i < 8; i++) {

			double d = livingEntity.getX() + (i % 2 - 0.5F) * livingEntity.getBbWidth() * 0.8F;
			double e = livingEntity.getEyeY() + ((i >> 1) % 2 - 0.5F) * 0.1F * livingEntity.getScale();
			double f = livingEntity.getZ() + ((i >> 2) % 2 - 0.5F) * livingEntity.getBbWidth() * 0.8F;

			mutable.set(d, e, f);
			BlockState blockState = level.getBlockState(mutable);

			if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.isViewBlocking(level, mutable)) {
				return new CachedBlock(mutable, blockState, level.getBlockEntity(mutable));
			}

		}

		return null;

	}

	public static boolean hasFinishedReading(StringReader reader) {
		return !reader.canRead()
			|| Character.isWhitespace(reader.peek());
	}

	@Nullable
	public static Entity getEntityFromCollision(CollisionContext collisionContext) {
		return collisionContext instanceof EntityCollisionContext entityCollision
			? entityCollision.getEntity()
			: null;
	}

	public static boolean collisionHasEntity(CollisionContext collisionContext) {
		return getEntityFromCollision(collisionContext) != null;
	}

	public static HolderLookup.Provider getLookupProvider(ReloadableServerResources resources) {
		return ((ReloadableServerRegistriesAccessor.HolderAccessor) resources.fullRegistries()).getRegistries();
	}

	public static String mergeErrors(String firstError, String secondError) {
		return padError(firstError) + firstError
			+ padError(secondError) + secondError;
	}

	private static String padError(String error) {

		if (error.startsWith(ERROR_PADDING)) {
			return "";
		}

		else {
			return ERROR_PADDING + " - ";
		}

	}

	public static DataResult<CompoundTag> asCompoundTag(Tag tag) {
		return tag instanceof CompoundTag compoundTag
			? DataResult.success(compoundTag)
			: DataResult.error(() -> "Not a compound tag: " + tag.toString());
	}

	public static BlockPos adjustSpawnLocationSafely(ServerPlayer serverPlayer, ServerLevel dimension, BlockPos blockPos) {
		return adjustSpawnLocationSafely((player, dim, pos) -> true, serverPlayer, dimension, blockPos);
	}

	public static BlockPos adjustSpawnLocationSafely(TriPredicate<ServerPlayer, ServerLevel, BlockPos> safeLocationCondition, ServerPlayer serverPlayer, ServerLevel dimension, BlockPos pos) {

		MinecraftServer server = dimension.getServer();
		AABB boundingBox = serverPlayer.getDimensions(Pose.STANDING).makeBoundingBox(Vec3.ZERO);

		if (server.getWorldData().getGameType() != GameType.ADVENTURE) {

			int distanceToBorder = Mth.floor(dimension.getWorldBorder().getDistanceToBorder(pos.getX(), pos.getZ()));
			int radius = Mth.clamp(Math.max(0, server.getSpawnRadius(dimension)), 1, distanceToBorder);

			int diameter = radius * 2 + 1;
			int max = diameter * diameter;

			int coprime = ((ServerPlayerAccessor) serverPlayer).callGetCoprime(max);
			int offset = RandomSource.create().nextInt(max);

			for (int step = 0; step < max; step++) {

				int q = (offset + coprime * step) % max;

				int xOffset = q % diameter;
				int zOffset = q / diameter;

				int x = pos.getX() + xOffset - radius;
				int z = pos.getZ() + zOffset - radius;

				BlockPos candidate = BlockPos.containing(x, pos.getY(), z);
				BlockPos safePos = Optional.ofNullable(DismountHelper.findSafeDismountLocation(serverPlayer.getType(), dimension, candidate, true))
					.map(BlockPos::containing)
					.orElse(null);

				if (safePos != null && safeLocationCondition.test(serverPlayer, dimension, safePos)) {
					return safePos;
				}

			}

        }

		BlockPos result = pos;
		while (result.getY() < dimension.getMaxY() - 1 && !dimension.noCollision(serverPlayer, boundingBox.move(result.getBottomCenter()))) {
			result = result.above();
		}

		while (result.getY() > dimension.getMinY() + 1 && dimension.noCollision(serverPlayer, boundingBox.move(result.below().getBottomCenter()))) {
			result = result.below();
		}

		return result;

	}

	public static <E> void iterateList(List<E> list, BiIntegerConsumer<E> consumer) {

		ListIterator<E> listIterator = list.listIterator();

		while (listIterator.hasNext()) {
			consumer.accept(listIterator.nextIndex(), listIterator.next());
		}

	}

	public static <T, U extends T> Function<T, DataResult<U>> validateType(Class<U> typeClass, Supplier<String> errorSupplier) {
		return type -> {

			if (typeClass.isInstance(type)) {
				return DataResult.success(typeClass.cast(type));
			}

			else {
				return DataResult.error(errorSupplier);
			}

		};
	}

	public static void sendToTracking(Entity tracked, CustomPacketPayload payload) {

		Set<ServerPlayer> trackers = getTrackingOrEmpty(tracked);
		CustomPacketPayload.Type<?> type = payload.type();

		for (var tracker : trackers) {

			if (ServerPlayNetworking.canSend(tracker, type)) {
				ServerPlayNetworking.send(tracker, payload);
			}

			else {
				NeoApoli.LOGGER.debug("Couldn't send payload of type \"{}\" to entity {}!", type.id(), tracker.getName().getString());
			}

		}

	}

	public static <T> DataResult<T> handleResult(DataResult<T> result, Consumer<T> resultOrPartial, Consumer<String> onPartial, Consumer<String> onError) {

		result.resultOrPartial().ifPresent(resultOrPartial);

		result.ifError(error -> {

			if (error.hasResultOrPartial()) {
				onPartial.accept(error.message());
			}

			else {
				onError.accept(error.message());
			}

		});

		return result;

	}

	public static Map<ResourceLocation, JsonWithSource> collectJson(ResourceManager manager, JsonFileToIdConverter converter, DynamicOps<JsonElement> ops, Consumer<String> errorHandler) {
		return collectJson(manager, converter, ops, GSON, errorHandler);
	}

	public static Map<ResourceLocation, JsonWithSource> collectJson(ResourceManager manager, JsonFileToIdConverter converter, DynamicOps<JsonElement> ops, Gson gson, Consumer<String> errorHandler) {

		Map<ResourceLocation, JsonWithSource> result = new Object2ObjectOpenHashMap<>();
		Map<ResourceLocation, JsonFormat> history = new Object2ObjectOpenHashMap<>();

		converter.listMatchingResources(manager).forEach((fileId, resource) -> {

			String packId = resource.sourcePackId();
			ResourceLocation resourceId = converter.fileToId(fileId);

			try (BufferedReader resourceReader = resource.openAsReader()) {

				JsonFormat jsonFormat = converter.getFormat(fileId);
				GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, jsonFormat));

				switch (gson.fromJson(gsonReader, JsonElement.class)) {
					case JsonElement asIs when isResourceConditionFulfilled(resourceId, asIs, converter.directory(), ops) -> {

						var prevFormat = history.putIfAbsent(resourceId, jsonFormat);

						if (prevFormat != null) {
							errorHandler.accept("JSON file \"" + fileId + "\" from pack [" + packId + "] has a duplicate with a different file extension! (prev. file extension: \"." + prevFormat.toString().toLowerCase(Locale.ROOT) + "\")");
						}

						else {
							result.putIfAbsent(resourceId, new JsonWithSource(asIs, packId));
						}

					}
					case JsonElement ignored -> {
						//  No-op since its resource conditions aren't fulfilled
					}
					case null ->
						errorHandler.accept("JSON file \"" + fileId + "\" is empty!");
					default -> {
						//  No-op since every JSON type should already be handled by the first case
					}
				}

			}

			catch (IOException e) {
				errorHandler.accept("Couldn't open JSON file \"" + fileId + "\" from pack [" + packId + "]: " + e);
			}

		});

		return result;

	}

	public static Map<ResourceLocation, List<JsonWithSource>> collectJsonStack(ResourceManager manager, JsonFileToIdConverter converter, DynamicOps<JsonElement> ops, Consumer<String> errorHandler) {
		return collectJsonStack(manager, converter, ops, GSON, errorHandler);
	}

	public static Map<ResourceLocation, List<JsonWithSource>> collectJsonStack(ResourceManager manager, JsonFileToIdConverter converter, DynamicOps<JsonElement> ops, Gson gson, Consumer<String> errorHandler) {

		Map<ResourceLocation, List<JsonWithSource>> result = new Object2ObjectOpenHashMap<>();
		Map<String, Map<ResourceLocation, JsonFormat>> history = new Object2ObjectOpenHashMap<>();

		converter.listMatchingResourceStacks(manager).forEach((fileId, resources) -> {

			for (var resource : resources) {

				String packId = resource.sourcePackId();
				ResourceLocation resourceId = converter.fileToId(fileId);

				try (BufferedReader resourceReader = resource.openAsReader()) {

					JsonFormat jsonFormat = converter.getFormat(fileId);
					GsonReader gsonReader = new GsonReader(JsonReader.create(resourceReader, jsonFormat));

					switch (gson.fromJson(gsonReader, JsonElement.class)) {
						case JsonElement asIs when isResourceConditionFulfilled(resourceId, asIs, converter.directory(), ops) -> {

							var prevFormat = history
								.computeIfAbsent(packId, k -> new Object2ObjectOpenHashMap<>())
								.putIfAbsent(resourceId, jsonFormat);

							if (prevFormat != null) {
								errorHandler.accept("JSON file \"" + fileId + "\" from pack [" + packId + "] has a duplicate with a different file extension! (prev. file extension: \"." + prevFormat.toString().toLowerCase(Locale.ROOT) + "\")");
							}

							else {
								result
									.computeIfAbsent(resourceId, k -> new ObjectArrayList<>())
									.add(new JsonWithSource(asIs, packId));
							}


						}
						case JsonElement ignored -> {
							//  No-op since its resource conditions aren't fulfilled
						}
						case null ->
							errorHandler.accept("JSON file \"" + fileId + "\" is empty!");
						default -> {
							//  No-op since every JSON type should already be handled by the first case
						}
					}

				}

				catch (IOException e) {
					errorHandler.accept("Couldn't open JSON file \"" + fileId + "\" from pack [" + packId + "]: " + e);
				}

			}

		});

		return result;

	}

}
