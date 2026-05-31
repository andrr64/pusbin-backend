# test_gen.md — iam

## CONTROLLER TESTS

### POST /api/v1/iam/register
| Case | Input | Expected |
|------|-------|----------|
| Happy path | `{ nip: "198001012010011001", password: "rahasia123" }` | 200, `success: true` |
| Missing nip | `{ password: "rahasia123" }` | 400 |
| Missing password | `{ nip: "198001012010011001" }` | 400 |
| Blank nip | `{ nip: "", password: "rahasia123" }` | 400 |
| Duplicate NIP | NIP that already exists | 409 |
| Rate limit exceeded | 4th request from same IP in 60 s | 429 |

### POST /api/v1/iam/login
| Case | Input | Expected |
|------|-------|----------|
| Happy path | Valid nip + correct password | 200, cookies `access_token` and `refresh_token` set |
| NIP not found | Unknown nip | 401, no cookies set |
| Wrong password | Correct nip + wrong password | 401, no cookies set |
| Missing nip | `{ password: "rahasia123" }` | 400 |
| Missing password | `{ nip: "198001012010011001" }` | 400 |
| Rate limit exceeded | 6th request from same IP in 60 s | 429 |

### POST /api/v1/iam/refresh
| Case | Input | Expected |
|------|-------|----------|
| Happy path | Valid `refresh_token` cookie | 200, new `access_token` and `refresh_token` cookies set |
| Missing cookie | No `refresh_token` cookie | 401 |
| Expired token | Token hash not found / past `expires_at` | 401 |
| Invalid/tampered token | Hash not in DB | 401 |

### POST /api/v1/iam/logout
| Case | Input | Expected |
|------|-------|----------|
| Happy path | Valid `access_token` cookie (authenticated) | 200, both cookies cleared (MaxAge=0) |
| No auth | Missing or invalid `access_token` cookie | 401 |

---

## SERVICE TESTS

### register()
| Case | Input | Expected |
|------|-------|----------|
| Happy path | New NIP, valid password | User saved with Argon2 hashed password |
| Duplicate NIP | Existing NIP | Throws `DuplicateResourceException` (409) |
| Rate limit exceeded | IP over limit | Throws `AppException` (429) before DB access |

### login()
| Case | Input | Expected |
|------|-------|----------|
| Happy path | Valid nip + correct password | Refresh token saved in DB, cookies set on response |
| NIP not found | Unknown NIP | Throws `UnauthorizedException` |
| Wrong password | Correct NIP, wrong password | Throws `UnauthorizedException` |
| Rate limit exceeded | IP over limit | Throws `AppException` (429) before DB access |

### refresh()
| Case | Input | Expected |
|------|-------|----------|
| Happy path | Valid raw refresh token | Old token deleted, new tokens saved, new cookies set |
| Null token | `null` refreshToken arg | Throws `UnauthorizedException` |
| Blank token | `""` refreshToken arg | Throws `UnauthorizedException` |
| Hash not in DB | Non-matching token | Throws `UnauthorizedException` |

### logout()
| Case | Input | Expected |
|------|-------|----------|
| Happy path | Any response object | Both cookies written with MaxAge=0 |

---

## REPOSITORY TESTS

### findByNip()
| Case | Input | Expected |
|------|-------|----------|
| Found | Existing NIP | `Optional` containing `UserRecord` |
| Not found | Unknown NIP | `Optional.empty()` |

### existsByNip()
| Case | Input | Expected |
|------|-------|----------|
| Exists | Existing NIP | `true` |
| Does not exist | Unknown NIP | `false` |

### saveUser()
| Case | Input | Expected |
|------|-------|----------|
| Happy path | New NIP + hashed password | Row inserted, no exception |
| Duplicate NIP | Existing NIP | `DataIntegrityViolationException` |

### saveRefreshToken()
| Case | Input | Expected |
|------|-------|----------|
| Happy path | Valid userId, hash, expiresAt | Row inserted in `refresh_tokens` |

### findNipByRefreshToken()
| Case | Input | Expected |
|------|-------|----------|
| Found and not expired | Valid hash, `expires_at` in future | `Optional` with NIP string |
| Not found | Hash not in DB | `Optional.empty()` |
| Expired | Valid hash, `expires_at` in past | `Optional.empty()` |

### deleteRefreshToken()
| Case | Input | Expected |
|------|-------|----------|
| Exists | Valid hash | Row deleted |
| Does not exist | Unknown hash | No-op, no exception |

---

## NOTES

- Mock `RateLimiter` in service tests so rate limit logic is tested separately.
- Mock `IamRepository` in service tests; mock `IamService` in controller tests.
- Controller tests must assert `Set-Cookie` headers on login/refresh responses.
- Controller tests must assert `Set-Cookie: access_token=; Max-Age=0` on logout.
- Seed the `users` and `refresh_tokens` tables before repository integration tests.
- Argon2 encoding is slow — use a weak config (e.g. 1 iteration) in test scope.
- Rate limiter state is in-memory (`ConcurrentHashMap`) — restart or mock between tests.
