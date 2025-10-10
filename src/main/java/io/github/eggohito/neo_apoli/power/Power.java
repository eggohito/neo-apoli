package io.github.eggohito.neo_apoli.power;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.networking.packet.s2c.SynchronizePowerDataS2CPacket;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.TextUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextImpl;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import lombok.Getter;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Unit;
import net.minecraft.util.context.ContextType;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 	<p>A power gives a certain "ability" to an entity upon being granted. As for what kind of "ability" it provides will
 * 	depend on the implementation (see: {@link Instance}) of the power itself.</p>
 *
 * 	<p>It has a set of properties which may determine its functionality, and cosmetics for displaying purposes:</p>
 * 	<ul>
 * 	    <li><code>type</code> - determines the type of the power, which provides its functionality. See {@link PowerTypes} for a list of usable power types.</li>
 * 	    <li><code>name</code> - the name of the power. Usually empty, but may take its {@linkplain PowerReference reference} when loaded via the {@link PowerManager} (e.g: when parsed from a data pack.)</li>
 * 	    <li><code>description</code> - the description of the power. Usually empty, but may take its {@linkplain PowerReference reference} when loaded via the {@link PowerManager} (e.g: when parsed from a data pack.)</li>
 * 	    <li><code>hidden</code> - determines whether the power should be excluded from being displayed; to be used by addons.</li>
 * 	</ul>
 */
@Getter
public abstract class Power {

	public static final String TYPE_KEY = "type";
	public static final MapCodec<Power> BASE_MAP_CODEC = PowerType.CODEC.dispatchMap(TYPE_KEY, Power::getType, PowerType::mapCodec);

	public static final Codec<Power> BASE_CODEC = BASE_MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, Power> BASE_PACKET_CODEC = PowerType.PACKET_CODEC.dispatch(Power::getType, PowerType::packetCodec);

	private final Properties properties;
	private final Optional<EntityCondition> activeCondition;

	public Power(Properties properties, Optional<EntityCondition> activeCondition) {
		this.properties = properties;
		this.activeCondition = activeCondition;
	}

	public Power(Properties properties) {
		this(properties, Optional.empty());
	}

	public void validate(ContextAware.ErrorReporter reporter) {
		this.getActiveCondition().ifPresent(activeCondition -> activeCondition.validate(reporter.makeChild(".active_condition")));
	}

	public abstract PowerType<?> getType();

	public abstract Instance<?> createInstance(Entity holder);

	public final Text getName() {
		return getProperties().name();
	}

	public final Text getDescription() {
		return getProperties().description();
	}

	public final boolean isHidden() {
		return getProperties().hidden();
	}

	public final boolean isSubPower() {
		return getProperties().subPower();
	}

	protected static <P extends Power> MapCodec<P> createSimpleCodec(Function<Properties, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).apply(instance, constructor));
	}

	protected static <P extends Power> MapCodec<P> createSimpleConditionedCodec(BiFunction<Properties, Optional<EntityCondition>, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addCommonConditionedFields(instance).apply(instance, constructor));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createSimplePacketCodec(Function<Properties, P> constructor) {
		return createCommonPacketCodec((buf, power) -> {}, (buf, properties) -> constructor.apply(properties));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createSimpleConditionedPacketCodec(BiFunction<Properties, Optional<EntityCondition>, P> constructor) {
		return createCommonConditionedPacketCodec((buf, power) -> {}, (buf, properties, condition) -> constructor.apply(properties, condition));
	}

	protected static <P extends Power> Products.P1<RecordCodecBuilder.Mu<P>, Properties> addCommonFields(RecordCodecBuilder.Instance<P> instance) {
		return instance.group(Properties.CODEC.forGetter(Power::getProperties));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createCommonPacketCodec(BiConsumer<RegistryByteBuf, P> encoder, BiFunction<RegistryByteBuf, Properties, P> decoder) {
		return new PacketCodec<>() {

			@Override
			public P decode(RegistryByteBuf buf) {
				Properties properties = Properties.PACKET_CODEC.decode(buf);
				return decoder.apply(buf, properties);
			}

			@Override
			public void encode(RegistryByteBuf buf, P power) {
				Properties.PACKET_CODEC.encode(buf, power.getProperties());
				encoder.accept(buf, power);
			}

		};
	}

	protected static <P extends Power> Products.P2<RecordCodecBuilder.Mu<P>, Properties, Optional<EntityCondition>> addCommonConditionedFields(RecordCodecBuilder.Instance<P> instance) {
		return addCommonFields(instance)
			.and(EntityCondition.CODEC.optionalFieldOf("active_condition").forGetter(Power::getActiveCondition));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createCommonConditionedPacketCodec(BiConsumer<RegistryByteBuf, P> encoder, TriFunction<RegistryByteBuf, Properties, Optional<EntityCondition>, P> decoder) {
		PacketCodec<RegistryByteBuf, Optional<EntityCondition>> optionalConditionCodec = PacketCodecs.optional(EntityCondition.PACKET_CODEC);
		return createCommonPacketCodec(
			(buf, power) -> {
				optionalConditionCodec.encode(buf, power.getActiveCondition());
				encoder.accept(buf, power);
			},
			(buf, properties) -> {
				Optional<EntityCondition> activeCondition = optionalConditionCodec.decode(buf);
				return decoder.apply(buf, properties, activeCondition);
			}
		);
	}

	/**
	 * 	<p>The class responsible for providing the functionality of a power. An instance of this class is created
	 * 	every time a power is granted to an entity to ensure that each instance is unique to each entity.</p>
	 *
	 * 	<p>The uniqueness of each instance is especially relevant for storing data.</p>
	 */
	public abstract static class Instance<P extends Power> {

		protected final Entity holder;
		protected final P power;

		protected Instance(@NotNull Entity holder, @NotNull P power) {
			this.holder = holder;
			this.power = power;
		}

		public final <B extends Context.Builder<B>> B addPowerContext(B builder) {

			Optional<PowerReference> powerReference = PowerManager.getReferenceAsResult(this.getPower()).result();
			ContextAware.ErrorReporter reporter = new ContextAware.ErrorReporter("{\"" + powerReference.map(PowerReference::toString).orElseGet(this.getPower()::toString) + "\"}")
				.withWrapperLookup(holder.getRegistryManager())
				.withContextType(this.getContextType());

			return builder
				.withReporter(reporter)
				.addOptional(ContextParameters.POWER_REFERENCE, powerReference)
				.add(ContextParameters.ENTITY, holder)
				.add(ContextParameters.ENTITY_POS, holder.getPos());

		}

		public final Context addPowerContext(Context context) {
			return this.addPowerContext(new ContextImpl.Builder(context)).build(context.getWorld());
		}

		public final ContextImpl.Builder createContextBuilder() {
			return this.addPowerContext(new ContextImpl.Builder());
		}

		public final Context createContext() {
			return this.createContextBuilder().build(holder.getWorld());
		}

		public final void syncData() {

			DataResult<PowerReference> referenceResult = PowerManager.getReferenceAsResult(this.getPower());
			RegistryOps<NbtElement> nbtOps = holder.getWorld().getRegistryManager().getOps(NbtOps.INSTANCE);

			switch (referenceResult) {
				case DataResult.Success<PowerReference> referenceSuccess when !holder.getWorld().isClient()	-> {

					switch (this.encodeData(nbtOps)) {
						case DataResult.Success<NbtElement> dataSuccess -> {

							SynchronizePowerDataS2CPacket packet = new SynchronizePowerDataS2CPacket(holder.getId(), referenceSuccess.value(), new Dynamic<>(nbtOps, dataSuccess.value()));

							for (ServerPlayerEntity trackingPlayer: MiscUtil.getTrackingPlayers(holder)) {
								ServerPlayNetworking.send(trackingPlayer, packet);
							}

						}
						case DataResult.Error<NbtElement> dataError ->
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

		public final ContextType getContextType() {
			return this.getPowerType().contextType();
		}

		public final PowerType<?> getPowerType() {
			return this.getPower().getType();
		}

		public final P getPower() {
			return power;
		}

		public boolean isActive(Context context) {
			return this.getPower().getActiveCondition()
				.map(activeCondition -> activeCondition.test(context.makeChild(".active_condition")))
				.orElse(true);
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

	}

	public static final class Properties {

		public static final MapCodec<Properties> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			TextCodecs.CODEC.optionalFieldOf("name", Text.empty()).forGetter(Properties::name),
			TextCodecs.CODEC.optionalFieldOf("description", Text.empty()).forGetter(Properties::description),
			PrimitiveCodec.BOOL.optionalFieldOf("hidden", false).forGetter(Properties::hidden)
		).apply(instance, Properties::new));

		public static final PacketCodec<RegistryByteBuf, Properties> PACKET_CODEC = PacketCodec.tuple(
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, Properties::name,
			TextCodecs.UNLIMITED_REGISTRY_PACKET_CODEC, Properties::description,
			PacketCodecs.BOOLEAN, Properties::hidden,
			Properties::new
		);

		private Text name;
		private Text description;

		private boolean subPower;
		private boolean hidden;

		public Properties(@NotNull Text name, @NotNull Text description, boolean hidden) {
			this.name = name;
			this.description = description;
			this.subPower = false;
			this.hidden = hidden;
		}

		public void withReference(PowerReference reference) {

			String translationKey = reference.createTranslationKey();

			this.name = TextUtil.forceTranslatable(translationKey + ".name", this.name());
			this.description = TextUtil.forceTranslatable(translationKey + ".description", this.description());

			this.subPower = reference.isSubPower();
			this.hidden |= this.subPower;

		}

		public Text name() {
			return name;
		}

		public Text description() {
			return description;
		}

		public boolean hidden() {
			return hidden;
		}

		public boolean subPower() {
			return subPower;
		}

	}

}
