Sample Practice Project: Online Food Order
Processing System
1. Problem Statement
You are tasked with building the backend and a simple frontend for an Online Food Ordering
System. The application needs to handle a high volume of orders asynchronously to ensure the
system doesn't crash during peak meal times.
When a user places an order, the system should not process the entire payment and kitchen
assignment synchronously. Instead, it should initiate a Camunda Workflow that orchestrates
the order lifecycle, using ActiveMQ to pass messages between distinct microservices.
Functional Requirements
- A customer can place an order (customer name, item, amount) via a React UI.
- Each order goes through a lifecycle: Placed → Payment → Kitchen Preparation →
Delivery → Delivered (or Cancelled on payment failure).
- The order lifecycle is orchestrated by a Camunda BPMN workflow.
- Inter-service communication for each lifecycle step happens asynchronously via
ActiveMQ queues.
- The customer can view real-time order status on the UI dashboard.
Non-Functional Requirements
- Each lifecycle step must log the order ID, step name, and outcome to the console (e.g.,
[OrderService] Order #123 - Workflow started, [PaymentService]
Order #123 - Payment SUCCESS).
- The system should handle payment failures gracefully by cancelling the order in the
workflow.
Your Goal: Implement this system from scratch using the Antigravity IDE. You must use AI to
write 100% of the code (vibe coding).

2. Architecture Overview
The system consists of a React Frontend and several Spring Boot microservices communicating
via ActiveMQ and orchestrated by Camunda.

graph TD
UI[React Frontend UI]

subgraph Backend
OS[Order Service]
MQ[[ActiveMQ Broker]]
CAM[Camunda Workflow Engine]
PS[Payment Service]
KS[Kitchen Service]
DS[Delivery Service]
end

DB[(MySQL Database)]

UI -->|REST: Create Order| OS
OS -->|Persist Order| DB
OS -->|Publish Order Event| MQ
MQ -->|Consume & Start Workflow| CAM

CAM -->|Step 1: Call| PS
PS -->|Save & Return Result| DB
PS -->|Payment Result| CAM

CAM -->|Step 2: Call| KS
KS -->|Save & Return Result| DB
KS -->|Kitchen Result| CAM

CAM -->|Step 3: Call| DS
DS -->|Save & Return Result| DB
DS -->|Delivery Result| CAM

CAM -->|Update Final Status| OS
OS -->|Update Order Status| DB

3. Camunda BPMN Workflow
The Order Service embeds the Camunda workflow engine. The BPMN process should look like
this logically:
stateDiagram-v2
[*] --> OrderPlaced
OrderPlaced --> PaymentProcessing : Publish to Payment Queue
PaymentProcessing --> KitchenPrep : Payment Success
PaymentProcessing --> OrderCancelled : Payment Failed
KitchenPrep --> OutForDelivery : Food Ready
OutForDelivery --> Delivered : Driver Drops Off
Delivered --> [*]

OrderCancelled --> [*]

4. Database Design Expectations
You will need a MySQL database to store the state of the orders, payments, kitchen tickets, and
deliveries.
Instruction: We have purposefully omitted the Database Schema and Low-Level Design (LLD).
You must use the AI (Antigravity IDE tool) to design the database schema, tables, relationships,
and entities based on the problem statement and the architectural flow. You are expected to
prompt the AI to generate the appropriate SQL schemas or JPA entities for you.

5. Required Implementation, API List, and Sequence
You are expected to use Antigravity IDE to generate the following systems without any provided
API contracts. You must design the request/response payloads yourself.
A. End-to-End Sequence Diagram
The following diagram illustrates the complete order lifecycle. The Order Service publishes an
order event to ActiveMQ. Camunda consumes it, starts the workflow, and orchestrates each
step by calling the respective microservice.
sequenceDiagram
actor Customer
participant ReactUI as React UI
participant OrderSvc as Order Service
participant MQ as ActiveMQ
participant Camunda as Camunda Workflow Engine
participant PaymentSvc as Payment Service
participant KitchenSvc as Kitchen Service

participant DeliverySvc as Delivery Service
Customer->>ReactUI: Enters order details
ReactUI->>OrderSvc: POST /api/orders (Create Order)
OrderSvc->>OrderSvc: Save Order to DB (Status: PLACED)
OrderSvc->>OrderSvc: Log: Order #id - PLACED
OrderSvc->>MQ: Publish order event to 'order.created' queue
OrderSvc-->>ReactUI: Return Order ID (HTTP 201)
MQ->>Camunda: Consume 'order.created', start workflow
rect rgb(240, 248, 255)
Note over Camunda, PaymentSvc: Workflow Step 1: Payment Processing
Camunda->>PaymentSvc: Call Payment Service (Service Task)
PaymentSvc->>PaymentSvc: Process Payment (mock success/fail)
PaymentSvc->>PaymentSvc: Save Payment record to DB
PaymentSvc->>PaymentSvc: Log: Order #id - Payment SUCCESS/FAILED
PaymentSvc-->>Camunda: Return payment result
end
alt Payment Success
rect rgb(240, 255, 240)
Note over Camunda, KitchenSvc: Workflow Step 2: Kitchen Preparation
Camunda->>KitchenSvc: Call Kitchen Service (Service Task)
KitchenSvc->>KitchenSvc: Prepare food, save ticket to DB
KitchenSvc->>KitchenSvc: Log: Order #id - Food READY

KitchenSvc-->>Camunda: Return kitchen result
end
rect rgb(255, 248, 240)
Note over Camunda, DeliverySvc: Workflow Step 3: Delivery
Camunda->>DeliverySvc: Call Delivery Service (Service Task)
DeliverySvc->>DeliverySvc: Assign driver, save delivery to DB
DeliverySvc->>DeliverySvc: Log: Order #id - DELIVERED
DeliverySvc-->>Camunda: Return delivery result
end
Camunda->>OrderSvc: Update order status to DELIVERED
OrderSvc->>OrderSvc: Log: Order #id - Workflow COMPLETE
else Payment Failed
Camunda->>OrderSvc: Update order status to CANCELLED
OrderSvc->>OrderSvc: Log: Order #id - CANCELLED
end
Note over ReactUI, OrderSvc: UI continuously polls for status updates
ReactUI->>OrderSvc: GET /api/orders
OrderSvc-->>ReactUI: Return all orders with current statuses
B. List of Required APIs & Microservices
1. Order Service (REST — Entry Point):
- POST /api/orders: Accepts customer name, item, and amount. Saves order
to DB with status PLACED. Publishes an order event to ActiveMQ
(order.created queue).
- GET /api/orders: Returns a list of all orders and their real-time statuses
(used for the UI dashboard).

- GET /api/orders/{id}: Returns detailed status of a single order.
2. Payment Service (Called by Camunda as a Service Task):
- Invoked by Camunda during workflow Step 1.
- Mocks a success/fail calculation, saves payment record to DB.
- Returns payment result to Camunda.
3. Kitchen Service (Called by Camunda as a Service Task):
- Invoked by Camunda during workflow Step 2 (only if payment succeeded).
- Simulates food preparation, saves kitchen ticket to DB.
- Returns kitchen result to Camunda.
4. Delivery Service (Called by Camunda as a Service Task):
- Invoked by Camunda during workflow Step 3.
- Assigns a mock driver, saves delivery record to DB.
- Returns delivery result to Camunda.

C. ActiveMQ Queue
Queue Name Publisher Consumer Purpose
order.created Order Service Camunda Workflow

Engine

Triggers the start of
the order workflow
Note: The Payment, Kitchen, and Delivery services are called directly by Camunda
as service tasks within the BPMN workflow. ActiveMQ is used to decouple the
Order Service from the Camunda Workflow Engine, enabling asynchronous
initiation of the order processing pipeline.
D. The Frontend (React)
1. A simple form to enter a customer name, item, and amount, with a "Place Order" button.
2. A dashboard to view the real-time status of all orders (polling the Order Service GET
/api/orders endpoint every 2 seconds).

6. Vibe Coding Expectations & Project Setup
1. Set Up From Scratch in One Workspace: We do not provide starter templates. You
should set up Camunda, ActiveMQ, Database, Java server application, Microservices,
and the React Application in the same workspace from scratch. You must use the AI to
generate the Spring Boot pom.xml files, React package.json, Camunda dependencies,
etc.
2. Tool Restrictions: Do not use external AI agents. You must use the internal AI chat
bot and the artifactory/brain document format within Antigravity IDE to achieve the entire
implementation.

3. Prompt Engineering & Tool Usage: Break the problem down. Don't ask the AI to "build
the whole app" in one prompt. Ask it to set up the Spring Boot shell first, then the
Database connection, then ActiveMQ, etc. Effective prompting and tool usage can help
you build the entire application in quick time.
4. Debugging: If ActiveMQ fails to connect, or Camunda throws an execution exception,
practice feeding the error logs back to the AI to resolve the issue. You should overcome
all challenges using AI.
5. In-Person Assessment Expectation: You will be given a similar assignment at the
office during the in-person interview. You will be asked to do a similar setup and
implement a completely different application in 2 hours' time.

7. Submission Deliverables (Mandatory)
After completing your practice, you must prepare and bring the following deliverables to your
interview. These prove that you practiced at home and help us understand your approach.
Deliverable 1: API Low-Level Design (LLD)
Use the AI to generate a document that describes the API contracts you designed, including:
- Request/response payloads for each REST endpoint.
- Queue message formats for each ActiveMQ queue.
- Error handling and edge cases.
Deliverable 2: Database Design
Use the AI to generate a document that describes the database schema you designed,
including:
- Table names, columns, data types, primary keys, and foreign key relationships.
- An ER diagram (Mermaid or any format) showing the table relationships.
Deliverable 3: Frontend Screenshots
Capture screenshots of your working React application showing:
- The order placement form.
- The order dashboard displaying real-time status updates as an order progresses through
the workflow.

Deliverable 4: Log Statements (Order Processing Flow)
Capture console/terminal log output that demonstrates the end-to-end flow of at least one order
being processed. The logs should clearly show the order moving through each workflow step,
for example:
[OrderService] Order #1 - Status: PLACED, Workflow started
[PaymentService] Order #1 - Payment processing... SUCCESS
[KitchenService] Order #1 - Kitchen ticket created, preparing food... READY
[DeliveryService] Order #1 - Driver assigned, delivering... DELIVERED
[OrderService] Order #1 - Workflow COMPLETE
Deliverable 5: AI-Generated Implementation Report
Use the prompt below in your Antigravity IDE to auto-generate an honest assessment of your
implementation. Do not edit this report manually — it should reflect the actual state of your
workspace.
PROMPT TO COPY & PASTE INTO IDE:
Please analyze my entire workspace and generate a comprehensive
"Implementation Report" based on the requirements of the Online Food Order
Processing System. Format the output in Markdown.
Structure the report as follows:
1. Executive Summary: A brief paragraph on what was achieved.
2. Completed Items: A checklist of microservices, APIs, Camunda workflows,
ActiveMQ queues, Database tables, and React components that are fully
implemented and working.
3. Missing Implementations: A list of components or requirements from the
architecture that are not yet built or are incomplete.
4. Integration Gaps & Issues: Describe any broken integrations (e.g.,
ActiveMQ failing to send messages, Camunda state not updating, React UI
unable to fetch from backend). Provide technical specifics based on the
codebase state.
5. Quality Assessment: Briefly evaluate the AI-generated code quality
(modularity, error handling, configuration separation).

8. Summary Checklist
Before your interview, verify that you have completed the following:
# Item Status
1 Project set up from scratch in
one workspace (no starter
templates)

☐

2 All microservices created
(Order, Payment, Kitchen,
Delivery)

☐

3 Camunda BPMN workflow
implemented and
orchestrating the order
lifecycle

☐

4 ActiveMQ queues configured
and services communicating
asynchronously

☐

5 MySQL database designed

and integrated

☐

6 React UI with order form and

status dashboard

☐

7 Log statements printing the
order processing flow

☐

8 Submission: API LLD

document

☐

9 Submission: Database
Design document

☐

10 Submission: Frontend

screenshots

☐

11 Submission: Log output of

order flow

☐

12 Submission: AI-generated
Implementation Report

☐

Good luck with your practice! Be prepared to build a similar architecture (but for a
completely different business domain) during your in-person Round 2 assessment.