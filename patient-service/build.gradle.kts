plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("jacoco")
	id("org.owasp.dependencycheck") version "9.0.7"
}

group = "com.pam"
version = "0.0.1-SNAPSHOT"

jacoco {
	toolVersion = "0.8.11"
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

val integrationTest by tasks.registering(Test::class) {
	description = "Runs integration tests."
	group = "verification"

	useJUnitPlatform {
		includeTags("integration")
	}

	mustRunAfter(tasks.test)
}

tasks.jacocoTestReport {
	executionData(tasks.test.get().extensions.getByType<JacocoTaskExtension>().destinationFile)
	classDirectories.setFrom(
			files(classDirectories.files.map {
				fileTree(it) {
					exclude(
							"**/model/**",
							"**/config/**"
					)
				}
			})
	)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks.jacocoTestCoverageVerification {
	dependsOn(tasks.jacocoTestReport)
	violationRules {
		rule {
			limit {
				minimum = "0.80".toBigDecimal()
			}
		}
	}
}

dependencyCheck {
	failBuildOnCVSS = 7.0f
	formats = listOf("HTML", "JSON")
	suppressionFile = "config/dependency-check/suppression.xml"
	analyzers.apply {
		assemblyEnabled = false
		nodeEnabled = false
	}
}

// Quality check task dependencies
tasks.check {
	dependsOn(
			integrationTest,
			tasks.jacocoTestCoverageVerification,
			tasks.dependencyCheckAnalyze,
	)
}

// Define task ordering
tasks.jacocoTestCoverageVerification {
	mustRunAfter(tasks.jacocoTestReport)
}

tasks.dependencyCheckAnalyze {
	mustRunAfter(tasks.jacocoTestCoverageVerification)
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2023.0.3"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-config")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.retry:spring-retry:2.0.10")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}