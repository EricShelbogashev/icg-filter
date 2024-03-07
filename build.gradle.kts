plugins {
    id("java")
}

group = "ru.nsu.e.shelbogashev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

