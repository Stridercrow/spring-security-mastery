# Module 7: Microservices Security

## Target architecture

```text
             +----------------+
Client ----> | API Gateway    |
             +-------+--------+
                     |
              Bearer access token
                     |
        +------------+------------+
        |                         |
 +------v------+           +------v------+
 | Resource A  |           | Resource B  |
 +-------------+           +-------------+
        ^
        |
   Authorization
     Server / IdP
```

Practice:
- OAuth2 access tokens
- Gateway vs service authorization
- Audience validation
- Service-to-service credentials
- Token propagation
- Zero-trust boundaries
- Failure isolation
