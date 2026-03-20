plugins {
	java
	id("org.springframework.boot") version "4.0.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

val gatling: Configuration by configurations.creating

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	gatling("io.gatling.highcharts:gatling-charts-highcharts:3.15.0")
}

sourceSets {
	create("gatling") {
		java.srcDir("src/gatling/java")
		compileClasspath += configurations["gatling"]
		runtimeClasspath += configurations["gatling"]
	}
}

tasks.register<JavaExec>("gatlingRun") {
	group = "verification"
	dependsOn(tasks.named("compileGatlingJava"))
	classpath = sourceSets["gatling"].runtimeClasspath
	mainClass.set("io.gatling.app.Gatling")
	jvmArgs(
		"--add-opens=java.base/java.lang=ALL-UNNAMED",
		"--add-opens=java.base/java.lang.reflect=ALL-UNNAMED",
		"--add-opens=java.base/java.io=ALL-UNNAMED",
		"--add-opens=java.base/java.util=ALL-UNNAMED",
		"--add-opens=java.base/java.util.concurrent=ALL-UNNAMED"
	)
	args(
		"-s", "com.example.demo.SampleSimulation",
		"-rf", "${layout.buildDirectory.get()}/reports/gatling"
	)
}

tasks.withType<Test> {
	useJUnitPlatform()
}
