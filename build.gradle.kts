plugins {
    id("java")
}

group = "ru.nsu.e.shelbogashev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven{
        url = uri("https://jitpack.io")
    }
}

dependencies {
    implementation("com.github.EricShelbogashev:icg-filter:main-SNAPSHOT")
//    testImplementation(platform("org.junit:junit-bom:5.9.1"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

