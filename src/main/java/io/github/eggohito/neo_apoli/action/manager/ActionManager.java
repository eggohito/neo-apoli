package io.github.eggohito.neo_apoli.action.manager;

import com.mojang.serialization.DataResult;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.action.ActionHolder;
import io.github.eggohito.neo_apoli.util.manager.ContentAndTagManager;
import io.github.eggohito.neo_apoli.util.services.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

@ApiStatus.NonExtendable
public interface ActionManager extends ContentAndTagManager<ResourceLocation, ActionHolder<?>> {

	ResourceLocation ID = NeoApoli.id("manager/action");
	ActionManager INSTANCE = Services.load(ActionManager.class);

	TagEntry.Lookup<ActionHolder<?>> TAG_LOOKUP = new TagEntry.Lookup<>() {

		@Override
		public @Nullable ActionHolder<?> element(ResourceLocation id, boolean required) {
			return INSTANCE.getAsResult(id).result().orElse(null);
		}

		@Override
		public @Nullable Collection<ActionHolder<?>> tag(ResourceLocation id) {
			return INSTANCE.getTagAsResult(id).result().orElse(null);
		}

		@Override
		public String toString() {
			return "Action manager";
		}

	};

	@Override
	default DataResult<ActionHolder<?>> getAsResult(ResourceLocation key) {
		return this.getAsResult(key, k -> "Unknown action: \"" + k + "\"");
	}

	@Override
	default DataResult<ResourceLocation> getKeyAsResult(ActionHolder<?> value) {
		return this.getKeyAsResult(value, v -> "Unregistered action holder: \"" + v + "\"");
	}

	@Override
	default DataResult<List<ActionHolder<?>>> getTagAsResult(ResourceLocation id) {
		return this.getTagAsResult(id, i -> "Unknown action tag: \"" + i + "\"");
	}

	default DataResult<ResourceLocation> getKeyAsResult(Action action) {

		for (var candidate : values()) {

			if (candidate.value() == action) {
				return DataResult.success(candidate.id());
			}

		}

		return DataResult.error(() -> "Unregistered action: " + action);

	}

	default ResourceLocation getKey(Action action) {
		return this.getKeyAsResult(action).getOrThrow();
	}

}
