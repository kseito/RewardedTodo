# RewardedTodo

## Project Overview
RewardedTodo is an Android application that manages rewards and points using Todoist. It follows Clean Architecture with a multi-module structure, using MVVM for the presentation layer.

This is a side project I use to experiment new libraries, framework features and so on.

## Project Structure
The project is organized into the following modules:

- **app**: Main application module
- **feature**: Feature modules (reward, todo, setting)
- **domain**: Domain layer modules (reward, todo)
- **data**: Data layer modules (reward, todo, ticket, todoist)
- **application**: Application layer modules (reward, todo)
- **common**: Common utilities (database, kvs, ui)
- **test**: Test modules

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design
- **Architecture**: Clean Architecture + MVVM
- **Dependency Injection**: Dagger Hilt
- **Database**: Room
- **Networking**: Retrofit with Moshi
- **Asynchronous Programming**: Coroutines
- **Navigation**: Jetpack Navigation Compose

## Building the Project
- Use Android Studio to open the project
- Build with Gradle: `./gradlew assembleDebug`
- Install on device: `./gradlew installDebug`

## Running Tests
- Run unit tests: `./gradlew testDebugUnitTest`
- Run specific module tests: `./gradlew :module:testDebugUnitTest`
- Run lint checks: `./gradlew lintDebug`

## Static Analysis
- Format Kotlin code: `./gradlew spotlessKotlinApply`
- Check code style: `./gradlew spotlessCheck`
- Run detekt: `./gradlew detekt`

## Development Workflow
1. Create a feature branch from main: `feature/your-feature-name`
2. Implement your changes following the architecture patterns
3. Write tests for your changes
4. Run tests and lint checks locally
5. Submit a pull request

## Documentation
Detailed documentation is available in the `docs/` directory:

- [`docs/domain-model.md`](docs/domain-model.md) - Domain model and business rules
- [`docs/how-to-add-new-feature.md`](docs/how-to-add-new-feature.md) - Step-by-step guide for adding new use cases
- [`docs/module-dependency.md`](docs/module-dependency.md) - Module dependencies and forbidden rules
- [`docs/di-setup.md`](docs/di-setup.md) - Hilt DI structure and file layout
- [`docs/review-policy.md`](docs/review-policy.md) - Review policy
- [`docs/adr/`](docs/adr) - Architecture Decision Records
- [`docs/specs/`](docs/specs) - Feature specifications

## Best Practices
- Follow the existing architecture pattern (Clean Architecture + MVVM)
- Keep modules focused on their specific responsibilities
- Write unit tests for new functionality
- Use Compose previews for UI components
- Follow Kotlin coding conventions
- Use dependency injection with Hilt
- Keep feature modules independent and reusable

