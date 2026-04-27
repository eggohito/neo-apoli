package io.github.eggohito.neo_apoli.client.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.isxander.yacl3.config.v3.ConfigEntry;
import io.github.eggohito.neo_apoli.config.AbstractJsonCodecConfig;
import io.github.eggohito.neo_apoli.util.CodecUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.fabricmc.loader.api.FabricLoader;
import org.quiltmc.parsers.json.JsonFormat;

@SuppressWarnings("UnstableApiUsage")
public final class NeoApoliClientConfig extends AbstractJsonCodecConfig<NeoApoliClientConfig> {

	public static final NeoApoliClientConfig INSTANCE = new NeoApoliClientConfig();

	public final ConfigEntry<ResourceBars> resourceBars = register("resource_bars", ResourceBars.DEFAULT, ResourceBars.CODEC);

	private NeoApoliClientConfig() {
		super(FabricLoader.getInstance().getConfigDir().resolve("neo-apoli/client.json5"), JsonFormat.JSON5);
	}

	@AllArgsConstructor
	@Accessors(fluent = true)
	@Data
	public static final class ResourceBars {

		public static final ResourceBars DEFAULT = new ResourceBars(0, 0);

		public static final Codec<ResourceBars> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			CodecUtil.nonNegativeInt().fieldOf("offset_x").forGetter(ResourceBars::offsetX),
			CodecUtil.nonNegativeInt().fieldOf("offset_y").forGetter(ResourceBars::offsetY)
		).apply(instance, ResourceBars::new));

		private int offsetX;
		private int offsetY;

	}

}
