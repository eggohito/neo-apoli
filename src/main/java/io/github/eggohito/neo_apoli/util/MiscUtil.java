package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableBiMap;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.exception.DummyCommandExceptionType;
import io.github.eggohito.neo_apoli.mixin.access.RegistryOpsAccessor;
import io.github.eggohito.neo_apoli.mixin.access.ReloadableServerRegistriesAccessor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MiscUtil {

	public static final ImmutableBiMap<String, InteractionResult> ACTION_RESULTS = ImmutableBiMap.<String, InteractionResult>builder()
		.put("success", InteractionResult.SUCCESS)
		.put("success_server", InteractionResult.SUCCESS_SERVER)
		.put("consume", InteractionResult.CONSUME)
		.put("fail", InteractionResult.FAIL)
		.put("pass", InteractionResult.PASS)
		.build();

	public static final String ERROR_PADDING = "\n\t";

	public static CommandSyntaxException createCommandException(Message message) {
		return new CommandSyntaxException(DummyCommandExceptionType.INSTANCE, message);
	}

	public static CommandSyntaxException createCommandExceptionWithContext(ImmutableStringReader reader, Message message) {
		return new CommandSyntaxException(DummyCommandExceptionType.INSTANCE, message, reader.getString(), reader.getCursor());
	}

	private static final MapCodec<Optional<ResourceCondition>> RESOURCE_CONDITION_MAP_CODEC = ResourceCondition.CONDITION_CODEC.optionalFieldOf(ResourceConditions.CONDITIONS_KEY);

	public static <I> boolean isResourceConditionFulfilled(ResourceLocation resourceId, I input, String directory, RegistryOps<I> ops) {
		return ops.getMap(input).mapOrElse(mapInput -> isResourceConditionFulfilled(resourceId, mapInput, directory, ops), error -> true);
	}

	public static <I> boolean isResourceConditionFulfilled(ResourceLocation resourceId, MapLike<I> mapInput, String directory, RegistryOps<I> ops) {
		RegistryOps.RegistryInfoLookup infoLookup = ((RegistryOpsAccessor) ops).getLookupProvider();
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

	public static void tryCatch(Runnable action, Consumer<Exception> catcher) {

		try {
			action.run();
		}

		catch (Exception e) {
			catcher.accept(e);
		}

	}

	public static Set<ServerPlayer> getTrackingPlayers(Entity entity) {

		Set<ServerPlayer> players = new ObjectOpenHashSet<>(PlayerLookup.tracking(entity));

		if (entity instanceof ServerPlayer selfPlayer) {
			players.add(selfPlayer);
		}

		return players;

	}

	@Nullable
	public static SavedBlockPosition getInWallBlock(LivingEntity entity) {

		Level level = entity.level();
		BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

		for (int i = 0; i < 8; i++) {

			double d = entity.getX() + (i % 2 - 0.5F) * entity.getBbWidth() * 0.8F;
			double e = entity.getEyeY() + ((i >> 1) % 2 - 0.5F) * 0.1F * entity.getScale();
			double f = entity.getZ() + ((i >> 2) % 2 - 0.5F) * entity.getBbWidth() * 0.8F;

			mutable.set(d, e, f);
			BlockState blockState = level.getBlockState(mutable);

			if (blockState.getRenderShape() != RenderShape.INVISIBLE && blockState.isViewBlocking(level, mutable)) {
				return new SavedBlockPosition(level, mutable, blockState, level.getBlockEntity(mutable));
			}

		}

		return null;

	}

	public static boolean hasFinishedReading(StringReader reader) {
		return !reader.canRead()
			|| reader.peek() == ' ';
	}

	public static boolean hasEntity(CollisionContext collisionContext) {
		return collisionContext instanceof EntityCollisionContext entityCollisionContext
			&& entityCollisionContext.getEntity() != null;
	}

	public static HolderLookup.Provider getLookupProvider(ReloadableServerResources resources) {
		return ((ReloadableServerRegistriesAccessor.HolderAccessor) resources.fullRegistries()).getRegistries();
	}

	public static String mergeErrors(String firstError, String secondError) {

		String firstPrefix = padError(firstError);
		String secondPrefix = padError(secondError);

		return 	firstPrefix + firstError +
				secondPrefix + secondError;

	}

	private static String padError(String error) {

		if (error.startsWith(ERROR_PADDING)) {
			return "";
		}

		else {
			return ERROR_PADDING + " - ";
		}

	}

	public static DecimalFormat decimalPlacesFormat(int decimals) {
		return new DecimalFormat("#." + Strings.repeat("#", Math.max(decimals, 1)));
	}

	public static DataResult<CompoundTag> asCompoundTag(Tag tag) {
		return tag instanceof CompoundTag compoundTag
			? DataResult.success(compoundTag)
			: DataResult.error(() -> "Not a compound tag: " + tag.toString());
	}

}
