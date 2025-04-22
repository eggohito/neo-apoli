package io.github.eggohito.neo_apoli.power;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.context.entity.EntityConditionContext;
import io.github.eggohito.neo_apoli.condition.meta.entity.ConstantEntityCondition;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.TextUtil;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryOps;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Unit;
import net.minecraft.util.context.ContextType;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class Power implements ContextAware {

	public static final ContextType.Builder DEFAULT_CONTEXT_TYPE_BUILDER = new ContextType.Builder()
		.require(LootContextParameters.THIS_ENTITY)
		.require(LootContextParameters.ORIGIN)
		.allow(LootContextParameters.ATTACKING_ENTITY)
		.allow(LootContextParameters.LAST_DAMAGE_PLAYER);

	public static final String TYPE_KEY = "type";
	public static final MapCodec<Power> MAP_CODEC = PowerTypes.CODEC.dispatchMap(TYPE_KEY, Power::getType, Type::mapCodec);

	public static final Codec<Power> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, Power> PACKET_CODEC = PowerTypes.PACKET_CODEC.dispatch(Power::getType, Type::packetCodec);

	private final Properties properties;
	private final EntityCondition activeCondition;

	public Power(Properties properties, EntityCondition activeCondition) {
		this.properties = properties;
		this.activeCondition = activeCondition;
	}

	public Power(Properties properties) {
		this(properties, new ConstantEntityCondition(true));
	}

	@Override
	public void validate(ErrorReporter reporter) {
		getActiveCondition().validate(reporter.makeChild("active_condition"));
	}

	@Override
	public String asDisplayString() {
		return PowerManager.getReferenceAsResult(this)
			.result()
			.map(PowerReference::asDisplayString)
			.orElseGet(() -> "Power type \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, this.getType()));
	}

	public abstract Type<?> getType();

	public abstract Impl<?> createImpl(Entity holder);

	public final Properties getProperties() {
		return properties;
	}

	public final EntityCondition getActiveCondition() {
		return activeCondition;
	}

	public final Text getName() {
		return getProperties().name();
	}

	public final Text getDescription() {
		return getProperties().description();
	}

	public final boolean isHidden() {
		return getProperties().hidden();
	}

	protected static <P extends Power> MapCodec<P> createSimpleCodec(Function<Properties, P> constructor) {
		return RecordCodecBuilder.mapCodec(instance -> addCommonFields(instance).apply(instance, constructor));
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createSimplePacketCodec(Function<Properties, P> constructor) {
		return createCommonPacketCodec((buf, power) -> {}, (buf, metadata) -> constructor.apply(metadata));
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
			public void encode(RegistryByteBuf buf, P value) {
				Properties.PACKET_CODEC.encode(buf, value.getProperties());
				encoder.accept(buf, value);
			}

		};
	}

	protected static <P extends Power> Products.P2<RecordCodecBuilder.Mu<P>, Properties, EntityCondition> addCommonAndConditionFields(RecordCodecBuilder.Instance<P> instance) {
		return instance.group(
			Properties.CODEC.forGetter(Power::getProperties),
			EntityCondition.CODEC.optionalFieldOf("active_condition", new ConstantEntityCondition(true)).forGetter(Power::getActiveCondition)
		);
	}

	protected static <P extends Power> PacketCodec<RegistryByteBuf, P> createCommonConditionedPacketCodec(BiConsumer<RegistryByteBuf, P> encoder, TriFunction<RegistryByteBuf, Properties, EntityCondition, P> decoder) {
		return createCommonPacketCodec(
			(buf, power) -> {
				EntityCondition.PACKET_CODEC.encode(buf, power.getActiveCondition());
				encoder.accept(buf, power);
			},
			(buf, properties) -> {
				EntityCondition activeCondition = EntityCondition.PACKET_CODEC.decode(buf);
				return decoder.apply(buf, properties, activeCondition);
			}
		);
	}

	public static abstract class Impl<P extends Power> {

		protected final Entity holder;
		protected final P power;

		protected final EntityCondition activeCondition;

		public Impl(@NotNull Entity holder, @NotNull P power) {
			this.holder = holder;
			this.power = power;
			this.activeCondition = power.getActiveCondition();
		}

		public abstract ContextType getContextType();

		public ErrorReporter getErrorReporter() {
			return new ErrorReporter(this.getContextType()).withWrapperLookup(holder.getRegistryManager());
		}

		public <I> DataResult<I> encodeData(RegistryOps<I> ops) {
			return DataResult.success(ops.emptyMap());
		}

		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, I data) {
			return DataResult.success(Unit.INSTANCE);
		}

		public P getPower() {
			return power;
		}

		public boolean isActive() {
			return activeCondition.test(this.getErrorReporter(), new EntityConditionContext(holder));
		}

		public <A extends Action<?, ?>> void executeAndReport(A action, BiConsumer<ErrorReporter, A> consumer) {

			PowerReference reference = PowerManager.getReferenceAsResult(this.getPower()).mapOrElse(Function.identity(), error -> null);
			ErrorReporter reporter = this.getErrorReporter();

			consumer.accept(reporter, action);

			if (reporter.anyErrored()) {
				NeoApoli.LOGGER.warn("Error executing {} {} due to error(s) {}", action.asDisplayString(false), (reference != null ? "in " +  reference.asDisplayString(false) : ""), reporter.getErrorsAsString());
			}

		}

		public void onAdded() {

		}

		public void onGranted() {

		}

		public void onRemoved() {

		}

		public void onRevoked() {

		}

		public void onRespawn() {

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

		private final boolean hidden;

		public Properties(@NotNull Text name, @NotNull Text description, boolean hidden) {
			this.name = name;
			this.description = description;
			this.hidden = hidden;
		}

		public Properties withReference(PowerReference reference) {

			String translationKey = reference.createTranslationKey();

			this.name = TextUtil.forceTranslatable(translationKey + ".name", this.name());
			this.description = TextUtil.forceTranslatable(translationKey + ".description", this.description());

			return this;

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

	}

	public record Type<P extends Power>(MapCodec<P> mapCodec, PacketCodec<RegistryByteBuf, P> packetCodec) {

	}

}
