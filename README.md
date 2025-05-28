# 📄 Resume Processing Pipeline – Serverless Java AWS Project

Welcome! This project demonstrates a **serverless resume processing pipeline** built with **Spring Boot** and **AWS services** (S3, Lambda, SQS). It’s perfect for showcasing cloud architecture skills and hands-on experience with event-driven design.

---

## 🚀 What It Does

1. ✅ **Upload resumes to S3**
2. ⚡ **Lambda gets triggered** on upload
3. 📥 **Message sent to SQS** for further processing
4. 🧰 **Designed for scalability and loose coupling**



## 🧱 Tech Stack

| Layer            | Technology        |
|------------------|-------------------|
| Backend          | Java, Spring Boot |
| Storage          | AWS S3            |
| Event Processing | AWS Lambda        |
| Messaging        | AWS SQS           |
| Local Testing    | LocalStack        |


## 🧩 Architecture Diagram

```mermaid
graph TD
    A[Resume Upload to S3] --> B[Lambda Triggered]
    B --> C[S3 Object Info Extracted]
    C --> D[SQS Message Queued]
