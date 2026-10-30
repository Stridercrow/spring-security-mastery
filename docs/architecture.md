# Architecture

Spring Security is primarily a filter-chain based security framework. Authentication establishes an `Authentication`; authorization evaluates that authentication against request/method rules.

```text
HTTP -> SecurityFilterChain -> Authentication -> SecurityContext -> Authorization -> Controller
```
