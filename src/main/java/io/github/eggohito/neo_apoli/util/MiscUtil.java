package io.github.eggohito.neo_apoli.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.eggohito.neo_apoli.exception.DummyCommandExceptionType;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.fabricmc.fabric.mixin.resource.conditions.RegistryOpsAccessor;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.context.ContextParameter;
import net.minecraft.util.context.ContextType;

import java.util.Arrays;
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

	public static boolean shouldOverrideResult(ActionResult oldResult, ActionResult newResult) {
		return (newResult.isAccepted() && !oldResult.isAccepted())
			|| (newResult instanceof ActionResult.Success bSuccess && bSuccess.swingSource() != ActionResult.SwingSource.NONE && (!(oldResult instanceof ActionResult.Success aSuccess) || aSuccess.swingSource() == ActionResult.SwingSource.NONE));
	}

	public static ActionResult overrideResult(ActionResult oldResult, ActionResult newResult) {

		if (shouldOverrideResult(oldResult, newResult)) {
			return newResult;
		}

		else {
			return oldResult;
		}

	}

	public static ContextType mergeContextTypes(ContextType first, ContextType second) {

		ContextType.Builder builder = new ContextType.Builder();

		Set<ContextParameter<?>> requiredParameters = Sets.union(first.getRequired(), second.getRequired());
		Set<ContextParameter<?>> allowedParameters = Sets.union(first.getAllowed(), second.getAllowed());

		requiredParameters.forEach(parameter -> tryCatch(() -> builder.require(parameter), e -> {}));
		allowedParameters.forEach(parameter -> tryCatch(() -> builder.allow(parameter), e -> {}));

		return builder.build();

	}

	public static ContextType mergeContextTypes(ContextType... contextTypes) {
		return Arrays.stream(contextTypes)
			.reduce(MiscUtil::mergeContextTypes)
			.orElseThrow();
	}

	public static void tryCatch(Runnable action, Consumer<Exception> catcher) {

		try {
			action.run();
		}

		catch (Exception e) {
			catcher.accept(e);
		}

	}

}
