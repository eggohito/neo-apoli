package io.github.eggohito.neo_apoli.mixin.impl.power.custom.replace_loot_table;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.eggohito.neo_apoli.duck.internal.KeyableLootTable;
import io.github.eggohito.neo_apoli.power.custom.ReplaceLootTablePower;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerRegistries.Holder.class)
public abstract class ReloadableServerResourcesMixin {

	@Inject(method = "<init>", at = @At("TAIL"))
	void setupLootTables(HolderLookup.Provider registries, CallbackInfo ci) {
		registries.lookup(Registries.LOOT_TABLE).ifPresent(lootTables -> lootTables.listElements().forEach(reference -> {

			ResourceKey<LootTable> key = reference.key();

			if (reference.value() instanceof KeyableLootTable keyable) {
				keyable.neo_apoli$setup(key, (ReloadableServerRegistries.Holder) (Object) this);
			}

		}));
	}

	@ModifyReturnValue(method = "getLootTable", at = @At("RETURN"))
	LootTable getReplacedTable(LootTable original, ResourceKey<LootTable> key) {

		if (key.equals(ReplaceLootTablePower.REPLACED_TABLE_KEY)) {
			return ReplaceLootTablePower.peek();
		}

		else {
			return original;
		}

	}

}
