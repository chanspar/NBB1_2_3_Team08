package edu.example.learner_kotlin.courseabout.order.service

import edu.example.learner_kotlin.courseabout.order.dto.OrderCreateDTO
import edu.example.learner_kotlin.courseabout.order.dto.OrderDTO


interface OrderService {
    fun add(orderDTO: OrderCreateDTO, memberId: Long): OrderCreateDTO
    fun read(orderId: Long): OrderDTO?
    fun update(orderDTO: OrderDTO, orderId: Long): OrderDTO

    fun delete(courseId: Long)
    fun readAll(): List<OrderDTO>
    fun getOrdersById(memberId: Long): List<OrderDTO>
    fun deleteAll()
    fun purchaseOrderItems(orderId: Long, memberId: Long): OrderDTO;
}
