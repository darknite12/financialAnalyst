package org.diytechprojects.financialanalyst.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.diytechprojects.financialanalyst.FinancialAnalystApp;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = FinancialAnalystApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
