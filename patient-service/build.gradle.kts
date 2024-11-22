plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	id("checkstyle")
	id("jacoco")
	id("org.owasp.dependencycheck") version "9.0.7"
}

group = "com.pam"
version = "0.0.1-SNAPSHOT"

checkstyle {
	configFile = file("config/checkstyle/checkstyle.xml")
	toolVersion = "10.12.5"
}


jacoco {
	toolVersion = "0.8.11"
}

tasks.withType<Test> {
	useJUnitPlatform {
		exclude("integration")
	}
	finalizedBy("jacocoTestReport")
}

// Separate unit and integration tests
sourceSets {
	create("integrationTest") {
		java {
			compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
			runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
			srcDir(file("src/integrationTest/java"))
		}
		resources.srcDir(file("src/integrationTest/resources"))
	}
}

val integrationTest = task<Test>("integrationTest") {
	description = "Runs integration tests."
	group = "verification"

	testClassesDirs = sourceSets["integrationTest"].output.classesDirs
	classpath = sourceSets["integrationTest"].runtimeClasspath

	useJUnitPlatform {
		includeTags("integration")
	}

	shouldRunAfter(tasks.test)
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	executionData(tasks.test, integrationTest)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	afterEvaluate {
		classDirectories.setFrom(files(classDirectories.files.map {
			fileTree(it) {
				exclude(
						"**/model/**",
						"**/config/**"
				)
			}
		}))
	}
	dependsOn(tasks.test)
	dependsOn(integrationTest)
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.80".toBigDecimal()
			}
		}
	}
	dependsOn(tasks.jacocoTestReport)
}

tasks.withType<Checkstyle>().configureEach {
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

dependencyCheck {
	failBuildOnCVSS = 7
	formats = listOf("HTML", "JSON")
	suppressionFile = "config/dependency-check/suppression.xml"
	analyzers {
		assemblyEnabled = false
		nodeEnabled = false
	}
}


// Configure task dependencies
tasks.check {
	dependsOn(integrationTest)
	dependsOn(tasks.jacocoTestCoverageVerification)
	dependsOn(tasks.dependencyCheckAnalyze)
	dependsOn(tasks.checkstyleMain)
	dependsOn(tasks.checkstyleTest)
}

// Define task ordering
tasks.checkstyleMain {
	shouldRunAfter(tasks.clean)
}

tasks.checkstyleTest {
	shouldRunAfter(tasks.checkstyleMain)
}

tasks.test {
	shouldRunAfter(tasks.checkstyleTest)
}

integrationTest {
	shouldRunAfter(tasks.test)
}

tasks.jacocoTestReport {
	shouldRunAfter(integrationTest)
}

tasks.jacocoTestCoverageVerification {
	shouldRunAfter(tasks.jacocoTestReport)
}

tasks.dependencyCheckAnalyze {
	shouldRunAfter(tasks.jacocoTestCoverageVerification)
}

// Add configurations for integration test dependencies
configurations {
	create("integrationTestImplementation") {
		extendsFrom(configurations["testImplementation"])
	}
	create("integrationTestRuntimeOnly") {
		extendsFrom(configurations["testRuntimeOnly"])
	}
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
