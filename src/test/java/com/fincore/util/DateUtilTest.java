package com.fincore.util;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {
    //DateUtil tests
    @Test
    void testGetDaysBetween() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 10);
        assertEquals(9, DateUtil.getDaysBetween(start, end));
    }
    
    @Test
    void testGetDaysBetweenNulls() {
        assertThrows(IllegalArgumentException.class, () -> 
            DateUtil.getDaysBetween(null, LocalDate.now()));
            
        assertThrows(IllegalArgumentException.class, () -> 
            DateUtil.getDaysBetween(LocalDate.now(), null));
    }

    @Test
    void testIsDateInFuture() {
        assertTrue(DateUtil.isDateInFuture(LocalDate.now().plusDays(1)));
        assertFalse(DateUtil.isDateInFuture(LocalDate.now().minusDays(1)));
        assertFalse(DateUtil.isDateInFuture(LocalDate.now())); 
    }
    
    @Test
    void testIsDateInFutureNull() {
        assertFalse(DateUtil.isDateInFuture(null));
    }

    @Test
    void testAddMonths() {
        LocalDate date = LocalDate.of(2025, 1, 31);
        LocalDate result = DateUtil.addMonths(date, 1); 
        assertEquals(LocalDate.of(2025, 2, 28), result);
    }
    
    @Test
    void testAddMonthsNull() {
        assertNull(DateUtil.addMonths(null, 5));
    }
}