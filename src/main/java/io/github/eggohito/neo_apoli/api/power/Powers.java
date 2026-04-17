package io.github.eggohito.neo_apoli.api.power;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.attachment.NeoApoliEntityAttachments;
import io.github.eggohito.neo_apoli.impl.power.PowersImpl;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerReference;
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

	int VERSION = 1;


	Set<PowerReference> getAllReferences();

	Set<ResourceLocation> getAllSources();


	List<PowerEntry<?>> getAll(boolean includeSubPowers);

	default List<PowerEntry<?>> getAll() {
		return getAll(true);
	}


	List<PowerEntry<?>> getAllFromSource(ResourceLocation source);

	Set<ResourceLocation> getSources(PowerReference reference);


	@NotNull
	Power.Instance<?> getInstance(PowerReference reference);

	@Nullable
	default Power.Instance<?> getNullableInstance(PowerReference reference) {

		if (this.hasInstance(reference)) {
			return this.getInstance(reference);
		}

		else {
			return null;
		}

	}

	default Optional<Power.Instance<?>> getOptionalInstance(PowerReference reference) {
		return Optional.ofNullable(this.getNullableInstance(reference));
	}


	boolean hasInstance(PowerReference reference, ResourceLocation source);

	boolean hasInstance(PowerReference reference);


	<I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass, Predicate<I> instanceFilter);

	default <I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass) {
		return this.getInstances(instanceClass, instance -> true);
	}


	List<Power.Instance<?>> getAllInstances();


	<I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass, Predicate<I> instanceFilter);

	default <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass) {
		return this.hasInstances(instanceClass, instance -> true);
	}


	boolean grant(PowerReference reference, ResourceLocation source, boolean invokeCallbacks);

	default boolean grantWithCallback(PowerReference reference, ResourceLocation source) {
		return this.grant(reference, source, true);
	}

	default boolean grantWithoutCallback(PowerReference reference, ResourceLocation source) {
		return this.grant(reference, source, false);
	}

	default boolean grantImmediately(PowerReference reference, ResourceLocation sources, boolean invokeCallbacks) {

		boolean result = this.grant(reference, sources, invokeCallbacks);
		this.update();

		return result;

	}

	default boolean grantImmediatelyWithCallback(PowerReference reference, ResourceLocation source) {
		return this.grantImmediately(reference, source, true);
	}

	default boolean grantImmediatelyWithoutCallback(PowerReference reference, ResourceLocation source) {
		return this.grantImmediately(reference, source, false);
	}


	boolean revoke(PowerReference reference, ResourceLocation source, boolean invokeCallbacks);

	default boolean revokeWithCallback(PowerReference reference, ResourceLocation source) {
		return this.revoke(reference, source, true);
	}

	default boolean revokeWithoutCallback(PowerReference reference, ResourceLocation source) {
		return this.revoke(reference, source, false);
	}

	default boolean revokeImmediately(PowerReference reference, ResourceLocation sources, boolean invokeCallbacks) {

		boolean result = this.revoke(reference, sources, invokeCallbacks);
		this.update();

		return result;

	}

	default boolean revokeImmediatelyWithCallback(PowerReference reference, ResourceLocation source) {
		return this.revokeImmediately(reference, source, true);
	}

	default boolean revokeImmediatelyWithoutCallback(PowerReference reference, ResourceLocation source) {
		return this.revokeImmediately(reference, source, false);
	}


	void update();


	static Optional<Powers> getOptional(Entity holder) {
		return Optional.ofNullable(holder)
			.filter(entity -> entity.hasAttached(NeoApoliEntityAttachments.POWERS))
			.map(Powers::getOrCreate);
	}

	static @Nullable Powers getNullable(Entity holder) {
		return getOptional(holder).orElse(null);
	}

	static Powers getOrCreate(@NotNull Entity holder) {
		return new PowersImpl(holder, holder.getAttachedOrCreate(NeoApoliEntityAttachments.POWERS));
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
