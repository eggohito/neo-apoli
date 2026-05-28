package io.github.eggohito.neo_apoli.power.custom;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.api.config.ConfigCategoryRegistrant;
import io.github.eggohito.neo_apoli.config.AbstractJsonCodecConfig;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerHolder;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.registry.NeoApoliPowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.resource.json.JsonWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.parsers.json.JsonFormat;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("UnstableApiUsage")
@EqualsAndHashCode
@Getter
public class MultiplePower extends Power {

	public static final ResourceLocation ID = NeoApoli.id("multiple");

	public static final MapCodec<ImmutableSet<PowerHolder<?>>> SUB_POWERS_CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.empty();
		}

		@Override
		public <I> DataResult<ImmutableSet<PowerHolder<?>>> decode(DynamicOps<I> ops, MapLike<I> mapInput) {

			Set<PowerHolder<?>> succeeded = new ObjectOpenHashSet<>();
			DataResult<Unit> result = mapInput.entries().reduce(
				DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
				(identity, keyAndValue) -> {

					DataResult<String> keyResult = ops.getStringValue(keyAndValue.getFirst());
					if (keyResult.mapOrElse(MultiplePower::isFieldIgnored, error -> false)) {
						return identity;
					}

					DataResult<MapLike<I>> valueResult = ops.getMap(keyAndValue.getSecond());
					if (valueResult.isError()) {
						return identity.apply2stable((unit, o) -> unit, valueResult);
					}

					String subPowerKey = keyResult.getOrThrow();
					DataResult<PowerIdentifier> powerIdResult = PowerIdentifier.parseAsResult(subPowerKey)
						.flatMap(powerId -> powerId.isSubPower()
							? DataResult.success(powerId)
							: DataResult.error(() -> "The key for sub-power \"" + subPowerKey + "\" wasn't pre-processed!"));

					if (powerIdResult.isError()) {
						return identity.apply2stable((unit, o) -> unit, powerIdResult);
					}

					DataResult<Type<?>> typeResult = Type.CODEC.fieldOf(Power.TYPE_KEY).decode(ops, valueResult.getOrThrow())
						.flatMap(type -> type == NeoApoliPowerTypes.MULTIPLE
							? DataResult.error(() -> "Sub-power \"" + subPowerKey + "\" uses the \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, NeoApoliPowerTypes.MULTIPLE) + "\" power type, which isn't allowed!'")
							: DataResult.success(type));

					if (typeResult.isError()) {
						return identity.apply2stable((unit, o) -> unit, typeResult);
					}

					DataResult<PowerHolder<?>> subPowerResult = PowerHolder.MAP_CODEC.decode(ops, valueResult.getOrThrow());

					if (subPowerResult.isSuccess() && !succeeded.add(subPowerResult.getOrThrow())) {
						return identity.apply2stable((unit, o) -> unit, DataResult.error(() -> "Duplicate entry for key: \"" + subPowerKey + "\""));
					}

					return identity.apply2stable((unit, o) -> unit, subPowerResult);


				},
				(r1, r2) ->
					r1.apply2stable((u1, u2) -> u1, r2)
			);

			ImmutableSet<PowerHolder<?>> elements = ImmutableSet.copyOf(succeeded);
			return result.map(u -> elements).setPartial(elements);

		}

		@Override
		public <O> RecordBuilder<O> encode(ImmutableSet<PowerHolder<?>> entries, DynamicOps<O> ops, RecordBuilder<O> prefix) {

			for (var entry : entries) {
				prefix.add(entry.id().toString(), PowerHolder.CODEC.encodeStart(ops, entry));
			}

			return prefix;

		}

	};

	public static final StreamCodec<RegistryFriendlyByteBuf, ImmutableSet<PowerHolder<?>>> SUB_POWERS_STREAM_CODEC = new StreamCodec<>() {

		@Override
		public @NotNull ImmutableSet<PowerHolder<?>> decode(RegistryFriendlyByteBuf buf) {

			ImmutableSet.Builder<PowerHolder<?>> holders = ImmutableSet.builder();
			int size = buf.readVarInt();

			for (int i = 0; i < size; i++) {

				PowerHolder<?> powerHolder = PowerHolder.STREAM_CODEC.decode(buf);

				if (powerHolder.isSubPower()) {
					holders.add(powerHolder);
				}

				else {
					throw new IllegalArgumentException("Expected a sub-power, but got " + powerHolder.id().asDisplayString(false) + "!");
				}

			}

			return holders.build();

		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, ImmutableSet<PowerHolder<?>> holders) {

			buf.writeVarInt(holders.size());

			for (var holder : holders) {
				PowerHolder.STREAM_CODEC.encode(buf, holder);
			}

		}

	};

	public static final MapCodec<MultiplePower> CODEC = SUB_POWERS_CODEC.xmap(
		MultiplePower::new,
		MultiplePower::getSubPowers
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplePower> STREAM_CODEC = SUB_POWERS_STREAM_CODEC.map(
		MultiplePower::new,
		MultiplePower::getSubPowers
	);

	private final ImmutableSet<PowerHolder<?>> subPowers;

	public MultiplePower(ImmutableSet<PowerHolder<?>> subPowers) {
		this.subPowers = subPowers;
	}

	@Override
	public Type<?> getType() {
		return NeoApoliPowerTypes.MULTIPLE;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance<>(this) {};
	}

	@Override
	public boolean canBePartiallyParsed() {
		return true;
	}

	/**
	 * 	<p>Pre-process the sub-powers of a multiple power to prepare the sub-powers for proper parsing. This takes care
	 * 	of two things:</p>
	 * 	<ul>
	 * 	    <li>Creates a sub-power ID (by prepending the super-power's ID to the keys of the JSON object) to
	 * 	    ensure the sub-power will be recognized.</li>
	 * 	    <li>Adds the sub-power ID to the JSON objects of certain keys so the sub-powers will be parsed
	 * 	    as sub-power entries.</li>
	 * 	</ul>
	 */
	@ApiStatus.Internal
	public static void preProcessSubPowers(ResourceLocation id, JsonWithSource jsonWithSource, String directoryPath, DynamicOps<JsonElement> ops) {

		if (!(jsonWithSource.json() instanceof JsonObject powerJson) || !Type.CODEC.parse(ops, powerJson.get(TYPE_KEY)).mapOrElse(NeoApoliPowerTypes.MULTIPLE::equals, error -> false)) {
			return;
		}

		Map<String, JsonElement> powerJsonMap = powerJson.asMap();
		String separator = Character.toString(PowerIdentifier.SEPARATOR);

		JsonObject copy = powerJson.deepCopy();
		powerJsonMap.clear();

		copy.asMap().forEach((key, value) -> {

			if (!isFieldIgnored(key)) {

				if (!MiscUtil.isResourceConditionFulfilled(id, value, directoryPath, ops)) {
					return;
				}

				key = id + separator + key;

				if (value instanceof JsonObject jsonObject) {
					jsonObject.addProperty(PowerHolder.ID_KEY, key);
				}

			}

			powerJson.add(key, value);

		});

	}

	public static DataResult<String> validateSubPowerName(String name) {

		if (name.isEmpty()) {
			return DataResult.error(() -> "Empty sub-power names are not allowed!");
		}

		else if (ResourceLocation.isValidPath(name)) {
			return DataResult.success(name);
		}

		else {
			return DataResult.error(() -> "Non [a-z0-9/._-] character in sub-power name: " + name);
		}

	}

	public static boolean isFieldIgnored(String key) {

		for (var ignoredField : Config.INSTANCE.ignoredFields.get()) {

			if (ignoredField.matcher(key).find()) {
				return true;
			}

		}

		return false;

	}

	public static final class Config extends AbstractJsonCodecConfig<Config> implements ConfigCategoryRegistrant.Entry {

		public static final Config INSTANCE = new Config();
		public static final int VERSION = 1;

		public final ConfigEntry<List<Pattern>> ignoredFields = register("ignored_fields", makeDefaultIgnoredFields(), ExtraCodecs.PATTERN.listOf());
		public final ConfigEntry<Boolean> enabled = register("enabled", true, Codec.BOOL);
		public final ConfigEntry<Integer> version = register("version", VERSION, Codec.INT);

		Config() {
			super(FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/type/power/multiple.json5"), JsonFormat.JSON5);
		}

		private static List<Pattern> makeDefaultIgnoredFields() {

			ImmutableList.Builder<Pattern> builder = ImmutableList.builder();
			PowerHolder.MAP_CODEC.keys(JavaOps.INSTANCE)
				.map(Object::toString)
				.filter(Predicate.not("value"::equals))
				.distinct()
				.map(Pattern::compile)
				.forEach(builder::add);

			builder.add(Pattern.compile("^\\$"));
			builder.add(Pattern.compile(ResourceConditions.CONDITIONS_KEY));

			return builder.build();

		}

		//  TODO: Re-add the option for being able to modify the list of ignored fields
		//        (currently removed due to how list options are implemented in YACL's API)
		@Override
		public void addGroup(Consumer<OptionGroup> adder) {

		}

		@Override
		public boolean load() {
			return this.loadFromFile();
		}

		@Override
		public void save() {
			this.saveToFile();
		}

	}

}
