package io.github.eggohito.neo_apoli.power;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowerDataS2CPacket;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.NeoApoliContextKeys;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

/**
 * 	<p>A power gives a certain "ability" to an entity upon being granted. As for what kind of "ability" it provides will
 * 	depend on the implementation (see: {@link Instance}) of the power itself.</p>
 */
@EqualsAndHashCode
@Getter
public abstract class Power implements ContextAware {

	public static final String TYPE_KEY = "type";

	public static final MapCodec<Power> MAP_CODEC = PowerType.CODEC.dispatchMap(TYPE_KEY, Power::getType, PowerType::mapCodec);

	public static final Codec<Power> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<RegistryFriendlyByteBuf, Power> STREAM_CODEC = PowerType.STREAM_CODEC.dispatch(Power::getType, PowerType::packetCodec);

	private final Optional<Condition> activeCondition;

	public Power(Optional<Condition> activeCondition) {
		this.activeCondition = activeCondition;
	}

	public Power() {
		this(Optional.empty());
	}

	public abstract PowerType<?> getType();

	public abstract Instance<?> createInstance(Entity holder);

	@Override
	public void validate(Context.Validator validator) {
		this.getActiveCondition().ifPresent(activeCondition -> activeCondition.validate(validator.forChild(".active_condition")));
	}

	protected static <P extends Power> Products.P1<RecordCodecBuilder.Mu<P>, Optional<Condition>> addActiveConditionField(RecordCodecBuilder.Instance<P> instance) {
		return instance.group(Condition.CODEC.optionalFieldOf("active_condition").forGetter(Power::getActiveCondition));
	}

	/**
	 * 	<p>The class responsible for providing the functionality of a power. An instance of this class is created
	 * 	every time a power is granted to an entity to ensure that each instance is unique to each entity.</p>
	 *
	 * 	<p>The uniqueness of each instance is especially relevant for storing data.</p>
	 */
	@Getter
	public abstract static class Instance<P extends Power> implements ContextAware {

		protected final P power;
		protected final Entity holder;
		protected final PowerEntry<P> entry;

		protected Instance(@NotNull Entity holder, @NotNull P power) {
			this.power = power;
			this.holder = holder;
			this.entry = getMatchingEntry(power);
		}

		@Override
		public Set<ContextKey<?>> getRequiredParameters() {
			return power.getRequiredParameters();
		}

		@Override
		public void validate(Context.Validator validator) {
			power.validate(validator);
		}

		public Context.Validator createValidator() {
			return this.entry.createValidator().withLookupProvider(holder.level().registryAccess());
		}

		public Context.Builder createHolderContextBuilder() {
			return power.getType().contextBuilder()
				.withValidator(this.createValidator())
				.add(NeoApoliContextKeys.THIS_ENTITY, holder)
				.add(NeoApoliContextKeys.THIS_POS, holder.position());
		}

		public Context createHolderContext() {
			return this.createHolderContextBuilder().build(holder.level());
		}

		public final void syncData() {

			PowerReference reference = this.getEntry().reference();
			RegistryOps<Tag> nbtOps = holder.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);

			if (!holder.level().isClientSide()) {

				switch (this.encodeData(nbtOps)) {
					case DataResult.Success<Tag> success -> {

						Dynamic<Tag> data = new Dynamic<>(nbtOps, success.value());
						SynchronizePowerDataS2CPacket packet = new SynchronizePowerDataS2CPacket(holder.getId(), reference, data);

						for (ServerPlayer trackingPlayer : MiscUtil.getTrackingPlayers(holder)) {
							ServerPlayNetworking.send(trackingPlayer, packet);
						}

					}
					case DataResult.Error<Tag> error ->
						NeoApoli.LOGGER.warn("Couldn't encode data of {} to sync to entity {}: {}", reference.asDisplayString(false), holder.getName().getString(), error.message());
				}

			}

			else {
				NeoApoli.LOGGER.warn("Couldn't initialize syncing data of {} from entity {} in the client!", reference.asDisplayString(false), holder.getName().getString());
			}

		}

		public <O> RecordBuilder<O> encodeData(RegistryOps<O> ops, RecordBuilder<O> prefix) {
			return prefix;
		}

		public final <O> DataResult<O> encodeData(RegistryOps<O> ops) {
			return this.encodeData(ops, ops.mapBuilder()).build(ops.empty());
		}

		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, MapLike<I> mapInput) {
			return DataResult.success(Unit.INSTANCE);
		}

		public final <I> DataResult<Unit> decodeData(RegistryOps<I> ops, I input) {
			return ops.getMap(input).flatMap(mapInput -> this.decodeData(ops, mapInput));
		}

		public void onAdded() {

		}

		public void onGranted() {

		}

		public void onRemoved() {

		}

		public void onRevoked() {

		}

		public void onRespawned() {

		}

		public void onTick() {

		}

		public boolean isImmutable() {
			return true;
		}

		public boolean shouldTick() {
			return false;
		}

		public boolean isActive(Context context) {
			return power.getActiveCondition()
				.map(activeCondition -> activeCondition.test(context.forChild(".active_condition")))
				.orElse(true);
		}

	}

	private static <P extends Power> PowerEntry<P> getMatchingEntry(P power) {

		DataResult<PowerEntry<P>> entry = PowerManager.getReferenceAsResult(power).flatMap(PowerManager::getEntryAsResult).flatMap(e -> {

			if (power.getClass().isInstance(e.power())) {
				//noinspection unchecked
				return DataResult.success((PowerEntry<P>) e);
			}

			else {
				return DataResult.error(() -> "Power entry \"" + e.reference() + "\" from power manager doesn't match! (Power from instance: " + power + ", power from entry: " + e.power() + ")");
			}

		});

		return entry.getOrThrow();

	}

}
