# Health Manager Main

Simple fitness tracker where users can easily upload workouts, set goals and track overall progress.

# Development

Run with demo profile to have prefilled mocked data on database

- Swagger url is available at http://localhost:8081/swagger-ui/index.html
- H2 console url is available at http://localhost:8081/h2-console

## Usefull Plugins Commands

Jacoco

```
mvn jacoco:report
mvn jacoco:check
```

Check dependencies tree

```
mvn dependency:tree
```

Check used and declared; used and undeclared; unused and declared with:

```
mvn dependency:analyze-only
```

Run integration tests with

```
mvn failsafe:integration-test
```
