# RewardedTodo Developer Guidelines

## Project Overview
RewardedTodo is an Android application that manages rewards and points using Todoist. It follows MVVM architecture with a multi-module structure to separate concerns.

## Project Structure
The project is organized into the following modules:

- **app**: Main application module
- **feature**: Feature modules (reward, auth, todo, setting)
- **domain**: Domain layer modules (reward, todo)
- **data**: Data layer modules (reward, auth, todo, ticket, todoist)
- **application**: Application layer modules (reward, todo)
- **common**: Common utilities (database, kvs, ui)
- **test**: Test modules

## Tech Stack
- **Language**: Kotlin
- **UI**: Jetpack Compose with Material Design
- **Architecture**: MVVM
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

## Development Workflow
1. Create a feature branch from main: `feature/your-feature-name`
2. Implement your changes following the architecture patterns
3. Write tests for your changes
4. Run tests and lint checks locally
5. Submit a pull request

## Best Practices
- Follow the existing architecture pattern (MVVM)
- Keep modules focused on their specific responsibilities
- Write unit tests for new functionality
- Use Compose previews for UI components
- Follow Kotlin coding conventions
- Use dependency injection with Hilt
- Keep feature modules independent and reusable

## Deployment
- Staging builds are deployed to DeployGate from `stg-release/*` branches
- CI/CD is handled by CircleCI
