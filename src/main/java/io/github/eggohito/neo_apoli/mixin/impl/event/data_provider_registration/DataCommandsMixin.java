package io.github.eggohito.neo_apoli.mixin.impl.event.data_provider_registration;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.eggohito.neo_apoli.event.DataProviderRegistration;
import net.minecraft.server.commands.data.DataCommands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Function;

@Mixin(DataCommands.class)
public abstract class DataCommandsMixin {

	@Definition(id = "ALL_PROVIDERS", field = "Lnet/minecraft/server/commands/data/DataCommands;ALL_PROVIDERS:Ljava/util/List;")
	@Expression("ALL_PROVIDERS = @(?)")
	@ModifyExpressionValue(method = "<clinit>", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static ImmutableList<Function<String, DataCommands.DataProvider>> registerCustomDataProviders(ImmutableList<Function<String, DataCommands.DataProvider>> original) {

		ImmutableList.Builder<Function<String, DataCommands.DataProvider>> builder = ImmutableList.builder();
		builder.addAll(original);

		DataProviderRegistration.EVENT.invoker().register(builder::add);
		return builder.build();

	}

}
