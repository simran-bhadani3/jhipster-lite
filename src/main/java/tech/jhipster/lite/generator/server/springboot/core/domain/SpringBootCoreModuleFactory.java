package tech.jhipster.lite.generator.server.springboot.core.domain;

import static tech.jhipster.lite.module.domain.JHipsterModule.*;

import tech.jhipster.lite.error.domain.Assert;
import tech.jhipster.lite.module.domain.JHipsterDestination;
import tech.jhipster.lite.module.domain.JHipsterModule;
import tech.jhipster.lite.module.domain.JHipsterSource;
import tech.jhipster.lite.module.domain.javabuild.GroupId;
import tech.jhipster.lite.module.domain.javabuildplugin.JavaBuildPlugin;
import tech.jhipster.lite.module.domain.javadependency.JavaDependency;
import tech.jhipster.lite.module.domain.javadependency.JavaDependencyScope;
import tech.jhipster.lite.module.domain.javadependency.JavaDependencyType;
import tech.jhipster.lite.module.domain.properties.JHipsterModuleProperties;

public class SpringBootCoreModuleFactory {

  private static final JHipsterSource SOURCE = from("server/springboot/core");

  private static final GroupId SRPING_BOOT_GROUP = groupId("org.springframework.boot");
  private static final String APPLICATION_PROPERTIES = "application.properties";

  private static final JHipsterDestination MAIN_RESOURCE_DESTINATION = to("src/main/resources");
  private static final JHipsterDestination MAIN_CONFIG_DESTINATION = MAIN_RESOURCE_DESTINATION.append("config");
  private static final JHipsterDestination TEST_RESOURCES_DESTINATION = to("src/test/resources");
  private static final JHipsterDestination TEST_CONFIG_DESTINATION = TEST_RESOURCES_DESTINATION.append("config");

  public JHipsterModule buildModule(JHipsterModuleProperties properties) {
    Assert.notNull("properties", properties);

    String mainClassName = properties.projectBaseName().capitalized();
    String packagePath = properties.basePackage().path();
    JHipsterDestination testDestination = toSrcTestJava().append(packagePath);

    //@formatter:off
    return moduleBuilder(properties)
      .context()
        .put("mainClass", mainClassName)
        .and()
      .javaDependencies()
        .dependencyManagement(springBootBom())
        .dependency(SRPING_BOOT_GROUP, artifactId("spring-boot-starter"))
        .dependency(springBootConfigurationProcessor())
        .dependency(groupId("org.apache.commons"), artifactId("commons-lang3"))
        .dependency(springBootTest())
        .and()
      .javaBuildPlugins()
        .pluginManagement(springBootPluginManagement())
        .plugin(springBootMavenPlugin())
        .and()
      .files()
        .add(SOURCE.template("MainApp.java"), toSrcMainJava().append(packagePath).append(mainClassName + "App.java"))
        .add(SOURCE.template("MainAppTest.java"), testDestination.append(mainClassName + "AppTest.java"))
        .add(SOURCE.template("IntegrationTest.java"), testDestination.append("IntegrationTest.java"))
        .add(SOURCE.template(APPLICATION_PROPERTIES), MAIN_CONFIG_DESTINATION.append(APPLICATION_PROPERTIES))
        .add(SOURCE.template("application-local.properties"), MAIN_CONFIG_DESTINATION.append("application-local.properties"))
        .add(SOURCE.template("application-test.properties"), TEST_CONFIG_DESTINATION.append(APPLICATION_PROPERTIES))
        .add(SOURCE.template("logback-spring.xml"), MAIN_RESOURCE_DESTINATION.append("logback-spring.xml"))
        .add(SOURCE.template("logback.xml"), TEST_RESOURCES_DESTINATION.append("logback.xml"))
        .and()
      .build();
    //@formatter:on
  }

  private JavaDependency springBootBom() {
    return JavaDependency
      .builder()
      .groupId("org.springframework.boot")
      .artifactId("spring-boot-dependencies")
      .versionSlug("spring-boot")
      .type(JavaDependencyType.POM)
      .scope(JavaDependencyScope.IMPORT)
      .build();
  }

  private JavaDependency springBootConfigurationProcessor() {
    return JavaDependency.builder().groupId(SRPING_BOOT_GROUP).artifactId("spring-boot-configuration-processor").optional().build();
  }

  private JavaDependency springBootTest() {
    return JavaDependency
      .builder()
      .groupId(SRPING_BOOT_GROUP)
      .artifactId("spring-boot-starter-test")
      .scope(JavaDependencyScope.TEST)
      .build();
  }

  private JavaBuildPlugin springBootPluginManagement() {
    return JavaBuildPlugin
      .builder()
      .groupId(SRPING_BOOT_GROUP)
      .artifactId("spring-boot-maven-plugin")
      .versionSlug("spring-boot")
      .additionalElements(
        """
        <executions>
          <execution>
            <goals>
              <goal>repackage</goal>
            </goals>
          </execution>
        </executions>
        <configuration>
          <mainClass>${start-class}</mainClass>
        </configuration>
        """
      )
      .build();
  }

  private JavaBuildPlugin springBootMavenPlugin() {
    return JavaBuildPlugin.builder().groupId(SRPING_BOOT_GROUP).artifactId("spring-boot-maven-plugin").build();
  }
}
