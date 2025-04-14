package io.github.eggohito.neo_apoli.action.context.entity;

import io.github.eggohito.neo_apoli.action.context.ActionContext;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record EntityActionContext(Optional<Entity> entity) implements ActionContext<EntityConditionContext> {

	public EntityActionContext(@Nullable Entity entity) {
		this(Optional.ofNullable(entity));
	}

	@Override
	public EntityConditionContext convert() {
		return new EntityConditionContext(this.entity());
	}

}
