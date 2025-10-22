package pu.textblock;

import static org.junit.jupiter.api.Assertions.*;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
//import org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

public class TestTextBlocks
{
/**
 * Luckily, when using text blocks, we can still indent our code properly. To achieve that, part of the indentation is
 * treated as the source code while another part of the indentation is seen as a part of the text block. To make this
 * work, the compiler checks for the minimum indentation in all non-empty lines. Next, the compiler shifts the complete
 * text block to the left. Consider a text block containing some HTML:
 */
public static String getBlockOfHtml()
{
	return """
	    <html>

	        <body>
	            <span>example text</span>
	        </body>
	    </html>""";
}
/**
 * In this case, the minimum indentation is 12 spaces. Thus, all 12 spaces to the left of <html> and on all subsequent
 * lines are removed. Let’s test this:
 */
@Test
void givenAnOldStyleMultilineString_whenComparing_thenEqualsTextBlock()
{
	String expected = 
		"<html>\n" + 
			"\n" + 
		"    <body>\n" + 
		"        <span>example text</span>\n" + 
		"    </body>\n" +
		"</html>";
	assertEquals( expected, getBlockOfHtml() );
	//assertThat( getBlockOfHtml(), equals( expected ) );
}

@Test
void givenAnOldStyleString_whenComparing_thenEqualsTextBlock()
{
	String expected = "<html>\n\n    <body>\n        <span>example text</span>\n    </body>\n</html>";
	assertEquals( expected, getBlockOfHtml() );
}
/**
 * When we need explicit indentation, we can use less indentation for a non-empty line (or the last line):
 */
public String getNonStandardIndent() {
    return """
                Indent
            """;
}

@Test
void givenAnIndentedString_thenMatchesIndentedOldStyle() {
	assertEquals( "    Indent\n", getNonStandardIndent() );
}
/**
 * 4. Escaping
 * 4.1. Escaping Double-Quotes
 *
 * Inside text blocks, double-quotes don’t have to be escaped. We could even use three double-quotes again 
 * in our text block by escaping one of them:
*/
public String getTextWithEscapes() {
    return """
            "fun" with
            whitespace
            and other escapes \"""
            """;
}
/**
 * 4.2. Escaping Line Terminators
 * In general, newlines don’t have to be escaped inside text blocks.
 * However, note that even if a source file has Windows line endings (\r\n), the text blocks will only be terminated 
 * with newlines (\n). If we need carriage returns (\r) to be present, we have to explicitly add them to the text block:
 */
public String getTextWithCarriageReturns() {
return """
separated with\r
carriage returns""";
}

@Test
void givenATextWithCarriageReturns_thenItContainsBoth() {
assertEquals( "separated with\r\ncarriage returns", getTextWithCarriageReturns() );
}

/**
 * Sometimes, we might have long lines of text in our source code that we want to format in a readable way. 
 * Java 14 preview added a feature that allows us to do this. We can escape a newline so that it is ignored:
 */
public String getIgnoredNewLines() {
    return """
            This is a long test which looks to \
            have a newline but actually does not""";
}

/**
 * Actually this String literal will just equal a normal non-interrupted String:
 */
@Test
void givenAStringWithEscapedNewLines_thenTheResultHasNoNewLines() {
    String expected = "This is a long test which looks to have a newline but actually does not";
    assertEquals( expected, getIgnoredNewLines() );
}

/**
 * 4.3. Escaping Spaces

 * The compiler ignores all trailing spaces in text blocks. However, since Java 14 preview, we can escape
 *  a space using the new escape sequence \s. The compiler will also preserve any spaces in front of this escaped space.
 * Let's take a closer look at the impact of an escaped space:
 */
public String getEscapedSpaces() {
    return """    
            line 1       
            line 2       \s
            """;
}
@Test
void givenAStringWithEscapesSpaces_thenTheResultHasLinesEndingWithSpaces() {
    String expected = "line 1\nline 2        \n"; // Dit zijn 8 spaties, en er staan er maar 7 voor de \s. 
    // Betekent dat dat de \s ook als een spatie geldt?
    assertEquals( expected, getEscapedSpaces() );
}
/**
 * 5. Formatting
 * To aid with variable substitution, a new method was added that allows calling the String.format method directly on a String literal:
 */
public String getFormattedText(String parameter) 
{
    return """
        Some parameter: %s
        """.formatted(parameter);
}
@Test
void testFormattedStrings() {
    String expected = "Some parameter: pipo\n"; 
    assertEquals( expected, getFormattedText( "pipo" ) );
}
public String getFormattedTextWithoutNewline(String parameter) 
{
    return """
        Some parameter: %s""".formatted(parameter);
}
@Test
void testFormattedStringsWithoutNewline() {
    String expected = "Some parameter: pipo"; 
    assertEquals( expected, getFormattedTextWithoutNewline( "pipo" ) );
}
@Test
public void givenTwoString_thenInterpolateWithFormat() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String first = "Interpolation";
    String second = "Java";
    String result = String.format("String %s in %s with some %s examples.", first, second, second);
    assertEquals(EXPECTED_STRING, result);
}

/**
 * Additionally, we can reference a specific argument if we want to avoid variable repetitions in our format call:
 */
@Test
public void givenTwoString_thenInterpolateWithFormatStringReference() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String first = "Interpolation";
    String second = "Java";
    String result = String.format("String %1$s in %2$s with some %2$s examples.", first, second);
    assertEquals(EXPECTED_STRING, result);
}
@Test
public void givenTwoString_thenInterpolateWithMessageFormat() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String first = "Interpolation";
    String second = "Java";
    String result = MessageFormat.format("String {0} in {1} with some {1} examples.", first, second);
    assertEquals(EXPECTED_STRING, result);
}
@Test
public void givenTwoString_thenInterpolateWithStringSubstitutor() {
    String EXPECTED_STRING = "String Interpolation in Java with some Java examples.";
    String baseString = "String ${first} in ${second} with some ${second} examples.";
    String first = "Interpolation";
    String second = "Java";
    Map<String, String> parameters = new HashMap<>();
    parameters.put("first", first);
    parameters.put("second", second);
    StringSubstitutor substitutor = new StringSubstitutor(parameters);
    String result = substitutor.replace(baseString);
    assertEquals(EXPECTED_STRING, result);
}
}
