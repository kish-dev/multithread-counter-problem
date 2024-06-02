kotlin
plugins {
    kotlin("jvm") version "1.9.23"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Добавлена зависимость к стандартной библиотеке Kotlin для тестов,
    // если вы собираетесь использовать Kotlin в своих тестах
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // Зависимость JUnit 4
    testImplementation("junit:junit:4.13.2")
}

tasks.test {
    // Удалена настройка useJUnitPlatform, так как она специфична для JUnit 5
}

// Настройки для использования конкретной версии JVM в проекте Kotlin,
// убедитесь, что вы используете корректный номер версии JDK
kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(21))
    }
}
