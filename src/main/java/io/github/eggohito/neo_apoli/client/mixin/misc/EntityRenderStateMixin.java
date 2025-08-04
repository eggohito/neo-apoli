package io.github.eggohito.neo_apoli.client.mixin.misc;

import io.github.eggohito.neo_apoli.client.duck.EntityRenderCache;
import io.github.eggohito.neo_apoli.util.color.Argb;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements EntityRenderCache {

	@Unique
	protected WeakReference<Argb> neo_apoli$color = new WeakReference<>(null);

	@Unique
	protected WeakReference<Entity> neo_apoli$entity = new WeakReference<>(null);

	@Override
	public Argb neo_apoli$getColor() {
		return neo_apoli$color.get();
	}

	@Override
	public void neo_apoli$setColor(@Nullable Argb color) {

		if (color == null) {
			this.neo_apoli$color.clear();
		}

		else {
			this.neo_apoli$color = new WeakReference<>(color);
		}

	}

	@Override
	public Entity neo_apoli$getEntity() {
		return neo_apoli$entity.get();
	}

	@Override
	public void neo_apoli$setEntity(@Nullable Entity entity) {

		if (entity == null) {
			this.neo_apoli$entity.clear();
		}

		else {
			this.neo_apoli$entity = new WeakReference<>(entity);
		}

	}

}
