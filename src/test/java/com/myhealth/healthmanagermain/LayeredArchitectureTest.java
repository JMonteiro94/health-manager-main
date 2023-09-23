package com.myhealth.healthmanagermain;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.belongToAnyOf;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.myhealth.healthmanagermain", importOptions = DoNotIncludeTests.class)
public class LayeredArchitectureTest {

  @ArchTest
  public static final ArchRule domainEnumsPackageRule = classes()
      .that().resideInAPackage("..enums..")
      .should().onlyBeAccessed().byAnyPackage("..domain..", "..bootstrap..");

  @ArchTest
  public static final ArchRule respectsTechnicalArchitectureLayers =
      layeredArchitecture().consideringAllDependencies()
          .layer("Root").definedBy("..healthmanagermain..")
          .layer("Demo").definedBy("..bootstrap..")
          .layer("Config").definedBy("..config..")
          .layer("Web").definedBy("..web..")
          .optionalLayer("Exception").definedBy("..web.errors..")
          .optionalLayer("Service").definedBy("..service..")
          .optionalLayer("Domain Service").definedBy("..service.domain..")
          .layer("Security").definedBy("..security..")
          .layer("Persistence").definedBy("..repository..")
          .layer("Domain").definedBy("..healthmanagermain.domain..")

          .whereLayer("Config").mayOnlyBeAccessedByLayers("Root")
          .whereLayer("Web").mayOnlyBeAccessedByLayers("Service")
          .whereLayer("Exception").mayOnlyBeAccessedByLayers("Service", "Web")
          .whereLayer("Service").mayOnlyBeAccessedByLayers("Web", "Config", "Demo", "Security")
          .whereLayer("Security").mayOnlyBeAccessedByLayers("Config", "Service", "Web")
          .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Service")
          .whereLayer("Domain")
          .mayOnlyBeAccessedByLayers("Persistence", "Service", "Security", "Web", "Config", "Demo")

          .ignoreDependency(belongToAnyOf(HealthmanagermainApplication.class), alwaysTrue())
          .ignoreDependency(alwaysTrue(), belongToAnyOf(
              com.myhealth.healthmanagermain.config.Constants.class
          ));

  @ArchTest
  public static final ArchRule cyclesCheck = slices().matching("com.(**)").should()
      .beFreeOfCycles();

}
