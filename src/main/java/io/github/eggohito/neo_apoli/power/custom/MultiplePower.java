package io.github.eggohito.neo_apoli.power.custom;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerIdentifier;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.resource.json.JsonObjectWithSource;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@EqualsAndHashCode
@Getter
public class MultiplePower extends Power {

	//	TODO: This set of filters should be controllable via config
	private static final Set<Pattern> SUB_POWER_KEY_FILTERS = Util.make(new ObjectOpenHashSet<>(), filters -> {

		PowerEntry.MAP_CODEC.keys(JavaOps.INSTANCE)
			.map(Object::toString)
			.filter(Predicate.not("value"::equals))
			.distinct()
			.map(Pattern::compile)
			.forEach(filters::add);

		filters.add(Pattern.compile("^\\$"));
		filters.add(Pattern.compile(ResourceConditions.CONDITIONS_KEY));

	});

	public static final ResourceLocation ID = NeoApoli.id("multiple");

	public static final MapCodec<ImmutableSet<PowerEntry<?>>> SUB_POWERS_CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> ops) {
			return Stream.empty();
		}

		@Override
		public <I> DataResult<ImmutableSet<PowerEntry<?>>> decode(DynamicOps<I> ops, MapLike<I> mapInput) {

			Set<PowerEntry<?>> succeeded = new ObjectOpenHashSet<>();
			DataResult<Unit> result = mapInput.entries().reduce(
				DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
				(identity, keyAndValue) -> {

					DataResult<String> keyResult = ops.getStringValue(keyAndValue.getFirst());
					if (keyResult.mapOrElse(MultiplePower::isKeyIgnored, error -> false)) {
						return identity;
					}

					DataResult<MapLike<I>> valueResult = ops.getMap(keyAndValue.getSecond());
					if (valueResult.isError()) {
						return identity.apply2stable((unit, o) -> unit, valueResult);
					}

					DataResult<PowerIdentifier> powerIdResult = PowerIdentifier.parseAsResult(keyResult.getOrThrow())
						.flatMap(powerId -> powerId.isSubPower()
							? DataResult.success(powerId)
							: DataResult.error(() -> "A sub-power must have a sub-power ID!"));

					if (powerIdResult.isError()) {
						return identity.apply2stable((unit, o) -> unit, powerIdResult);
					}

					DataResult<PowerType<?>> typeResult = PowerType.CODEC.fieldOf(Power.TYPE_KEY).decode(ops, valueResult.getOrThrow())
						.flatMap(type -> type == PowerTypes.MULTIPLE
							? DataResult.error(() -> "Sub-power \"" + keyResult.getOrThrow() + "\" uses the \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, PowerTypes.MULTIPLE) + "\" power type, which isn't allowed!'")
							: DataResult.success(type));

					if (typeResult.isError()) {
						return identity.apply2stable((unit, o) -> unit, typeResult);
					}

					DataResult<PowerEntry<?>> subPowerResult = PowerEntry.MAP_CODEC.decode(ops, valueResult.getOrThrow());

					if (subPowerResult.isSuccess() && !succeeded.add(subPowerResult.getOrThrow())) {
						return identity.apply2stable((unit, o) -> unit, DataResult.error(() -> "Duplicate entry for key: \"" + keyResult.getOrThrow() + "\""));
					}

					return identity.apply2stable((unit, o) -> unit, subPowerResult);


				},
				(r1, r2) ->
					r1.apply2stable((u1, u2) -> u1, r2)
			);

			ImmutableSet<PowerEntry<?>> elements = ImmutableSet.copyOf(succeeded);
			return result.map(u -> elements).setPartial(elements);

		}

		@Override
		public <O> RecordBuilder<O> encode(ImmutableSet<PowerEntry<?>> entries, DynamicOps<O> ops, RecordBuilder<O> prefix) {

			for (var entry : entries) {
				prefix.add(entry.id().toString(), PowerEntry.CODEC.encodeStart(ops, entry));
			}

			return prefix;

		}

	};

	public static final StreamCodec<RegistryFriendlyByteBuf, ImmutableSet<PowerEntry<?>>> SUB_POWERS_STREAM_CODEC = new StreamCodec<>() {

		@Override
		public @NotNull ImmutableSet<PowerEntry<?>> decode(RegistryFriendlyByteBuf buf) {

			ImmutableSet.Builder<PowerEntry<?>> entries = ImmutableSet.builder();
			int size = buf.readVarInt();

			for (int i = 0; i < size; i++) {

				PowerEntry<?> entry = PowerEntry.STREAM_CODEC.decode(buf);

				if (entry.isSubPower()) {
					entries.add(entry);
				}

				else {
					throw new IllegalArgumentException("Expected a sub-power, but got " + entry.id().asDisplayString(false) + "!");
				}

			}

			return entries.build();

		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, ImmutableSet<PowerEntry<?>> entries) {

			buf.writeVarInt(entries.size());

			for (var entry : entries) {
				PowerEntry.STREAM_CODEC.encode(buf, entry);
			}

		}

	};

	public static final MapCodec<MultiplePower> MAP_CODEC = SUB_POWERS_CODEC.xmap(
		MultiplePower::new,
		MultiplePower::getSubPowers
	);

	public static final StreamCodec<RegistryFriendlyByteBuf, MultiplePower> STREAM_CODEC = SUB_POWERS_STREAM_CODEC.map(
		MultiplePower::new,
		MultiplePower::getSubPowers
	);

	private final ImmutableSet<PowerEntry<?>> subPowers;

	public MultiplePower(ImmutableSet<PowerEntry<?>> subPowers) {
		this.subPowers = subPowers;
	}

	@Override
	public PowerType<?> getType() {
		return PowerTypes.MULTIPLE;
	}

	@Override
	public Power.Instance<?> createInstance() {
		return new Instance<>(this) {};
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
	public static void preProcessSubPowers(ResourceLocation id, JsonObjectWithSource jsonObjectWithSource, String directoryPath, RegistryOps<JsonElement> ops) {

		JsonObject powerJson = jsonObjectWithSource.element();
		DataResult<PowerType<?>> powerTypeResult = PowerType.CODEC.parse(ops, powerJson.get(TYPE_KEY));

		if (!powerTypeResult.mapOrElse(PowerTypes.MULTIPLE::equals, error -> false)) {
			return;
		}

		Map<String, JsonElement> powerJsonMap = powerJson.asMap();
		String separator = Character.toString(PowerIdentifier.SEPARATOR);

		powerJsonMap.entrySet().removeIf(entry -> !isKeyIgnored(entry.getKey()) && !MiscUtil.isResourceConditionFulfilled(id, entry.getValue(), directoryPath, ops));

		JsonObject copy = powerJson.deepCopy();
		powerJsonMap.clear();

		copy.asMap().forEach((key, value) -> {

			if (!isKeyIgnored(key)) {

				key = id + separator + key;

				//	Append the sub-power's ID into its value object for proper parsing later
				if (value instanceof JsonObject jsonObject) {
					jsonObject.addProperty(PowerEntry.ID_KEY, key);
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

	public static boolean isKeyIgnored(String key) {

		for (var filter : SUB_POWER_KEY_FILTERS) {

			if (filter.matcher(key).find()) {
				return true;
			}

		}

		return false;

	}

}
