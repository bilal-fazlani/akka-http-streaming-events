# Sample Akka HTTP Server demostrating event streaming of CRUD events

- Starts an Akka HTTP Server for an "entity"
- Exposes following endpoints on http:localhost:9090


| METHOD | URL                 | DESCRIPTION                                                              |
|--------|---------------------|--------------------------------------------------------------------------|
| GET    | /entities           | Get all entities                                                         |
| GET    | /entities/subscribe | SSE Subscription of all CRUD events.  First event will be current state. |
| POST   | /entities           | Add new entity                                                           |
| DELETE | /entities/\<ID>     | delete an entity                                                         |
| DELETE | /entities           | delete all entities                                                      |


- "sub run"
