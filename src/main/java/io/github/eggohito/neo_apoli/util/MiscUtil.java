package io.github.eggohito.neo_apoli.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.exception.DummyCommandExceptionType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.fabricmc.fabric.mixin.resource.conditions.RegistryOpsAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

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

	@SuppressWarnings("UnstableApiUsage")
	public static boolean isResourceConditionFulfilled(Identifier resourceId, JsonObject jsonObject, String directory, RegistryOps<JsonElement> ops) {
		return ResourceConditionsImpl.applyResourceConditions(jsonObject, directory, resourceId, ((RegistryOpsAccessor) ops).getRegistryInfoGetter());
	}

	public static boolean isResourceConditionFulfilled(Identifier resourceId, JsonElement jsonElement, String directory, RegistryOps<JsonElement> ops) {
		return !(jsonElement instanceof JsonObject jsonObject)
			|| isResourceConditionFulfilled(resourceId, jsonObject, directory, ops);
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

}
