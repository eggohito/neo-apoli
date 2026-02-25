package io.github.eggohito.neo_apoli.component.entity;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public interface PowersComponent extends Component, AutoSyncedComponent, CommonTickingComponent, RespawnableComponent<PowersComponent> {

	ResourceLocation ID = NeoApoli.id("powers");

	@Override
	default boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory, boolean sameCharacter) {
		return true; //	TODO: Workaround for forcibly saving data. Remove if issue is fixed upstream
	}

	Set<PowerReference> getAllReferences();

	Set<ResourceLocation> getAllSources();


	List<PowerEntry<?>> getAll(boolean includingSubPowers);

	default List<PowerEntry<?>> getAll() {
		return getAll(true);
	}


	List<PowerEntry<?>> getAllFromSource(ResourceLocation source);

	Set<ResourceLocation> getSources(PowerReference reference);


	Power.@NotNull Instance<?> getInstance(PowerReference reference);

	default Power.@Nullable Instance<?> getNullableInstance(PowerReference reference) {

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
		return getInstances(instanceClass, instance -> true);
	}


	List<Power.Instance<?>> getAllInstances();


	<I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass, Predicate<I> instanceFilter);

	default <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass) {
		return hasInstances(instanceClass, instance -> true);
	}


	boolean grantPower(PowerReference reference, ResourceLocation source, boolean invokeCallbacks);

	default boolean grantPower(PowerReference reference, ResourceLocation source) {
		return this.grantPower(reference, source, true);
	}

	default boolean grantPowerNoCallback(PowerReference reference, ResourceLocation source) {
		return this.grantPower(reference, source, false);
	}

	default boolean grantPowerImmediately(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {

		boolean result = this.grantPower(reference, source, invokeCallbacks);
		this.checkForUpdates();

		return result;

	}

	default boolean grantPowerImmediately(PowerReference reference, ResourceLocation source) {
		return this.grantPowerImmediately(reference, source, true);
	}

	default boolean grantPowerImmediatelyNoCallback(PowerReference reference, ResourceLocation source) {
		return this.grantPowerImmediately(reference, source, false);
	}


	boolean revokePower(PowerReference reference, ResourceLocation source, boolean invokeCallbacks);

	default boolean revokePower(PowerReference reference, ResourceLocation source) {
		return this.revokePower(reference, source, true);
	}

	default boolean revokePowerNoCallback(PowerReference reference, ResourceLocation source) {
		return this.revokePower(reference, source, false);
	}

	default boolean revokePowerImmediately(PowerReference reference, ResourceLocation source, boolean invokeCallbacks) {

		boolean result = this.revokePower(reference, source, invokeCallbacks);
		this.checkForUpdates();

		return result;

	}

	default boolean revokePowerImmediately(PowerReference reference, ResourceLocation source) {
		return this.revokePowerImmediately(reference, source, true);
	}

	default boolean revokePowerImmediatelyNoCallback(PowerReference reference, ResourceLocation source) {
		return this.revokePowerImmediately(reference, source, false);
	}


	void checkForUpdates();


	static <I extends Power.Instance<?>> List<I> getInstances(Entity holder, Class<I> instanceClass, Predicate<I> instanceFilter) {
		return NeoApoliEntityComponents.POWERS.maybeGet(holder)
			.map(powersComponent -> powersComponent.getInstances(instanceClass, instanceFilter))
			.orElseGet(ObjectArrayList::new);
	}

	static <I extends Power.Instance<?>> List<I> getInstances(Entity holder, Class<I> instanceClass) {
		return NeoApoliEntityComponents.POWERS.maybeGet(holder)
			.map(powersComponent -> powersComponent.getInstances(instanceClass))
			.orElseGet(ObjectArrayList::new);
	}

	static List<Power.Instance<?>> getAllInstances(Entity holder) {
		return NeoApoliEntityComponents.POWERS.maybeGet(holder)
			.map(PowersComponent::getAllInstances)
			.orElseGet(ObjectArrayList::new);
	}


	static <I extends Power.Instance<?>> boolean hasInstances(Entity holder, Class<I> instanceClass, Predicate<I> instanceFilter) {
		return NeoApoliEntityComponents.POWERS.maybeGet(holder)
			.map(powersComponent -> powersComponent.hasInstances(instanceClass, instanceFilter))
			.orElse(false);
	}

	static <I extends Power.Instance<?>> boolean hasInstances(Entity holder, Class<I> instanceClass) {
		return NeoApoliEntityComponents.POWERS.maybeGet(holder)
			.map(powersComponent -> powersComponent.hasInstances(instanceClass))
			.orElse(false);
	}

}
