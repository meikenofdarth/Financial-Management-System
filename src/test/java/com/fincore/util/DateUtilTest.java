package com.fincore.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void testGetDaysBetween() {
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 10);
        
        // Expected: 9 days
        assertEquals(9, DateUtil.getDaysBetween(start, end));
    }
    
    @Test
    void testGetDaysBetween_Nulls() {
        // Kills mutants that remove the null check
        assertThrows(IllegalArgumentException.class, () -> 
            DateUtil.getDaysBetween(null, LocalDate.now()));
            
        assertThrows(IllegalArgumentException.class, () -> 
            DateUtil.getDaysBetween(LocalDate.now(), null));
    }

    @Test
    void testIsDateInFuture() {
        // Kills Relational Operator mutants (changing > to >=)
        assertTrue(DateUtil.isDateInFuture(LocalDate.now().plusDays(1)));
        assertFalse(DateUtil.isDateInFuture(LocalDate.now().minusDays(1)));
        assertFalse(DateUtil.isDateInFuture(LocalDate.now())); // Boundary check
    }
    
    @Test
    void testIsDateInFuture_Null() {
        assertFalse(DateUtil.isDateInFuture(null));
    }

    @Test
    void testAddMonths() {
        LocalDate date = LocalDate.of(2023, 1, 31);
        // Java date math handles end-of-month automatically
        LocalDate result = DateUtil.addMonths(date, 1); 
        assertEquals(LocalDate.of(2023, 2, 28), result); // 2023 is not leap year
    }
    
    @Test
    void testAddMonths_Null() {
        assertNull(DateUtil.addMonths(null, 5));
    }
}