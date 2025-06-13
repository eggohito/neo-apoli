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
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class Power {

	public static final String TYPE_KEY = "type";
	public static final MapCodec<Power> BASE_MAP_CODEC = PowerSerializers.CODEC.dispatchMap(TYPE_KEY, Power::getSerializer, Serializer::mapCodec);

	public static final Codec<Power> BASE_CODEC = BASE_MAP_CODEC.codec();
	public static final PacketCodec<RegistryByteBuf, Power> BASE_PACKET_CODEC = PowerSerializers.PACKET_CODEC.dispatch(Power::getSerializer, Serializer::packetCodec);

	private final Properties properties;
	private final EntityCondition activeCondition;

	public Power(Properties properties, EntityCondition activeCondition) {
		this.properties = properties;
		this.activeCondition = activeCondition;
	}

	public Power(Properties properties) {
		this(properties, new ConstantEntityCondition(true));
	}

	public void validate(ContextAware.ErrorReporter reporter) {
		getActiveCondition().validate(reporter.makeChild("active_condition"));
	}

	public abstract Serializer<?> getSerializer();

	public abstract Type<?> createType(Entity holder);

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

	protected static <P extends Power> Products.P2<RecordCodecBuilder.Mu<P>, Properties, EntityCondition> addCommonConditionedFields(RecordCodecBuilder.Instance<P> instance) {
		return addCommonFields(instance)
			.and(EntityCondition.CODEC.optionalFieldOf("active_condition", new ConstantEntityCondition(true)).forGetter(Power::getActiveCondition));
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

	public interface Serializer<P extends Power> {

		ContextType contextType();

		MapCodec<P> mapCodec();

		PacketCodec<RegistryByteBuf, P> packetCodec();

		default Context.Builder contextBuilder() {
			return Context.builder(this.contextType());
		}

	}

	public abstract static class Type<P extends Power> {

		protected final Entity holder;
		protected final P power;

		protected Type(@NotNull Entity holder, @NotNull P power) {
			this.holder = holder;
			this.power = power;
		}

		protected final Context createGenericContext() {
			return this.getSerializer().contextBuilder()
				.add(ContextParameters.THIS_ENTITY, holder)
				.add(ContextParameters.POSITION, holder.getPos())
				.build(holder.getWorld());
		}

		public <I> DataResult<I> encodeData(RegistryOps<I> ops) {
			return DataResult.success(ops.emptyMap());
		}

		public <I> DataResult<Unit> decodeData(RegistryOps<I> ops, I data) {
			return DataResult.success(Unit.INSTANCE);
		}

		public final Serializer<?> getSerializer() {
			return this.getPower().getSerializer();
		}

		public final P getPower() {
			return power;
		}

		public boolean isActive(Context context) {
			return testAndReport("active_condition", getPower().getActiveCondition(), context);
		}

		protected <R> R processAndReport(Context context, String path, Function<Context, R> resultFunctor, BiFunction<ContextAware.ErrorReporter, String, String> errorSupplier) {

			Context subContext = context.makeChild(path);
			R result = resultFunctor.apply(subContext);

			if (context.hasAnyErrors()) {
				NeoApoli.LOGGER.warn(errorSupplier.apply(context.getReporter(), path));
			}

			return result;

		}

		public <A extends Action<?>> void executeAndReport(String path, A action, UnaryOperator<Context.Builder> builder) {
			executeAndReport(path, action, builder.apply(new Context.Builder(this.getSerializer().contextType())).build(holder.getWorld()));
		}

		public <A extends Action<?>> void executeAndReport(String path, A action, Context context) {
			Optional<PowerReference> reference = PowerManager.getReferenceAsResult(this.getPower()).result();
			processAndReport(context, path, ctx -> MiscUtil.run(() -> action.execute(ctx)), (reporter, _path) -> "Couldn't fully execute " + StringUtils.uncapitalize(action.getCategory().toString()) + " at path \"" + _path + "\"" + reference.map(ref -> " in " + ref.asDisplayString(false) + " ").orElse("") + "due to error(s) " + reporter.getErrorsAsString());
		}

		public <C extends Condition<?>> boolean testAndReport(String path, C condition, UnaryOperator<Context.Builder> builder) {
			return testAndReport(path, condition, builder.apply(new Context.Builder(this.getSerializer().contextType())).build(holder.getWorld()));
		}

		public <C extends Condition<?>> boolean testAndReport(String path, C condition, Context context) {
			Optional<PowerReference> reference = PowerManager.getReferenceAsResult(this.getPower()).result();
			return Boolean.TRUE.equals(processAndReport(context, path, condition::test, (reporter, _path) -> "Couldn't fully test " + StringUtils.uncapitalize(condition.getCategory().toString()) + " at path \"" + _path + "\"" + reference.map(ref -> " in " + ref.asDisplayString(false) + " ").orElse("") + "due to error(s) " + reporter.getErrorsAsString()));
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
