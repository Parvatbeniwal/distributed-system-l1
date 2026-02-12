plugins {
    java
}

subprojects {
    apply(plugin = "java")

    // Java compatibility
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        "testImplementation"("org.junit.jupiter:junit-jupiter:5.9.2")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
