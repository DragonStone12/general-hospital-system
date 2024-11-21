plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("jacoco")
	// id("org.owasp.dependencycheck") version "11.1.0"
}

group = "com.pam"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
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

	// Spring Boot dependencies
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.retry:spring-retry:2.0.10")

	// Lombok configuration
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")

	// Development and testing dependencies
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

	useJUnitPlatform {
		includeTags("integration")
	}

	mustRunAfter(tasks.test)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

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

// dependencyCheck {
//    suppressionFile = "${project.projectDir}/config/dependency-check/suppression.xml"
//    analyzers.apply {
//       assemblyEnabled = false
//       nodeEnabled = false
//    }
//    failBuildOnCVSS = 7.0f
//    formats = listOf("HTML", "JSON")
//    scanConfigurations = listOf("runtimeClasspath", "testRuntimeClasspath")
// }

tasks.check {
	dependsOn(
			integrationTest,
			tasks.jacocoTestCoverageVerification,
			// tasks.dependencyCheckAnalyze
	)
}