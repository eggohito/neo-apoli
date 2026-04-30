package io.github.eggohito.neo_apoli.client.mixin.impl.misc.render_cache;

import io.github.eggohito.neo_apoli.client.api.misc.EntityRenderCache;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.lang.ref.WeakReference;
import java.util.Optional;

@Mixin(EntityRenderState.class)
public abstract class EntityRenderStateMixin implements EntityRenderCache {

	@Unique
	protected WeakReference<Integer> neo_apoli$color = new WeakReference<>(null);

	@Unique
	protected WeakReference<Entity> neo_apoli$entity = new WeakReference<>(null);

	@Override
	public int neo_apoli$getColor() {
		return Optional.ofNullable(neo_apoli$color.get()).orElse(-1);
	}

	@Override
	public void neo_apoli$setColor(int color) {

		if (color == -1) {
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
