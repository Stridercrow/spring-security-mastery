# FAQ

**Should I write my own JWT implementation?** Usually no. Learn it here to understand the mechanics, but use established OAuth2/OIDC infrastructure in production.

**Where should authorization live?** Coarse URL authorization belongs in the HTTP layer; sensitive business authorization should also be enforced at the service/method boundary.
