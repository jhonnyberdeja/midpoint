/*
 * Copyright (c) 2010-2018 Evolveum
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.evolveum.midpoint.testing.schrodinger.labs;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.evolveum.midpoint.schrodinger.MidPoint;
import com.evolveum.midpoint.schrodinger.page.configuration.ImportObjectPage;
import com.evolveum.midpoint.schrodinger.page.resource.ListResourcesPage;
import com.evolveum.midpoint.schrodinger.page.resource.ResourceWizardPage;
import com.evolveum.midpoint.schrodinger.page.resource.ViewResourcePage;
import com.evolveum.midpoint.schrodinger.util.Schrodinger;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.evolveum.midpoint.testing.schrodinger.TestBase;

import java.io.File;

import static com.codeborne.selenide.Selenide.$;


/**
 * Created by honchar
 */
public class ImportResourceTest extends TestBase {

    private static final File CSV_RESOURCE = new File("./src/test/resources/labs/resources/localhost-csvfile-1-document-access.xml");
    private static final String RESOURCE_NAME = "CSV-1 (Document Access)";
    private static final String UNIQUE_ATTRIBUTE_NAME = "login";
    private static final String PASSWORD_ATTRIBUTE_NAME = "password";
    private static final String PASSWORD_ATTRIBUTE_RESOURCE_KEY = "User password attribute name";
    private static final String UNIQUE_ATTRIBUTE_RESOURCE_KEY = "Unique attribute name";

    @Test
    public void test001ImportCsvResource() {
        ImportObjectPage importPage = basicPage.importObject();
        //import resource
        Assert.assertTrue(
                importPage
                        .getObjectsFromFile()
                        .chooseFile(CSV_RESOURCE)
                        .checkOverwriteExistingObject()
                        .clickImport()
                        .feedback()
                        .isSuccess()
        );

        ListResourcesPage listResourcesPage = basicPage.listResources();

        //test connection
        Assert.assertTrue(listResourcesPage
                .testConnectionClick(RESOURCE_NAME)
                .feedback()
                .isSuccess());
    }

    @Test(dependsOnMethods = {"test001ImportCsvResource"}, priority = 1)
    public void test002ViewResourceDetailsPage(){

        //click Edit configuration on the resource edit page
        navigateToViewResourcePage()
                .clickEditResourceConfiguration();

        SelenideElement uniqueAttributeField = $(Schrodinger.byDataResourceKey(UNIQUE_ATTRIBUTE_RESOURCE_KEY))
                .waitUntil(Condition.appear, MidPoint.TIMEOUT_DEFAULT_2_S);

        // Unique attribute name should be login
        Assert.assertTrue(uniqueAttributeField
                .$(By.tagName("input"))
                .waitUntil(Condition.appear, MidPoint.TIMEOUT_DEFAULT_2_S)
                .getValue()
                .equals(UNIQUE_ATTRIBUTE_NAME));

        SelenideElement passwordAttributeField = $(Schrodinger.byDataResourceKey(PASSWORD_ATTRIBUTE_RESOURCE_KEY))
                .waitUntil(Condition.appear, MidPoint.TIMEOUT_DEFAULT_2_S);

        // Password attribute name should be password
        Assert.assertTrue(passwordAttributeField
                .$(By.tagName("input"))
                .waitUntil(Condition.appear, MidPoint.TIMEOUT_DEFAULT_2_S)
                .getValue()
                .equals(PASSWORD_ATTRIBUTE_NAME));

    }

    @Test(dependsOnMethods = {"test001ImportCsvResource"}, priority = 2)
    public void test003showUsingWizard(){
        navigateToViewResourcePage()
                .clickShowUsingWizard();

        Assert.assertTrue($(By.className("wizard"))
                .waitUntil(Condition.appear, MidPoint.TIMEOUT_DEFAULT_2_S)
                .exists());

        //TODO finish navigating through tabs
    }


    private ViewResourcePage navigateToViewResourcePage(){
        ListResourcesPage resourcesList = basicPage.listResources();

        return resourcesList
                .table()
                .clickByName(RESOURCE_NAME);
    }
}

