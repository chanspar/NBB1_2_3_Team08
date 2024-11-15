package edu.example.learner_kotlin.courseabout.course.service

import edu.example.learner_kotlin.courseabout.course.dto.CourseCreateDTO
import edu.example.learner_kotlin.courseabout.course.dto.CourseDTO
import edu.example.learner_kotlin.courseabout.course.dto.CourseUpdateDTO
import edu.example.learner_kotlin.courseabout.course.dto.MemberCourseDTO
import edu.example.learner_kotlin.courseabout.course.entity.CourseAttribute
import org.springframework.stereotype.Service


interface CourseService {
    fun addCourse(dto: CourseCreateDTO): CourseCreateDTO
    fun read(courseId: Long): CourseDTO
    fun readByAttribute(courseAttribute: CourseAttribute): List<CourseDTO?>
    fun readReview(courseId: Long): CourseDTO
    fun updateCourse(dto: CourseUpdateDTO): CourseUpdateDTO
    fun deleteCourse(courseId: Long)
    fun readAll(): List<CourseDTO>
    fun getCoursesByNickname(nickname: String): List<CourseDTO>
    fun getMemberCoursesByMemberId(memberId: Long): List<MemberCourseDTO>
    fun getCoursesByMemberId(memberId: Long): List<CourseDTO>
}
