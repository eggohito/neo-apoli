package io.github.eggohito.neo_apoli.component.entity;

import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.util.PowerReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import org.ladysnake.cca.api.v3.entity.RespawnableComponent;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public interface PowersComponent extends Component, AutoSyncedComponent, CommonTickingComponent, RespawnableComponent<PowersComponent> {

	ResourceLocation ID = NeoApoli.id("powers");

	@Override
	default boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory, boolean sameCharacter) {
		return true; //	TODO: Workaround for forcibly saving data. Remove if issue is fixed upstream
	}


	List<PowerEntry<?>> getAll(boolean includingSubPowers);

	default List<PowerEntry<?>> getAll() {
		return getAll(true);
	}


	List<PowerEntry<?>> getAllFromSource(ResourceLocation source);

	Set<ResourceLocation> getSources(PowerEntry<?> entry);

	default Set<ResourceLocation> getSources(PowerReference reference) {
		return PowerManager.getEntryAsResult(reference)
			.map(this::getSources)
			.mapOrElse(Function.identity(), error -> new ObjectOpenHashSet<>());
	}


	@NotNull
	Power.Instance<?> getInstance(PowerEntry<?> entry);

	@NotNull
	default Power.Instance<?> getInstance(PowerReference reference) {
		return PowerManager.getEntryAsResult(reference)
			.map(this::getInstance)
			.getOrThrow();
	}

	@Nullable
	Power.Instance<?> getNullableInstance(PowerEntry<?> entry);

	@Nullable
	default Power.Instance<?> getNullableInstance(PowerReference reference) {
		return PowerManager.getEntryAsResult(reference)
			.map(this::getNullableInstance)
			.mapOrElse(Function.identity(), error -> null);
	}


	boolean hasInstance(PowerEntry<?> entry, ResourceLocation source);

	default boolean hasInstance(PowerReference reference, ResourceLocation source) {
		return PowerManager.getEntryAsResult(reference)
			.result()
			.map(entry -> this.hasInstance(entry, source))
			.orElse(false);
	}

	boolean hasInstance(PowerEntry<?> entry);

	default boolean hasInstance(PowerReference reference) {
		return PowerManager.getEntryAsResult(reference)
			.result()
			.map(this::hasInstance)
			.orElse(false);
	}


	<I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass, Predicate<I> instanceFilter);

	default <I extends Power.Instance<?>> List<I> getInstances(Class<I> instanceClass) {
		return getInstances(instanceClass, instance -> true);
	}


	List<Power.Instance<?>> getAllInstances();


	<I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass, Predicate<I> instanceFilter);

	default <I extends Power.Instance<?>> boolean hasInstances(Class<I> instanceClass) {
		return hasInstances(instanceClass, instance -> true);
	}


	boolean grantPower(PowerEntry<?> entry, ResourceLocation source);

	default boolean grantPower(PowerReference reference, ResourceLocation source) {
		return PowerManager.getEntryAsResult(reference)
			.map(entry -> this.grantPower(entry, source))
			.mapOrElse(Function.identity(), error -> false);
	}

	boolean grantPowerImmediately(PowerEntry<?> entry, ResourceLocation source);

	default boolean grantPowerImmediately(PowerReference reference, ResourceLocation source) {
		return PowerManager.getEntryAsResult(reference)
			.map(entry -> this.grantPowerImmediately(entry, source))
			.mapOrElse(Function.identity(), error -> false);
	}


	boolean revokePower(PowerEntry<?> entry, ResourceLocation source);

	default boolean revokePower(PowerReference reference, ResourceLocation source) {
		return PowerManager.getEntryAsResult(reference)
			.map(entry -> this.revokePower(entry, source))
			.mapOrElse(Function.identity(), error -> false);
	}

	boolean revokePowerImmediately(PowerEntry<?> entry, ResourceLocation source);

	default boolean revokePowerImmediately(PowerReference reference, ResourceLocation source) {
		return PowerManager.getEntryAsResult(reference)
			.map(entry -> this.revokePowerImmediately(entry, source))
			.mapOrElse(Function.identity(), error -> false);
	}


	void updateGrantedPowers();

	void updateRevokedPowers();

	default void updateChanges() {
		updateGrantedPowers();
		updateRevokedPowers();
	}


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
