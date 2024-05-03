plugins {
    id("java")
}

group = "ru.nsu.e.shelbogashev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.1.0")
    implementation("com.formdev:flatlaf:3.4.1")
}

tasks.test {
    useJUnitPlatform()
}

