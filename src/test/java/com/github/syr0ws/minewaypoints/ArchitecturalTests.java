package com.github.syr0ws.minewaypoints;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class ArchitecturalTests {

    private static final String ROOT_PACKAGE = "com.github.syr0ws.minewaypoints";

    private static final JavaClasses classes = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(ROOT_PACKAGE);

    @Test
    public void application_should_not_depend_on_platform_api() {
        noClasses()
                .that().resideInAPackage("..plugin..")
                .should().dependOnClassesThat().resideInAPackage("org.bukkit..")
                .because("Application cannot depend on the Minecraft Server API")
                .check(classes);
    }

    @Test
    public void application_should_not_depend_on_infrastructure() {
        noClasses()
                .that().resideInAPackage("..plugin..")
                .should().dependOnClassesThat().resideInAPackage("..infrastructure..")
                .because("Application cannot depend on the infrastructure layer")
                .check(classes);
    }

    @Test
    public void application_should_not_depend_on_platform() {
        noClasses()
                .that().resideInAPackage("..plugin..")
                .should().dependOnClassesThat().resideInAPackage("..platform..")
                .because("Application cannot depend on the platform layer")
                .check(classes);
    }

    @Test
    public void infrastructure_should_not_depend_on_platform() {
        noClasses()
                .that().resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat().resideInAPackage("..platform..")
                .because("Infrastructure cannot depend on the platform layer")
                .check(classes);
    }
}
