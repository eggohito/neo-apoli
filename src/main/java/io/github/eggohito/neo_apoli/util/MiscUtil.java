package io.github.eggohito.neo_apoli.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.exception.DummyCommandExceptionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.fabricmc.fabric.mixin.resource.conditions.RegistryOpsAccessor;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class MiscUtil {

	public static CommandSyntaxException createCommandException(Message message) {
		return new CommandSyntaxException(DummyCommandExceptionType.INSTANCE, message);
	}

	public static CommandSyntaxException createCommandExceptionWithContext(ImmutableStringReader reader, Message message) {
		return new CommandSyntaxException(DummyCommandExceptionType.INSTANCE, message, reader.getString(), reader.getCursor());
	}

	public static <T> Function<T, Void> run(Runnable runnable) {
		runnable.run();
		return null;
	}

	@SuppressWarnings("UnstableApiUsage")
	public static boolean isResourceConditionFulfilled(Identifier resourceId, JsonObject jsonObject, String directory, RegistryOps<JsonElement> ops) {
		return ResourceConditionsImpl.applyResourceConditions(jsonObject, directory, resourceId, ((RegistryOpsAccessor) ops).getRegistryInfoGetter());
	}

	public static boolean isResourceConditionFulfilled(Identifier resourceId, JsonElement jsonElement, String directory, RegistryOps<JsonElement> ops) {
		return !(jsonElement instanceof JsonObject jsonObject)
			|| isResourceConditionFulfilled(resourceId, jsonObject, directory, ops);
	}

}
