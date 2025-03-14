plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
    id("jacoco")
    id("com.github.spotbugs") version "6.0.18"
    id("checkstyle")
}

group = "com.pam"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

jacoco {
    toolVersion = "0.8.11"
    reportsDirectory.set(layout.buildDirectory.dir("reports/jacoco"))
}

repositories {
    mavenCentral()
}

val springCloudVersion = "2023.0.3"

dependencies {
    constraints {
        implementation("com.fasterxml.jackson:jackson-bom:2.16.1")
        implementation("org.apache.commons:commons-lang3:3.14.0")
        implementation("org.apache.commons:commons-text:1.11.0")
    }

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.retry:spring-retry")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-client")
    implementation("org.springframework.cloud:spring-cloud-starter-kubernetes-client-config")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("io.micrometer:micrometer-registry-prometheus")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    runtimeOnly("org.postgresql:postgresql")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    useJUnitPlatform {
        excludeTags("integration")
    }
    finalizedBy(tasks.jacocoTestReport)
}

val integrationTest = tasks.register<Test>("integrationTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath

    useJUnitPlatform {
        includeTags("integration")
    }

    mustRunAfter(tasks.test)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    outputs.cacheIf { true }

    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude("**/model/**", "**/config/**")
            }
        })
    )

    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/html"))
    }
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.withType<Checkstyle>().configureEach {
    inputs.files(tasks.compileJava)
    inputs.files(tasks.compileTestJava)

    outputs.cacheIf { true }
    outputs.upToDateWhen { true }
}

tasks.checkstyleMain {
    dependsOn(tasks.compileJava, tasks.compileTestJava)
    shouldRunAfter(tasks.compileTestJava)

    source = fileTree(project.projectDir) {
        include("src/main/java/**/*.java")
        exclude("**/generated/**")
    }
}

tasks.checkstyleTest {
    dependsOn(tasks.compileTestJava)

    source = fileTree(project.projectDir) {
        include("src/test/java/**/*.java")
        exclude("**/generated/**")
    }
}

tasks.check {
    dependsOn(
        tasks.compileJava,
        tasks.compileTestJava,
        tasks.test,
        tasks.checkstyleMain,
        tasks.checkstyleTest,
        tasks.jacocoTestReport,
        tasks.jacocoTestCoverageVerification,
        tasks.spotbugsMain,
        tasks.spotbugsTest,
        integrationTest
    )
}
