package io.github.eggohito.neo_apoli.condition.manager;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.util.manager.ContentManager;
import io.github.eggohito.neo_apoli.util.services.Services;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.NonExtendable
public interface ConditionManager extends ContentManager<ResourceLocation, Condition> {

	ResourceLocation ID = NeoApoli.id("manager/condition");

	ConditionManager INSTANCE = Services.load(ConditionManager.class);

}
