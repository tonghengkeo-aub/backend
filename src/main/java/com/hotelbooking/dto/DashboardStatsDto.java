package com.hotelbooking.dto;

import java.math.BigDecimal;

public class DashboardStatsDto {
    private long totalHotels;
    private long activeHotels;
    private long pendingHotels;
    private long totalRooms;
    private long totalBookings;
    private long confirmedBookings;
    private long pendingBookings;
    private long totalUsers;
    private long totalCustomers;
    private BigDecimal totalRevenueUsd;
    private BigDecimal totalRevenueKhr;
    private BigDecimal totalCommissionUsd;

    // Rule 6: Stats for revenue today/week/month, unpaid balance, occupancy
    private BigDecimal revenueToday = BigDecimal.ZERO;
    private BigDecimal revenueThisWeek = BigDecimal.ZERO;
    private BigDecimal revenueThisMonth = BigDecimal.ZERO;
    private BigDecimal unpaidBalance = BigDecimal.ZERO;
    private double occupancyRate = 0.0;

    public DashboardStatsDto() {
        this.totalRevenueUsd = BigDecimal.ZERO;
        this.totalRevenueKhr = BigDecimal.ZERO;
        this.totalCommissionUsd = BigDecimal.ZERO;
    }

    public long getTotalHotels() { return totalHotels; }
    public void setTotalHotels(long totalHotels) { this.totalHotels = totalHotels; }

    public long getActiveHotels() { return activeHotels; }
    public void setActiveHotels(long activeHotels) { this.activeHotels = activeHotels; }

    public long getPendingHotels() { return pendingHotels; }
    public void setPendingHotels(long pendingHotels) { this.pendingHotels = pendingHotels; }

    public long getTotalRooms() { return totalRooms; }
    public void setTotalRooms(long totalRooms) { this.totalRooms = totalRooms; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public long getConfirmedBookings() { return confirmedBookings; }
    public void setConfirmedBookings(long confirmedBookings) { this.confirmedBookings = confirmedBookings; }

    public long getPendingBookings() { return pendingBookings; }
    public void setPendingBookings(long pendingBookings) { this.pendingBookings = pendingBookings; }

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public BigDecimal getTotalRevenueUsd() { return totalRevenueUsd; }
    public void setTotalRevenueUsd(BigDecimal totalRevenueUsd) { this.totalRevenueUsd = totalRevenueUsd; }

    public BigDecimal getTotalRevenueKhr() { return totalRevenueKhr; }
    public void setTotalRevenueKhr(BigDecimal totalRevenueKhr) { this.totalRevenueKhr = totalRevenueKhr; }

    public BigDecimal getTotalCommissionUsd() { return totalCommissionUsd; }
    public void setTotalCommissionUsd(BigDecimal totalCommissionUsd) { this.totalCommissionUsd = totalCommissionUsd; }

    public BigDecimal getRevenueToday() { return revenueToday != null ? revenueToday : BigDecimal.ZERO; }
    public void setRevenueToday(BigDecimal revenueToday) { this.revenueToday = revenueToday; }

    public BigDecimal getRevenueThisWeek() { return revenueThisWeek != null ? revenueThisWeek : BigDecimal.ZERO; }
    public void setRevenueThisWeek(BigDecimal revenueThisWeek) { this.revenueThisWeek = revenueThisWeek; }

    public BigDecimal getRevenueThisMonth() { return revenueThisMonth != null ? revenueThisMonth : BigDecimal.ZERO; }
    public void setRevenueThisMonth(BigDecimal revenueThisMonth) { this.revenueThisMonth = revenueThisMonth; }

    public BigDecimal getUnpaidBalance() { return unpaidBalance != null ? unpaidBalance : BigDecimal.ZERO; }
    public void setUnpaidBalance(BigDecimal unpaidBalance) { this.unpaidBalance = unpaidBalance; }

    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }
}
