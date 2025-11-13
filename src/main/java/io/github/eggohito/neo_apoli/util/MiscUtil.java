package io.github.eggohito.neo_apoli.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.MapCodec;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.exception.DummyCommandExceptionType;
import io.github.eggohito.neo_apoli.mixin.access.RegistryOpsAccessor;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class MiscUtil {

	public static ImmutableBiMap<String, ActionResult> ACTION_RESULTS = ImmutableBiMap.<String, ActionResult>builder()
		.put("success", ActionResult.SUCCESS)
		.put("success_server", ActionResult.SUCCESS_SERVER)
		.put("consume", ActionResult.CONSUME)
		.put("fail", ActionResult.FAIL)
		.put("pass", ActionResult.PASS)
		.build();

	public static CommandSyntaxException createCommandException(Message message) {
		return new CommandSyntaxException(DummyCommandExceptionType.INSTANCE, message);
	}

	public static CommandSyntaxException createCommandExceptionWithContext(ImmutableStringReader reader, Message message) {
		return new CommandSyntaxException(DummyCommandExceptionType.INSTANCE, message, reader.getString(), reader.getCursor());
	}

	private static final MapCodec<Optional<ResourceCondition>> RESOURCE_CONDITION_MAP_CODEC = ResourceCondition.CONDITION_CODEC.optionalFieldOf(ResourceConditions.CONDITIONS_KEY);

	public static boolean isResourceConditionFulfilled(Identifier resourceId, JsonElement jsonElement, String directory, RegistryOps<JsonElement> ops) {
		RegistryOps.RegistryInfoGetter infoGetter = ((RegistryOpsAccessor) ops).getRegistryInfoGetter();
		return ops.getMap(jsonElement)
			.result()
			.flatMap(mapLike -> RESOURCE_CONDITION_MAP_CODEC.decode(ops, mapLike)
				.ifError(error -> NeoApoli.LOGGER.error("Failed to parse resource conditions for file of type {} with id {}, skipping: {}", directory, resourceId, error.message()))
				.result()
				.map(optCondition -> optCondition
					.map(condition -> condition.test(infoGetter))
					.orElse(true)))
			.orElse(true);
	}

	public static boolean isResultPass(ActionResult result) {
		return (result instanceof ActionResult.Success(ActionResult.SwingSource swingSource, ActionResult.ItemContext ignored) && swingSource == ActionResult.SwingSource.NONE)
			|| result instanceof ActionResult.PassToDefaultBlockAction
			|| result instanceof ActionResult.Pass;
	}

	public static ActionResult overrideResult(ActionResult oldResult, ActionResult newResult) {

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

	public static Set<ServerPlayerEntity> getTrackingPlayers(Entity entity) {

		Set<ServerPlayerEntity> players = new ObjectOpenHashSet<>(PlayerLookup.tracking(entity));

		if (entity instanceof ServerPlayerEntity selfPlayer) {
			players.add(selfPlayer);
		}

		return players;

	}

	@Nullable
	public static SavedBlockPosition getInWallBlock(LivingEntity entity) {

		World world = entity.getWorld();
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < 8; i++) {

			double d = entity.getX() + (i % 2 - 0.5F) * entity.getWidth() * 0.8F;
			double e = entity.getEyeY() + ((i >> 1) % 2 - 0.5F) * 0.1F * entity.getScale();
			double f = entity.getZ() + ((i >> 2) % 2 - 0.5F) * entity.getWidth() * 0.8F;

			mutable.set(d, e, f);
			BlockState blockState = entity.getWorld().getBlockState(mutable);

			if (blockState.getRenderType() != BlockRenderType.INVISIBLE && blockState.shouldBlockVision(world, mutable)) {
				return new SavedBlockPosition(world, mutable, blockState, world.getBlockEntity(mutable));
			}

		}

		return null;

	}

	public static boolean hasFinishedReading(StringReader reader) {
		return !reader.canRead()
			|| reader.peek() == ' ';
	}

	public static boolean hasEntity(ShapeContext shapeContext) {
		return shapeContext instanceof EntityShapeContext entityShapeContext
			&& entityShapeContext.getEntity() != null;
	}

}
