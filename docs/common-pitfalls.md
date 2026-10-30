# Common Pitfalls

## 401 vs 403
401 generally means authentication is missing/invalid. 403 means the request was understood but access was denied.

## Stateless does not mean secure by itself
Statelessness changes authentication state management; it does not replace token validation, authorization or transport security.
