/*
 * Copyright 2017 Phillip Hsu
 *
 * This file is part of ClockPlus.
 *
 * ClockPlus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ClockPlus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ClockPlus.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.claudiofus.clock2.alarms.misc;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Phillip Hsu on 6/1/2016.
 */
public class DaysOfWeekTest {

    @Test
    public void testSundayAsFirstDayOfWeek() {
        DaysOfWeek days = new DaysOfWeek(DaysOfWeek.SUNDAY);
        Assert.assertEquals(days.weekDayAt(0), DaysOfWeek.SUNDAY);
        Assert.assertEquals(days.weekDayAt(1), DaysOfWeek.MONDAY);
        Assert.assertEquals(days.weekDayAt(2), DaysOfWeek.TUESDAY);
        Assert.assertEquals(days.weekDayAt(3), DaysOfWeek.WEDNESDAY);
        Assert.assertEquals(days.weekDayAt(4), DaysOfWeek.THURSDAY);
        Assert.assertEquals(days.weekDayAt(5), DaysOfWeek.FRIDAY);
        Assert.assertEquals(days.weekDayAt(6), DaysOfWeek.SATURDAY);

        assertEquals(days.positionOf(DaysOfWeek.SUNDAY), 0);
        assertEquals(days.positionOf(DaysOfWeek.MONDAY), 1);
        assertEquals(days.positionOf(DaysOfWeek.TUESDAY), 2);
        assertEquals(days.positionOf(DaysOfWeek.WEDNESDAY), 3);
        assertEquals(days.positionOf(DaysOfWeek.THURSDAY), 4);
        assertEquals(days.positionOf(DaysOfWeek.FRIDAY), 5);
        assertEquals(days.positionOf(DaysOfWeek.SATURDAY), 6);
    }

    @Test
    public void testSaturdayAsFirstDayOfWeek() {
        DaysOfWeek days = new DaysOfWeek(DaysOfWeek.SATURDAY);
        Assert.assertEquals(days.weekDayAt(0), DaysOfWeek.SATURDAY);
        Assert.assertEquals(days.weekDayAt(1), DaysOfWeek.SUNDAY);
        Assert.assertEquals(days.weekDayAt(2), DaysOfWeek.MONDAY);
        Assert.assertEquals(days.weekDayAt(3), DaysOfWeek.TUESDAY);
        Assert.assertEquals(days.weekDayAt(4), DaysOfWeek.WEDNESDAY);
        Assert.assertEquals(days.weekDayAt(5), DaysOfWeek.THURSDAY);
        Assert.assertEquals(days.weekDayAt(6), DaysOfWeek.FRIDAY);

        assertEquals(days.positionOf(DaysOfWeek.SUNDAY), 1);
        assertEquals(days.positionOf(DaysOfWeek.MONDAY), 2);
        assertEquals(days.positionOf(DaysOfWeek.TUESDAY), 3);
        assertEquals(days.positionOf(DaysOfWeek.WEDNESDAY), 4);
        assertEquals(days.positionOf(DaysOfWeek.THURSDAY), 5);
        assertEquals(days.positionOf(DaysOfWeek.FRIDAY), 6);
        assertEquals(days.positionOf(DaysOfWeek.SATURDAY), 0);
    }

    @Test
    public void testMondayAsFirstDayOfWeek() {
        DaysOfWeek days = new DaysOfWeek(DaysOfWeek.MONDAY);
        Assert.assertEquals(days.weekDayAt(0), DaysOfWeek.MONDAY);
        Assert.assertEquals(days.weekDayAt(1), DaysOfWeek.TUESDAY);
        Assert.assertEquals(days.weekDayAt(2), DaysOfWeek.WEDNESDAY);
        Assert.assertEquals(days.weekDayAt(3), DaysOfWeek.THURSDAY);
        Assert.assertEquals(days.weekDayAt(4), DaysOfWeek.FRIDAY);
        Assert.assertEquals(days.weekDayAt(5), DaysOfWeek.SATURDAY);
        Assert.assertEquals(days.weekDayAt(6), DaysOfWeek.SUNDAY);

        assertEquals(days.positionOf(DaysOfWeek.SUNDAY), 6);
        assertEquals(days.positionOf(DaysOfWeek.MONDAY), 0);
        assertEquals(days.positionOf(DaysOfWeek.TUESDAY), 1);
        assertEquals(days.positionOf(DaysOfWeek.WEDNESDAY), 2);
        assertEquals(days.positionOf(DaysOfWeek.THURSDAY), 3);
        assertEquals(days.positionOf(DaysOfWeek.FRIDAY), 4);
        assertEquals(days.positionOf(DaysOfWeek.SATURDAY), 5);
    }
}
