package org.diytechprojects.financialanalyst;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {
        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("org.diytechprojects.financialanalyst");

        noClasses()
            .that()
            .resideInAnyPackage("org.diytechprojects.financialanalyst.service..")
            .or()
            .resideInAnyPackage("org.diytechprojects.financialanalyst.repository..")
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..org.diytechprojects.financialanalyst.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses);
    }
}
