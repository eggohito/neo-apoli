package io.github.eggohito.neo_apoli.impl.power;

import com.google.common.collect.SetMultimap;
import io.github.eggohito.neo_apoli.api.power.Powers;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.manager.PowerManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public sealed class AbstractPowers implements Powers permits PowersImpl, PowersBuilderImpl {

	protected final Entity holder;
	protected final Map<PowerIdentifier, Power.Instance<?>> instances;
	protected final SetMultimap<PowerIdentifier, ResourceLocation> sources;

	protected AbstractPowers(Entity holder, Map<PowerIdentifier, Power.Instance<?>> instances, SetMultimap<PowerIdentifier, ResourceLocation> sources) {
		this.holder = holder;
		this.instances = instances;
		this.sources = sources;
	}

	@Override
	public Set<PowerIdentifier> getAllIds() {
		return new ObjectLinkedOpenHashSet<>(instances.keySet());
	}

	@Override
	public Set<ResourceLocation> getAllSources() {
		return new ObjectLinkedOpenHashSet<>(sources.values());
	}

	@Override
	public List<PowerHolder<?>> getAll(boolean includeSubPowers) {

		List<PowerHolder<?>> result = new ObjectArrayList<>();
		instances.keySet().forEach(reference -> PowerManager.getInstance().getAsResult(reference)
			.result()
			.filter(entry -> includeSubPowers || !entry.isSubPower())
			.ifPresent(result::add));

		return result;

	}

	@Override
	public List<PowerHolder<?>> getAllFromSource(ResourceLocation source) {

		List<PowerHolder<?>> result = new ObjectArrayList<>();
		sources.asMap().forEach((reference, sources) -> PowerManager.getInstance().getAsResult(reference)
			.result()
			.filter(entry -> sources.contains(source))
			.ifPresent(result::add));

		return result;

	}

	@Override
	public Set<ResourceLocation> getSources(PowerIdentifier id) {

		Set<ResourceLocation> result = new ObjectLinkedOpenHashSet<>();
		if (sources.containsKey(id)) {
			result.addAll(sources.get(id));
		}

		return result;

	}

	@Override
	public @NotNull Power.Instance<?> getInstance(PowerIdentifier id) {
		return Objects.requireNonNull(instances.get(id), "Entity " + holder.getName().getString() + " didn't have " + id.asDisplayString(false) + " granted!");
	}

	@Override
	public boolean hasInstance(PowerIdentifier id, ResourceLocation source) {
		return sources.get(id).contains(source);
	}

	@Override
	public boolean hasInstance(PowerIdentifier id) {
		return instances.containsKey(id);
	}

	@Override
	public <I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass, Predicate<I> instanceFilter) {

		List<I> result = new ObjectArrayList<>();
		instances.values().forEach(instance -> {

			if (instanceClass.isInstance(instance)) {

				I casted = instanceClass.cast(instance);

				if (instanceFilter.test(casted)) {
					result.add(casted);
				}

			}

		});

		return result;

	}

	@Override
	public List<Power.Instance<?>> getAllInstances() {
		return new ObjectArrayList<>(instances.values());
	}

	@Override
	public <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass, Predicate<I> instanceFilter) {

		for (var instance : instances.values()) {

			if (instanceClass.isInstance(instance) && instanceFilter.test(instanceClass.cast(instance))) {
				return true;
			}

		}

		return false;

	}

}
