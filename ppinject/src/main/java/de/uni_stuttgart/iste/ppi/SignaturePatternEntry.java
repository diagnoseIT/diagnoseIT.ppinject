package de.uni_stuttgart.iste.ppi;

import java.util.regex.Pattern;

import kieker.monitoring.core.signaturePattern.InvalidPatternException;
import kieker.monitoring.core.signaturePattern.PatternParser;

/**
 * Instances of this class are used to store the configuration for a performance problem by pointcut.
 * 
 * @author Philipp Keck (University of Stuttgart)
 *
 * @param <C> The configuration type.
 */
public class SignaturePatternEntry<C extends ProblemConfiguration> {

    private final Pattern pattern;
    private final String strPattern;
    private final C configuration;

    /**
     * Creates a new pattern entry using the given parameters.
     * 
     * @param strPattern The pattern, which pointcuts are match against to decide, if the given configuration applies.
     * @param configuration The configuration for problem instances matching the pattern.
     * 
     * @throws InvalidPatternException If the given pattern is invalid.
     */
    public SignaturePatternEntry(final String strPattern, final C configuration) throws InvalidPatternException {
        this(strPattern, PatternParser.parseToPattern(strPattern), configuration);
    }

    /**
     * Creates a new pattern entry using the given parameters.
     * 
     * @param strPattern The pattern string.
     * @param pattern The pattern, which pointcuts are match against to decide, if the given configuration applies.
     * @param configuration The configuration for problem instances matching the pattern.
     */
    public SignaturePatternEntry(final String strPattern, final Pattern pattern, final C configuration) {
        this.strPattern = strPattern;
        this.pattern = pattern;
        this.configuration = configuration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((this.pattern == null) ? 0 : this.pattern.hashCode()); // NOCS
        result = (prime * result) + ((this.strPattern == null) ? 0 : this.strPattern.hashCode()); // NOCS
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final SignaturePatternEntry<?> other = (SignaturePatternEntry<?>) obj;
        if (this.pattern == null) {
            if (other.pattern != null) {
                return false;
            }
        } else if (!this.pattern.equals(other.pattern)) {
            return false;
        }
        if (this.strPattern == null) {
            if (other.strPattern != null) {
                return false;
            }
        } else if (!this.strPattern.equals(other.strPattern)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SignaturePatternEntry [pattern=" + this.pattern + ", strPattern=" + this.strPattern + ", configuration=" + this.configuration + "]";
    }

    /**
     * @return The pattern string.
     */
    public String getStrPattern() {
        return this.strPattern;
    }

    /**
     * @return The pattern, which pointcuts are match against to decide, if the given configuration applies.
     */
    public Pattern getPattern() {
        return this.pattern;
    }

    /**
     * @return The configuration for problem instances matching the pattern.
     */
    public C getConfiguration() {
        return this.configuration;
    }
}
