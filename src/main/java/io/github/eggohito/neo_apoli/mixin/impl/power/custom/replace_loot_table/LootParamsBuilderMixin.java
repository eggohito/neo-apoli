package io.github.eggohito.neo_apoli.mixin.impl.power.custom.replace_loot_table;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.impl.misc.ContextKeySetHolder;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LootParams.Builder.class)
public abstract class LootParamsBuilderMixin {

	@ModifyReturnValue(method = "create", at = @At("RETURN"))
	LootParams cacheKeySet(LootParams original, ContextKeySet keySet) {

		if (original instanceof ContextKeySetHolder keySetHolder) {
			keySetHolder.neo_apoli$setKeySet(keySet);
		}

		return original;

	}

}
