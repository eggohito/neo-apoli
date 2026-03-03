package io.github.eggohito.neo_apoli.component.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.codec.NeoApoliCodecs;
import io.github.eggohito.neo_apoli.codec.NeoApoliStreamCodecs;
import io.github.eggohito.neo_apoli.component.NeoApoliEntityComponents;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerReference;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
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

/**
 *  TODO:   Migrate to Fabric's data attachment API. Blocking issue: the API doesn't allow for partial values when
 *          encoding/decoding data attachments, which I think is necessary for lenient parsing of
 *          {@linkplain Packed packed powers}.
 */
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

	record Packed<T>(PowerReference reference, PowerType<?> type, Set<ResourceLocation> sources, Dynamic<T> data) {

		public static final Codec<Packed<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PowerReference.CODEC.fieldOf("id").forGetter(Packed::reference),
			PowerType.CODEC.fieldOf("type").forGetter(Packed::type),
			NeoApoliCodecs.NON_EMPTY_IDENTIFIER_SET.fieldOf("sources").forGetter(Packed::sources),
			Codec.PASSTHROUGH.fieldOf("data").forGetter(Packed::data)
		).apply(instance, Packed::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Packed<?>> STREAM_CODEC = StreamCodec.composite(
			PowerReference.STREAM_CODEC, Packed::reference,
			PowerType.STREAM_CODEC, Packed::type,
			NeoApoliStreamCodecs.NON_EMPTY_IDENTIFIER_SET, Packed::sources,
			NeoApoliStreamCodecs.REGISTRY_PASSTHROUGH, Packed::data,
			Packed::new
		);

	}

}
