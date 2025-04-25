This Spring Boot application was developed for the **Bajaj Finserv Health – SRM Programming Challenge (April 2025)**.  
It solves **Question 2: Nth-Level Followers** by implementing an automated data registration, processing, and result submission flow.
Given:
- A user registration endpoint (`/generateWebhook`)
- A list of users with follower relationships
- An integer `n` and a `findId`
**Objective**: Compute all users who are exactly `n` levels deep in the follower graph from the user with ID `findId`.

---

## 🔧 Functionality Overview

- Registers user details on `/generateWebhook`
- Extracts:
  - `n` (level)
  - `findId` (start user)
  - `users` (follower graph)
- Performs **BFS traversal** to compute Nth-level followers
- Sends output to `webhook` with required `Authorization` token
- Implements a **retry mechanism** for robustness (up to 4 attempts)


## 🚀 How to Run

After building, execute the application:

```bash
java -jar target/followerfinder-0.0.1-SNAPSHOT.jar
```

You will see output like:

```
📌 Using n = 2, findId = 1
📤 Sending webhook to: https://.../testWebhook
📄 Payload: { "regNo": "REG172", "output": { "followers": [4] } }
✅ Webhook sent successfully
```

---

## 📦 Output Format

The final result is posted to the provided `webhook` as:

```json
{
  "regNo": "REG172",
  "output": {
    "followers": [4]
  }
}
```

With the access token set in the header:

```http
Authorization: Bearer <accessToken>
```

---

## 📝 Submission Details

- **Name**: Sana Lokesh Reddy  
- **Registration No.**: REG172  
- **Challenge**: Bajaj Finserv Health – SRM Coding Challenge (April 2025)  
- **Question Solved**: Q2 - Nth-Level Followers  
- **GitHub Repo**: *(Insert GitHub Link Here)*  
- **Raw JAR Link**: *(Insert direct link to JAR file here)*  

---

## 📃 License

This project is submitted as part of an official coding evaluation. Redistribution or reuse is subject to contest rules and author's discretion.

---

_Developed with 💻 by Sana Lokesh Reddy (REG172)_
