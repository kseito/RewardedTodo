# GEMINI.md

This file provides guidance to Gemini when working with code in this repository.

## Architecture Overview

This Android project implements **Clean Architecture** with a **multi-modular structure** using MVVM for the presentation layer. The architecture consists of:

- **Domain Layer** (`/domain/reward`, `/domain/todo`) - Business entities, use cases interfaces, repository interfaces
- **Application Layer** (`/application/reward`, `/application/todo`) - Use case implementations (interactors) containing business logic
- **Data Layer** (`/data/*`) - Repository implementations, Room database access, external APIs (Todoist)
- **Feature Layer** (`/feature/*`) - ViewModels, UI screens (mix of Views and Compose), navigation
- **Common Layer** (`/common/*`) - Shared utilities (database, encrypted storage, UI components)

### Key Patterns
- **Use Case/Interactor Pattern**: Each business operation has a use case interface implemented by an interactor
- **Repository Pattern**: Domain defines interfaces, data layer provides implementations
- **Value Objects**: Type-safe domain modeling with inline value classes (`RewardName`, `RewardId`)
- **Dependency Injection**: Dagger Hilt with modular DI setup

## Build Commands

```bash
# Build project
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Run unit tests (all modules)
./gradlew testDebugUnitTest

# Run specific module tests
./gradlew :module:testDebugUnitTest

# Run lint checks
./gradlew lintDebug

# Format Kotlin code
./gradlew spotlessKotlinApply

# Screenshot comparison tests (Roborazzi)
./gradlew compareRoborazziDebug
```

## Module Structure

The project uses 34+ modules organized by domain:
- Each domain area (reward, todo, auth) has separate modules for domain, application, data, and feature layers
- Common modules provide shared functionality
- Build-logic contains custom Gradle convention plugins

## Testing

- Unit tests are located in each module's `src/test/` directory
- Uses JUnit, Truth, MockK, and Robolectric for testing
- Screenshot testing with Roborazzi for visual regression testing
- Test utilities in `/test/` modules (e.g., `DummyCreator` for test data)

## Development Notes

- **Dependencies**: Use version catalog (`gradle/libs.versions.toml`) for dependency management
- **Architecture**: Follow strict layer separation - domain layer has no Android dependencies
- **Code Style**: Spotless enforces Kotlin code formatting
- **Branch Strategy**: Feature branches from main, staging deployments from `stg-release/*` branches
- **CI/CD**: GitHub Actions for testing, screenshot comparison, and DeployGate deployments
