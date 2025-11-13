package io.github.eggohito.neo_apoli.power.custom;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import io.github.eggohito.neo_apoli.NeoApoli;
import io.github.eggohito.neo_apoli.power.Power;
import io.github.eggohito.neo_apoli.power.PowerEntry;
import io.github.eggohito.neo_apoli.power.PowerManager;
import io.github.eggohito.neo_apoli.power.type.PowerType;
import io.github.eggohito.neo_apoli.power.type.PowerTypes;
import io.github.eggohito.neo_apoli.registry.NeoApoliRegistries;
import io.github.eggohito.neo_apoli.util.MiscUtil;
import io.github.eggohito.neo_apoli.util.PowerReference;
import io.github.eggohito.neo_apoli.util.RegistryUtil;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.entity.Entity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Getter
public class MultiplePower extends Power {

	//	TODO: This set of filters should be controllable via config
	private static final Set<Pattern> SUB_POWER_KEY_FILTERS = Util.make(new ObjectOpenHashSet<>(), filters -> {

		PowerEntry.CODEC.keys(JavaOps.INSTANCE)
			.map(Object::toString)
			.distinct()
			.map(Pattern::compile)
			.forEach(filters::add);

		filters.add(Pattern.compile("^\\$"));
		filters.add(Pattern.compile(ResourceConditions.CONDITIONS_KEY));

	});

	public static final Identifier ID = NeoApoli.id("multiple");

	public static final MapCodec<ImmutableSet<PowerEntry<?>>> SUB_POWERS_CODEC = new MapCodec<>() {

		@Override
		public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
			return Stream.empty();
		}

		@Override
		public <I> DataResult<ImmutableSet<PowerEntry<?>>> decode(DynamicOps<I> ops, MapLike<I> mapInput) {

			Set<PowerEntry<?>> succeeded = new ObjectOpenHashSet<>();
			DataResult<Unit> result = mapInput.entries().reduce(
				DataResult.success(Unit.INSTANCE, Lifecycle.stable()),
				(r, pair) -> {

					DataResult<String> keyResult = Codec.STRING.parse(ops, pair.getFirst());
					if (keyResult.mapOrElse(MultiplePower::isKeyIgnored, error -> false)) {
						return r;
					}

					DataResult<MapLike<I>> childMapResult = ops.getMap(pair.getSecond());
					if (childMapResult.isError()) {
						return r.apply2stable((unit, obj) -> unit, childMapResult);
					}

					MapLike<I> childMapInput = childMapResult.getOrThrow();
					DataResult<PowerReference.SubPower> subReferenceResult = keyResult
						.flatMap(PowerReference::ofValidated)
						.flatMap(reference -> reference instanceof PowerReference.SubPower subReference
							? DataResult.success(subReference)
							: DataResult.error(() -> "A sub-power must have a sub-power reference!"));

					if (subReferenceResult.isSuccess()) {

						PowerReference.SubPower subReference = subReferenceResult.getOrThrow();
						DataResult<PowerType<?>> typeResult = PowerType.CODEC.fieldOf(Power.TYPE_KEY)
							.decode(ops, childMapInput)
							.flatMap(type -> Objects.equals(type, PowerTypes.MULTIPLE)
								? DataResult.error(() -> subReference.asDisplayString() + " uses the \"" + RegistryUtil.getId(NeoApoliRegistries.POWER_TYPE, type) + "\" power type, which is not allowed!")
								: DataResult.success(type));

						if (typeResult.isSuccess()) {

							DataResult<PowerEntry<?>> subPowerResult = PowerEntry.CODEC.decode(ops, childMapInput);

							if (subPowerResult.isSuccess()) {

								PowerEntry<?> subPower = subPowerResult.getOrThrow();

								if (!succeeded.add(subPower)) {
									return r.apply2stable((u, o) -> u, DataResult.error(() -> "Duplicate entry for key: \"" + subReference.name() + "\""));
								}

							}

							return r.apply2stable((u, o) -> u, subPowerResult);

						}

						return r.apply2stable((u, o) -> u, typeResult);

					}

					return r.apply2stable((u, o) -> u, subReferenceResult);

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
				prefix.add(entry.reference().toString(), PowerEntry.CODEC.encode(entry, ops, ops.mapBuilder()).build(ops.empty()));
			}

			return prefix;

		}

	};

	public static final PacketCodec<RegistryByteBuf, ImmutableSet<PowerEntry<?>>> SUB_POWERS_PACKET_CODEC = new PacketCodec<>() {

		@Override
		public ImmutableSet<PowerEntry<?>> decode(RegistryByteBuf buf) {

			ImmutableSet.Builder<PowerEntry<?>> entries = ImmutableSet.builder();
			int size = buf.readVarInt();

			for (int i = 0; i < size; i++) {

				PowerEntry<?> entry = PowerEntry.PACKET_CODEC.decode(buf);

				if (entry.subPower()) {
					entries.add(entry);
				}

				else {
					throw new IllegalArgumentException("Expected a sub-power, but got " + entry.reference().asDisplayString(false) + "!");
				}

			}

			return entries.build();

		}

		@Override
		public void encode(RegistryByteBuf buf, ImmutableSet<PowerEntry<?>> entries) {

			buf.writeVarInt(entries.size());

			for (var entry : entries) {
				PowerEntry.PACKET_CODEC.encode(buf, entry);
			}

		}

	};

	public static final MapCodec<MultiplePower> CODEC = SUB_POWERS_CODEC.xmap(
		MultiplePower::new,
		MultiplePower::getSubPowers
	);

	public static final PacketCodec<RegistryByteBuf, MultiplePower> PACKET_CODEC = SUB_POWERS_PACKET_CODEC.xmap(
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
	public Power.Instance<?> createInstance(Entity holder) {
		return new Instance<>(holder, this) {};
	}

	/**
	 * 	<p>Pre-process the sub-powers of a multiple power to prepare the sub-powers for proper parsing. This takes care
	 * 	of two things:</p>
	 * 	<ul>
	 * 	    <li>Creates a sub-power reference (by prepending the super-power's ID to the keys of the JSON object) to
	 * 	    ensure the sub-power will be recognized.</li>
	 * 	    <li>Adds the sub-power reference to the JSON objects of certain keys so the sub-powers will be parsed
	 * 	    as sub-power entries.</li>
	 * 	</ul>
	 */
	@ApiStatus.Internal
	public static void preProcessSubPowers(Identifier id, PowerManager.Entry entry, String directoryPath, RegistryOps<JsonElement> ops) {

		JsonObject powerJson = entry.element();
		DataResult<PowerType<?>> powerTypeResult = PowerType.CODEC.parse(ops, powerJson.get(TYPE_KEY));

		if (!powerTypeResult.mapOrElse(type -> Objects.equals(type, PowerTypes.MULTIPLE), error -> false)) {
			return;
		}

		powerJson.entrySet().removeIf(e -> !isKeyIgnored(e.getKey()) && !MiscUtil.isResourceConditionFulfilled(id, e.getValue(), directoryPath, ops));
		JsonObject copy = powerJson.deepCopy();

		powerJson.asMap().clear();
		copy.asMap().forEach((key, value) -> {

			if (!isKeyIgnored(key)) {

				key = id + Character.toString(PowerReference.SubPower.SEPARATOR) + key;

				//	Append the sub-power's reference into its value object for proper parsing
				if (value instanceof JsonObject jsonObject) {
					jsonObject.addProperty(PowerEntry.REFERENCE_KEY, key);
				}

			}

			powerJson.add(key, value);

		});

	}

	public static DataResult<String> validateSubPowerName(String name) {

		if (name.isEmpty()) {
			return DataResult.error(() -> "Empty sub-power names are not allowed!");
		}

		else if (Identifier.isPathValid(name)) {
			return DataResult.success(name);
		}

		else {
			DataResult<String> result = DataResult.error(() -> "Non [a-z0-9/._-] character in sub-power name: " + name);
			return result.setPartial(name);
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
