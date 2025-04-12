package io.github.eggohito.neo_apoli.condition.context.entity;

import io.github.eggohito.neo_apoli.condition.context.ConditionContext;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record EntityConditionContext(Optional<Entity> entity) implements ConditionContext {

	public EntityConditionContext(@Nullable Entity entity) {
		this(Optional.ofNullable(entity));
	}

}
