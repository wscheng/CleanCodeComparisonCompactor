package wscheng.cleancode.example.comparisoncompactor;


import junit.framework.Assert;

public class ComparisonCompactor {

    private static final String ELLIPSIS = "...";
    private static final String DELTA_END = "]";
    private static final String DELTA_START = "[";

    private int contextLength;
    private String expected;
    private String actual;
    private String compactExpected;
    private String compactActual;
    private int prefixLength;
    private int suffixLengthPlusOne;

    public ComparisonCompactor(int contextLength, String expected, String actual) {
        this.contextLength = contextLength;
        this.expected = expected;
        this.actual = actual;
    }

    public String formatCompactedComparison(String message) {
        if (canBeCompacted()) {
            compactExpectedAndActual();
            return Assert.format(message, compactExpected, compactActual);
        } else {
            return Assert.format(message, expected, actual);
        }
    }

    private boolean canBeCompacted() {
        return expected != null && actual != null && !areStringsEqual();
    }

    public void compactExpectedAndActual() {
        findCommonPrefixAndSuffix();

        compactExpected = compactString(expected);
        compactActual = compactString(actual);
    }

    private void findCommonPrefixAndSuffix() {
        findCommonPrefix();
        int suffixLength = 0;
        for (; !suffixOverlapsPrefix(suffixLength); suffixLength++) {
            if (charFromEnd(expected, suffixLength) != charFromEnd(actual, suffixLength)) {
                break;
            }
        }
        suffixLengthPlusOne = suffixLength + 1;
    }

    private boolean suffixOverlapsPrefix(int suffixLength) {
        return actual.length() - suffixLength <= prefixLength || expected.length() - suffixLength <= prefixLength;
    }

    private char charFromEnd(String s, int i) {
        return s.charAt(s.length() -i -1);
    }

    private String compactString(String source) {
        String result = DELTA_START + source.substring(prefixLength, source.length() - suffixLengthPlusOne + 1) + DELTA_END;
        if (prefixLength > 0) {
            result = computeCommonPrefix() + result;
        }
        if (suffixLengthPlusOne > 0) {
            result = result + computeCommonSuffix();
        }
        return result;
    }

    private void findCommonPrefix() {
        prefixLength = 0;
        int end = Math.min(expected.length(), actual.length());
        for (; prefixLength < end; prefixLength++) {
            if (expected.charAt(prefixLength) != actual.charAt(prefixLength)) {
                break;
            }
        }
    }

    private String computeCommonPrefix() {
        return (prefixLength > contextLength ? ELLIPSIS : "") + expected.substring(Math.max(0, prefixLength - contextLength), prefixLength);
    }

    private String computeCommonSuffix() {
        int end = Math.min(expected.length() - suffixLengthPlusOne + 1 + contextLength, expected.length());
        return expected.substring(expected.length() - suffixLengthPlusOne + 1, end) + (expected.length() - suffixLengthPlusOne + 1 < expected.length() - contextLength ? ELLIPSIS : "");
    }

    private boolean areStringsEqual() {
        return expected.equals(actual);
    }
}
