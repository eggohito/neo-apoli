package io.github.eggohito.neo_apoli.key;

import net.minecraft.util.Mth;

import java.util.Objects;

public record KeyState(String id, long pressedTime) {

	public KeyState(String id) {
		this(id, 0);
	}

	public KeyState {
		pressedTime = Mth.clamp(0, pressedTime, Long.MAX_VALUE);
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		else if (obj instanceof KeyState that) {
			return Objects.equals(this.id(), that.id());
		}

		else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.id());
	}

	public boolean pressed() {
		return pressedTime() > 0;
	}

}
