package io.github.eggohito.neo_apoli.condition.context;

import net.minecraft.entity.Entity;

public record EntityConditionContext(Entity entity) implements ConditionContext {

}
