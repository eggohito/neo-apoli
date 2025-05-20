package io.github.eggohito.neo_apoli.power;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.action.Action;
import io.github.eggohito.neo_apoli.condition.Condition;
import io.github.eggohito.neo_apoli.condition.EntityCondition;
import io.github.eggohito.neo_apoli.condition.meta.entity.ConstantEntityCondition;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import io.github.eggohito.neo_apoli.util.TextUtil;
import io.github.eggohito.neo_apoli.util.context.Context;
import io.github.eggohito.neo_apoli.util.context.ContextAware;
import io.github.eggohito.neo_apoli.util.context.ContextParameters;
import net.minecraft.entity.Entity;
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

import java.util.Optional;
import java.util.function.*;

public abstract class Power implements ContextAware {

	public static final String TYPE_KEY = "type";
	public static final MapCodec<Power> MAP_CODEC = PowerTypes.CODEC.dispatchMap(TYPE_KEY, Power::getType, PowerType::mapCodec);

	public static final Codec<Power> CODEC = MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, Power> PACKET_CODEC = PowerTypes.PACKET_CODEC.dispatch(Power::getType, PowerType::packetCodec);

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
			.orElseGet(() -> "Power (with type \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, this.getType()) + "\")");
	}

	public abstract PowerType<?> getType();

	public abstract Impl<?> createImpl(Entity holder);

	public abstract ContextType getContextType();

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

	public final boolean isSubPower() {
		return getProperties().subPower();
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

	protected static ContextType createContextType(UnaryOperator<ContextType.Builder> operator) {
		return operator.apply(new ContextType.Builder()
			.require(ContextParameters.POSITION)
			.require(ContextParameters.THIS_ENTITY)
			.allow(ContextParameters.ACTOR)
			.allow(ContextParameters.TARGET)).build();
	}

	public abstract static class Impl<P extends Power> {

		protected final Entity holder;
		protected final P power;

		protected Impl(@NotNull Entity holder, @NotNull P power) {
			this.holder = holder;
			this.power = power;
		}

		public Context.Builder createContextBuilder() {
			return new Context.Builder(this.getPower().getContextType())
				.add(ContextParameters.THIS_ENTITY, holder)
				.add(ContextParameters.POSITION, holder.getPos());
		}

		public Context createContext(UnaryOperator<Context.Builder> builder) {
			return builder.apply(this.createContextBuilder()).build(holder.getWorld());
		}

		public <I> DataResult<I> encodeData(RegistryOps<I> ops) {
			return DataResult.success(ops.emptyMap());
		}

		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, I data) {
			return DataResult.success(Unit.INSTANCE);
		}

		public ContextType getContextType() {
			return this.getPower().getContextType();
		}

		public P getPower() {
			return power;
		}

		public boolean isActive(Context context) {
			return testAndReport("active_condition", getPower().getActiveCondition(), context);
		}

		protected <R, C extends ContextAware> R processAndReport(String path, C contextAware, BiFunction<C, Context, R> resultFunctor, Supplier<R> fallback, Context context) {

			Context childContext = context.makeChild(path);
			R result = resultFunctor.apply(contextAware, childContext);

			if (context.hasAnyErrors()) {
				report(context, contextAware);
				return fallback.get();
			}
			
			else {
				return result;
			}

		}

		public <A extends Action<?>> void executeAndReport(String path, A action, UnaryOperator<Context.Builder> builder) {
			executeAndReport(path, action, this.createContext(builder));
		}

		public <A extends Action<?>> void executeAndReport(String path, A action, Context context) {
			processAndReport(path, action, (a, ctx) -> MiscUtil.run(() -> a.execute(ctx)), () -> null, context);
		}

		public <C extends Condition<?>> boolean testAndReport(String path, C condition, UnaryOperator<Context.Builder> builder) {
			return testAndReport(path, condition, this.createContext(builder));
		}

		public <C extends Condition<?>> boolean testAndReport(String path, C condition, Context context) {
			return Boolean.TRUE.equals(processAndReport(path, condition, Condition::test, () -> false, context));
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

		public void onTick() {

		}

		public boolean shouldTick() {
			return false;
		}

		protected <C extends ContextAware> void report(Context context, C contextAware) {
			
			if (!context.hasAnyErrors()) {
				return;
			}
			
			Optional<PowerReference> reference = PowerManager.getReferenceAsResult(this.getPower()).result();
			NeoApoli.LOGGER.warn("Couldn't fully process {} due to error(s) {}", (contextAware.asDisplayString(false) + reference.map(ref -> " in " + ref.asDisplayString(false)).orElse("")), context.getReporter().getErrorsAsString());
		
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
