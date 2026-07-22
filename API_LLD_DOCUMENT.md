# API Low-Level Design (LLD) Document
**Project:** Online Food Ordering & Microservices Processing System  
**Deliverable:** 1 of 5 (Submission Deliverables)  
**Date:** July 22, 2026  

---

## 1. Executive Summary

This document details the Low-Level Design (LLD) for the APIs, inter-service HTTP endpoints, and message queues in the Online Food Ordering System. The application consists of four microservices:
1. **Order Service** (`:8080`) – Core REST entry-point and host of the Camunda Workflow Engine.
2. **Payment Service** (`:8081`) – Internal service invoked during workflow Step 1 to process payments.
3. **Kitchen Service** (`:8082`) – Internal service invoked during workflow Step 2 to prepare food tickets.
4. **Delivery Service** (`:8083`) – Internal service invoked during workflow Step 3 to assign driver and deliver orders.

Communication occurs synchronously via REST HTTP between Camunda service delegates and microservices, and asynchronously via Apache ActiveMQ (`order.created` queue) between Order Service and Camunda engine.

---

## 2. REST API Specifications

### 2.1 Order Service APIs (`:8080`)

#### A. `POST /api/orders`
* **Description:** Accepts customer order details, persists order in DB with status `PLACED`, and triggers asynchronous workflow processing via ActiveMQ queue.
* **Request Headers:**
  * `Content-Type: application/json`
* **Request Body (`OrderRequest`):**
```json
{
  "customerName": "John Doe",
  "item": "Margherita Pizza",
  "amount": 14.99
}
```
* **Validation Rules:**
  * `customerName`: `@NotBlank`
  * `item`: `@NotBlank`
  * `amount`: `@NotNull`, `@Positive`
* **Response Body (`OrderResponse` — `HTTP 201 Created`):**
```json
{
  "id": 101,
  "customerName": "John Doe",
  "item": "Margherita Pizza",
  "amount": 14.99,
  "status": "PLACED",
  "createdAt": "2026-07-22T12:00:00Z",
  "updatedAt": "2026-07-22T12:00:00Z"
}
```

#### B. `GET /api/orders`
* **Description:** Retrieves all orders and their real-time statuses. Used by React UI dashboard (polled every 2 seconds).
* **Response Body (`List<OrderResponse>` — `HTTP 200 OK`):**
```json
[
  {
    "id": 101,
    "customerName": "John Doe",
    "item": "Margherita Pizza",
    "amount": 14.99,
    "status": "DELIVERED",
    "createdAt": "2026-07-22T12:00:00Z",
    "updatedAt": "2026-07-22T12:02:15Z"
  }
]
```

#### C. `GET /api/orders/{id}`
* **Description:** Fetches single order details by ID.
* **Response Code:** `HTTP 200 OK` or `HTTP 404 Not Found`.

#### D. `PUT /api/orders/{id}/status` *(Internal Workflow Update)*
* **Description:** Called by Camunda BPMN Java Delegates to update the status lifecycle of an order.
* **Request Body:**
```json
{
  "status": "PAYMENT_SUCCESS"
}
```
* **Status Enum Options:** `PLACED`, `PAYMENT_SUCCESS`, `PAYMENT_FAILED`, `KITCHEN_PREPARING`, `FOOD_READY`, `OUT_FOR_DELIVERY`, `DELIVERED`, `CANCELLED`.
* **Response Code:** `HTTP 200 OK`.

---

### 2.2 Payment Service API (`:8081`)

#### `POST /api/payments/process`
* **Description:** Called by Camunda `ProcessPaymentDelegate` during Step 1. Mocks payment processing (80% success rate), saves payment record to MySQL, and returns outcome to Camunda.
* **Request Body (`PaymentRequest`):**
```json
{
  "orderId": 101,
  "amount": 14.99
}
```
* **Response Body (`PaymentResponse` — `HTTP 200 OK`):**
```json
{
  "orderId": 101,
  "status": "SUCCESS",
  "message": "Payment success"
}
```
*(Or `"status": "FAILED"` on simulated payment failure)*.

---

### 2.3 Kitchen Service API (`:8082`)

#### `POST /api/kitchen/prepare`
* **Description:** Called by Camunda `PrepareKitchenDelegate` during Step 2 when payment succeeds. Prepares ticket and saves to DB.
* **Request Body (`KitchenRequest`):**
```json
{
  "orderId": 101,
  "item": "Margherita Pizza"
}
```
* **Response Body (`KitchenResponse` — `HTTP 200 OK`):**
```json
{
  "orderId": 101,
  "status": "READY"
}
```

---

### 2.4 Delivery Service API (`:8083`)

#### `POST /api/delivery/assign`
* **Description:** Called by Camunda `DeliverOrderDelegate` during Step 3. Assigns driver, records delivery entry, and completes delivery step.
* **Request Body (`DeliveryRequest`):**
```json
{
  "orderId": 101
}
```
* **Response Body (`DeliveryResponse` — `HTTP 200 OK`):**
```json
{
  "orderId": 101,
  "driverName": "Driver John Doe",
  "status": "DELIVERED"
}
```

---

## 3. ActiveMQ Messaging Specification

| Queue Name | Publisher | Consumer | Payload Format | Purpose |
|---|---|---|---|---|
| `order.created` | `OrderService` | `ActiveMQListener` (OrderService / Camunda Engine) | Plain text / String (`orderId` e.g. `"101"`) | Triggers the asynchronous start of the Camunda BPMN Order Processing Process (`order-process`) |

* **Asynchronous Flow:**
  1. `OrderController` receives `POST /api/orders`, persists order with `PLACED` status.
  2. `OrderController` calls `jmsTemplate.convertAndSend("order.created", orderId)`.
  3. `OrderMessageListener` consumes `orderId` from queue and executes `runtimeService.startProcessInstanceByKey("order-process", variables)`.

---

## 4. Error Handling & Edge Cases

1. **Validation Failures (`HTTP 400 Bad Request`):**
   * Handled by `@Valid` annotations and Global Exception Handler returns structured JSON response with field error descriptions.
2. **Resource Not Found (`HTTP 404 Not Found`):**
   * Returned when querying `/api/orders/{id}` for a non-existent ID.
3. **Payment Failure Handling:**
   * If `PaymentService` returns `FAILED`, Camunda Exclusive Gateway routes process flow to `Cancel Order` end event, setting Order status to `CANCELLED` and terminating workflow cleanly.
4. **Service Unavailability / Network Retries:**
   * Camunda service tasks throw `BpmnError` or HTTP exceptions to handle transient network issues cleanly during inter-service communication.
