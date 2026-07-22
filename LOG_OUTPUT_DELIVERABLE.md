# Log Output of Order Processing Flow
**Project:** Online Food Ordering & Microservices Processing System  
**Deliverable:** 4 of 5 (Submission Deliverables)  
**Date:** July 22, 2026  

---

## 1. Executive Summary

This deliverable captures console and terminal log outputs demonstrating the end-to-end processing flow of orders in the Online Food Ordering System. The logs prove the successful execution across all microservices (`OrderService`, `PaymentService`, `KitchenService`, `DeliveryService`), the Camunda BPMN Engine, and the ActiveMQ broker.

Two complete scenarios are logged:
1. **Successful Order Lifecycle:** `PLACED` → `PAYMENT SUCCESS` → `FOOD READY` → `DELIVERED` → `COMPLETE`.
2. **Payment Failure Scenario:** `PLACED` → `PAYMENT FAILED` → `CANCELLED` → `COMPLETE`.

---

## 2. End-to-End Log Output — Scenario A: Successful Order (#101)

```text
2026-07-22 12:50:01.102  INFO 12041 --- [order-service] [nio-8080-exec-1] c.w.o.controller.OrderController         : [OrderService] Order #101 - PLACED
2026-07-22 12:50:01.115  INFO 12041 --- [order-service] [nio-8080-exec-1] c.w.o.controller.OrderController         : [OrderService] Order #101 - Published to 'order.created' queue
2026-07-22 12:50:01.140  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.OrderEventListener       : [OrderService] Order #101 - Consumed from ActiveMQ, starting Camunda workflow
2026-07-22 12:50:01.178  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.PaymentDelegate           : [Camunda] Order #101: Calling Payment Service...
2026-07-22 12:50:01.210  INFO 14120 --- [payment-service] [nio-8081-exec-2] c.w.p.controller.PaymentController       : [PaymentService] Order #101 - Payment processing... SUCCESS
2026-07-22 12:50:01.235  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.PaymentDelegate           : [Camunda] Order #101: Payment Service returned SUCCESS
2026-07-22 12:50:01.245  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.UpdateStatusDelegate      : [OrderService] Order #101 - Status updated to PAYMENT_SUCCESS
2026-07-22 12:50:01.260  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.KitchenDelegate          : [Camunda] Order #101: Calling Kitchen Service...
2026-07-22 12:50:01.285  INFO 15022 --- [kitchen-service] [nio-8082-exec-1] c.w.k.controller.KitchenController       : [KitchenService] Order #101 - Kitchen ticket created, preparing food... READY
2026-07-22 12:50:01.300  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.KitchenDelegate          : [Camunda] Order #101: Kitchen Service food preparation complete
2026-07-22 12:50:01.310  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.UpdateStatusDelegate      : [OrderService] Order #101 - Status updated to FOOD_READY
2026-07-22 12:50:01.325  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.DeliveryDelegate         : [Camunda] Order #101: Calling Delivery Service...
2026-07-22 12:50:01.350  INFO 16301 --- [delivery-service] [nio-8083-exec-1] c.w.d.controller.DeliveryController     : [DeliveryService] Order #101 - Driver assigned, delivering... DELIVERED
2026-07-22 12:50:01.365  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.DeliveryDelegate         : [Camunda] Order #101: Delivery Service driver assigned
2026-07-22 12:50:01.375  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.UpdateStatusDelegate      : [OrderService] Order #101 - Status updated to DELIVERED
2026-07-22 12:50:01.380  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.UpdateStatusDelegate      : [OrderService] Order #101 - Workflow COMPLETE
```

---

## 3. End-to-End Log Output — Scenario B: Payment Failure & Order Cancellation (#102)

```text
2026-07-22 12:52:10.010  INFO 12041 --- [order-service] [nio-8080-exec-4] c.w.o.controller.OrderController         : [OrderService] Order #102 - PLACED
2026-07-22 12:52:10.022  INFO 12041 --- [order-service] [nio-8080-exec-4] c.w.o.controller.OrderController         : [OrderService] Order #102 - Published to 'order.created' queue
2026-07-22 12:52:10.045  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.OrderEventListener       : [OrderService] Order #102 - Consumed from ActiveMQ, starting Camunda workflow
2026-07-22 12:52:10.070  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.PaymentDelegate           : [Camunda] Order #102: Calling Payment Service...
2026-07-22 12:52:10.105  INFO 14120 --- [payment-service] [nio-8081-exec-5] c.w.p.controller.PaymentController       : [PaymentService] Order #102 - Payment processing... FAILED
2026-07-22 12:52:10.125  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.PaymentDelegate           : [Camunda] Order #102: Payment Service returned FAILED
2026-07-22 12:52:10.135  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.UpdateStatusDelegate      : [OrderService] Order #102 - Status updated to CANCELLED
2026-07-22 12:52:10.140  INFO 12041 --- [order-service] [ActiveMQ-Listener] c.w.o.delegate.UpdateStatusDelegate      : [OrderService] Order #102 - Workflow COMPLETE
```

---

## 4. Verification Summary

* **ActiveMQ Integration:** Asynchronous handoff from `order.created` queue to `OrderEventListener` confirmed.
* **Camunda BPMN Gateway Routing:** Exclusive gateway correctly branches to Kitchen/Delivery on `SUCCESS` and branches directly to `UpdateStatusDelegate` (`CANCELLED`) on `FAILED`.
* **Microservices Console Output:** All 4 microservices log exact order IDs and step outcomes as specified in Section 1 Non-Functional Requirements.
