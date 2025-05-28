# Resume Processing Pipeline – Serverless Java AWS Project

Welcome! This project demonstrates a **serverless resume processing pipeline** built with **Spring Boot** and **AWS services** (S3, Lambda, SQS). It’s perfect for showcasing cloud architecture skills and hands-on experience with event-driven design.

---

## What It Does

1. **Upload resumes to S3**
2. **Lambda gets triggered** on upload
3. **Message sent to SQS** for further processing
4. **Designed for scalability and loose coupling**



## Tech Stack

| Layer            | Technology        |
|------------------|-------------------|
| Backend          | Java, Spring Boot |
| Storage          | AWS S3            |
| Event Processing | AWS Lambda        |
| Messaging        | AWS SQS           |
| Local Testing    | LocalStack        |


## Architecture Diagram

```mermaid
graph LR
    A[Resume Upload to S3] --> B[Lambda Triggered]
    B --> C[SQS Message Queued]
    C --> D[Spring Boot Consumer Service]
    D --> E[Download from S3]
    E --> F[Extract Name - Mock Behaviour]
    F --> G[Update Status in PostgreSQL]```

---
## 🔗 Live Demo (Deployed on AWS EC2)

The project is deployed on an EC2 instance and available for testing.

### 📤 Upload Resume (POST)

**URL**:  
[`http://13.200.165.194:8080/resume/upload`](http://13.200.165.194:8080/resume/upload)

**Method**:  
`POST`

**Form Parameters**:
- `file` → Resume file (PDF)
- `name` → Candidate's full name
- `job` → Job title being applied for
