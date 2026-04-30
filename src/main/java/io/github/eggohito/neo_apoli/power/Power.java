package io.github.eggohito.neo_apoli.power;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.context.Context;
import io.github.eggohito.neo_apoli.context.ContextUser;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowerDataS2CPacket;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.registry.NeoApoliContextParams;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.Reporter;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

/**
 * 	<p>A power gives a certain "ability" to an entity upon being granted. As for what kind of "ability" it provides will
 * 	depend on the implementation (see: {@link Instance}) of the power itself.</p>
 */
@EqualsAndHashCode
@Getter
public abstract class Power implements ContextUser {

	public static final String TYPE_KEY = "type";

	public static final MapCodec<Power> MAP_CODEC = PowerType.CODEC.dispatchMap(TYPE_KEY, Power::getType, PowerType::mapCodec);

	public static final Codec<Power> CODEC = MAP_CODEC.codec();

	public static final StreamCodec<RegistryFriendlyByteBuf, Power> STREAM_CODEC = PowerType.STREAM_CODEC.dispatch(Power::getType, PowerType::streamCodec);

	private final Optional<Condition> activeCondition;

	public Power(Optional<Condition> activeCondition) {
		this.activeCondition = activeCondition;
	}

	public Power() {
		this(Optional.empty());
	}

	public abstract PowerType<?> getType();

	public abstract Instance<?> createInstance();

	@Override
	public void validate(Context.Validator validator) {
		this.getActiveCondition().ifPresent(activeCondition -> activeCondition.validate(validator.forChild(".active_condition")));
	}

	protected static <P extends Power> Products.P1<RecordCodecBuilder.Mu<P>, Optional<Condition>> addActiveConditionField(RecordCodecBuilder.Instance<P> instance) {
		return instance.group(Condition.CODEC.optionalFieldOf("active_condition").forGetter(Power::getActiveCondition));
	}

	/**
	 * 	<p>The class is responsible for providing the functionality of a power. An instance of this class is created
	 * 	every time a power is granted to an entity to ensure that each instance is unique to each entity.</p>
	 *
	 * 	<p>The uniqueness of each instance is especially relevant for storing data.</p>
	 */
	@Getter
	public abstract static class Instance<P extends Power> implements ContextUser {

		protected final PowerIdentifier id;
		protected final P power;

		protected Instance(@NotNull P power) {
			this.id = PowerManager.getIdAsResult(power).getOrThrow(error -> new IllegalStateException("Tried to created an instance of an unregistered power!"));
			this.power = power;
		}

		@Override
		public Set<ContextKey<?>> getRequiredParameters() {
			return power.getRequiredParameters();
		}

		@Override
		public void validate(Context.Validator validator) {
			power.validate(validator);
		}

		public Context.Builder createHolderContextBuilder(Entity holder) {
			return new Context.Builder()
				.withReporter(new Reporter("{\"" + this.getId() + "\"}"))
				.withRequired(NeoApoliContextParams.THIS_ENTITY, holder)
				.withRequired(NeoApoliContextParams.THIS_POS, holder.position());
		}

		public Context createHolderContext(Entity holder) {
			return this.createHolderContextBuilder(holder).build(holder.level());
		}

		public final void syncData(Entity holder) {

			Level level = holder.level();
			RegistryOps<Tag> ops = holder.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);

			if (level.isClientSide()) {
				NeoApoli.LOGGER.warn("Couldn't initialize syncing data of {} from entity {} in the client!", id.asDisplayString(false), holder.getName().getString());
			}

			else if (!PowerManager.contains(id)) {
				NeoApoli.LOGGER.warn("Tried syncing instance data of unregistered {} from entity {}!", id.asDisplayString(false), holder.getName().getString());
			}

			else {
				MiscUtil.handleResult(
					this.encodeData(ops),
					tag -> MiscUtil.sendToTracking(holder, SynchronizePowerDataS2CPacket.single(holder.getId(), ops, id, tag)),
					warning -> NeoApoli.LOGGER.warn("Couldn't fully encode instance data of {} to send to entity {} (sending partially encoded data): {}", id.asDisplayString(false), holder.getName().getString(), warning),
					error -> NeoApoli.LOGGER.error("Couldn't encode instance data of {} to send to entity {}! (skipping): {}", id.asDisplayString(false), holder.getName().getString(), error)
				);
			}

		}

		public <O> RecordBuilder<O> encodeData(DynamicOps<O> ops, RecordBuilder<O> prefix) {
			return prefix;
		}

		public final <O> DataResult<O> encodeData(DynamicOps<O> ops) {
			return this.encodeData(ops, ops.mapBuilder()).build(ops.empty());
		}

		public <I> DataResult<Unit> decodeData(DynamicOps<I> ops, MapLike<I> mapInput) {
			return DataResult.success(Unit.INSTANCE);
		}

		public final <I> DataResult<Unit> decodeData(DynamicOps<I> ops, I input) {
			return ops.getMap(input).flatMap(mapInput -> this.decodeData(ops, mapInput));
		}

		public void onAdded(Entity holder) {

		}

		public void onGranted(Entity holder) {

		}

		public void onRemoved(Entity holder) {

		}

		public void onRevoked(Entity holder) {

		}

		public void onRespawned(Entity holder) {

		}

		public void onTick(Entity holder) {

		}

		public boolean isImmutable(Entity holder) {
			return true;
		}

		public boolean shouldTick(Entity holder) {
			return false;
		}

		public boolean isActive(Context context) {
			return power.getActiveCondition()
				.map(activeCondition -> activeCondition.test(context.forChild(".active_condition")))
				.orElse(true);
		}

	}

}
