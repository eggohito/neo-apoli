package io.github.eggohito.neo_apoli.client.mixin.misc;

import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.util.color.Argb;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements EntityRenderCache {

	@Unique
	Argb neo_apoli$color;

	@Unique
	Entity neo_apoli$entity;

	@Override
	public Argb neo_apoli$getColor() {
		return neo_apoli$color;
	}

	@Override
	public void neo_apoli$setColor(Argb color) {
		this.neo_apoli$color = color;
	}

	@Override
	public Entity neo_apoli$getEntity() {
		return neo_apoli$entity;
	}

	@Override
	public void neo_apoli$setEntity(Entity entity) {
		this.neo_apoli$entity = entity;
	}

}
