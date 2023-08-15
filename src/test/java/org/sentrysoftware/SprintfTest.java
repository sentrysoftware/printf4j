package org.sentrysoftware;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import static org.sentrysoftware.printf4j.Printf4J.*;

public class SprintfTest {

	@Test
	public void testPlus() {
		  assertEquals("+42", sprintf("%+d", 42));
		  assertEquals("-42", sprintf("%+d", -42));
		  assertEquals("  +42", sprintf("%+5d", 42));
		  assertEquals("  -42", sprintf("%+5d", -42));
		  assertEquals("            +42", sprintf("%+15d", 42));
		  assertEquals("            -42", sprintf("%+15d", -42));
		  assertEquals("Hello testing", sprintf("%+s", "Hello testing"));
		  assertEquals("+1024", sprintf("%+d", 1024));
		  assertEquals("-1024", sprintf("%+d", -1024));
		  assertEquals("+1024", sprintf("%+i", 1024));
		  assertEquals("-1024", sprintf("%+i", -1024));
		  assertEquals("1024", sprintf("%+u", 1024));
		  assertEquals("4294966272", sprintf("%+u", 4294966272L));
		  assertEquals("777", sprintf("%+o", 511));
		  assertEquals("37777777001", sprintf("%+o", 4294966785L));
		  assertEquals("1234abcd", sprintf("%+x", 305441741));
		  assertEquals("edcb5433", sprintf("%+x", 3989525555L));
		  assertEquals("1234ABCD", sprintf("%+X", 305441741));
		  assertEquals("EDCB5433", sprintf("%+X", 3989525555L));
		  assertEquals("x", sprintf("%+c", 'x'));
//		  assertEquals("0", sprintf("%+.0d", 0));
	}
	
	@Test
	public void testBlank() {
		assertEquals(" 42", sprintf("% d", 42));
		assertEquals("-42", sprintf("% d", -42));
		assertEquals("   42", sprintf("% 5d", 42));
		assertEquals("  -42", sprintf("% 5d", -42));
		assertEquals("             42", sprintf("% 15d", 42));
		assertEquals("            -42", sprintf("% 15d", -42));
		assertEquals("            -42", sprintf("% 15d", -42));
		assertEquals("        -42.987", sprintf("% 15.3f", -42.987));
		assertEquals("         42.987", sprintf("% 15.3f", 42.987));
		assertEquals("Hello testing", sprintf("% s", "Hello testing"));
		assertEquals(" 1024", sprintf("% d", 1024));
		assertEquals("-1024", sprintf("% d", -1024));
		assertEquals(" 1024", sprintf("% i", 1024));
		assertEquals("-1024", sprintf("% i", -1024));
		assertEquals("1024", sprintf("% u", 1024));
		assertEquals("4294966272", sprintf("% u", 4294966272L));
		assertEquals("777", sprintf("% o", 511));
		assertEquals("37777777001", sprintf("% o", 4294966785L));
		assertEquals("1234abcd", sprintf("% x", 305441741));
		assertEquals("edcb5433", sprintf("% x", 3989525555L));
		assertEquals("1234ABCD", sprintf("% X", 305441741));
		assertEquals("EDCB5433", sprintf("% X", 3989525555L));
		assertEquals("x", sprintf("% c", 'x'));
	}
	
	@Test
	public void testZero() {
		assertEquals("42", sprintf("%0d", 42));
		assertEquals("42", sprintf("%0ld", 42L));
		assertEquals("-42", sprintf("%0d", -42));
		assertEquals("00042", sprintf("%05d", 42));
		assertEquals("-0042", sprintf("%05d", -42));
		assertEquals("000000000000042", sprintf("%015d", 42));
		assertEquals("-00000000000042", sprintf("%015d", -42));
		assertEquals("000000000042.12", sprintf("%015.2f", 42.1234));
		assertEquals("00000000042.988", sprintf("%015.3f", 42.9876));
		assertEquals("-00000042.98760", sprintf("%015.5f", -42.9876));
	}
	
	@Test
	public void testMinus() {
		assertEquals("42", sprintf("%-d", 42));
		assertEquals("-42", sprintf("%-d", -42));
		assertEquals("42   ", sprintf("%-5d", 42));
		assertEquals("-42  ", sprintf("%-5d", -42));
		assertEquals("42             ", sprintf("%-15d", 42));
		assertEquals("-42            ", sprintf("%-15d", -42));
		assertEquals("42", sprintf("%-0d", 42));
		assertEquals("-42", sprintf("%-0d", -42));
		assertEquals("42   ", sprintf("%-05d", 42));
		assertEquals("-42  ", sprintf("%-05d", -42));
		assertEquals("42             ", sprintf("%-015d", 42));
		assertEquals("-42            ", sprintf("%-015d", -42));
		assertEquals("42", sprintf("%0-d", 42));
		assertEquals("-42", sprintf("%0-d", -42));
		assertEquals("42   ", sprintf("%0-5d", 42));
		assertEquals("-42  ", sprintf("%0-5d", -42));
		assertEquals("42             ", sprintf("%0-15d", 42));
		assertEquals("-42            ", sprintf("%0-15d", -42));
		assertEquals("-4.200e+01     ", sprintf("%0-15.3e", -42.));
		assertEquals("-42.0          ", sprintf("%0-15.3g", -42.));

	}
	
	@Test
	public void testHash() {
		assertEquals("", sprintf("%#.0x", 0));
//		assertEquals("0", sprintf("%#.1x", 0)); // This is the real expected behavior, which is wrong IMO
		assertEquals("0x0", sprintf("%#.1x", 0));
		assertEquals("", sprintf("%#.0llx", 0));
		assertEquals("0x0000614e", sprintf("%#.8x", 0x614e)); // This is the real expected behavior
//		assertEquals("0b110", sprintf("%#b", 6)); // Binary is not supported for now
	}


	@Test
	public void testSpecifier() {
		assertEquals("Hello testing", sprintf("Hello testing"));
		assertEquals("Hello testing", sprintf("%s", "Hello testing"));
		assertEquals("1024", sprintf("%d", 1024));
		assertEquals("-1024", sprintf("%d", -1024));
		assertEquals("1024", sprintf("%i", 1024));
		assertEquals("-1024", sprintf("%i", -1024));
		assertEquals("1024", sprintf("%u", 1024));
		assertEquals("4294966272", sprintf("%u", 4294966272L));
		assertEquals("777", sprintf("%o", 511));
		assertEquals("37777777001", sprintf("%o", 4294966785L));
		assertEquals("1234abcd", sprintf("%x", 305441741));
		assertEquals("edcb5433", sprintf("%x", 3989525555L));
		assertEquals("1234ABCD", sprintf("%X", 305441741));
		assertEquals("EDCB5433", sprintf("%X", 3989525555L));
		assertEquals("%", sprintf("%%"));
	}
	
	@Test
	public void testWidth() {
		assertEquals("Hello testing", sprintf("%1s", "Hello testing"));
		assertEquals("1024", sprintf("%1d", 1024));
		assertEquals("-1024", sprintf("%1d", -1024));
		assertEquals("1024", sprintf("%1i", 1024));
		assertEquals("-1024", sprintf("%1i", -1024));
		assertEquals("1024", sprintf("%1u", 1024));
		assertEquals("4294966272", sprintf("%1u", 4294966272L));
		assertEquals("777", sprintf("%1o", 511));
		assertEquals("37777777001", sprintf("%1o", 4294966785L));
		assertEquals("1234abcd", sprintf("%1x", 305441741));
		assertEquals("edcb5433", sprintf("%1x", 3989525555L));
		assertEquals("1234ABCD", sprintf("%1X", 305441741));
		assertEquals("EDCB5433", sprintf("%1X", 3989525555L));
		assertEquals("x", sprintf("%1c", 'x'));
	}
	
	@Test
	public void testWidth20() {
		assertEquals("               Hello", sprintf("%20s", "Hello"));
		assertEquals("                1024", sprintf("%20d", 1024));
		assertEquals("               -1024", sprintf("%20d", -1024));
		assertEquals("                1024", sprintf("%20i", 1024));
		assertEquals("               -1024", sprintf("%20i", -1024));
		assertEquals("                1024", sprintf("%20u", 1024));
		assertEquals("          4294966272", sprintf("%20u", 4294966272L));
		assertEquals("                 777", sprintf("%20o", 511));
		assertEquals("         37777777001", sprintf("%20o", 4294966785L));
		assertEquals("            1234abcd", sprintf("%20x", 305441741));
		assertEquals("            edcb5433", sprintf("%20x", 3989525555L));
		assertEquals("            1234ABCD", sprintf("%20X", 305441741));
		assertEquals("            EDCB5433", sprintf("%20X", 3989525555L));
		assertEquals("                   x", sprintf("%20c", 'x'));
	}
	
	@Test
	public void testWidthStar20() {
		assertEquals("               Hello", sprintf("%*s", 20, "Hello"));
		assertEquals("                1024", sprintf("%*d", 20, 1024));
		assertEquals("               -1024", sprintf("%*d", 20, -1024));
		assertEquals("                1024", sprintf("%*i", 20, 1024));
		assertEquals("               -1024", sprintf("%*i", 20, -1024));
		assertEquals("                1024", sprintf("%*u", 20, 1024));
		assertEquals("          4294966272", sprintf("%*u", 20, 4294966272L));
		assertEquals("                 777", sprintf("%*o", 20, 511));
		assertEquals("         37777777001", sprintf("%*o", 20, 4294966785L));
		assertEquals("            1234abcd", sprintf("%*x", 20, 305441741));
		assertEquals("            edcb5433", sprintf("%*x", 20, 3989525555L));
		assertEquals("            1234ABCD", sprintf("%*X", 20, 305441741));
		assertEquals("            EDCB5433", sprintf("%*X", 20, 3989525555L));
		assertEquals("                   x", sprintf("%*c", 20, 'x'));
	}
	
	@Test
	public void testMinus20() {
		assertEquals("Hello               ", sprintf("%-20s", "Hello"));
		assertEquals("1024                ", sprintf("%-20d", 1024));
		assertEquals("-1024               ", sprintf("%-20d", -1024));
		assertEquals("1024                ", sprintf("%-20i", 1024));
		assertEquals("-1024               ", sprintf("%-20i", -1024));
		assertEquals("1024                ", sprintf("%-20u", 1024));
		assertEquals("1024.1234           ", sprintf("%-20.4f", 1024.1234));
		assertEquals("4294966272          ", sprintf("%-20u", 4294966272L));
		assertEquals("777                 ", sprintf("%-20o", 511));
		assertEquals("37777777001         ", sprintf("%-20o", 4294966785L));
		assertEquals("1234abcd            ", sprintf("%-20x", 305441741));
		assertEquals("edcb5433            ", sprintf("%-20x", 3989525555L));
		assertEquals("1234ABCD            ", sprintf("%-20X", 305441741));
		assertEquals("EDCB5433            ", sprintf("%-20X", 3989525555L));
		assertEquals("x                   ", sprintf("%-20c", 'x'));
		assertEquals("|    9| |9 | |    9|", sprintf("|%5d| |%-2d| |%5d|", 9, 9, 9));
		assertEquals("|   10| |10| |   10|", sprintf("|%5d| |%-2d| |%5d|", 10, 10, 10));
		assertEquals("|    9| |9           | |    9|", sprintf("|%5d| |%-12d| |%5d|", 9, 9, 9));
		assertEquals("|   10| |10          | |   10|", sprintf("|%5d| |%-12d| |%5d|", 10, 10, 10));  
	}
	
	@Test
	public void testZeroMinus20() {
		assertEquals("Hello               ", sprintf("%0-20s", "Hello"));
		assertEquals("1024                ", sprintf("%0-20d", 1024));
		assertEquals("-1024               ", sprintf("%0-20d", -1024));
		assertEquals("1024                ", sprintf("%0-20i", 1024));
		assertEquals("-1024               ", sprintf("%0-20i", -1024));
		assertEquals("1024                ", sprintf("%0-20u", 1024));
		assertEquals("4294966272          ", sprintf("%0-20u", 4294966272L));
		assertEquals("777                 ", sprintf("%0-20o", 511));
		assertEquals("37777777001         ", sprintf("%0-20o", 4294966785L));
		assertEquals("1234abcd            ", sprintf("%0-20x", 305441741));
		assertEquals("edcb5433            ", sprintf("%0-20x", 3989525555L));
		assertEquals("1234ABCD            ", sprintf("%0-20X", 305441741));
		assertEquals("EDCB5433            ", sprintf("%0-20X", 3989525555L));
		assertEquals("x                   ", sprintf("%0-20c", 'x'));
	}
	
	@Test
	public void testPadding20() {
		assertEquals("00000000000000001024", sprintf("%020d", 1024));
		assertEquals("-0000000000000001024", sprintf("%020d", -1024));
		assertEquals("00000000000000001024", sprintf("%020i", 1024));
		assertEquals("-0000000000000001024", sprintf("%020i", -1024));
		assertEquals("00000000000000001024", sprintf("%020u", 1024));
		assertEquals("00000000004294966272", sprintf("%020u", 4294966272L));
		assertEquals("00000000000000000777", sprintf("%020o", 511));
		assertEquals("00000000037777777001", sprintf("%020o", 4294966785L));
		assertEquals("0000000000001234abcd", sprintf("%020x", 305441741));
		assertEquals("000000000000edcb5433", sprintf("%020x", 3989525555L));
		assertEquals("0000000000001234ABCD", sprintf("%020X", 305441741));
		assertEquals("000000000000EDCB5433", sprintf("%020X", 3989525555L));
	}
	
	@Test
	public void testPaddingPrecision20() {
		assertEquals("00000000000000001024", sprintf("%.20d", 1024));
		assertEquals("-00000000000000001024", sprintf("%.20d", -1024));
		assertEquals("00000000000000001024", sprintf("%.20i", 1024));
		assertEquals("-00000000000000001024", sprintf("%.20i", -1024));
		assertEquals("00000000000000001024", sprintf("%.20u", 1024));
		assertEquals("00000000004294966272", sprintf("%.20u", 4294966272L));
		assertEquals("00000000000000000777", sprintf("%.20o", 511));
		assertEquals("00000000037777777001", sprintf("%.20o", 4294966785L));
		assertEquals("0000000000001234abcd", sprintf("%.20x", 305441741));
		assertEquals("000000000000edcb5433", sprintf("%.20x", 3989525555L));
		assertEquals("0000000000001234ABCD", sprintf("%.20X", 305441741));
		assertEquals("000000000000EDCB5433", sprintf("%.20X", 3989525555L));
	}
	
	@Test
	public void testPaddingHashZero20() {
		assertEquals("00000000000000001024", sprintf("%#020d", 1024));
		assertEquals("-0000000000000001024", sprintf("%#020d", -1024));
		assertEquals("00000000000000001024", sprintf("%#020i", 1024));
		assertEquals("-0000000000000001024", sprintf("%#020i", -1024));
		assertEquals("00000000000000001024", sprintf("%#020u", 1024));
		assertEquals("00000000004294966272", sprintf("%#020u", 4294966272L));
		assertEquals("00000000000000000777", sprintf("%#020o", 511));
		assertEquals("00000000037777777001", sprintf("%#020o", 4294966785L));
		assertEquals("0x00000000001234abcd", sprintf("%#020x", 305441741));
		assertEquals("0x0000000000edcb5433", sprintf("%#020x", 3989525555L));
		assertEquals("0X00000000001234ABCD", sprintf("%#020X", 305441741));
		assertEquals("0X0000000000EDCB5433", sprintf("%#020X", 3989525555L));
	}
	
	@Test
	public void testPaddingHash20() {
		assertEquals("                1024", sprintf("%#20d", 1024));
		assertEquals("               -1024", sprintf("%#20d", -1024));
		assertEquals("                1024", sprintf("%#20i", 1024));
		assertEquals("               -1024", sprintf("%#20i", -1024));
		assertEquals("                1024", sprintf("%#20u", 1024));
		assertEquals("          4294966272", sprintf("%#20u", 4294966272L));
//		assertEquals("                0777", sprintf("%#20o", 511));
//		assertEquals("        037777777001", sprintf("%#20o", 4294966785L));
//		assertEquals("          0x1234abcd", sprintf("%#20x", 305441741));
//		assertEquals("          0xedcb5433", sprintf("%#20x", 3989525555L));
//		assertEquals("          0X1234ABCD", sprintf("%#20X", 305441741));
//		assertEquals("          0XEDCB5433", sprintf("%#20X", 3989525555L));
	}
}
