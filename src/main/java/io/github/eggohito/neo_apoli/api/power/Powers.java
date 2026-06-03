package io.github.eggohito.neo_apoli.api.power;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.impl.power.PowersBuilderImpl;
import io.github.eggohito.neo_apoli.impl.power.PowersImpl;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.registry.attachment.NeoApoliEntityAttachments;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
@ApiStatus.NonExtendable
public interface Powers {

	ResourceLocation ID = NeoApoli.id("powers");


	Set<PowerIdentifier> getAllIds();

	Set<ResourceLocation> getAllSources();


	List<PowerHolder<?>> getAll(boolean includeSubPowers);

	default List<PowerHolder<?>> getAll() {
		return getAll(true);
	}


	List<PowerHolder<?>> getAllFromSource(ResourceLocation source);

	Set<ResourceLocation> getSources(PowerIdentifier id);


	@NotNull
	Power.Instance<?> getInstance(PowerIdentifier id);

	@Nullable
	default Power.Instance<?> getNullableInstance(PowerIdentifier id) {

		if (this.hasInstance(id)) {
			return this.getInstance(id);
		}

		else {
			return null;
		}

	}

	default Optional<Power.Instance<?>> getOptionalInstance(PowerIdentifier id) {
		return Optional.ofNullable(this.getNullableInstance(id));
	}


	boolean hasInstance(PowerIdentifier id, ResourceLocation source);

	boolean hasInstance(PowerIdentifier id);


	<I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass, Predicate<I> instanceFilter);

	default <I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass) {
		return this.getInstances(instanceClass, instance -> true);
	}


	List<Power.Instance<?>> getAllInstances();


	<I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass, Predicate<I> instanceFilter);

	default <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass) {
		return this.hasInstances(instanceClass, instance -> true);
	}


	static Optional<Powers> getOptional(Entity holder) {
		return Optional.ofNullable(holder)
			.filter(entity -> entity.hasAttached(NeoApoliEntityAttachments.POWERS))
			.map(Powers::getOrCreate);
	}

	static @Nullable Powers getNullable(Entity holder) {
		return getOptional(holder).orElse(null);
	}

	static Powers getOrCreate(@NotNull Entity holder) {
		return PowersImpl.of(holder);
	}


	static PowersBuilder builder(@NotNull Entity holder) {
		return PowersBuilderImpl.of(holder);
	}


	static boolean has(Entity holder) {
		return holder != null
			&& holder.hasAttached(NeoApoliEntityAttachments.POWERS);
	}


	static <I extends Power.Instance<?>> List<I> getInstances(Entity holder, Class<I> instanceClass, Predicate<I> instanceFilter) {
		return getOptional(holder)
			.map(powers -> powers.getInstances(instanceClass, instanceFilter))
			.orElseGet(ObjectArrayList::new);
	}

	static <I extends Power.Instance<?>> List<I> getInstances(Entity holder, Class<I> instanceClass) {
		return getOptional(holder)
			.map(powers -> powers.getInstances(instanceClass))
			.orElseGet(ObjectArrayList::new);
	}


	static List<Power.Instance<?>> getAllInstances(Entity holder) {
		return getOptional(holder)
			.map(Powers::getAllInstances)
			.orElseGet(ObjectArrayList::new);
	}


	static <I extends Power.Instance<?>> boolean hasInstances(Entity holder, Class<I> instanceClass, Predicate<I> instanceFilter) {
		return getOptional(holder)
			.stream()
			.anyMatch(powers -> powers.hasInstances(instanceClass, instanceFilter));
	}

	static <I extends Power.Instance<?>> boolean hasInstances(Entity holder, Class<I> instanceClass) {
		return getOptional(holder)
			.stream()
			.anyMatch(powers -> powers.hasInstances(instanceClass));
	}

}
