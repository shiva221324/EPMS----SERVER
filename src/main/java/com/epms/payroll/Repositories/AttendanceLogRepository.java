//package com.epms.payroll.Repositories;
//
//import com.epms.payroll.Entities.AttendanceLog;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Repository
//public interface AttendanceLogRepository extends JpaRepository<AttendanceLog, Long> {
//
//    List<AttendanceLog> findByEmployee_EmployeeId(Long employeeId);
//
//    List<AttendanceLog> findByEmployee_EmployeeIdAndAttendanceDateBetween(
//            Long employeeId, LocalDate startDate, LocalDate endDate);
//
//    @Query("SELECT a FROM AttendanceLog a WHERE a.employee.employeeId = :employeeId AND YEAR(a.attendanceDate) = :year")
//    List<AttendanceLog> findByEmployee_EmployeeIdAndAttendanceDateYear(
//            @Param("employeeId") Long employeeId, @Param("year") int year);
//
//    // New: Find by employee and attendance status
//    List<AttendanceLog> findByEmployee_EmployeeIdAndAttendanceStatus(
//            Long employeeId, String attendanceStatus);
//
//    // New: Find by employee and shift type
//    List<AttendanceLog> findByEmployee_EmployeeIdAndShiftType(
//            Long employeeId, String shiftType);
//
//    // New: Find by employee and work location
//    List<AttendanceLog> findByEmployee_EmployeeIdAndWorkLocation(
//            Long employeeId, String workLocation);
//}