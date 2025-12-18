package io.github.eggohito.neo_apoli.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.ProblemReporter;

import java.util.Optional;
import java.util.function.Supplier;

public class Reporter implements ProblemReporter {

	private final Multimap<String, String> errors;
	private final Supplier<String> pathCache;

	protected Reporter(Multimap<String, String> errors, Supplier<String> path) {
		this.errors = errors;
		this.pathCache = Suppliers.memoize(path::get);
	}

	public Reporter(String path) {
		this(LinkedHashMultimap.create(), () -> path);
	}

	public Reporter() {
		this("");
	}

	@Override
	public Reporter forChild(String name) {
		return new Reporter(this.errors, () -> this.getPath() + name);
	}

	@Override
	public void report(String message) {
		this.errors.put(this.getPath(), message);
	}

	public ImmutableMultimap<String, String> getErrors() {
		return ImmutableMultimap.copyOf(this.errors);
	}

	public Optional<String> getErrorsFlattened() {

		if (errors.isEmpty()) {
			return Optional.empty();
		}

		boolean moreThanOnePaths = this.errors.size() > 1;
		StringBuilder resultBuilder = new StringBuilder()
			.append("at")
			.append(moreThanOnePaths ? " these paths: " : " path ");

		errors.asMap().forEach((path, errors) -> {

			resultBuilder
				.append(moreThanOnePaths ? "\n\t - " : "")
				.append(path).append(": ");

			errors.forEach(error -> resultBuilder
				.append(errors.size() > 1 ? "\n\t\t * " : "")
				.append(error));

		});

		return Optional.of(resultBuilder.toString());

	}

	public boolean pathHasErrors(String path) {
		return this.errors.containsKey(path)
			&& !this.errors.get(path).isEmpty();
	}

	public boolean selfPathHasErrors() {
		return this.pathHasErrors(this.getPath());
	}

	public boolean hasErrors() {
		return !this.errors.isEmpty();
	}

	public String getPath() {
		return this.pathCache.get();
	}

}
