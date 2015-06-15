package com.github.diagnoseit.ppinject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import kieker.monitoring.core.signaturePattern.InvalidPatternException;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Stores the activation state (including configuration) for a performance
 * problem. This class corresponds to Kieker's ProbeController.
 * 
 * @author Philipp Keck (University of Stuttgart)
 *
 * @param <C>
 *            The type of configuration that the problem implementation needs to
 *            store.
 */
public final class SignatureConfigurationStore<C extends ProblemConfiguration> {

	/**
	 * Cache for {@link #signatureToLongString(Signature)}.
	 */
	private static final ConcurrentMap<Signature, String> SIGNATURE_TO_STRING_CACHE = new ConcurrentHashMap<Signature, String>();

	/**
	 * Better handling of AspectJ Signature.toLongString (especially with
	 * constructors).
	 * 
	 * @param sig
	 *            an AspectJ Signature
	 * @return LongString representation of the signature
	 */
	private static String signatureToLongString(final Signature sig) {
		String signatureString = SIGNATURE_TO_STRING_CACHE.get(sig);
		if (null != signatureString) {
			return signatureString;
		} else {
			if (sig instanceof MethodSignature) {
				final MethodSignature signature = (MethodSignature) sig;
				final StringBuilder sb = new StringBuilder(256);
				// modifiers
				final String modString = Modifier.toString(signature
						.getModifiers());
				sb.append(modString);
				if (modString.length() > 0) {
					sb.append(' ');
				}
				// return
				addType(sb, signature.getReturnType());
				sb.append(' ');
				// component
				sb.append(signature.getDeclaringTypeName());
				sb.append('.');
				// name
				sb.append(signature.getName());
				// parameters
				sb.append('(');
				addTypeList(sb, signature.getParameterTypes());
				sb.append(')');
				// throws
				// this.addTypeList(sb, signature.getExceptionTypes());
				signatureString = sb.toString();
			} else if (sig instanceof ConstructorSignature) {
				final ConstructorSignature signature = (ConstructorSignature) sig;
				final StringBuilder sb = new StringBuilder(256);
				// modifiers
				final String modString = Modifier.toString(signature
						.getModifiers());
				sb.append(modString);
				if (modString.length() > 0) {
					sb.append(' ');
				}
				// component
				sb.append(signature.getDeclaringTypeName());
				sb.append('.');
				// name
				sb.append(signature.getName());
				// parameters
				sb.append('(');
				addTypeList(sb, signature.getParameterTypes());
				sb.append(')');
				// throws
				// this.addTypeList(sb, signature.getExceptionTypes());
				signatureString = sb.toString();
			} else {
				signatureString = sig.toLongString();
			}
		}
		SIGNATURE_TO_STRING_CACHE.putIfAbsent(sig, signatureString);
		return signatureString;
	}

	private static StringBuilder addTypeList(final StringBuilder sb,
			final Class<?>[] clazzes) {
		if (null != clazzes) {
			boolean first = true;
			for (final Class<?> clazz : clazzes) {
				if (first) {
					first = false;
				} else {
					sb.append(", ");
				}
				addType(sb, clazz);
			}
		}
		return sb;
	}

	private static StringBuilder addType(final StringBuilder sb,
			final Class<?> clazz) {
		if (null == clazz) {
			sb.append("ANONYMOUS");
		} else if (clazz.isArray()) {
			final Class<?> componentType = clazz.getComponentType();
			addType(sb, componentType);
			sb.append("[]");
		} else {
			sb.append(clazz.getName());
		}
		return sb;
	}

	/**
	 * Cache for {@link #matchesPattern(String)}.
	 */
	private final ConcurrentMap<String, C> signatureConfigurationCache = new ConcurrentHashMap<String, C>();

	/**
	 * The actual configuration data.
	 */
	private final List<SignaturePatternEntry<C>> patternList = new ArrayList<SignaturePatternEntry<C>>();

	/**
	 * Adds a new configuration entry.
	 * 
	 * @param pattern
	 *            The pattern, which matches all signatures that shall be
	 *            configured.
	 * @param configuration
	 *            The configuration.
	 * @throws InvalidPatternException
	 */
	public void addPattern(String pattern, C configuration)
			throws InvalidPatternException {
		this.invalidateCache();
		patternList.add(new SignaturePatternEntry<C>(pattern, configuration));
	}

	/**
	 * This method tests if the given signature matches a pattern and returns
	 * the corresponding configuration.
	 * 
	 * @param signature
	 *            The signature to match.
	 * @return The configuration or null, if no match.
	 */
	// private C matchesPattern(final String signature) {
	// synchronized (this) {
	// final ListIterator<SignaturePatternEntry<C>> patternListIterator =
	// this.patternList.listIterator(0);
	// C lastMatchingConfig;
	// while (patternListIterator.hasNext()) {
	// final SignaturePatternEntry<C> patternEntry = patternListIterator.next();
	// if (patternEntry.getPattern().matcher(signature).matches()) {
	// final C configuration = patternEntry.getConfiguration();
	// this.signatureConfigurationCache.put(signature, configuration);
	// return configuration;
	// }
	// }
	// }
	//
	// // Not found, so null (inactive).
	// this.signatureConfigurationCache.put(signature, null);
	//
	// return null; // if nothing matches, the default is null!
	// }

	private C matchesPattern(final String signature) {
		synchronized (this) {
			final ListIterator<SignaturePatternEntry<C>> patternListIterator = this.patternList
					.listIterator(0);
			C lastMatchingConfig = null;
			while (patternListIterator.hasNext()) {
				final SignaturePatternEntry<C> patternEntry = patternListIterator
						.next();
				if (patternEntry.getPattern().matcher(signature).matches()) {
					lastMatchingConfig = patternEntry.getConfiguration();
				}
			}
			this.signatureConfigurationCache.put(signature, lastMatchingConfig);
			return lastMatchingConfig;
		}
	}

	/**
	 * Retrieves the configuration for a signature.
	 * 
	 * @param signature
	 *            The signature.
	 * @return The corresponding configuration or <tt>null</tt>, if the
	 *         signature is not configured (i.e. deactivated).
	 */
	public C getConfiguration(Signature signature) {
		String strSignature = signatureToLongString(signature);
		return this.getConfiguration(strSignature);
	}

	public C getConfiguration(String signature) {
		if (signatureConfigurationCache.containsKey(signature)) {
			return signatureConfigurationCache.get(signature); // May be null,
																// but that's
																// fine.
		} else {
			return matchesPattern(signature);
		}
	}

	private void invalidateCache() {
		this.signatureConfigurationCache.clear();
	}

	public String getSignatureForConfiguration(ProblemConfiguration pc) {
		for (SignaturePatternEntry<C> spe : this.patternList) {
			if (spe.getConfiguration() == pc) {
				return spe.getStrPattern();
			}
		}
		return "";
	}
}
