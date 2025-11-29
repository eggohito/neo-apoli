package io.github.eggohito.neo_apoli.power;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.network.packet.s2c.SynchronizePowerDataS2CPacket;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
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
import net.minecraft.util.Unit;
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

		protected final Entity holder;
		protected final P power;

		protected Instance(@NotNull Entity holder, @NotNull P power) {
			this.holder = holder;
			this.power = power;
		}

		@Override
		public Set<ContextKey<?>> getRequiredParameters() {
			return power.getRequiredParameters();
		}

		@Override
		public void validate(ProblemReporter reporter) {
			power.validate(reporter);
		}

		public final ContextImpl.Builder createHolderContextBuilder() {
			return power.getType().contextBuilder()
				.withReporter(this.createReporter())
				.add(NeoApoliContextKeys.THIS_ENTITY, holder)
				.add(NeoApoliContextKeys.THIS_POS, holder.position());
		}

		public final Context createHolderContext() {
			return createHolderContextBuilder().build(holder.level());
		}

		public final ProblemReporter createReporter() {
			return new ProblemReporter("{\"" + PowerManager.getReference(power) + "\"}").withKeySet(power.getType().contextType());
		}

		public final void syncData() {

			DataResult<PowerReference> referenceResult = PowerManager.getReferenceAsResult(this.getPower());
			RegistryOps<Tag> nbtOps = holder.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);

			switch (referenceResult) {
				case DataResult.Success<PowerReference> referenceSuccess when !holder.level().isClientSide()	-> {

					switch (this.encodeData(nbtOps)) {
						case DataResult.Success<Tag> dataSuccess -> {

							SynchronizePowerDataS2CPacket packet = new SynchronizePowerDataS2CPacket(holder.getId(), referenceSuccess.value(), new Dynamic<>(nbtOps, dataSuccess.value()));

							for (ServerPlayer trackingPlayer: MiscUtil.getTrackingPlayers(holder)) {
								ServerPlayNetworking.send(trackingPlayer, packet);
							}

						}
						case DataResult.Error<Tag> dataError ->
							NeoApoli.LOGGER.warn("Couldn't encode data of {} to sync to entity {}: {}", referenceSuccess.value().asDisplayString(false), holder.getName().getString(), dataError.message());
					}

				}
				case DataResult.Success<PowerReference> referenceSuccess ->
					NeoApoli.LOGGER.warn("Couldn't initiate syncing data of {} from entity {} in the client!", referenceSuccess.value().asDisplayString(false), holder.getName().getString());
				case DataResult.Error<PowerReference> ignored ->
					NeoApoli.LOGGER.warn("Couldn't initiate syncing data of unregistered power ({}) of entity {}!", this.getPower(), holder.getName().getString());
			}

		}

		public <I> DataResult<I> encodeData(RegistryOps<I> ops) {
			return DataResult.success(ops.emptyMap());
		}

		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, I data) {
			return DataResult.success(Unit.INSTANCE);
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

		public boolean shouldTick() {
			return false;
		}

		public boolean isActive(Context context) {
			return power.getActiveCondition()
				.map(activeCondition -> activeCondition.test(context.makeChild(".active_condition")))
				.orElse(true);
		}

	}

}
